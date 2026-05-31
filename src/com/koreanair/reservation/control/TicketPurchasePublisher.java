package com.koreanair.reservation.control;

import com.koreanair.reservation.domain.bus.BusCity;
import com.koreanair.reservation.domain.bus.BusTicketRequest;
import com.koreanair.reservation.domain.event.EventPublisher;
import com.koreanair.reservation.domain.event.TicketIssuedEvent;
import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.reservation.Ticket;

/**
 * 항공권 구매 완료 이벤트의 Subject.
 *
 * <p>Iteration 3 추가 요구사항(우등고속 셔틀 연계)을 Observer 패턴으로 연결한다.
 * Iteration 4 에서 BusTicketRequest 로 좌석·스케줄까지 캡슐화한 요청 전달.
 */
public class TicketPurchasePublisher extends EventPublisher {

    /** 교과서 ConcreteSubject 의 관찰 대상 상태(-subjectState) — 마지막으로 발행한 발권 이벤트. */
    private TicketIssuedEvent subjectState;

    /** 교과서 ConcreteSubject.getState() — 현재 관찰 상태 반환. */
    public TicketIssuedEvent getState() {
        return subjectState;
    }

    /** 교과서 ConcreteSubject.setState() — 상태를 저장한 뒤 옵서버에게 통지(notify)한다. */
    public void setState(TicketIssuedEvent event) {
        this.subjectState = event;
        publish(event);
    }

    /** Iteration 4: 좌석·스케줄 포함 BusTicketRequest 전달 */
    public void publishTicketIssued(Reservation reservation, Ticket ticket, BusTicketRequest req) {
        setState(new TicketIssuedEvent(reservation, ticket, req));
    }

    /** Legacy: city only (iter3 호환). */
    public void publishTicketIssued(Reservation reservation, Ticket ticket, BusCity busCity) {
        setState(new TicketIssuedEvent(reservation, ticket, busCity));
    }
}
