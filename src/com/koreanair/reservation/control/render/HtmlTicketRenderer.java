package com.koreanair.reservation.control.render;

import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.reservation.Ticket;

/** DP#6 Template Method — ConcreteClass: HTML e-Ticket. */
public class HtmlTicketRenderer extends TicketRenderer {

    @Override
    protected String header(Reservation reservation, Ticket ticket) {
        return "<!DOCTYPE html><html><body>"
                + "<h1>Korean Air e-Ticket</h1>"
                + "<table><tr><th>Ticket</th><td>" + safe(ticket.getTicketNumber()) + "</td></tr>"
                + "<tr><th>PNR</th><td>" + safe(reservation != null ? reservation.getPnrNumber() : null) + "</td></tr></table>";
    }

    @Override
    protected String body(Reservation reservation, Ticket ticket) {
        return "<section><p><b>Passenger:</b> "
                + (ticket.getPassenger() != null ? ticket.getPassenger().getName() : "-") + "</p>"
                + "<p><b>Issued:</b> " + ticket.getIssuedAt() + "</p></section>";
    }

    @Override
    protected String footer(Reservation reservation, Ticket ticket) {
        return "<footer><p>Arrive 40 min before departure.</p>"
                + "<a href='https://kr.koreanair.com/booking/manage'>Manage booking</a></footer></body></html>";
    }

    @Override
    protected String separator() {
        return "<hr/>";
    }

    private String safe(String s) {
        return s != null ? s : "-";
    }
}
