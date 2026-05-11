from pathlib import Path
from pypdf import PdfReader, PdfWriter


DECK = Path(__file__).resolve().parents[1]
SLIDE_DIR = DECK / "iter2-vector-slide-pdfs"
OUT = DECK / "iter2-final-hq.pdf"


def main():
    files = sorted(SLIDE_DIR.glob("*.pdf"))
    if len(files) != 15:
        raise SystemExit(f"Expected 15 slide PDFs in {SLIDE_DIR}, found {len(files)}")

    writer = PdfWriter()
    for path in files:
        reader = PdfReader(str(path))
        if not reader.pages:
            raise SystemExit(f"No pages in {path}")
        writer.add_page(reader.pages[0])

    with OUT.open("wb") as fp:
        writer.write(fp)

    print(f"Wrote {OUT} ({len(writer.pages)} pages)")


if __name__ == "__main__":
    main()
