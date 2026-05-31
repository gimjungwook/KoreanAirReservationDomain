package com.koreanair.reservation.control.payment;

import com.koreanair.reservation.boundary.PaymentGatewayInterface;
import com.koreanair.reservation.domain.payment.Payment;
import com.koreanair.reservation.domain.payment.CreditCardPayment;
import com.koreanair.reservation.domain.payment.PaymentMethod;

/** DP#6 Factory Method — ConcreteCreator: 신용카드. */
public class CreditCardPaymentProcessor extends PaymentMethodProcessor {

    public CreditCardPaymentProcessor(PaymentGatewayInterface gateway) {
        super(gateway);
    }

    @Override
    public Payment createPayment() {
        return new CreditCardPayment();
    }

    @Override
    public PaymentMethod method() {
        return PaymentMethod.CREDIT_CARD;
    }
}
