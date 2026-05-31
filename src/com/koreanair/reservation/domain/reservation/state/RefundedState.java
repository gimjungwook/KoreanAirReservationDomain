package com.koreanair.reservation.domain.reservation.state;

/**
 * 환불 완료 상태 — 최종 상태 (Final State).
 *
 * <p>모든 메서드가 {@link ReservationState} 인터페이스의 디폴트 거부 로직을 그대로 사용한다.
 * Iteration 2 이후에도 override 할 필요가 없다. 이 상태에서 어떤 행동도 허용되지 않기 때문.
 */
public class RefundedState implements ReservationState {

    @Override
    public String name() {
        return "Refunded";
    }

    // 모든 행동 거부 — override 없음.
}
