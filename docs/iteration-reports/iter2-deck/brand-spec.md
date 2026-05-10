# Iter2 Deck · Brand Spec

> Adopted: 2026-05-10 · Style direction: Stripe / Linear Technical Documentation
> Context: ECE312 OODP Iteration 2 presentation (5/12), Korean Air reservation system

---

## 🎯 Core Assets

### Logo / wordmark
- Custom: "iter 2" wordmark in monospace (lowercase) is set inline in cover slide.
- No external brand logo (academic project, not a commercial deliverable).

### Diagram PNG slots (placeholder until Eclipse export ready)
All UML PNGs land in `assets/diagrams/`. Slots referenced by slides:

| Slot | Iter1 file | Iter2 file | Used by slide |
| --- | --- | --- | --- |
| Class diagram | `classDiagram-iter1.png` | `classDiagram-iter2.png` | 05 |
| Use case diagram | `reservationSystem-iter1.png` | `reservationSystem-iter2.png` | 06 |
| Reservation state diagram | `reservationState-iter1.png` | `reservationState-iter2.png` | 04 |
| Sequence — Book flight (iter1) | `bookFlight-iter1.png` | — | 07 |
| Sequence — Cancel + refund (iter2) | — | `cancelRefund-iter2.png` | 07 |
| Sequence — Lookup reservation (iter2) | — | `lookupReservation-iter2.png` | 07 |

Until those files exist, slides render an inline **placeholder card** (dashed border + caption) so layout is final and only the `<img>` source needs swapping.

---

## 🎨 Tokens

### Color
| Token | Value | Use |
| --- | --- | --- |
| `--bg` | `#FAFAFA` | Slide background |
| `--surface` | `#FFFFFF` | Card / code block bg |
| `--ink` | `#0A0A0A` | Primary text + headings |
| `--ink-2` | `#27272A` | Mono/code |
| `--ink-3` | `#52525B` | Secondary text |
| `--ink-4` | `#A1A1AA` | Captions, muted labels |
| `--rule` | `#E4E4E7` | Hairline rules, card borders |
| `--accent` | `#5E6AD2` | Brand accent (Linear violet) — patterns, links |
| `--success` | `#1AAB66` | Iter 2 active items (mint) |
| `--danger` | `#D62828` | Change marker, deprecated |
| `--warn` | `#E8AA42` | Stub / partial |

Rule of thumb: 90% of every slide is `--bg`/`--surface`/`--ink`/`--rule`. Color carries semantic meaning only — `--success` for "iter 2 newly active", `--danger` for "this changed", `--accent` for pattern names. **Never decorate.**

### Type
| Role | Stack |
| --- | --- |
| Display / Heading | `"Inter Tight", "Inter", -apple-system, sans-serif` (700) |
| Body | `"Inter", -apple-system, "Pretendard", sans-serif` (400 / 500) |
| Mono — code, state names, file paths | `"JetBrains Mono", "SF Mono", ui-monospace, monospace` (500) |

Letter spacing: heads `-0.02em`, eyebrows (label-12pt small caps) `0.12em`, body default. Numerals `tabular-nums` everywhere counts appear.

### Grid
- Canvas: 1920 × 1080
- Outer padding: 96 px
- Header band: 96 px (eyebrow 12pt small-caps + 56pt title)
- Body: 12-col, 24 px gutter
- Baseline: 8 px

### Signature detail
- **One** detail done at 120%: the **changelog-style header** on every body slide — small monospace eyebrow `iter1 → iter2 · slide N` + tabular dot indicators. Everything else stays at 80% calm.

---

## ❌ Banned

- Purple gradients, blue-pink SaaS hero gradients
- Decorative emoji as bullets or icons
- Drop shadows on cards (use hairlines)
- Rounded-card-with-left-color-accent template
- Stock illustration / SVG-drawn humans / abstract waves
- Inventing data viz "stats" that aren't in the report

---

## 🗣 Speaker notes

Korean speaker notes live in `speaker-notes.md` (one section per slide). Slides themselves stay **English-only** per course convention (slides English, oral Korean).
