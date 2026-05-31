package com.koreanair.reservation.domain.reservation.state;

import com.koreanair.reservation.domain.reservation.Reservation;

/**
 * State 패턴 — Reservation 의 생애주기를 상태 객체로 캡슐화한다.
 *
 * <p>모든 구체 상태는 8개 전이 이벤트를 선언하지만, 자신이 허용하는 전이만
 * 실제 로직을 구현하고 나머지는 {@link InvalidStateTransitionException} 을 던진다.
 *
 * <p>Iteration 1 실제 동작 경로:
 *   InitiatedState -> PendingPaymentState -> ConfirmedState
 * 나머지 상태는 declaration 만 제공하고 모든 메서드는 예외 (Iteration 2~4 에서 구현).
 */
public interface ReservationState {

    /** 현재 상태의 사람이 읽을 수 있는 이름 (예: "Confirmed"). */
    String name();

    // --- Reservation 생명주기 8개 이벤트 (디폴트 동작: 전이 거부) ---
    // 교과서 State 그림과 동일하게 abstract class 중간층 없이 «interface» 가 직접 1:n 으로 구체 상태와 연결된다.
    // 각 구체 상태는 자신이 허용하는 전이만 override 하고, 나머지는 아래 디폴트 거부를 사용한다.

    default void enterPassengerInfo(Reservation ctx) {
        throw new InvalidStateTransitionException(name(), "enterPassengerInfo");
    }

    default void processPayment(Reservation ctx) {
        throw new InvalidStateTransitionException(name(), "processPayment");
    }

    default void handlePaymentFailure(Reservation ctx) {
        throw new InvalidStateTransitionException(name(), "handlePaymentFailure");
    }

    default void issueTicket(Reservation ctx) {
        throw new InvalidStateTransitionException(name(), "issueTicket");
    }

    default void requestCancellation(Reservation ctx) {
        throw new InvalidStateTransitionException(name(), "requestCancellation");
    }

    default void confirmCancellation(Reservation ctx) {
        throw new InvalidStateTransitionException(name(), "confirmCancellation");
    }

    default void requestRefund(Reservation ctx) {
        throw new InvalidStateTransitionException(name(), "requestRefund");
    }

    default void processRefundDecision(Reservation ctx, boolean approved) {
        throw new InvalidStateTransitionException(name(), "processRefundDecision");
    }
}
