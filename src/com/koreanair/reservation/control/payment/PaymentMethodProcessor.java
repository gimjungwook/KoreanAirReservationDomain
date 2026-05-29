package com.koreanair.reservation.control.payment;

import java.math.BigDecimal;

import com.koreanair.reservation.boundary.PaymentGatewayInterface;
import com.koreanair.reservation.domain.event.EventPublisher;
import com.koreanair.reservation.domain.event.PaymentFailedEvent;
import com.koreanair.reservation.domain.payment.Payment;
import com.koreanair.reservation.domain.payment.PaymentMethod;

/**
 * DP#6 Factory Method — Creator.
 *
 * <p>구체 결제 수단별 ConcreteCreator 가 {@link #createPayment(long)} 을 오버라이드해
 * 자기 종류의 Payment 객체를 생성한다. processCharge() 가 템플릿으로 동작한다.
 *
 * <p>EventPublisher 를 상속해 결제 실패 시 PaymentFailedEvent 를 발행한다 (Observer 연계).
 */
public abstract class PaymentMethodProcessor extends EventPublisher {

    private static long paymentSequence = 1;
    private final PaymentGatewayInterface gateway;

    protected PaymentMethodProcessor(PaymentGatewayInterface gateway) {
        this.gateway = gateway;
    }

    /** Factory Method: 서브클래스에서 자기 method 타입의 Payment 생성. */
    protected abstract Payment createPayment(long amount);

    /** PaymentMethod 메타. 로깅/리포팅용. */
    public abstract PaymentMethod method();

    /**
     * 템플릿 메서드 흐름: createPayment → authorize → pay/fail.
     */
    public Payment processCharge(long amount, String reservationPnr) {
        if (amount <= 0) {
            throw new IllegalArgumentException("결제 금액은 0보다 커야 합니다.");
        }
        Payment payment = createPayment(amount);
        boolean authorized = authorize(payment);
        if (authorized) {
            payment.pay();
        } else {
            payment.fail();
            publish(new PaymentFailedEvent(payment, reservationPnr, declineReason()));
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

    /** Helper: BigDecimal amount 변환. */
    protected Payment basePayment(long amount, PaymentMethod m) {
        Payment p = new Payment();
        p.setPaymentId(nextPaymentId());
        p.setAmount(BigDecimal.valueOf(amount));
        p.setPaymentMethod(m);
        return p;
    }
}
