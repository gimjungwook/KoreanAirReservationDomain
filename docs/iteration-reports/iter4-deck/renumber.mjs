// 슬라이드 카운터(.slide-counter)를 manifest 순서대로 NN / TOTAL 로 갱신.
import { readFileSync, writeFileSync } from "fs";
import { resolve, dirname } from "path";
import { fileURLToPath } from "url";
const __dirname = dirname(fileURLToPath(import.meta.url));
const idx = readFileSync(resolve(__dirname, "deck-manifest.js"), "utf-8");
const files = [...idx.match(/DECK_MANIFEST\s*=\s*\[([\s\S]*?)\];/)[1].matchAll(/"slides\/([^"]+\.html)"/g)].map(m => m[1]);
const total = files.length;
files.forEach((f, i) => {
  const p = resolve(__dirname, "slides", f);
  let html = readFileSync(p, "utf-8");
  const n = String(i + 1).padStart(2, "0");
  const before = html;
  html = html.replace(/(<span class="slide-counter">)[^<]*(<\/span>)/, `$1${n} / ${total}$2`);
  if (html !== before) writeFileSync(p, html);
  console.log(`${n}/${total}  ${f}${html===before?"  (no counter)":""}`);
});
console.log(`Total ${total} slides`);
