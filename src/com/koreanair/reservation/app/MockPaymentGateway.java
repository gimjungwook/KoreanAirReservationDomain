package com.koreanair.reservation.app;

import java.math.BigDecimal;

import com.koreanair.reservation.boundary.PaymentGatewayInterface;
import com.koreanair.reservation.domain.payment.Payment;

/**
 * PG(결제대행사) 연동 Mock — Iteration 1 Walking Skeleton 전용.
 *
 * <p>Iteration 1: 항상 승인 반환. 실제 PG 사 API 연동은 추후 iteration.
 * <p>TODO(iter3): 실패 시나리오 (카드 한도 초과, 네트워크 타임아웃) 시뮬레이션.
 */
public class MockPaymentGateway implements PaymentGatewayInterface {

    @Override
    public Object sendAuthorizationRequest(BigDecimal amount, Object paymentInfo) {
        // TODO(iter3): 실제 PG API.
        return null;
    }

    @Override
    public Object receiveTransactionResult() {
        return null;
    }

    @Override
    public Object sendRefund(String originalPaymentId, BigDecimal amount) {
        System.out.printf("[PG] 환불 요청 paymentId=%s refund=%s%n", originalPaymentId, amount);
        return null;
    }

    @Override
    public boolean authorize(Payment payment) {
        System.out.printf("[PG] 승인 요청 amount=%s%n", payment.getAmount());
        return true;
    }

    @Override
    public boolean refund(Payment payment, BigDecimal amount) {
        // Iteration 2: in-memory Mock — 항상 환불 승인 (authorize 와 동일한 패턴).
        System.out.println("[PG] Refund authorized: " + amount + " for " + payment.getPaymentId());
        return true;
    }
}
