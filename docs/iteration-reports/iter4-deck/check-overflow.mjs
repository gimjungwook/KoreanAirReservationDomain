// 1920×1080 넘는 슬라이드 탐지.
import { chromium } from "playwright";
import { readFileSync } from "fs";
import { resolve, dirname } from "path";
import { fileURLToPath } from "url";
const __dirname = dirname(fileURLToPath(import.meta.url));
const slidesDir = resolve(__dirname, "slides");
const src = readFileSync(resolve(__dirname, "deck-manifest.js"), "utf-8");
const files = [...src.match(/DECK_MANIFEST\s*=\s*\[([\s\S]*?)\];/)[1].matchAll(/"slides\/([^"]+\.html)"/g)].map((m) => m[1]);
const browser = await chromium.launch();
const ctx = await browser.newContext({ viewport: { width: 1920, height: 1080 } });
const page = await ctx.newPage();
const W = 1920, H = 1080; const issues = [];
for (const file of files) {
  await page.goto(`file://${resolve(slidesDir, file)}`, { waitUntil: "networkidle" });
  await page.waitForTimeout(1500);
  const m = await page.evaluate(() => {
    const s = document.querySelector("section.slide") || document.body;
    return { scrollW: s.scrollWidth, scrollH: s.scrollHeight, bodyScrollH: document.body.scrollHeight, bodyScrollW: document.body.scrollWidth };
  });
  const overH = m.scrollH > H || m.bodyScrollH > H;
  const overW = m.scrollW > W || m.bodyScrollW > W;
  console.log(`${(overH||overW)?"⚠️ ":"✓ "}${file}  · slide ${m.scrollW}×${m.scrollH}`);
  if (overH || overW) issues.push({ file, ...m });
}
await browser.close();
console.log(issues.length === 0 ? "\nAll slides fit 1920×1080." : `\n${issues.length} overflow.`);
