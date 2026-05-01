package com.koreanair.reservation.domain.reservation.state;

import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.reservation.ReservationStatus;

/**
 * 발권 완료 상태 — Ticket 이 ISSUED 된 직후의 상태.
 *
 * <p>Iteration 2 활성화: requestCancellation() 으로 CancellationRequestedState 전이.
 * 발권 후 취소이므로 후속 환불 단계에서 발권 수수료 / 위약금이 추가될 수 있다.
 */
public class TicketedState extends AbstractReservationState {

    @Override
    public String name() {
        return "Ticketed";
    }

    @Override
    public void requestCancellation(Reservation ctx) {
        ctx.setState(new CancellationRequestedState());
        ctx.updateStatus(ReservationStatus.CANCELLATION_REQUESTED);
    }
}
