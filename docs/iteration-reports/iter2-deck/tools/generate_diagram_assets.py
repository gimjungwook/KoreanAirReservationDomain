from pathlib import Path
from PIL import Image, ImageDraw, ImageFont


ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "assets" / "diagrams"
OUT.mkdir(parents=True, exist_ok=True)


def font(size, bold=False):
    candidates = [
        "/System/Library/Fonts/AppleSDGothicNeo.ttc",
        "/System/Library/Fonts/Supplemental/Arial Unicode.ttf",
        "/Library/Fonts/Arial Unicode.ttf",
        "/System/Library/Fonts/Helvetica.ttc",
    ]
    for path in candidates:
        try:
            return ImageFont.truetype(path, size=size, index=8 if bold and path.endswith(".ttc") else 0)
        except Exception:
            pass
    return ImageFont.load_default()


F = {
    "title": font(48, True),
    "h1": font(34, True),
    "h2": font(26, True),
    "body": font(22),
    "small": font(18),
    "mono": font(18),
}

INK = "#171923"
MUTED = "#667085"
BLUE = "#2563eb"
RED = "#e11d48"
GREEN = "#039855"
SURFACE = "#ffffff"
SOFT = "#f6f8fb"
RULE = "#d9e1ec"
YELLOW = "#fff7d6"
PURPLE = "#f4efff"


def canvas(w=1600, h=1000, title=None, subtitle=None):
    img = Image.new("RGB", (w, h), "#f3f6fb")
    d = ImageDraw.Draw(img)
    d.rounded_rectangle((24, 24, w - 24, h - 24), radius=34, fill=SURFACE, outline=RULE, width=2)
    if title:
        d.text((64, 48), title, fill=INK, font=F["title"])
    if subtitle:
        d.text((66, 106), subtitle, fill=MUTED, font=F["body"])
    return img, d


def wrap(draw, text, font_obj, width):
    words = text.split()
    lines, line = [], ""
    for word in words:
        test = (line + " " + word).strip()
        if draw.textlength(test, font=font_obj) <= width:
            line = test
        else:
            if line:
                lines.append(line)
            line = word
    if line:
        lines.append(line)
    return lines


def box(d, xy, title, lines=None, fill=SOFT, outline=RULE, accent=None, title_font=None):
    x1, y1, x2, y2 = xy
    d.rounded_rectangle(xy, radius=18, fill=fill, outline=outline, width=3 if accent else 2)
    if accent:
        d.rounded_rectangle((x1, y1, x1 + 10, y2), radius=7, fill=accent)
    d.text((x1 + 24, y1 + 18), title, fill=INK, font=title_font or F["h2"])
    y = y1 + 62
    for line in lines or []:
        d.text((x1 + 24, y), line, fill=MUTED, font=F["small"])
        y += 28


def arrow(d, start, end, color=BLUE, width=4, label=None, label_pos=None):
    d.line((start, end), fill=color, width=width)
    x1, y1 = start
    x2, y2 = end
    dx, dy = x2 - x1, y2 - y1
    length = max((dx * dx + dy * dy) ** 0.5, 1)
    ux, uy = dx / length, dy / length
    px, py = -uy, ux
    tip = [
        (x2, y2),
        (x2 - ux * 20 + px * 9, y2 - uy * 20 + py * 9),
        (x2 - ux * 20 - px * 9, y2 - uy * 20 - py * 9),
    ]
    d.polygon(tip, fill=color)
    if label:
        lx, ly = label_pos or ((x1 + x2) / 2, (y1 + y2) / 2)
        tw = d.textlength(label, font=F["small"])
        d.rounded_rectangle((lx - 10, ly - 8, lx + tw + 10, ly + 24), radius=8, fill="#ffffff", outline=RULE)
        d.text((lx, ly - 5), label, fill=color, font=F["small"])


def save(img, name):
    img.save(OUT / name, quality=95)


def usecase(iter2=False):
    title = "Use Case Diagram - iter 2" if iter2 else "Use Case Diagram - iter 1"
    img, d = canvas(title=title, subtitle="Korean Air Skypass Reservation System")
    d.rounded_rectangle((420, 180, 1190, 860), radius=30, fill="#f8fbff", outline=BLUE if iter2 else RULE, width=3)
    d.text((650, 205), "Reservation System", fill=BLUE if iter2 else INK, font=F["h1"])
    actors = [("Member", 170, 310), ("Guest", 170, 610), ("Admin", 1340, 410), ("Payment Gateway", 1320, 650)]
    if iter2:
        actors += [("GDS", 1345, 260), ("Skypass", 1330, 780)]
    for name, x, y in actors:
        d.ellipse((x - 22, y - 58, x + 22, y - 14), outline=INK, width=4)
        d.line((x, y - 14, x, y + 48), fill=INK, width=4)
        d.line((x - 42, y + 8, x + 42, y + 8), fill=INK, width=4)
        d.line((x, y + 48, x - 36, y + 95), fill=INK, width=4)
        d.line((x, y + 48, x + 36, y + 95), fill=INK, width=4)
        d.text((x - 80, y + 112), name, fill=INK, font=F["body"])
    items1 = [
        ("Login", 560, 300, False),
        ("Search Flight", 820, 300, False),
        ("Book Flight", 560, 465, False),
        ("Enter Passenger", 820, 465, False),
        ("Pay", 560, 630, False),
        ("Confirm Reservation", 820, 630, False),
    ]
    items2 = [
        ("View Booking", 1050, 300, True),
        ("Issue e-Ticket", 1050, 465, True),
        ("Cancel Booking", 1050, 630, True),
        ("Refund Request", 820, 770, True),
        ("Guest PNR Lookup", 560, 770, True),
        ("Review Refund", 560, 180, True),
    ] if iter2 else []
    for text, x, y, new in items1 + items2:
        fill = "#fff1f3" if new else "#ffffff"
        outline = RED if new else RULE
        d.ellipse((x - 120, y - 45, x + 120, y + 45), fill=fill, outline=outline, width=3)
        tw = d.textlength(text, font=F["small"])
        d.text((x - tw / 2, y - 11), text, fill=RED if new else INK, font=F["small"])
    save(img, f"reservationSystem-iter{2 if iter2 else 1}.png")


def classdiagram(iter2=False):
    img, d = canvas(title=f"Class Diagram - iter {2 if iter2 else 1}", subtitle="ECB package overview and pattern families")
    columns = [
        ("Boundary", 90, "#eef4ff", ["ReservationUI", "SwingReservationUI", "LoginPanel", "SearchPanel", "PaymentPanel"]),
        ("Control", 440, "#f0fdf4", ["BookingController", "AuthService", "FlightSearchService", "PaymentProcessor"]),
        ("Domain", 790, "#fff7ed", ["Reservation", "ReservationState", "FareRule", "SeatInventory", "Passenger", "Payment"]),
        ("External", 1140, "#f8fafc", ["PaymentGateway", "GDSInterface", "SkypassInterface"]),
    ]
    if iter2:
        columns[0][3].extend(["LookupPanel", "SeatSelectionPanel", "CancellationPanel", "RefundPanel"])
        columns[1][3].extend(["RefundHandler", "ReservationLookupService"])
        columns[2][3].extend(["Ticket", "Refund", "RefundRequest", "RefundPolicy", "FullRefundPolicy", "PartialRefundPolicy", "NoRefundPolicy"])
    for title, x, fill, names in columns:
        d.text((x, 180), title, fill=INK, font=F["h1"])
        y = 235
        for name in names:
            new = iter2 and name in {"LookupPanel", "SeatSelectionPanel", "CancellationPanel", "RefundPanel", "RefundHandler", "ReservationLookupService", "Ticket", "Refund", "RefundRequest", "RefundPolicy", "FullRefundPolicy", "PartialRefundPolicy", "NoRefundPolicy"}
            box(d, (x, y, x + 300, y + 64), name, fill="#fff1f3" if new else fill, outline=RED if new else RULE, accent=RED if new else None, title_font=F["small"])
            y += 78
    arrow(d, (390, 450), (440, 450), BLUE, label="calls", label_pos=(390, 410))
    arrow(d, (740, 450), (790, 450), BLUE, label="updates", label_pos=(720, 410))
    arrow(d, (1090, 600), (1140, 600), BLUE, label="adapter", label_pos=(1080, 560))
    if iter2:
        d.rounded_rectangle((760, 735, 1110, 925), radius=22, fill=PURPLE, outline=RED, width=3)
        d.text((790, 760), "<<Strategy>> RefundPolicy", fill=RED, font=F["h2"])
        d.text((790, 815), "Full / Partial / No refund", fill=INK, font=F["body"])
        d.text((790, 855), "RefundHandler depends on interface", fill=MUTED, font=F["small"])
    save(img, f"classDiagram-iter{2 if iter2 else 1}.png")


def state(iter2=False):
    img, d = canvas(title=f"Reservation State Diagram - iter {2 if iter2 else 1}", subtitle="State pattern transition coverage")
    coords = {
        "Initiated": (160, 390),
        "PendingPayment": (430, 390),
        "Confirmed": (735, 390),
        "Ticketed": (1010, 250),
        "CancellationRequested": (1010, 520),
        "Cancelled": (1270, 520),
        "RefundRequested": (1270, 750),
        "Refunded": (1010, 750),
    }
    active = {"Initiated->PendingPayment", "PendingPayment->Confirmed", "PendingPayment->Initiated"}
    if iter2:
        active |= {"Confirmed->Ticketed", "Confirmed->CancellationRequested", "Ticketed->CancellationRequested", "CancellationRequested->Cancelled", "Cancelled->RefundRequested", "RefundRequested->Refunded"}
    for name, (x, y) in coords.items():
        fill = "#fff1f3" if iter2 and name in {"Ticketed", "CancellationRequested", "Cancelled", "RefundRequested", "Refunded"} else "#ffffff"
        outline = RED if iter2 and name in {"Ticketed", "CancellationRequested", "Cancelled", "RefundRequested", "Refunded"} else RULE
        box(d, (x, y, x + 220, y + 80), name, fill=fill, outline=outline, accent=RED if outline == RED else None, title_font=F["body"])
    flows = [
        ("Initiated", "PendingPayment", "enterPassengerInfo"),
        ("PendingPayment", "Confirmed", "processPayment"),
        ("Confirmed", "Ticketed", "issueTicket"),
        ("Confirmed", "CancellationRequested", "requestCancellation"),
        ("Ticketed", "CancellationRequested", "requestCancellation"),
        ("CancellationRequested", "Cancelled", "confirmCancellation"),
        ("Cancelled", "RefundRequested", "requestRefund"),
        ("RefundRequested", "Refunded", "processRefundDecision"),
    ]
    for a, b, lab in flows:
        x1, y1 = coords[a]
        x2, y2 = coords[b]
        key = f"{a}->{b}"
        color = RED if iter2 and key in active and a not in {"Initiated", "PendingPayment"} else (BLUE if key in active else "#98a2b3")
        width = 5 if color in {RED, BLUE} else 3
        arrow(d, (x1 + 220, y1 + 40), (x2, y2 + 40), color=color, width=width, label=lab, label_pos=((x1 + x2) / 2, (y1 + y2) / 2 - 20))
    save(img, f"reservationState-iter{2 if iter2 else 1}.png")


def sequence_cancel():
    img, d = canvas(title="Sequence - Cancel + Refund", subtitle="Strategy delegation point is highlighted in red")
    lanes = ["Member", "ReservationUI", "BookingController", "Reservation", "State", "RefundHandler", "RefundPolicy", "PaymentGateway"]
    xs = [100, 300, 520, 740, 940, 1140, 1350, 1530]
    for x, lane in zip(xs, lanes):
        box(d, (x - 70, 170, x + 70, 225), lane, fill="#ffffff", title_font=F["small"])
        d.line((x, 225, x, 890), fill="#cbd5e1", width=2)
    messages = [
        (0, 1, "cancel(reservationId)", BLUE),
        (1, 2, "processCancellation()", BLUE),
        (2, 3, "requestCancellation()", BLUE),
        (3, 4, "Confirmed -> CancellationRequested", RED),
        (2, 5, "requestRefund()", BLUE),
        (5, 6, "calculateRefundAmount()", RED),
        (6, 5, "amount", RED),
        (5, 7, "sendRefund()", BLUE),
        (7, 5, "approved", BLUE),
        (5, 4, "RefundRequested -> Refunded", RED),
    ]
    y = 280
    for a, b, text, color in messages:
        arrow(d, (xs[a], y), (xs[b], y), color=color, width=4, label=text, label_pos=(min(xs[a], xs[b]) + 20, y - 34))
        y += 58
    d.rounded_rectangle((1080, 560, 1450, 680), radius=18, fill="#fff1f3", outline=RED, width=3)
    d.text((1110, 585), "Strategy hotspot", fill=RED, font=F["h2"])
    d.text((1110, 625), "RefundHandler knows only RefundPolicy", fill=INK, font=F["small"])
    save(img, "cancelRefund-iter2.png")


def sequence_lookup():
    img, d = canvas(title="Sequence - Guest PNR Lookup", subtitle="3-factor verification: PNR + name + email")
    lanes = ["Guest", "LookupPanel", "AuthService", "ReservationLookupService", "Reservation"]
    xs = [160, 450, 760, 1080, 1380]
    for x, lane in zip(xs, lanes):
        box(d, (x - 90, 170, x + 90, 225), lane, fill="#ffffff", title_font=F["small"])
        d.line((x, 225, x, 885), fill="#cbd5e1", width=2)
    messages = [
        (0, 1, "submit(PNR, name, email)", BLUE),
        (1, 2, "verifyGuestIdentity()", BLUE),
        (2, 3, "findByGuestPnr()", BLUE),
        (3, 4, "findByPnr()", BLUE),
        (4, 3, "reservation", GREEN),
        (3, 1, "detail DTO", GREEN),
        (1, 0, "show reservation", GREEN),
    ]
    y = 300
    for a, b, text, color in messages:
        arrow(d, (xs[a], y), (xs[b], y), color=color, width=4, label=text, label_pos=(min(xs[a], xs[b]) + 20, y - 34))
        y += 70
    d.rounded_rectangle((560, 650, 1130, 810), radius=20, fill=YELLOW, outline="#f59e0b", width=3)
    d.text((595, 675), "Denied branch", fill="#b45309", font=F["h2"])
    d.text((595, 720), "Return only INVALID_CREDENTIALS", fill=INK, font=F["body"])
    d.text((595, 760), "No PNR existence leak; lock after 5 failures", fill=MUTED, font=F["small"])
    save(img, "lookupReservation-iter2.png")


def demo_card(name, title, subtitle, bullets, code=False):
    img, d = canvas(900, 560)
    d.rounded_rectangle((42, 42, 858, 518), radius=22, fill="#ffffff", outline=RULE, width=2)
    d.rectangle((42, 42, 858, 108), fill="#0f172a")
    d.text((72, 62), title, fill="#ffffff", font=F["h2"])
    d.text((72, 124), subtitle, fill=MUTED, font=F["body"])
    y = 175
    if code:
        d.rounded_rectangle((72, y, 828, 475), radius=14, fill="#111827", outline="#1f2937")
        for line in bullets:
            d.text((98, y + 22), line, fill="#d1fae5" if "STRATEGY" in line or "STATE" in line else "#e5e7eb", font=F["mono"])
            y += 38
    else:
        for label, value in bullets:
            d.rounded_rectangle((72, y, 828, y + 52), radius=12, fill=SOFT, outline=RULE)
            d.text((95, y + 14), label, fill=INK, font=F["small"])
            d.text((340, y + 14), value, fill=BLUE if "OK" in value or "Full" in value else MUTED, font=F["small"])
            y += 68
    save(img, name)


def demos():
    demo_card("demo-01-lookup.png", "LookupPanel", "PNR member/guest lookup", [("PNR", "KE26A7"), ("Guest verify", "name + email OK"), ("Result", "Reservation detail loaded")])
    demo_card("demo-02-seat.png", "SeatSelectionPanel", "Seat map selection", [("Cabin", "Economy"), ("Selected", "12A"), ("Inventory", "HOLD -> ASSIGNED")])
    demo_card("demo-03-cancellation.png", "CancellationPanel", "Cancel request + preview", [("Status", "Ticketed"), ("Reason", "Schedule changed"), ("Preview", "FullRefundPolicy")])
    demo_card("demo-04-refund.png", "RefundPanel", "Refund execution", [("Policy", "FullRefundPolicy"), ("Gateway", "MockPaymentGateway OK"), ("Status", "Refunded")])
    demo_card("demo-05-console.png", "Console", "State transition backup", [
        "[STATE] Confirmed -> Ticketed",
        "[STATE] Ticketed -> CancellationRequested",
        "[STATE] CancellationRequested -> Cancelled",
        "[STATE] Cancelled -> RefundRequested",
        "[STATE] RefundRequested -> Refunded",
    ], code=True)
    demo_card("demo-06-strategy.png", "Console", "Strategy policy backup", [
        "[STRATEGY] fareClass=Y -> FullRefundPolicy",
        "[REFUND] paid=421000 penalty=0 amount=421000",
        "[PG] sendRefund(originalPaymentId, amount)",
        "[RESULT] refundStatus=APPROVED",
    ], code=True)


if __name__ == "__main__":
    usecase(False)
    usecase(True)
    classdiagram(False)
    classdiagram(True)
    state(False)
    state(True)
    sequence_cancel()
    sequence_lookup()
    demos()
    print(f"generated assets in {OUT}")
