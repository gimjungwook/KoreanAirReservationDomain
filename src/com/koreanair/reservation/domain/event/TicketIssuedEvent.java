package com.koreanair.reservation.domain.event;

import com.koreanair.reservation.domain.bus.BusCity;
import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.reservation.Ticket;

/**
 * 항공 e-Ticket 발급 완료 시 발행되는 이벤트. Iteration 3 bus-ticket 연계 요구사항.
 *
 * <p>발행자: TicketPurchasePublisher. 구독자: BusTicketPurchaseListener.
 */
public class TicketIssuedEvent extends DomainEvent {

    private final Reservation reservation;
    private final Ticket ticket;
    private final BusCity requestedBusCity;

    public TicketIssuedEvent(Reservation reservation, Ticket ticket, BusCity requestedBusCity) {
        super(ticket != null && ticket.getTicketNumber() != null
                ? ticket.getTicketNumber()
                : "unknown-ticket");
        this.reservation = reservation;
        this.ticket = ticket;
        this.requestedBusCity = requestedBusCity;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public BusCity getRequestedBusCity() {
        return requestedBusCity;
    }
}
