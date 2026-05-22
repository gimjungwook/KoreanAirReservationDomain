// export-pdf.mjs · 각 슬라이드를 PDF 페이지로 렌더 후 pdf-lib로 병합.
// three.js 슬라이드는 렌더 안정화를 위해 대기 시간을 넉넉히 둔다.
import { chromium } from "playwright";
import { PDFDocument } from "pdf-lib";
import { mkdirSync, writeFileSync, readFileSync } from "fs";
import { resolve, dirname } from "path";
import { fileURLToPath } from "url";

const __dirname = dirname(fileURLToPath(import.meta.url));
const slidesDir = resolve(__dirname, "slides");
const outDir = resolve(__dirname, "_pdf");
mkdirSync(outDir, { recursive: true });

const manifestSrc = readFileSync(resolve(__dirname, "deck-manifest.js"), "utf-8");
const manifestMatch = manifestSrc.match(/DECK_MANIFEST\s*=\s*\[([\s\S]*?)\];/);
if (!manifestMatch) { console.error("DECK_MANIFEST 없음"); process.exit(1); }
const files = [...manifestMatch[1].matchAll(/"slides\/([^"]+\.html)"/g)].map((m) => m[1]);
console.log(`manifest 순서: ${files.length} slides`);

const browser = await chromium.launch();
const ctx = await browser.newContext({ viewport: { width: 1920, height: 1080 }, deviceScaleFactor: 2 });
const page = await ctx.newPage();
const merged = await PDFDocument.create();

for (const file of files) {
  const url = `file://${resolve(slidesDir, file)}`;
  console.log(`→ ${file}`);
  await page.goto(url, { waitUntil: "networkidle" });
  // three.js / 위젯 애니메이션 안정화 대기
  await page.waitForTimeout(2600);
  const pdfBytes = await page.pdf({ width: "1920px", height: "1080px", printBackground: true, margin: { top: 0, bottom: 0, left: 0, right: 0 } });
  const single = await PDFDocument.load(pdfBytes);
  const [pg] = await merged.copyPages(single, [0]);
  merged.addPage(pg);
  console.log(`  ✓ added`);
}

await browser.close();
const out = await merged.save();
const outPath = resolve(outDir, "OODP-iter3-revision-Observer-deck.pdf");
writeFileSync(outPath, out);
console.log(`\nDone → ${outPath}`);
