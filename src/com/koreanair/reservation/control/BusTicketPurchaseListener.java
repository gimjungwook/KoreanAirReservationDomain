package com.koreanair.reservation.control;

import com.koreanair.reservation.domain.bus.BusTicketRequest;
import com.koreanair.reservation.domain.event.DomainEvent;
import com.koreanair.reservation.domain.event.EventListener;
import com.koreanair.reservation.domain.event.TicketIssuedEvent;

/**
 * 항공 e-Ticket 발급 이벤트를 구독해 우등고속 셔틀 티켓을 연계 발매하는 Observer.
 *
 * <p>Iteration 4: 좌석 선택 + 스케줄을 포함한 BusTicketRequest 처리.
 */
public class BusTicketPurchaseListener implements EventListener {

    private final BusTicketingService busTicketingService;
    /** 교과서 ConcreteObserver 가 관찰하는 ConcreteSubject 역참조(-concreteSubject). */
    private TicketPurchasePublisher subject;

    public BusTicketPurchaseListener(BusTicketingService busTicketingService) {
        this.busTicketingService = busTicketingService;
    }

    /** 관찰 대상 Subject 주입 — 교과서 ConcreteObserver -> ConcreteSubject 연관. */
    public void setSubject(TicketPurchasePublisher subject) {
        this.subject = subject;
    }

    public TicketPurchasePublisher getSubject() {
        return subject;
    }

    @Override
    public void onEvent(DomainEvent event) {
        if (!(event instanceof TicketIssuedEvent)) {
            return;
        }
        TicketIssuedEvent ticketIssued = (TicketIssuedEvent) event;
        BusTicketRequest req = ticketIssued.getBusTicketRequest();
        if (req == null || req.getOriginCity() == null) {
            System.out.println("[BUS] no linked bus origin city selected; skip shuttle issue");
            return;
        }
        busTicketingService.issuePremiumTicket(
                ticketIssued.getReservation(),
                ticketIssued.getTicket(),
                req.getOriginCity(),
                req.getSchedule(),
                req.getSeat());
    }
}
