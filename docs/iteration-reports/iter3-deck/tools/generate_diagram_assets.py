#!/usr/bin/env python3
"""
Iter3 deck — diagram assets generator (Vercel-light, NO gradients).

Produces clean PNGs for sequence / class / use-case diagrams + 6 demo
backups. Layout is computed so:
  - lifelines never overlap labels
  - inheritance arrows are vertical, never cross
  - actors and use-case ovals have breathing room

Usage:
    python3 -m pip install pillow
    python3 tools/generate_diagram_assets.py
"""

from pathlib import Path
from PIL import Image, ImageDraw, ImageFont
import math


ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "assets" / "diagrams"
OUT.mkdir(parents=True, exist_ok=True)


# ── Fonts (CJK-capable for Korean labels) ─────────────
def font(size, bold=False):
    candidates = [
        "/System/Library/Fonts/AppleSDGothicNeo.ttc",
        "/Library/Fonts/AppleSDGothicNeo.ttc",
        "/System/Library/Fonts/Supplemental/AppleGothic.ttf",
        "/System/Library/Fonts/Helvetica.ttc",
    ]
    for path in candidates:
        try:
            idx = 0
            if path.endswith("AppleSDGothicNeo.ttc"):
                idx = 7 if bold else 2
            elif bold and path.endswith(".ttc"):
                idx = 8
            return ImageFont.truetype(path, size=size, index=idx)
        except Exception:
            pass
    return ImageFont.load_default()


def mono_font(size):
    """Mono-ish font that still supports Korean."""
    candidates = [
        "/System/Library/Fonts/Menlo.ttc",
        "/System/Library/Fonts/AppleSDGothicNeo.ttc",
        "/System/Library/Fonts/Helvetica.ttc",
    ]
    for path in candidates:
        try:
            return ImageFont.truetype(path, size=size, index=0)
        except Exception:
            pass
    return ImageFont.load_default()


F = {
    "title": font(32, True),
    "h1":    font(22, True),
    "h2":    font(17, True),
    "body":  font(14),
    "mono":  font(13),
    "small": font(11),
    "tiny":  font(10),
}


# ── Vercel-light palette (no gradients) ────────────────
INK       = "#000000"
INK2      = "#171717"
INK3      = "#404040"
MUTED     = "#737373"
RULE      = "#EAEAEA"
SURFACE   = "#FFFFFF"
SURFACE2  = "#FAFAFA"
SURFACE3  = "#F4F4F5"
ACCENT    = "#DC2626"
ACCENT_BG = "#FEE2E2"


def text_w(d, text, fnt):
    return d.textlength(text, font=fnt)


def text_h(fnt):
    return fnt.size + 4


# ── Canvas helper ─────────────────────────────────────
def canvas(w=1800, h=1100, title=None, subtitle=None, eyebrow=None):
    img = Image.new("RGB", (w, h), SURFACE)
    d = ImageDraw.Draw(img)
    pad_x = 64
    if eyebrow:
        d.text((pad_x, 40), eyebrow.upper(), fill=MUTED, font=F["small"])
    if title:
        d.text((pad_x, 64), title, fill=INK, font=F["title"])
    if subtitle:
        d.text((pad_x, 110), subtitle, fill=MUTED, font=F["body"])
    d.line((pad_x, 156, w - pad_x, 156), fill=RULE, width=1)
    return img, d


def draw_arrowhead(d, x, y, angle, size=10, color=INK):
    pts = [
        (x, y),
        (x - size * math.cos(angle - math.pi / 6),
         y - size * math.sin(angle - math.pi / 6)),
        (x - size * math.cos(angle + math.pi / 6),
         y - size * math.sin(angle + math.pi / 6)),
    ]
    d.polygon(pts, fill=color)


def hline(d, x1, x2, y, color=INK, width=2, dashed=False):
    if not dashed:
        d.line((x1, y, x2, y), fill=color, width=width)
        return
    sign = 1 if x2 > x1 else -1
    px = x1
    while sign * (x2 - px) > 0:
        nxt = px + sign * 8
        if sign * (nxt - x2) > 0:
            nxt = x2
        d.line((px, y, nxt, y), fill=color, width=width)
        px = nxt + sign * 6


def vline(d, x, y1, y2, color=RULE, width=1, dashed=True):
    if not dashed:
        d.line((x, y1, x, y2), fill=color, width=width)
        return
    py = y1
    while py < y2:
        nxt = min(py + 6, y2)
        d.line((x, py, x, nxt), fill=color, width=width)
        py = nxt + 4


def round_rect(d, xy, fill=SURFACE, outline=RULE, width=1, radius=6):
    d.rounded_rectangle(xy, radius=radius, fill=fill, outline=outline, width=width)


def draw_box(d, x1, y1, x2, y2, label, fill=SURFACE, border=RULE,
             text_color=INK, fnt=None, border_width=1):
    fnt = fnt or F["mono"]
    round_rect(d, (x1, y1, x2, y2), fill=fill, outline=border,
               width=border_width, radius=6)
    tw = text_w(d, label, fnt)
    th = text_h(fnt)
    tx = x1 + ((x2 - x1) - tw) / 2
    ty = y1 + ((y2 - y1) - th) / 2 + 1
    d.text((tx, ty), label, fill=text_color, font=fnt)


# ════════════════════════════════════════════════════════
#  SEQUENCE DIAGRAM
# ════════════════════════════════════════════════════════
def sequence_diagram(title, subtitle, lifelines, messages, out_path,
                     w=1920, h=None):
    """Iter3 v4: no in-PNG title, dynamic height so canvas is tightly
    sized to the content (no empty bottom). Bigger fonts + thicker lines."""
    label_fnt = font(24, bold=True)
    msg_fnt   = font(20)
    num_fnt   = font(16, bold=True)

    pad_x = 100
    top = 80
    msg_step = 80
    header_h = 80
    msg_block_h = msg_step * len(messages) + 60
    if h is None:
        h = top + header_h + msg_block_h + 80
        h = max(h, 900)

    img = Image.new("RGB", (w, h), SURFACE)
    d = ImageDraw.Draw(img)

    bottom = h - 60

    n = len(lifelines)
    avail = (w - 2 * pad_x)
    lane_w = avail / n

    label_h = 70
    label_w = min(lane_w - 28, 260)

    # Draw lifelines (header + dashed line)
    centers = []
    for i, (lab, accent) in enumerate(lifelines):
        cx = int(pad_x + lane_w * (i + 0.5))
        centers.append(cx)
        x1 = cx - int(label_w / 2)
        x2 = cx + int(label_w / 2)

        border = ACCENT if accent else INK
        bg = ACCENT_BG if accent else SURFACE
        text_color = ACCENT if accent else INK

        d.rectangle((x1, top, x2, top + label_h),
                    fill=bg, outline=border, width=3)
        tw = text_w(d, lab, label_fnt)
        d.text((cx - tw / 2, top + (label_h - label_fnt.size) / 2 - 1),
               lab, fill=text_color, font=label_fnt)

        # darker dashed lifeline (was light gray, hard to see)
        vline(d, cx, top + label_h + 8, bottom,
              color="#9CA3AF", width=2, dashed=True)

    # Messages — fixed step so labels/lines don't crowd
    msg_top = top + label_h + 60

    for i, msg in enumerate(messages):
        kind = msg[3] if len(msg) > 3 else None
        a, b, label = msg[0], msg[1], msg[2]
        y = int(msg_top + i * msg_step)

        # message number on left margin (bigger, bold)
        num = f"{i + 1:02d}"
        d.text((pad_x - 64, y - 10), num, fill=INK3, font=num_fnt)

        if kind == "note":
            tw = text_w(d, label, msg_fnt)
            nx1 = pad_x + 12
            nx2 = nx1 + tw + 28
            ny1 = y - 14
            ny2 = y + 16
            round_rect(d, (nx1, ny1, nx2, ny2),
                       fill="#FFFBEB", outline="#F5C84F",
                       width=2, radius=4)
            d.text((nx1 + 14, ny1 + 6), label,
                   fill="#92400E", font=msg_fnt)
            continue

        if kind == "self":
            cx = centers[a]
            arc_w = 80
            d.line((cx, y, cx + arc_w, y), fill=INK, width=3)
            d.line((cx + arc_w, y, cx + arc_w, y + 18), fill=INK, width=3)
            d.line((cx + arc_w, y + 18, cx + 10, y + 18), fill=INK, width=3)
            draw_arrowhead(d, cx + 10, y + 18, math.pi, size=10, color=INK)
            d.text((cx + arc_w + 12, y - 8), label,
                   fill=INK2, font=msg_fnt)
            continue

        xa, xb = centers[a], centers[b]
        color = ACCENT if kind != "return" else "#525252"
        dashed = kind == "return"

        # Draw thicker arrow
        hline(d, xa, xb, y, color=color, width=3, dashed=dashed)
        if xa != xb:
            angle = 0 if xb > xa else math.pi
            draw_arrowhead(d, xb, y, angle, size=12, color=color)

        # label centered above arrow (bigger font)
        midx = (xa + xb) / 2
        tw = text_w(d, label, msg_fnt)
        d.text((midx - tw / 2, y - 24), label,
               fill=INK, font=msg_fnt)

    img.save(out_path)
    print(f"  -> {out_path.name}")


# ════════════════════════════════════════════════════════
#  CLASS DIAGRAM (no crossing arrows)
# ════════════════════════════════════════════════════════
# ── UML class-box renderer ─────────────────────
def draw_class_box(d, x1, y1, width, name, stereotype=None,
                   attrs=None, ops=None,
                   header_fg=INK, header_bg=SURFACE,
                   body_fg=INK2, body_bg=SURFACE, border=INK,
                   new_badge=False):
    """Proper UML class notation: stereotype + name header, attrs section,
    ops section, with separator lines. Returns y2 (bottom).
    new_badge=True draws a small NEW chip in the top-right of the header."""
    attrs = attrs or []
    ops = ops or []
    line_h = 18
    pad = 8
    header_h = 26 + (16 if stereotype else 0)
    attrs_h = (len(attrs) * line_h) + (pad * 2 if attrs else 0)
    ops_h = (len(ops) * line_h) + (pad * 2 if ops else 0)
    h = header_h + attrs_h + ops_h
    if not attrs and not ops:
        h = header_h + pad * 2

    x2 = x1 + width
    y2 = y1 + h

    # outer box
    d.rectangle((x1, y1, x2, y2), fill=body_bg, outline=border, width=1)
    # header band
    d.rectangle((x1, y1, x2, y1 + header_h), fill=header_bg,
                outline=border, width=1)

    # stereotype + name (centered)
    cy = y1 + 4
    if stereotype:
        s = "«" + stereotype + "»"
        tw = text_w(d, s, F["small"])
        d.text((x1 + (width - tw) / 2, cy), s, fill=MUTED, font=F["small"])
        cy += 14
    tw = text_w(d, name, F["h2"])
    d.text((x1 + (width - tw) / 2, cy), name, fill=header_fg, font=F["h2"])

    # NEW badge top-right
    if new_badge:
        badge_font = font(14, bold=True)
        bw, bh = 50, 22
        bx2 = x2 - 6
        by1 = y1 + 4
        d.rectangle((bx2 - bw, by1, bx2, by1 + bh),
                    fill=ACCENT, outline=ACCENT)
        tw = text_w(d, "NEW", badge_font)
        d.text((bx2 - bw + (bw - tw) / 2,
                by1 + (bh - badge_font.size) / 2 - 1),
               "NEW", fill="#FFFFFF", font=badge_font)

    # attrs section
    cy = y1 + header_h
    if attrs:
        cy += pad
        for a in attrs:
            d.text((x1 + pad + 4, cy), a, fill=body_fg, font=F["mono"])
            cy += line_h
        cy += pad
        d.line((x1, cy, x2, cy), fill=border, width=1)

    # ops section
    if ops:
        cy += pad
        for o in ops:
            d.text((x1 + pad + 4, cy), o, fill=body_fg, font=F["mono"])
            cy += line_h
        cy += pad

    return y2


def class_diagram_iter3():
    w, h = 1920, 1200
    img, d = canvas(w, h,
                    title="Class Diagram — Iter 3 · Observer Pattern",
                    subtitle="3 hierarchies side-by-side · proper UML notation · domain extensions in bottom band",
                    eyebrow="ITER3 · CLASS DIAGRAM")

    pad_x = 60
    pad_y = 200

    col_w = 580
    col_gap = 30
    cx1 = pad_x
    cx2 = cx1 + col_w + col_gap
    cx3 = cx2 + col_w + col_gap

    # ── Column 1: EventPublisher -> Subjects ──
    base1_y2 = draw_class_box(
        d, cx1, pad_y, col_w,
        name="EventPublisher",
        stereotype="abstract",
        attrs=["- listeners: List<EventListener>"],
        ops=[
            "+ subscribe(listener: EventListener)",
            "+ unsubscribe(listener: EventListener)",
            "+ subscriberCount(): int",
            "# publish(event: DomainEvent)",
        ],
        header_fg=ACCENT, header_bg=ACCENT_BG, border=ACCENT,
    )

    # Concretes are slightly indented so a left spine fits between column
    # left edge and the box. spine_x = column_x + 14, concrete_x = column_x + 32.
    spine_pad = 14
    concrete_indent = 32
    spine_x_col1 = cx1 + spine_pad
    concrete_x_col1 = cx1 + concrete_indent
    concrete_w_col1 = col_w - concrete_indent

    sub_y = base1_y2 + 60
    sub_specs = [
        ("SeatHoldMonitor", "control", [
            "+ track(seat: Seat, pnr: String)",
            "+ sweep(): int",
            "+ trackedCount(): int",
        ], True),
        ("PaymentProcessor", "control · 확장", [
            "+ processPaymentCharge(amount, pnr): Payment",
            "+ processMileagePayment(account, cost, pnr): Payment",
        ], False),
        ("FlightSchedule", "domain · 확장", [
            "+ changeStatus(status: FlightStatus)",
            "+ getStatus(): FlightStatus",
        ], False),
    ]
    sub_anchors = []  # (left_edge_x, vertical_center_y)
    cy = sub_y
    for (n, st, ops_, is_new) in sub_specs:
        y2 = draw_class_box(d, concrete_x_col1, cy, concrete_w_col1,
                            name=n, stereotype=st, ops=ops_, new_badge=is_new)
        sub_anchors.append((concrete_x_col1, (cy + y2) / 2))
        cy = y2 + 22

    # ── Column 2: DomainEvent -> Events ──
    base2_y2 = draw_class_box(
        d, cx2, pad_y, col_w,
        name="DomainEvent",
        stereotype="abstract",
        attrs=[
            "- occurredAt: LocalDateTime",
            "- sourceId: String",
        ],
        ops=[
            "+ getOccurredAt(): LocalDateTime",
            "+ getSourceId(): String",
            "+ getEventType(): String",
        ],
        header_fg=ACCENT, header_bg=ACCENT_BG, border=ACCENT,
    )

    spine_x_col2 = cx2 + spine_pad
    concrete_x_col2 = cx2 + concrete_indent
    concrete_w_col2 = col_w - concrete_indent

    ev_specs = [
        ("SeatHoldExpiredEvent", "event", [
            "- seat: Seat",
            "- reservationPnr: String",
        ]),
        ("PaymentFailedEvent", "event", [
            "- payment: Payment",
            "- reservationPnr: String",
            "- reason: String",
        ]),
        ("FlightStatusChangedEvent", "event", [
            "- schedule: FlightSchedule",
            "- previousStatus: FlightStatus",
            "- newStatus: FlightStatus",
        ]),
        ("ReservationStateChangedEvent", "event", [
            "- pnr: String",
            "- previousState: String",
            "- newState: String",
        ]),
    ]
    ev_anchors = []
    cy = base2_y2 + 60
    for (n, st, attrs_) in ev_specs:
        y2 = draw_class_box(d, concrete_x_col2, cy, concrete_w_col2,
                            name=n, stereotype=st, attrs=attrs_, new_badge=True)
        ev_anchors.append((concrete_x_col2, (cy + y2) / 2))
        cy = y2 + 16

    # ── Column 3: EventListener -> Listeners ──
    base3_y2 = draw_class_box(
        d, cx3, pad_y, col_w,
        name="EventListener",
        stereotype="interface",
        ops=["+ onEvent(event: DomainEvent)"],
        header_fg=ACCENT, header_bg=ACCENT_BG, border=ACCENT,
    )

    spine_x_col3 = cx3 + spine_pad
    concrete_x_col3 = cx3 + concrete_indent
    concrete_w_col3 = col_w - concrete_indent

    lis_specs = [
        ("ReservationHoldListener", "control", [
            "+ onEvent(event: DomainEvent)",
            "  -> seat.release()",
            "  -> reservation.handlePaymentFailure()",
        ]),
        ("ReservationAutoCancelListener", "control", [
            "+ onEvent(event: DomainEvent)",
            "  -> reservation.handlePaymentFailure()",
        ]),
        ("AffectedReservationListener", "control", [
            "+ onEvent(event: DomainEvent)",
            "  -> registry.all() filter by schedule",
            "  -> r.evaluateImpactOfFlightStatusChange()",
        ]),
    ]
    lis_anchors = []
    cy = base3_y2 + 60
    for (n, st, ops_) in lis_specs:
        y2 = draw_class_box(d, concrete_x_col3, cy, concrete_w_col3,
                            name=n, stereotype=st, ops=ops_, new_badge=True)
        lis_anchors.append((concrete_x_col3, (cy + y2) / 2))
        cy = y2 + 16

    # ── Side-spine inheritance/realization (no crossing of concrete boxes) ──
    # Pattern per column:
    #   base bottom-center → elbow → spine_x at top → vertical down to last
    #   concrete vertical-center → horizontal stub from spine to each
    #   concrete left edge.
    tri_size = 11

    def draw_side_spine(spine_x, base_y2, base_cx, anchors, dashed=False):
        spine_top = base_y2 + 14
        last_cy = anchors[-1][1]
        if dashed:
            # vertical dashed from base bottom to spine_top, then to spine_x
            py = base_y2
            while py < spine_top:
                nxt = min(py + 5, spine_top)
                d.line((base_cx, py, base_cx, nxt), fill=INK, width=2)
                py = nxt + 4
            # horizontal dashed from base_cx to spine_x at spine_top
            px = min(base_cx, spine_x)
            px_end = max(base_cx, spine_x)
            while px < px_end:
                nxt = min(px + 5, px_end)
                d.line((px, spine_top, nxt, spine_top), fill=INK, width=2)
                px = nxt + 4
            # vertical dashed spine
            py = spine_top
            while py < last_cy:
                nxt = min(py + 5, last_cy)
                d.line((spine_x, py, spine_x, nxt), fill=INK, width=2)
                py = nxt + 4
            # stubs (dashed)
            for left_x, cy_ in anchors:
                px = spine_x
                while px < left_x:
                    nxt = min(px + 5, left_x)
                    d.line((px, cy_, nxt, cy_), fill=INK, width=2)
                    px = nxt + 4
        else:
            d.line((base_cx, base_y2, base_cx, spine_top), fill=INK, width=2)
            d.line((base_cx, spine_top, spine_x, spine_top), fill=INK, width=2)
            d.line((spine_x, spine_top, spine_x, last_cy), fill=INK, width=2)
            for left_x, cy_ in anchors:
                d.line((spine_x, cy_, left_x, cy_), fill=INK, width=2)
        # open triangle at base bottom
        d.polygon([
            (base_cx, base_y2),
            (base_cx - tri_size, base_y2 + tri_size + 2),
            (base_cx + tri_size, base_y2 + tri_size + 2),
        ], outline=INK, fill=SURFACE)

    base1_cx = cx1 + col_w / 2
    base2_cx = cx2 + col_w / 2
    base3_cx = cx3 + col_w / 2
    draw_side_spine(spine_x_col1, base1_y2, base1_cx, sub_anchors, dashed=False)
    draw_side_spine(spine_x_col2, base2_y2, base2_cx, ev_anchors,  dashed=False)
    draw_side_spine(spine_x_col3, base3_y2, base3_cx, lis_anchors, dashed=True)

    # ── Bottom band: Domain extensions (separate, no inheritance) ──
    # Compute bottom y just under longest column
    band_top = max(cy, h - 200)
    band_top = min(band_top, h - 180)
    d.line((pad_x, band_top - 24, w - pad_x, band_top - 24),
           fill=RULE, width=1)
    d.text((pad_x, band_top - 44),
           "DOMAIN EXTENSIONS · 도메인 확장 (no inheritance to Observer infra)",
           fill=MUTED, font=F["small"])

    extensions = [
        ("Itinerary",            "domain · 확장", ["+ connecting(a, b)", "+ multiCity(list)", "+ isConnectionTimeValid(mct)"], False),
        ("ItinerarySearchService", "control",     ["+ searchDirect(...)", "+ searchConnecting(..., mct)"],                   True),
        ("ReservationRegistry",  "control",       ["+ register(r)", "+ findByPnr(pnr)", "+ all(): Collection<Reservation>"], True),
        ("MockSkypassInterface", "boundary",      ["+ getMileageBalance(...)", "+ verifyAndDeduct(...)"],                    True),
    ]
    ext_w = (w - 2 * pad_x - 30) / 4
    for i, (n, st, ops_, is_new) in enumerate(extensions):
        x1 = pad_x + int(i * (ext_w + 10))
        draw_class_box(d, x1, band_top, int(ext_w),
                       name=n, stereotype=st, ops=ops_, new_badge=is_new)

    # Legend
    legend_y = h - 50
    tri_x = pad_x
    d.polygon([
        (tri_x, legend_y),
        (tri_x - 8, legend_y + 12),
        (tri_x + 8, legend_y + 12),
    ], outline=INK, fill=SURFACE)
    d.line((tri_x, legend_y + 12, tri_x, legend_y + 22), fill=INK, width=2)
    d.text((tri_x + 18, legend_y),
           "inheritance (solid + open triangle)",
           fill=INK, font=F["small"])

    tri2_x = pad_x + 380
    d.polygon([
        (tri2_x, legend_y),
        (tri2_x - 8, legend_y + 12),
        (tri2_x + 8, legend_y + 12),
    ], outline=INK, fill=SURFACE)
    py = legend_y + 12
    while py < legend_y + 22:
        d.line((tri2_x, py, tri2_x, min(py + 3, legend_y + 22)), fill=INK, width=2)
        py += 6
    d.text((tri2_x + 18, legend_y),
           "realization (dashed + open triangle)",
           fill=INK, font=F["small"])

    d.rectangle((pad_x + 760, legend_y, pad_x + 778, legend_y + 16),
                fill=ACCENT_BG, outline=ACCENT, width=2)
    d.text((pad_x + 788, legend_y),
           "abstract / interface base (Observer infra)",
           fill=ACCENT, font=F["small"])

    out = OUT / "classDiagram-iter3.png"
    img.save(out)
    print(f"  -> {out.name}")


# ════════════════════════════════════════════════════════
#  USE CASE DIAGRAM (no overlap)
# ════════════════════════════════════════════════════════
def usecase_diagram_iter3():
    """Iter3 v2: full-canvas UCD with NEW badges on iter3 ovals,
    inline include/extend annotations, larger fonts. No outer canvas
    title since slide host owns the heading."""
    w, h = 1920, 1080
    img = Image.new("RGB", (w, h), SURFACE)
    d = ImageDraw.Draw(img)

    actor_fnt   = font(20, bold=True)
    uc_fnt      = font(18, bold=True)
    badge_fnt   = font(17, bold=True)
    legend_fnt  = font(16)
    annot_fnt   = font(15)
    sys_fnt     = font(15)

    pad_x = 30
    top = 40
    bottom = h - 70   # leave room for legend below box

    sys_x1, sys_x2 = 280, 1640
    sys_y1, sys_y2 = top, bottom
    d.rounded_rectangle((sys_x1, sys_y1, sys_x2, sys_y2),
                        radius=14, fill=SURFACE2, outline=INK, width=2)
    d.text((sys_x1 + 20, sys_y1 + 14),
           "Korean Air Reservation System (boundary)",
           fill=MUTED, font=sys_fnt)

    inner_left_x = sys_x1 + 70
    inner_right_x = sys_x1 + 660
    uc_w, uc_h = 320, 64
    row_h = 120  # UC 사이 56px 간격 — include/extend label pill + arrow 충분
    LEFT, RIGHT = 0, 1

    # Manual placement so every iter3 include/extend pair is in adjacent
    # rows (same column) → straight short line, no UC crossings.
    # Layout matrix: each cell is (uc_name, is_iter3_new)
    LEFT_COL = [
        ("View Booking",                False),
        ("Cancel Booking",              False),
        ("Search Flights",              False),
        ("Search Connecting Flights",   True),   # adj Search Flights
        ("Book Flight",                 False),
        ("Book Multi-city Trip",        True),   # adj Book Flight
        ("Manage Flight",               False),
        ("Notify Flight Change",        True),   # adj Manage Flight
    ]
    RIGHT_COL = [
        ("Login",                       False),
        ("Retrieve by PNR",             False),
        ("Issue e-Ticket",              False),
        ("Select Seat",                 False),
        ("Pay with Mileage",            True),   # adj Make Payment
        ("Make Payment",                False),
        ("Auto-cancel on Hold Expiry",  True),   # adj Make Payment
    ]

    # ── Override placement from tools/uc-layout.json if present.
    #    The interactive editor (tools/uc-editor.html) writes that file. ──
    layout_json = Path(__file__).parent / "uc-layout.json"
    layout_override = None
    if layout_json.exists():
        import json
        try:
            layout_override = json.loads(layout_json.read_text(encoding="utf-8"))
            print(f"  [layout] using uc-layout.json ({len(layout_override.get('ucs', []))} UCs)")
        except Exception as ex:
            print(f"  [layout] failed to parse uc-layout.json: {ex}")

    existing = []
    new_ucs = []
    if layout_override:
        # JSON has explicit (x, y) per UC
        for u in layout_override["ucs"]:
            entry = (u["name"], int(u["x"]), int(u["y"]))
            if u.get("isNew") or u.get("is_new"):
                new_ucs.append(entry)
            else:
                existing.append(entry)
    else:
        for i, (uc, is_new) in enumerate(LEFT_COL):
            y = sys_y1 + 70 + i * row_h
            if is_new:
                new_ucs.append((uc, inner_left_x, y))
            else:
                existing.append((uc, inner_left_x, y))
        for i, (uc, is_new) in enumerate(RIGHT_COL):
            y = sys_y1 + 70 + i * row_h
            if is_new:
                new_ucs.append((uc, inner_right_x, y))
            else:
                existing.append((uc, inner_right_x, y))

    DEFAULT_ASSOCS = [
        # Passenger (parent actor) — base UCs inherited by Skypass/Guest
        ("Passenger",       "Search Flights"),
        ("Passenger",       "Book Flight"),
        ("Passenger",       "Select Seat"),
        ("Passenger",       "View Booking"),
        ("Passenger",       "Cancel Booking"),
        ("Passenger",       "Search Connecting Flights"),
        ("Passenger",       "Book Multi-city Trip"),
        # Skypass Member specific
        ("Skypass Member",  "Login"),
        ("Skypass Member",  "Pay with Mileage"),
        # Guest specific
        ("Guest",           "Retrieve by PNR"),
        # Admin
        ("Admin",           "Login"),
        ("Admin",           "Manage Flight"),
        ("Admin",           "Notify Flight Change"),
        # External system actors
        ("Payment Gateway", "Make Payment"),
        ("Skypass System",  "Pay with Mileage"),
        ("GDS",             "Search Connecting Flights"),
    ]
    if layout_override and "assocs" in layout_override:
        assocs = [tuple(a) for a in layout_override["assocs"]]
    else:
        assocs = DEFAULT_ASSOCS

    # Actor generalization (child →▷ parent open triangle)
    DEFAULT_ACTOR_GENS = [
        ("Skypass Member", "Passenger"),
        ("Guest",          "Passenger"),
    ]
    if layout_override and ("actorGens" in layout_override
                            or "actor_gens" in layout_override):
        actor_gens = [tuple(g) for g in
                      layout_override.get("actorGens",
                                          layout_override.get("actor_gens", []))]
    else:
        actor_gens = DEFAULT_ACTOR_GENS

    # Default actor seed positions
    default_left_actors = [
        ("Passenger",       sys_y1 + 110),
        ("Skypass Member",  sys_y1 + 290),
        ("Guest",           sys_y1 + 470),
        ("Admin",           sys_y1 + 660),
    ]
    default_right_actors = [
        ("Payment Gateway", sys_y1 + 200),
        ("Skypass System",  sys_y1 + 420),
        ("GDS",             sys_y1 + 640),
    ]

    if layout_override and "actors" in layout_override:
        left_actors = []
        right_actors = []
        for a in layout_override["actors"]:
            entry = (a["name"], int(a["y"]))
            if a.get("side") == "right":
                right_actors.append(entry)
            else:
                left_actors.append(entry)
    else:
        left_actors = default_left_actors
        right_actors = default_right_actors

    uc_anchors = {}
    for (lab, x, y) in existing + new_ucs:
        cy = y + uc_h / 2
        uc_anchors[lab] = {"left": (x, cy), "right": (x + uc_w, cy),
                           "top": (x + uc_w / 2, y),
                           "bot": (x + uc_w / 2, y + uc_h)}

    # actor_info[name] = {"cx": stick_center_x, "y_origin": body_top_y, "side": "left|right"}
    actor_info = {}
    if layout_override and "actors" in layout_override:
        for a in layout_override["actors"]:
            ax = int(a["x"])
            ay = int(a["y"])
            actor_info[a["name"]] = {
                "cx": ax,
                "y_origin": ay,
                "side": a.get("side", "left"),
            }
    else:
        for (lab, y) in left_actors:
            actor_info[lab] = {"cx": pad_x + 70, "y_origin": y, "side": "left"}
        for (lab, y) in right_actors:
            actor_info[lab] = {"cx": w - pad_x - 70, "y_origin": y, "side": "right"}

    def actor_anchor_for(actor_name, target_y):
        """Pick stick figure edge anchor closest to target_y.
        Stick figure body extends:
          head: y_origin - 26 .. y_origin + 6
          body: y_origin + 4 .. y_origin + 44
          arms: y_origin + 14 (cx ± 22)
          legs: y_origin + 60 (cx ± 14)
        """
        info = actor_info[actor_name]
        cx = info["cx"]
        y0 = info["y_origin"]
        is_left = info["side"] == "left"
        # Side direction: lines extend toward UCs (right for LEFT actors)
        dir_sign = 1 if is_left else -1
        if target_y < y0 - 8:
            # UC above actor — anchor at head side
            return (cx + dir_sign * 16, y0 - 16)
        elif target_y > y0 + 40:
            # UC below actor — anchor at leg
            return (cx + dir_sign * 14, y0 + 60)
        else:
            # UC roughly level — anchor at arm tip
            return (cx + dir_sign * 22, y0 + 14)

    # ── Draw associations as STRAIGHT lines from actor edge closest to
    #    target UC, ending at UC edge. UC ovals (drawn later, white fill)
    #    hide any line segment passing through their interior. ──
    LINE_COLOR = "#404040"
    LINE_WIDTH = 2

    for (a, u) in assocs:
        if a not in actor_info or u not in uc_anchors:
            continue
        # Determine UC edge based on actor side
        side = actor_info[a]["side"]
        if side == "left":
            ux, uy = uc_anchors[u]["left"]
        else:
            ux, uy = uc_anchors[u]["right"]
        ax, ay = actor_anchor_for(a, uy)
        d.line((ax, ay, ux, uy), fill=LINE_COLOR, width=LINE_WIDTH)
        d.ellipse((ux - 4, uy - 4, ux + 4, uy + 4),
                  fill=LINE_COLOR, outline=LINE_COLOR)

    # ── Actor generalization: child →▷ parent (open triangle).
    #    Endpoints at figure EDGE (head top / leg bottom), not center.
    #    Stick figure spans actor.y - 26 (head top) to actor.y + 70 (leg bottom).
    GEN_COLOR = "#171717"
    FIG_TOP = -36  # head top + 10px breathing room
    FIG_BOT = 82   # leg bottom + 12px breathing room
    for (child, parent) in actor_gens:
        if child not in actor_info or parent not in actor_info:
            continue
        cx_child = actor_info[child]["cx"]
        cx_parent = actor_info[parent]["cx"]
        child_y_origin = actor_info[child]["y_origin"]
        parent_y_origin = actor_info[parent]["y_origin"]
        if child_y_origin > parent_y_origin:
            # child below — line UP from child head top to parent leg bottom
            sx = cx_child
            sy = child_y_origin + FIG_TOP
            ex = cx_parent
            ey = parent_y_origin + FIG_BOT
            ang = -math.pi / 2
        else:
            sx = cx_child
            sy = child_y_origin + FIG_BOT
            ex = cx_parent
            ey = parent_y_origin + FIG_TOP
            ang = math.pi / 2
        d.line((sx, sy, ex, ey), fill=GEN_COLOR, width=2)
        # Open triangle on parent end
        head = 14
        d.polygon([
            (ex, ey),
            (ex - head * math.cos(ang - math.pi / 6),
             ey - head * math.sin(ang - math.pi / 6)),
            (ex - head * math.cos(ang + math.pi / 6),
             ey - head * math.sin(ang + math.pi / 6)),
        ], outline=GEN_COLOR, fill=SURFACE)

    # ── Include / extend dashed arrows routed via OUTSIDE channel of
    #    each column so they never cross UC ovals. Layout per column:
    #      LEFT column UCs (x ∈ inner_left_x..+uc_w) → channel x = sys_x1+10
    #        (between system-box left edge and column left edge)
    #      RIGHT column UCs → channel x = sys_x2-10
    #    Arrow path: src.LEFT_or_RIGHT edge → channel x → vertical to
    #    dst y → back to dst LEFT_or_RIGHT edge → arrowhead. ──
    def dashed_segment(x1, y1, x2, y2, color=ACCENT):
        dx, dy = x2 - x1, y2 - y1
        length = (dx ** 2 + dy ** 2) ** 0.5
        if length == 0:
            return
        steps = max(1, int(length // 12))
        for i in range(steps):
            t1 = i / steps
            t2 = (i + 0.55) / steps
            d.line((x1 + dx * t1, y1 + dy * t1,
                    x1 + dx * t2, y1 + dy * t2),
                   fill=color, width=2)

    def channel_arrow(src, dst, label, lane_idx=0):
        """Route src→dst through outer channel (between system box edge
        and UC column) so line never crosses any UC. lane_idx slightly
        offsets parallel verticals so they don't overlap each other."""
        sa = uc_anchors[src]
        da = uc_anchors[dst]
        src_x, _ = sa["left"]
        lane_offset = lane_idx * 14
        if src_x == inner_left_x:
            chan_x = sys_x1 + 18 + lane_offset
            src_pt = sa["left"]
            dst_pt = da["left"]
            ang = 0
        else:
            chan_x = sys_x2 - 18 - lane_offset
            src_pt = sa["right"]
            dst_pt = da["right"]
            ang = math.pi
        sx, sy = src_pt
        dx_, dy_ = dst_pt
        dashed_segment(sx, sy, chan_x, sy)
        dashed_segment(chan_x, sy, chan_x, dy_)
        dashed_segment(chan_x, dy_, dx_, dy_)
        head = 10
        d.polygon([
            (dx_, dy_),
            (dx_ - head * math.cos(ang - math.pi / 6),
             dy_ - head * math.sin(ang - math.pi / 6)),
            (dx_ - head * math.cos(ang + math.pi / 6),
             dy_ - head * math.sin(ang + math.pi / 6)),
        ], outline=ACCENT, fill=SURFACE)
        # label pill at midpoint of vertical leg, sitting beside channel
        mid_y = (sy + dy_) / 2
        tw = text_w(d, label, annot_fnt)
        pill_cx = (chan_x + sx) / 2
        d.rectangle((pill_cx - tw / 2 - 6, mid_y - 11,
                     pill_cx + tw / 2 + 6, mid_y + 13),
                    fill=SURFACE, outline=ACCENT, width=1)
        d.text((pill_cx - tw / 2, mid_y - 9), label,
               fill=ACCENT, font=annot_fnt)

    # iter3 NEW UC-UC relationships + iter1/2 baseline (Issue e-Ticket
    # connection). Same-column adjacent pairs → straight short arrows.
    # Cross-column pairs → routed via top channel.
    DEFAULT_REL_SPECS = [
        ("Search Connecting Flights", "Search Flights", "include"),
        ("Book Multi-city Trip",      "Book Flight",    "include"),
        ("Book Flight",               "Issue e-Ticket", "include"),
        ("Pay with Mileage",          "Make Payment",   "extend"),
        ("Auto-cancel on Hold Expiry","Make Payment",   "extend"),
        ("Notify Flight Change",      "Manage Flight",  "extend"),
    ]
    if layout_override and "rels" in layout_override:
        rel_specs = [tuple(r) for r in layout_override["rels"]]
    else:
        rel_specs = DEFAULT_REL_SPECS
    # Drawn after UCs.

    # Existing UC ovals
    for (lab, x, y) in existing:
        d.ellipse((x, y, x + uc_w, y + uc_h),
                  outline=INK2, width=2, fill=SURFACE)
        tw = text_w(d, lab, uc_fnt)
        d.text((x + (uc_w - tw) / 2, y + (uc_h - uc_fnt.size) / 2),
               lab, fill=INK2, font=uc_fnt)

    # New UC ovals + NEW badge on top-right
    for (lab, x, y) in new_ucs:
        d.ellipse((x, y, x + uc_w, y + uc_h),
                  outline=ACCENT, width=3, fill=ACCENT_BG)
        tw = text_w(d, lab, uc_fnt)
        d.text((x + (uc_w - tw) / 2, y + (uc_h - uc_fnt.size) / 2),
               lab, fill=ACCENT, font=uc_fnt)
        # NEW badge — solid red rectangle + white "NEW" text on top
        bw, bh = 64, 26
        bx1 = x + uc_w - bw - 6
        by1 = y - 4
        bx2 = bx1 + bw
        by2 = by1 + bh
        d.rectangle((bx1, by1, bx2, by2), fill=ACCENT, outline=ACCENT)
        tw_n = text_w(d, "NEW", badge_fnt)
        # explicitly position centered
        tx = bx1 + (bw - tw_n) / 2
        ty = by1 + (bh - badge_fnt.size) / 2 - 1
        d.text((tx, ty), "NEW", fill="#FFFFFF", font=badge_fnt)

    # Stick figures + labels below
    def stick(cx, y, lab):
        d.ellipse((cx - 16, y - 26, cx + 16, y + 6),
                  outline=INK, width=2, fill=SURFACE)
        d.line((cx, y + 6, cx, y + 44), fill=INK, width=2)
        d.line((cx - 24, y + 18, cx + 24, y + 18), fill=INK, width=2)
        d.line((cx, y + 44, cx - 16, y + 70), fill=INK, width=2)
        d.line((cx, y + 44, cx + 16, y + 70), fill=INK, width=2)
        tw = text_w(d, lab, actor_fnt)
        d.text((cx - tw / 2, y + 84), lab, fill=INK, font=actor_fnt)

    if layout_override and "actors" in layout_override:
        for a in layout_override["actors"]:
            stick(int(a["x"]), int(a["y"]), a["name"])
    else:
        for (lab, y) in left_actors:
            stick(pad_x + 70, y, lab)
        for (lab, y) in right_actors:
            stick(w - pad_x - 70, y, lab)

    # ── include/extend arrows. Per-arrow lane offset so multiple
    #    arrows targeting same UC don't overlap. Label pill positioned
    #    EXACTLY centered on the arrow line. ──
    def label_pill(cx, cy, label):
        tw = text_w(d, label, annot_fnt)
        pad = 8
        ph = annot_fnt.size + 8
        rx1 = cx - tw / 2 - pad
        rx2 = cx + tw / 2 + pad
        ry1 = cy - ph / 2
        ry2 = cy + ph / 2
        d.rectangle((rx1, ry1, rx2, ry2),
                    fill=SURFACE, outline=ACCENT, width=1)
        d.text((cx - tw / 2, ry1 + (ph - annot_fnt.size) / 2 - 1),
               label, fill=ACCENT, font=annot_fnt)

    def straight_uc_arrow(src, dst, label, lane=0):
        sa = uc_anchors[src]
        da = uc_anchors[dst]
        sx_top, sy_top = sa["top"]
        sx_bot, sy_bot = sa["bot"]
        dx_top, dy_top = da["top"]
        dx_bot, dy_bot = da["bot"]
        # Same column straight vertical arrow.
        if sy_top > dy_bot:
            # src below dst → arrow UP
            sx, sy = sx_top, sy_top
            ex, ey = dx_bot, dy_bot
            ang = -math.pi / 2
        else:
            sx, sy = sx_bot, sy_bot
            ex, ey = dx_top, dy_top
            ang = math.pi / 2
        # lane offset so arrows from/to same UC don't overlap
        offset = lane * 28
        sx += offset
        ex += offset
        dashed_segment(sx, sy, ex, ey)
        head = 11
        d.polygon([
            (ex, ey),
            (ex - head * math.cos(ang - math.pi / 6),
             ey - head * math.sin(ang - math.pi / 6)),
            (ex - head * math.cos(ang + math.pi / 6),
             ey - head * math.sin(ang + math.pi / 6)),
        ], outline=ACCENT, fill=SURFACE)
        # Label centered on line midpoint (sx == ex since vertical)
        mid_y = (sy + ey) / 2
        label_pill(sx, mid_y, label)

    def cross_col_uc_arrow(src, dst, label, lane=0):
        """Cross-column UC→UC: STRAIGHT diagonal between facing edges.
        UC ovals (drawn after these arrows? no, before) cover any line
        passing through their fill. We draw arrows AFTER UCs so endpoints
        sit on top of UC edges crisply, but earlier UCs cover line bodies
        via white fill ONLY if drawn after — order matters. Arrow must
        therefore be drawn AFTER UCs (current order) so it appears OVER
        UCs at endpoints; intermediate crossings are unavoidable."""
        sa = uc_anchors[src]
        da = uc_anchors[dst]
        # Use facing edges: if src LEFT of dst, src.right → dst.left
        sx_l, _ = sa["left"]
        dx_l, _ = da["left"]
        if sx_l < dx_l:
            sp = sa["right"]
            ep = da["left"]
        else:
            sp = sa["left"]
            ep = da["right"]
        sx, sy = sp
        ex, ey = ep
        dashed_segment(sx, sy, ex, ey)
        # Arrowhead at dst pointing into facing edge
        head = 11
        ang = math.atan2(ey - sy, ex - sx)
        d.polygon([
            (ex, ey),
            (ex - head * math.cos(ang - math.pi / 6),
             ey - head * math.sin(ang - math.pi / 6)),
            (ex - head * math.cos(ang + math.pi / 6),
             ey - head * math.sin(ang + math.pi / 6)),
        ], outline=ACCENT, fill=SURFACE)
        # Label centered on midpoint
        mid_x = (sx + ex) / 2
        mid_y = (sy + ey) / 2
        label_pill(mid_x, mid_y, label)

    # Group arrows by destination so we can assign lanes
    from collections import defaultdict
    dst_groups = defaultdict(list)
    for spec in rel_specs:
        s, t, k = spec
        if s in uc_anchors and t in uc_anchors:
            dst_groups[t].append(spec)

    for dst, specs in dst_groups.items():
        for lane_idx, (s, t, k) in enumerate(specs):
            # lane sign: alternate -1, +1, -2, +2 ... centered around 0
            lane_sign = (-1) ** lane_idx * ((lane_idx + 1) // 2)
            sx_, _ = uc_anchors[s]["top"]
            tx_, _ = uc_anchors[t]["top"]
            same_col = abs(sx_ - tx_) < 5
            if same_col:
                straight_uc_arrow(s, t, f"«{k}»", lane=lane_sign)
            else:
                # Cross-column lane offset within top channel
                cross_col_uc_arrow(s, t, f"«{k}»", lane=lane_idx)

    # Legend strip at bottom (inline, since slide footer hidden)
    leg_y = h - 50
    # NEW badge swatch
    d.rectangle((pad_x, leg_y - 2, pad_x + 50, leg_y + 18),
                fill=ACCENT, outline=ACCENT, width=1)
    tw = text_w(d, "NEW", badge_fnt)
    d.text((pad_x + (50 - tw) / 2, leg_y - 1),
           "NEW", fill="#FFFFFF", font=badge_fnt)
    d.text((pad_x + 60, leg_y), "iter3 신규 use case (5)",
           fill=ACCENT, font=legend_fnt)
    # existing
    d.ellipse((pad_x + 360, leg_y, pad_x + 384, leg_y + 18),
              outline=INK2, width=2, fill=SURFACE)
    d.text((pad_x + 394, leg_y), "iter1/2 기존 use case",
           fill=INK2, font=legend_fnt)
    # association
    d.line((pad_x + 660, leg_y + 9, pad_x + 700, leg_y + 9),
           fill=LINE_COLOR, width=2)
    d.text((pad_x + 710, leg_y), "actor — UC association",
           fill=MUTED, font=legend_fnt)
    # generalization
    gx = pad_x + 940
    d.line((gx, leg_y + 18, gx + 30, leg_y), fill=GEN_COLOR, width=2)
    d.polygon([(gx + 30, leg_y), (gx + 30 - 8, leg_y + 12),
               (gx + 30 + 4, leg_y + 12)],
              outline=GEN_COLOR, fill=SURFACE)
    d.text((gx + 46, leg_y), "actor generalization →▷",
           fill=INK2, font=legend_fnt)
    # include/extend
    d.text((pad_x + 1240, leg_y),
           "«include» / «extend» (dashed → UC)",
           fill=ACCENT, font=legend_fnt)

    out = OUT / "reservationSystem-iter3.png"
    img.save(out)
    print(f"  -> {out.name}")


# ════════════════════════════════════════════════════════
#  Demo screens removed — real console output is captured
#  by `java -cp bin com.koreanair.reservation.app.Iter3DemoRunner`
#  and rendered as code blocks in slides/16-demo-*.html.
# ════════════════════════════════════════════════════════


# ════════════════════════════════════════════════════════
#  Build all
# ════════════════════════════════════════════════════════
def build_all():
    print("Generating iter3 diagram assets ...")

    # SC-01: Seat Hold Expiry
    sequence_diagram(
        "Sequence — Seat Hold Expiry (SC-01)",
        "SeatHoldMonitor.sweep() detects expired Seat -> publish event -> listener releases + cancels",
        [
            ("Scheduler",                False),
            ("SeatHoldMonitor",          True),
            ("Seat",                     False),
            ("SeatHoldExpiredEvent",     True),
            ("ReservationHoldListener",  True),
            ("Reservation",              False),
        ],
        [
            (0, 1, "sweep()",                       None),
            (1, 1, "for each tracked seat",         "self"),
            (1, 2, "isHoldExpired()",               None),
            (2, 1, "true",                          "return"),
            (1, 3, "new SeatHoldExpiredEvent(seat, pnr)", None),
            (1, 4, "publish(event)",                None),
            (4, 2, "release()",                     None),
            (4, 5, "findByPnr(pnr)",                None),
            (4, 5, "handlePaymentFailure()",        None),
            (5, 4, "ok",                            "return"),
        ],
        OUT / "seatHoldExpiry-iter3.png",
    )

    # SC-02: Payment Failure
    sequence_diagram(
        "Sequence — Payment Failure Auto-cancel (SC-02)",
        "PaymentProcessor publishes PaymentFailedEvent · ReservationAutoCancelListener transitions Reservation",
        [
            ("Passenger",                False),
            ("BookingController",        False),
            ("PaymentProcessor",         True),
            ("PaymentGateway",           False),
            ("PaymentFailedEvent",       True),
            ("AutoCancelListener",       True),
            ("Reservation",              False),
        ],
        [
            (0, 1, "confirmPayment(...)",                       None),
            (1, 2, "processPaymentCharge(amount, pnr)",         None),
            (2, 3, "authorize(payment)",                        None),
            (3, 2, "false",                                     "return"),
            (2, 2, "payment.fail()",                            "self"),
            (2, 4, "new PaymentFailedEvent(payment, pnr, reason)", None),
            (2, 5, "publish(event)",                            None),
            (5, 6, "findByPnr(pnr)",                            None),
            (5, 6, "handlePaymentFailure()",                    None),
        ],
        OUT / "paymentFailureAutoCancel-iter3.png",
    )

    # SC-03: FlightSchedule Propagation
    sequence_diagram(
        "Sequence — FlightSchedule Status Propagation (SC-03)",
        "changeStatus publishes event · AffectedReservationListener scans registry · notifies each Reservation",
        [
            ("Admin",                       False),
            ("BookingController",           False),
            ("FlightSchedule",              True),
            ("FlightStatusChangedEvent",    True),
            ("AffectedReservationListener", True),
            ("ReservationRegistry",         False),
            ("Reservation",                 False),
        ],
        [
            (0, 1, "changeFlightStatus(no, CANCELLED)",     None),
            (1, 2, "changeStatus(CANCELLED)",               None),
            (2, 3, "new FlightStatusChangedEvent(prev, new)", None),
            (2, 4, "publish(event)",                        None),
            (4, 5, "all()",                                 None),
            (5, 4, "List<Reservation>",                     "return"),
            (4, 4, "filter Reservations referencing schedule", "self"),
            (4, 6, "evaluateImpactOfFlightStatusChange()",  None),
        ],
        OUT / "flightStatusPropagation-iter3.png",
    )

    # SC-04: Connecting Search
    sequence_diagram(
        "Sequence — Connecting Flight Search (SC-04)",
        "ItinerarySearchService composes 1-stop pairs and validates MCT (90m international)",
        [
            ("Passenger",               False),
            ("ReservationUI",           False),
            ("ItinerarySearchService",  True),
            ("FlightSearchService",     False),
            ("Itinerary",               False),
        ],
        [
            (0, 1, "searchConnecting(from, to, date)",                          None),
            (1, 2, "searchConnecting(from, to, date, MCT)",                     None),
            (2, 3, "getCatalog()",                                              None),
            (3, 2, "List<FlightSchedule>",                                      "return"),
            (2, 2, "for each (a, b) where a.dest == b.origin && b.dest == to", "self"),
            (2, 4, "Itinerary.connecting(a, b)",                                None),
            (2, 4, "isConnectionTimeValid(MCT)",                                None),
            (4, 2, "true / false",                                              "return"),
            (2, 1, "List<Itinerary> (filtered)",                                "return"),
        ],
        OUT / "connectingSearch-iter3.png",
    )

    # SC-05: Mileage Payment
    sequence_diagram(
        "Sequence — Mileage Payment (SC-05)",
        "PaymentProcessor.processMileagePayment debits MileageAccount · external Skypass mock verifies",
        [
            ("Skypass Member",      False),
            ("ReservationUI",       False),
            ("BookingController",   False),
            ("PaymentProcessor",    True),
            ("MileageAccount",      False),
            ("SkypassInterface",    False),
            ("Reservation",         False),
        ],
        [
            (0, 1, "payWithMileage(reservation, cost)",         None),
            (1, 2, "confirmMileagePayment(reservation, account, cost)", None),
            (2, 3, "processMileagePayment(account, cost, pnr)", None),
            (3, 4, "getBalance()",                              None),
            (4, 3, "balance",                                   "return"),
            (3, 4, "withdraw(amount)",                          None),
            (3, 5, "verifyAndDeduct(skypassNumber, amount)",    None),
            (5, 3, "ok",                                        "return"),
            (3, 3, "payment.pay()",                             "self"),
            (2, 6, "addPayment(payment)",                       None),
            (2, 6, "processPayment()",                          None),
        ],
        OUT / "mileagePayment-iter3.png",
    )

    class_diagram_iter3()
    usecase_diagram_iter3()

    print("Done.")


if __name__ == "__main__":
    build_all()
