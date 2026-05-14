package com.koreanair.reservation.control;

import com.koreanair.reservation.domain.event.DomainEvent;
import com.koreanair.reservation.domain.event.EventListener;
import com.koreanair.reservation.domain.event.PaymentFailedEvent;
import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.reservation.state.InvalidStateTransitionException;

/**
 * {@link PaymentFailedEvent} 구독자. Iteration 3.
 *
 * <p>결제 실패 이벤트를 받으면 관련 Reservation의 handlePaymentFailure 전이를 자동 호출한다.
 * Iteration 2까지는 호출부에서 명시적으로 호출했고, iter3에서는 publisher-listener 분리로 변경.
 */
public class ReservationAutoCancelListener implements EventListener {

    @Override
    public void onEvent(DomainEvent event) {
        if (!(event instanceof PaymentFailedEvent)) {
            return;
        }
        PaymentFailedEvent e = (PaymentFailedEvent) event;
        String pnr = e.getReservationPnr();
        if (pnr == null) {
            return;
        }
        Reservation r = Reservation.findByPnr(pnr);
        if (r == null) {
            return;
        }
        try {
            r.handlePaymentFailure();
            System.out.println("[AUTO-CANCEL] PNR=" + pnr + " reason=" + e.getReason());
        } catch (InvalidStateTransitionException ex) {
            System.out.println("[AUTO-CANCEL] PNR=" + pnr + " 전이 실패 state="
                    + r.getStateName());
        }
    }
}
