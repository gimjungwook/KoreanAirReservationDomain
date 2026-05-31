package com.koreanair.reservation.control;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.koreanair.reservation.domain.event.EventPublisher;
import com.koreanair.reservation.domain.event.SeatHoldExpiredEvent;
import com.koreanair.reservation.domain.flight.Seat;

/**
 * 좌석 hold 만료 감시자 (Observer 패턴의 Subject). Iteration 3.
 *
 * <p>실서비스라면 스케줄러가 주기적으로 {@link #sweep()}을 호출한다. 본 학습 프로젝트에서는
 * 데모/테스트 코드가 직접 호출한다.
 *
 * <p>등록된 좌석 중 hold가 만료된 좌석에 대해 {@link SeatHoldExpiredEvent}를 발행한다.
 * 좌석 해제는 listener가 수행한다 (책임 분리).
 */
public class SeatHoldMonitor extends EventPublisher {

    private final List<HeldSeat> tracked = new ArrayList<>();

    /** 교과서 ConcreteSubject 의 관찰 상태(-subjectState) — 마지막 좌석 hold 만료 이벤트. */
    private SeatHoldExpiredEvent subjectState;

    /** 교과서 ConcreteSubject.getState(). */
    public SeatHoldExpiredEvent getState() {
        return subjectState;
    }

    /** 교과서 ConcreteSubject.setState(state) — 상태 저장 후 무인자 notifyObservers(). */
    public void setState(SeatHoldExpiredEvent event) {
        this.subjectState = event;
        notifyObservers();
    }

    public void track(Seat seat, String reservationPnr) {
        if (seat == null) {
            return;
        }
        tracked.add(new HeldSeat(seat, reservationPnr));
    }

    public int trackedCount() {
        return tracked.size();
    }

    public int sweep() {
        return sweep(LocalDateTime.now());
    }

    public int sweep(LocalDateTime now) {
        int fired = 0;
        List<HeldSeat> snapshot = new ArrayList<>(tracked);
        for (HeldSeat h : snapshot) {
            if (h.seat.isHoldExpiredAt(now)) {
                setState(new SeatHoldExpiredEvent(h.seat, h.reservationPnr));
                tracked.remove(h);
                fired++;
            }
        }
        return fired;
    }

    private static class HeldSeat {
        final Seat seat;
        final String reservationPnr;

        HeldSeat(Seat seat, String pnr) {
            this.seat = seat;
            this.reservationPnr = pnr;
        }
    }
}
