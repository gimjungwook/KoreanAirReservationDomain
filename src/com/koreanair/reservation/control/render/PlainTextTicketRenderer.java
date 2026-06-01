package com.koreanair.reservation.control.render;

import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.reservation.Ticket;

/** DP#6 Template Method — ConcreteClass: 평문 e-Ticket. */
public class PlainTextTicketRenderer extends TicketRenderer {

    @Override
    protected String header(Reservation reservation, Ticket ticket) {
        return "=== KOREAN AIR e-TICKET ===\n"
                + "Ticket No : " + safe(ticket.getTicketNumber()) + "\n"
                + "PNR       : " + safe(reservation != null ? reservation.getPnrNumber() : null);
    }

    @Override
    protected String body(Reservation reservation, Ticket ticket) {
        return "Passenger : " + (ticket.getPassenger() != null ? ticket.getPassenger().getName() : "-") + "\n"
                + "Issued    : " + ticket.getIssuedAt();
    }

    @Override
    protected String footer(Reservation reservation, Ticket ticket) {
        return "Please arrive at the gate 40 min before departure.\n"
                + "https://kr.koreanair.com/booking/manage";
    }

    @Override
    protected String separator() {
        return "\n----------------------------\n";
    }

    private String safe(String s) {
        return s != null ? s : "-";
    }
}
