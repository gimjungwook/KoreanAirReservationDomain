// export-pdf.mjs · iter4-deck-simple
// manifest.js 순서대로 각 슬라이드(자체 완결 HTML)를 1920x1080 PDF 페이지로 렌더 후 pdf-lib로 병합.
import { chromium } from "playwright";
import { PDFDocument } from "pdf-lib";
import { writeFileSync, readFileSync } from "fs";
import { resolve, dirname } from "path";
import { fileURLToPath } from "url";

const __dirname = dirname(fileURLToPath(import.meta.url));
const slidesDir = resolve(__dirname, "slides");

const manifestSrc = readFileSync(resolve(__dirname, "manifest.js"), "utf-8");
const m = manifestSrc.match(/DECK_MANIFEST\s*=\s*\[([\s\S]*?)\];/);
if (!m) { console.error("DECK_MANIFEST 없음"); process.exit(1); }
const files = [...m[1].matchAll(/"slides\/([^"]+\.html)"/g)].map((x) => x[1]);
console.log(`manifest: ${files.length} slides`);

const browser = await chromium.launch();
const ctx = await browser.newContext({ viewport: { width: 1920, height: 1080 }, deviceScaleFactor: 2 });
const page = await ctx.newPage();
const merged = await PDFDocument.create();

for (const file of files) {
  const url = `file://${resolve(slidesDir, file)}`;
  await page.goto(url, { waitUntil: "networkidle" });
  await page.waitForTimeout(500);
  const pdfBytes = await page.pdf({ width: "1920px", height: "1080px", printBackground: true, margin: { top: 0, bottom: 0, left: 0, right: 0 } });
  const single = await PDFDocument.load(pdfBytes);
  const [pg] = await merged.copyPages(single, [0]);
  merged.addPage(pg);
  process.stdout.write(`  ✓ ${file}\n`);
}

await browser.close();
const out = await merged.save();
const outPath = resolve(__dirname, "iter4-decorator-deck.pdf");
writeFileSync(outPath, out);
console.log(`\nDone → ${outPath}  (${files.length} pages)`);
