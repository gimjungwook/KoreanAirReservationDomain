package com.koreanair.reservation.control.payment;

import com.koreanair.reservation.boundary.PaymentGatewayInterface;
import com.koreanair.reservation.domain.payment.Payment;
import com.koreanair.reservation.domain.payment.KakaoPayPayment;
import com.koreanair.reservation.domain.payment.PaymentMethod;

/** DP#5 Factory Method — ConcreteCreator: KakaoPay. */
public class KakaoPayPaymentProcessor extends PaymentMethodProcessor {

    public KakaoPayPaymentProcessor(PaymentGatewayInterface gateway) {
        super(gateway);
    }

    @Override
    public Payment createPayment() {
        return new KakaoPayPayment();
    }

    @Override
    public PaymentMethod method() {
        return PaymentMethod.KAKAO_PAY;
    }

    @Override
    protected String declineReason() {
        return "kakaopay-balance-low";
    }
}
