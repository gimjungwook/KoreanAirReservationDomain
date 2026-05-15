package com.koreanair.reservation.control;

import com.koreanair.reservation.domain.bus.BusCity;
import com.koreanair.reservation.domain.event.EventPublisher;
import com.koreanair.reservation.domain.event.TicketIssuedEvent;
import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.reservation.Ticket;

/**
 * 항공권 구매 완료 이벤트의 Subject.
 *
 * <p>Iteration 3 추가 요구사항인 "항공권 구매와 연계된 6개 대도시 우등고속
 * 버스 티켓 발매"를 Observer 패턴으로 연결한다. 이 publisher는 버스 발매
 * 구현체를 모르고, TicketIssuedEvent만 발행한다.
 */
public class TicketPurchasePublisher extends EventPublisher {

    public void publishTicketIssued(Reservation reservation, Ticket ticket, BusCity busCity) {
        publish(new TicketIssuedEvent(reservation, ticket, busCity));
    }
}
