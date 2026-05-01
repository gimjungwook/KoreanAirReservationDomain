package com.koreanair.reservation.control;

import java.math.BigDecimal;

import com.koreanair.reservation.boundary.PaymentGatewayInterface;
import com.koreanair.reservation.domain.flight.FareRule;
import com.koreanair.reservation.domain.payment.Payment;
import com.koreanair.reservation.domain.payment.PaymentMethod;

/**
 * PaymentProcessor — Control 계층.
 *
 * <p>기존 시그니처는 그대로 보존. Iteration 1 Walking Skeleton 용 메서드는
 * 이름을 달리하여(or 파라미터 타입 차이로) 오버로드 추가.
 *
 * <p>TODO(iter3): applyMileage — MileageAccount·SkypassInterface 연동.
 */
public class PaymentProcessor {

    private PaymentGatewayInterface gateway;
    private static long paymentSequence = 1;

    public PaymentProcessor() {
    }

    /** Walking skeleton 전용 생성자 (gateway 주입). */
    public PaymentProcessor(PaymentGatewayInterface gateway) {
        this.gateway = gateway;
    }

    // --- 기존 시그니처 (보존) ---

    public boolean processPayment(Long reservationId, Object paymentInfo) {
        return reservationId != null;
    }

    public boolean validateFareRule(String fareClass) {
        // TODO(iter2): fareClass 기반 유효성.
        return fareClass != null && !fareClass.isEmpty();
    }

    public BigDecimal calculateTotal(BigDecimal fare, BigDecimal tax, BigDecimal seatSurcharge) {
        return fare.add(tax).add(seatSurcharge);
    }

    public boolean applyMileage(Long reservationId, int mileageAmount) {
        return false;
    }

    // --- Iteration 1 Walking Skeleton 전용 메서드 (신규) ---

    /**
     * FareRule 객체 기반 검증. Iteration 1 에서는 "규칙 객체가 존재하고 fareClass 가 유효한가" 만 확인.
     * TODO(iter2): 운임 클래스별 유효성 (예약 시한, 최소 체류일).
     */
    public boolean validateFareRule(FareRule rule) {
        return rule != null
                && rule.getFareClass() != null
                && !rule.getFareClass().isBlank()
                && rule.getChangeFee() != null
                && rule.getCancellationPenalty() != null
                && rule.getChangeFee().signum() >= 0
                && rule.getCancellationPenalty().signum() >= 0;
    }

    /**
     * 최종 결제 금액 계산 — Iteration 1 은 baseFare + tax 단순 합.
     * TODO(iter3): segments 별 부가세, 좌석 upcharge, 마일리지 차감 반영.
     */
    public long calculateTotalAmount(long baseFare, long tax) {
        if (baseFare <= 0 || tax < 0) {
            throw new IllegalArgumentException("결제 금액이 올바르지 않습니다.");
        }
        return baseFare + tax;
    }

    /**
     * 결제 처리 (단일 금액 기준).
     * @return Payment 객체 (성공 시 PAID, 실패 시 FAILED).
     */
    public Payment processPaymentCharge(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("결제 금액은 0보다 커야 합니다.");
        }
        Payment payment = new Payment();
        payment.setPaymentId(paymentSequence++);
        payment.setAmount(BigDecimal.valueOf(amount));
        payment.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        boolean authorized = gateway != null
                && gateway.authorize(payment);
        if (authorized) {
            payment.pay();
        } else {
            payment.fail();
        }
        return payment;
    }
}
