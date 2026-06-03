package com.koreanair.reservation.domain.reservation.state;

import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.reservation.ReservationStatus;
import com.koreanair.reservation.domain.reservation.state.CancelledState;

/**
 * 예약 개시 상태 — 승객 정보 입력만 허용.
 *
 * <p>Iteration 1: 실제 전이 로직 구현.
 * <p>다음 상태: {@link PendingPaymentState}.
 */
public class InitiatedState implements ReservationState {

    @Override
    public String name() {
        return "Initiated";
    }

    @Override
    public void enterPassengerInfo(Reservation ctx) {
        // TODO(iter1): Passenger 객체 검증 로직은 BookingController 가 이미 처리한 뒤 호출된다고 가정.
        //              여기서는 단순히 상태만 전이.
        ctx.setState(new PendingPaymentState());
        // 기존 ReservationStatus enum 과의 컨텍스트 동기화 (공존 원칙).
        ctx.updateStatus(ReservationStatus.PENDING_PAYMENT);
    }

    @Override
    public void requestCancellation(Reservation ctx) {
        ctx.setState(new CancelledState());
        ctx.updateStatus(ReservationStatus.CANCELLED);
    }
}
