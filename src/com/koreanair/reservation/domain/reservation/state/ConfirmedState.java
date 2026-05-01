package com.koreanair.reservation.domain.reservation.state;

import com.koreanair.reservation.domain.passenger.Passenger;
import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.reservation.ReservationStatus;
import com.koreanair.reservation.domain.reservation.Ticket;

/**
 * 확정 상태 — PNR 발급 후 상태. Ticketed 또는 CancellationRequested 로 분기.
 *
 * <p>Iteration 1: Happy Path 의 종착점. issueTicket() / requestCancellation() 는 선언만.
 * <p>Iteration 2: 두 전이 모두 실제 구현. issueTicket 은 승객별 Ticket.generate 호출,
 * requestCancellation 은 CancellationRequestedState 로 단순 전이.
 */
public class ConfirmedState extends AbstractReservationState {

    @Override
    public String name() {
        return "Confirmed";
    }

    @Override
    public void issueTicket(Reservation ctx) {
        // 승객별로 e-Ticket 을 생성. iter 2 단순화: 좌석은 null 로 두고 Ticket.generate 가 처리.
        for (Passenger passenger : ctx.getPassengers()) {
            Ticket ticket = Ticket.generate(ctx, passenger, null);
            ctx.addTicket(ticket);
            String passengerName = passenger != null ? passenger.getName() : "(unknown)";
            System.out.printf("[TICKET] e-Ticket %s issued for %s%n",
                    ticket.getTicketNumber(), passengerName);
        }
        ctx.setState(new TicketedState());
        ctx.updateStatus(ReservationStatus.TICKETED);
    }

    @Override
    public void requestCancellation(Reservation ctx) {
        ctx.setState(new CancellationRequestedState());
        ctx.updateStatus(ReservationStatus.CANCELLATION_REQUESTED);
    }
}
