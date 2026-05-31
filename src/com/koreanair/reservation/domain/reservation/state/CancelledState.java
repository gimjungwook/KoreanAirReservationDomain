package com.koreanair.reservation.domain.reservation.state;

import com.koreanair.reservation.domain.flight.FareRule;
import com.koreanair.reservation.domain.flight.FlightSchedule;
import com.koreanair.reservation.domain.reservation.Itinerary;
import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.reservation.ReservationStatus;
import com.koreanair.reservation.domain.reservation.Segment;

/**
 * 취소 확정 상태.
 *
 * <p>환불 불가 운임이면 이 상태가 최종 상태 (진입 후 종료).
 * <p>환불 가능 운임이면 requestRefund() 로 RefundRequestedState 전이.
 *
 * <p>Iteration 2 활성화: requestRefund() 에서 FareRule.isRefundable 1차 검증 후 전이.
 */
public class CancelledState implements ReservationState {

    @Override
    public String name() {
        return "Cancelled";
    }

    @Override
    public void requestRefund(Reservation ctx) {
        FareRule fareRule = resolveFareRule(ctx);
        // FareRule 이 명시적으로 환불 불가일 때만 거부. 정보가 없으면 iter 2 단순화로 통과.
        if (fareRule != null && !fareRule.isRefundable()) {
            throw new InvalidStateTransitionException(name(), "환불 불가 운임");
        }
        ctx.setState(new RefundRequestedState());
        ctx.updateStatus(ReservationStatus.REFUND_REQUESTED);
    }

    /**
     * Itinerary -> 첫 Segment -> FlightSchedule 경로로 FareRule 을 탐색한다.
     * Iteration 2 단순화: 다중 segment 의 최저 정책 통합 등은 다음 iter 에서.
     */
    private FareRule resolveFareRule(Reservation ctx) {
        Itinerary itinerary = ctx.getItinerary();
        if (itinerary == null || itinerary.getSegments() == null || itinerary.getSegments().isEmpty()) {
            return null;
        }
        Segment first = itinerary.getSegments().get(0);
        if (first == null) {
            return null;
        }
        FlightSchedule schedule = first.getFlightSchedule();
        return schedule != null ? schedule.getFareRule() : null;
    }
}
