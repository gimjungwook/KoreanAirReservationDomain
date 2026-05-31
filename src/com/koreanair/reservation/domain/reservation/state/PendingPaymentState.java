package com.koreanair.reservation.domain.reservation.state;

import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.reservation.ReservationStatus;

/**
 * 결제 대기 상태 — 결제 성공 / 실패 두 갈래 전이.
 *
 * <p>Iteration 1: 결제 성공 경로만 Happy Path 로 시연.
 * <p>TODO(iter3): handlePaymentFailure() 에서 Seat: Held -> Available 해제 트리거.
 */
public class PendingPaymentState implements ReservationState {

    @Override
    public String name() {
        return "PendingPayment";
    }

    @Override
    public void processPayment(Reservation ctx) {
        // 실제 결제 처리(PaymentProcessor 호출)는 Context 의 호출자가 선행한다고 가정.
        // 이 메서드가 호출된 시점에는 "결제가 승인되었다" 는 의미.
        ctx.setState(new ConfirmedState());
        ctx.updateStatus(ReservationStatus.CONFIRMED);
    }

    @Override
    public void handlePaymentFailure(Reservation ctx) {
        // TODO(iter3): Seat.release() 호출 연동
        ctx.setState(new CancelledState());
        ctx.updateStatus(ReservationStatus.CANCELLED);
    }
}
