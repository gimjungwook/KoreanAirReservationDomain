package com.koreanair.reservation.control.payment;

import com.koreanair.reservation.boundary.PaymentGatewayInterface;
import com.koreanair.reservation.domain.payment.Payment;
import com.koreanair.reservation.domain.payment.BankTransferPayment;
import com.koreanair.reservation.domain.payment.PaymentMethod;

/** DP#5 Factory Method — ConcreteCreator: 계좌이체. */
public class BankTransferPaymentProcessor extends PaymentMethodProcessor {

    public BankTransferPaymentProcessor(PaymentGatewayInterface gateway) {
        super(gateway);
    }

    @Override
    public Payment createPayment() {
        return new BankTransferPayment();
    }

    @Override
    public PaymentMethod method() {
        return PaymentMethod.BANK_TRANSFER;
    }

    @Override
    protected String declineReason() {
        return "bank-transfer-timeout";
    }
}
