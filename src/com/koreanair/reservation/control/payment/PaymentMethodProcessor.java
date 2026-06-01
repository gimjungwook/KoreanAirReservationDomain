package com.koreanair.reservation.control.payment;

import java.math.BigDecimal;

import com.koreanair.reservation.boundary.PaymentGatewayInterface;
import com.koreanair.reservation.domain.payment.Payment;
import com.koreanair.reservation.domain.payment.PaymentMethod;

/**
 * DP#5 Factory Method — Creator.
 *
 * <p>구체 결제 수단별 ConcreteCreator 가 {@link #createPayment()} 을 오버라이드해
 * 자기 종류의 Payment 객체를 생성한다. processCharge() 가 템플릿으로 동작한다.
 *
 * <p>SOLID(SRP): 이 클래스는 Factory Method 의 Creator 역할만 담당한다. Observer 의 Subject 역할은
 * 별도 클래스({@link com.koreanair.reservation.control.PaymentProcessor})가 맡아 결제 실패 이벤트를 발행한다.
 */
public abstract class PaymentMethodProcessor {

    private static long paymentSequence = 1;
    private final PaymentGatewayInterface gateway;

    protected PaymentMethodProcessor(PaymentGatewayInterface gateway) {
        this.gateway = gateway;
    }

    /** 교과서 factoryMethod(): Product — public, 무인자. ConcreteCreator 가 자기 종류의 Payment 를 생성. */
    public abstract Payment createPayment();

    /** PaymentMethod 메타. 로깅/리포팅용. */
    public abstract PaymentMethod method();

    /**
     * 템플릿 메서드 흐름: createPayment → authorize → pay/fail.
     */
    public Payment processCharge(long amount, String reservationPnr) {
        if (amount <= 0) {
            throw new IllegalArgumentException("결제 금액은 0보다 커야 합니다.");
        }
        // anOperation(템플릿): factoryMethod() 로 ConcreteProduct 생성 후 amount/seq 를 찍는다.
        Payment payment = stamp(createPayment(), amount);
        boolean authorized = authorize(payment);
        if (authorized) {
            payment.pay();
        } else {
            payment.fail();
            System.out.println("[PAYMENT] " + method() + " 결제 실패 pnr=" + reservationPnr
                    + " reason=" + declineReason());
        }
        return payment;
    }

    protected boolean authorize(Payment payment) {
        return gateway != null && gateway.authorize(payment);
    }

    protected String declineReason() {
        return "gateway-declined";
    }

    protected long nextPaymentId() {
        return paymentSequence++;
    }

    /** Helper: ConcreteCreator 가 만든 ConcreteProduct 에 paymentId/amount 를 찍어 반환. */
    protected Payment stamp(Payment payment, long amount) {
        payment.setPaymentId(nextPaymentId());
        payment.setAmount(BigDecimal.valueOf(amount));
        return payment;
    }
}
