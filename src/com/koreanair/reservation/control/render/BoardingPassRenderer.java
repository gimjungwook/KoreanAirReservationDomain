package com.koreanair.reservation.control.render;

import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.reservation.Ticket;

/**
 * DP#7 Template Method — ConcreteClass: 탑승권.
 *
 * <p>출력 형식이 e-Ticket 과 비슷하지만 게이트·좌석을 강조하고 바코드 placeholder 가 들어간다.
 */
public class BoardingPassRenderer extends TicketRenderer {

    @Override
    protected String header(Reservation reservation, Ticket ticket) {
        return "╔════════════════════════════╗\n"
                + "║  KOREAN AIR  BOARDING PASS  ║\n"
                + "╚════════════════════════════╝\n"
                + "TICKET " + safe(ticket.getTicketNumber()) + "   PNR " + safe(reservation != null ? reservation.getPnrNumber() : null);
    }

    @Override
    protected String body(Reservation reservation, Ticket ticket) {
        return "PASSENGER  " + (ticket.getPassenger() != null ? ticket.getPassenger().getName() : "-") + "\n"
                + "GATE       (assigned 60 min prior)\n"
                + "SEAT       (see boarding strip)\n"
                + "ISSUED     " + ticket.getIssuedAt();
    }

    @Override
    protected String footer(Reservation reservation, Ticket ticket) {
        return "▓▓▓▓▓ ▓ ▓▓ ▓▓▓▓ ▓ ▓▓▓ ▓ ▓▓▓▓▓▓\n"
                + "  Scan at gate";
    }

    @Override
    protected String separator() {
        return "\n----------------------------\n";
    }

    private String safe(String s) {
        return s != null ? s : "-";
    }
}
