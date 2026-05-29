package com.koreanair.reservation.control.payment;

import com.koreanair.reservation.boundary.PaymentGatewayInterface;
import com.koreanair.reservation.domain.payment.Payment;
import com.koreanair.reservation.domain.payment.PaymentMethod;

/** DP#6 Factory Method — ConcreteCreator: Apple Pay. */
public class ApplePayPaymentProcessor extends PaymentMethodProcessor {

    public ApplePayPaymentProcessor(PaymentGatewayInterface gateway) {
        super(gateway);
    }

    @Override
    protected Payment createPayment(long amount) {
        return basePayment(amount, PaymentMethod.APPLE_PAY);
    }

    @Override
    public PaymentMethod method() {
        return PaymentMethod.APPLE_PAY;
    }

    @Override
    protected String declineReason() {
        return "applepay-token-expired";
    }
}
