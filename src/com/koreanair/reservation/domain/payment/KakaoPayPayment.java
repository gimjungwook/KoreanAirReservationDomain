package com.koreanair.reservation.domain.payment;

/** DP#5 Factory Method — ConcreteProduct: 카카오페이 결제. 결제 수단은 서브클래스가 고정한다(setter 불필요). */
public class KakaoPayPayment extends Payment {
    public KakaoPayPayment() {
        super(PaymentMethod.KAKAO_PAY);
    }
}
