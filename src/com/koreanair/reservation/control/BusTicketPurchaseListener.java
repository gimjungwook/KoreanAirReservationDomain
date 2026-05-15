package com.koreanair.reservation.control;

import com.koreanair.reservation.domain.bus.BusCity;
import com.koreanair.reservation.domain.event.DomainEvent;
import com.koreanair.reservation.domain.event.EventListener;
import com.koreanair.reservation.domain.event.TicketIssuedEvent;

/**
 * 항공 e-Ticket 발급 이벤트를 구독해 버스 티켓을 연계 발매하는 Observer.
 */
public class BusTicketPurchaseListener implements EventListener {

    private final BusTicketingService busTicketingService;

    public BusTicketPurchaseListener(BusTicketingService busTicketingService) {
        this.busTicketingService = busTicketingService;
    }

    @Override
    public void onEvent(DomainEvent event) {
        if (!(event instanceof TicketIssuedEvent)) {
            return;
        }
        TicketIssuedEvent ticketIssued = (TicketIssuedEvent) event;
        BusCity city = ticketIssued.getRequestedBusCity();
        if (city == null) {
            System.out.println("[BUS] no linked bus city selected; skip bus ticket issue");
            return;
        }
        busTicketingService.issuePremiumTicket(
                ticketIssued.getReservation(),
                ticketIssued.getTicket(),
                city);
    }
}
