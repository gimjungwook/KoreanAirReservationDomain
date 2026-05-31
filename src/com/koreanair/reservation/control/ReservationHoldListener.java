package com.koreanair.reservation.control;

import com.koreanair.reservation.domain.event.EventListener;
import com.koreanair.reservation.domain.event.SeatHoldExpiredEvent;
import com.koreanair.reservation.domain.flight.Seat;
import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.reservation.state.InvalidStateTransitionException;

/**
 * {@link SeatHoldExpiredEvent} 구독자. Iteration 3.
 *
 * <p>좌석을 해제하고, 관련 Reservation이 PendingPayment 상태이면 자동으로
 * handlePaymentFailure 전이를 트리거하여 Cancelled로 종료한다.
 */
public class ReservationHoldListener implements EventListener {

    /** 교과서 ConcreteObserver 가 관찰하는 ConcreteSubject 역참조(-concreteSubject). */
    private SeatHoldMonitor subject;

    /** 관찰 대상 Subject 주입 — 교과서 ConcreteObserver -> ConcreteSubject 연관. */
    public void setSubject(SeatHoldMonitor subject) {
        this.subject = subject;
    }

    public SeatHoldMonitor getSubject() {
        return subject;
    }

    @Override
    public void update() {
        SeatHoldExpiredEvent e = subject != null ? subject.getState() : null;
        if (e == null) {
            return;
        }
        Seat seat = e.getSeat();
        if (seat != null) {
            seat.release();
            System.out.println("[HOLD-EXPIRY] seat " + seat.getSeatNumber() + " released");
        }
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
            System.out.println("[HOLD-EXPIRY] PNR=" + pnr + " auto-cancelled");
        } catch (InvalidStateTransitionException ignored) {
            System.out.println("[HOLD-EXPIRY] PNR=" + pnr + " 전이 무시 (state="
                    + r.getStateName() + ")");
        }
    }
}
