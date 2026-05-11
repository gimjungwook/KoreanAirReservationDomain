import { spawn } from "node:child_process";
import { existsSync } from "node:fs";
import { writeFile, mkdir } from "node:fs/promises";
import { dirname, join } from "node:path";
import { tmpdir } from "node:os";
import { fileURLToPath, pathToFileURL } from "node:url";

const toolDir = dirname(fileURLToPath(import.meta.url));
const deck = dirname(toolDir);
const outDir = process.env.ITER2_DECK_OUT || deck;
const slidePdfDir = `${outDir}/iter2-vector-slide-pdfs`;
const chromePath = process.env.CHROME_PATH || findChromePath();
const port = Number(process.env.CHROME_DEBUG_PORT || 9333);

const slides = [
  "01-cover.html",
  "02-feature-list.html",
  "03-extension-table.html",
  "04-rdp-table.html",
  "05-roles.html",
  "06-usecase.html",
  "07-uc-scenarios.html",
  "08-classdiagram.html",
  "09-class-detail.html",
  "10-strategy-textbook-vs-team.html",
  "11-strategy-code.html",
  "12-state-diagram.html",
  "13-sequence-diagram.html",
  "14-demo.html",
  "15-thanks.html",
];

function delay(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

function findChromePath() {
  const candidates = [
    "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome",
    "/Applications/Chromium.app/Contents/MacOS/Chromium",
    "/usr/bin/google-chrome",
    "/usr/bin/chromium",
    "/usr/bin/chromium-browser",
  ];
  return candidates.find((path) => existsSync(path));
}

async function waitForChrome() {
  for (let i = 0; i < 80; i += 1) {
    try {
      const res = await fetch(`http://127.0.0.1:${port}/json/list`);
      if (res.ok) return await res.json();
    } catch (_) {
      // Chrome is still starting.
    }
    await delay(150);
  }
  throw new Error("Timed out waiting for Chrome DevTools");
}

async function openWs(url) {
  return new Promise((resolve, reject) => {
    const ws = new WebSocket(url);
    ws.addEventListener("open", () => resolve(ws), { once: true });
    ws.addEventListener("error", reject, { once: true });
  });
}

function makeCdp(ws) {
  let id = 1;
  const pending = new Map();
  const listeners = new Map();

  ws.addEventListener("message", (event) => {
    const msg = JSON.parse(event.data);
    if (msg.id && pending.has(msg.id)) {
      const { resolve, reject } = pending.get(msg.id);
      pending.delete(msg.id);
      if (msg.error) reject(new Error(JSON.stringify(msg.error)));
      else resolve(msg.result);
      return;
    }
    if (msg.method && listeners.has(msg.method)) {
      for (const resolve of listeners.get(msg.method)) resolve(msg.params || {});
      listeners.delete(msg.method);
    }
  });

  function send(method, params = {}) {
    const callId = id++;
    ws.send(JSON.stringify({ id: callId, method, params }));
    return new Promise((resolve, reject) => pending.set(callId, { resolve, reject }));
  }

  function waitEvent(method, timeoutMs = 8000) {
    return new Promise((resolve, reject) => {
      const timer = setTimeout(() => reject(new Error(`Timed out waiting for ${method}`)), timeoutMs);
      const wrapped = (params) => {
        clearTimeout(timer);
        resolve(params);
      };
      if (!listeners.has(method)) listeners.set(method, []);
      listeners.get(method).push(wrapped);
    });
  }

  return { send, waitEvent };
}

async function renderOne(cdp, slide, index) {
  const url = pathToFileURL(`${deck}/slides/${slide}`).href;
  const loaded = cdp.waitEvent("Page.loadEventFired", 12000);
  await cdp.send("Page.navigate", { url });
  await loaded;
  await cdp.send("Runtime.evaluate", {
    expression: "document.fonts && document.fonts.ready ? document.fonts.ready.then(() => true) : true",
    awaitPromise: true,
  });
  await delay(250);

  const result = await cdp.send("Page.printToPDF", {
    landscape: false,
    printBackground: true,
    displayHeaderFooter: false,
    paperWidth: 20,
    paperHeight: 11.25,
    marginTop: 0,
    marginBottom: 0,
    marginLeft: 0,
    marginRight: 0,
    scale: 1,
    preferCSSPageSize: false,
  });
  const out = `${slidePdfDir}/${String(index).padStart(2, "0")}-${slide.replace(".html", ".pdf")}`;
  await writeFile(out, Buffer.from(result.data, "base64"));
  console.log(`printed ${out}`);
}

async function main() {
  if (!chromePath) {
    throw new Error("Chrome not found. Set CHROME_PATH=/path/to/chrome and retry.");
  }
  await mkdir(slidePdfDir, { recursive: true });
  const profile = join(tmpdir(), `codex-chrome-profile-${Date.now()}`);
  const chrome = spawn(chromePath, [
    "--headless=new",
    "--disable-gpu",
    "--no-first-run",
    "--disable-extensions",
    "--allow-file-access-from-files",
    "--hide-scrollbars",
    `--remote-debugging-port=${port}`,
    `--user-data-dir=${profile}`,
    "about:blank",
  ], { stdio: ["ignore", "pipe", "pipe"] });

  try {
    const targets = await waitForChrome();
    const page = targets.find((target) => target.type === "page");
    if (!page) throw new Error("No Chrome page target found");

    const ws = await openWs(page.webSocketDebuggerUrl);
    const cdp = makeCdp(ws);
    await cdp.send("Page.enable");
    await cdp.send("Runtime.enable");

    for (let i = 0; i < slides.length; i += 1) {
      await renderOne(cdp, slides[i], i + 1);
    }
    console.log(`\nNext: python3 tools/merge_slide_pdfs.py`);
    ws.close();
  } finally {
    chrome.kill();
  }
}

main().catch((error) => {
  console.error(error);
  process.exit(1);
});
