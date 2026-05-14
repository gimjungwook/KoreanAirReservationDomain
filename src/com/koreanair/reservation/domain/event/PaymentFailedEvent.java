package com.koreanair.reservation.domain.event;

import com.koreanair.reservation.domain.payment.Payment;

/**
 * 결제 실패 시 발행. Iteration 3.
 *
 * <p>발행자: PaymentProcessor. 구독자: ReservationAutoCancelListener
 * (Reservation.handlePaymentFailure 자동 호출).
 */
public class PaymentFailedEvent extends DomainEvent {

    private final Payment payment;
    private final String reservationPnr;
    private final String reason;

    public PaymentFailedEvent(Payment payment, String reservationPnr, String reason) {
        super(payment != null && payment.getPaymentId() != null
                ? String.valueOf(payment.getPaymentId())
                : "unknown");
        this.payment = payment;
        this.reservationPnr = reservationPnr;
        this.reason = reason;
    }

    public Payment getPayment() {
        return payment;
    }

    public String getReservationPnr() {
        return reservationPnr;
    }

    public String getReason() {
        return reason;
    }
}
