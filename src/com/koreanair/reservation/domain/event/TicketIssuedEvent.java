package com.koreanair.reservation.domain.event;

import com.koreanair.reservation.domain.bus.BusCity;
import com.koreanair.reservation.domain.bus.BusTicketRequest;
import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.reservation.Ticket;

/**
 * 항공 e-Ticket 발급 완료 시 발행되는 이벤트. Iteration 3 bus-ticket 연계 요구사항.
 *
 * <p>발행자: TicketPurchasePublisher. 구독자: BusTicketPurchaseListener.
 * <p>Iteration 4: BusTicketRequest 로 좌석·스케줄까지 캡슐화.
 */
public class TicketIssuedEvent extends DomainEvent {

    private final Reservation reservation;
    private final Ticket ticket;
    private final BusTicketRequest busTicketRequest;

    public TicketIssuedEvent(Reservation reservation, Ticket ticket, BusTicketRequest busTicketRequest) {
        super(ticket != null && ticket.getTicketNumber() != null
                ? ticket.getTicketNumber()
                : "unknown-ticket");
        this.reservation = reservation;
        this.ticket = ticket;
        this.busTicketRequest = busTicketRequest;
    }

    /** Legacy constructor (iter3 compatibility). */
    public TicketIssuedEvent(Reservation reservation, Ticket ticket, BusCity requestedBusCity) {
        this(reservation, ticket,
                requestedBusCity != null ? new BusTicketRequest(requestedBusCity, null, null) : null);
    }

    public Reservation getReservation() {
        return reservation;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public BusTicketRequest getBusTicketRequest() {
        return busTicketRequest;
    }

    /** Legacy accessor. */
    public BusCity getRequestedBusCity() {
        return busTicketRequest != null ? busTicketRequest.getOriginCity() : null;
    }
}
