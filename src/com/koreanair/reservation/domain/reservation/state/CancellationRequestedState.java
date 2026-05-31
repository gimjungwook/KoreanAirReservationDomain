package com.koreanair.reservation.domain.reservation.state;

import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.reservation.ReservationStatus;

/**
 * 취소 요청 접수 상태 — FareRule 확인과 cancellationPenalty 계산이라는 중간 단계를 표현한다.
 *
 * <p>Iteration 2 활성화: confirmCancellation() 으로 CancelledState 전이.
 */
public class CancellationRequestedState implements ReservationState {

    @Override
    public String name() {
        return "CancellationRequested";
    }

    @Override
    public void confirmCancellation(Reservation ctx) {
        ctx.setState(new CancelledState());
        ctx.updateStatus(ReservationStatus.CANCELLED);
    }
}
