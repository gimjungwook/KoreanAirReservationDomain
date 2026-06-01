package com.koreanair.reservation.domain.payment;

/** DP#5 Factory Method — ConcreteProduct: Apple Pay 결제. 결제 수단은 서브클래스가 고정한다(setter 불필요). */
public class ApplePayPayment extends Payment {
    public ApplePayPayment() {
        super(PaymentMethod.APPLE_PAY);
    }
}
