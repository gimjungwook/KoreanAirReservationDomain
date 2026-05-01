package com.koreanair.reservation.domain.reservation.state;

import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.reservation.ReservationStatus;

/**
 * 환불 요청 접수 상태.
 *
 * <p>RefundHandler 가 RefundPolicy (Strategy) 를 통해 환불 금액을 산정한 뒤 승인 / 거부를 결정.
 *
 * <p>Iteration 2 활성화: processRefundDecision 으로 분기.
 *   - approved=true  -> RefundedState (최종)
 *   - approved=false -> CancelledState 로 복귀
 */
public class RefundRequestedState extends AbstractReservationState {

    @Override
    public String name() {
        return "RefundRequested";
    }

    @Override
    public void processRefundDecision(Reservation ctx, boolean approved) {
        if (approved) {
            ctx.setState(new RefundedState());
            ctx.updateStatus(ReservationStatus.REFUNDED);
        } else {
            ctx.setState(new CancelledState());
            ctx.updateStatus(ReservationStatus.CANCELLED);
        }
    }
}
