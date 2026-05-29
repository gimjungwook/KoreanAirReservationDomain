package com.koreanair.reservation.control.payment;

import com.koreanair.reservation.boundary.PaymentGatewayInterface;
import com.koreanair.reservation.domain.passenger.MileageAccount;
import com.koreanair.reservation.domain.payment.PaymentMethod;

/**
 * DP#6 Factory Method — 적용처 헬퍼.
 *
 * <p>PaymentMethod 에 매핑되는 ConcreteCreator (PaymentMethodProcessor 서브클래스)를 반환한다.
 * 호출자(BookingController, SwingApp)는 이 헬퍼만 알면 되고, 새 결제 수단 추가 시
 * Concrete Processor 클래스 하나 + switch 한 줄만 늘어난다.
 */
public final class PaymentProcessorFactory {

    private PaymentProcessorFactory() {}

    public static PaymentMethodProcessor forMethod(PaymentMethod method,
                                                   PaymentGatewayInterface gateway,
                                                   MileageAccount mileageAccount) {
        if (method == null) {
            throw new IllegalArgumentException("PaymentMethod is required");
        }
        switch (method) {
            case CREDIT_CARD:
                return new CreditCardPaymentProcessor(gateway);
            case DEBIT_CARD:
                return new CreditCardPaymentProcessor(gateway) {
                    @Override
                    public PaymentMethod method() {
                        return PaymentMethod.DEBIT_CARD;
                    }
                };
            case BANK_TRANSFER:
                return new BankTransferPaymentProcessor(gateway);
            case SIMPLE_PAY:
                return new BankTransferPaymentProcessor(gateway) {
                    @Override
                    public PaymentMethod method() {
                        return PaymentMethod.SIMPLE_PAY;
                    }
                };
            case APPLE_PAY:
                return new ApplePayPaymentProcessor(gateway);
            case KAKAO_PAY:
                return new KakaoPayPaymentProcessor(gateway);
            case MILEAGE:
                return new MileagePaymentProcessor(mileageAccount);
            default:
                throw new IllegalArgumentException("Unsupported method: " + method);
        }
    }
}
