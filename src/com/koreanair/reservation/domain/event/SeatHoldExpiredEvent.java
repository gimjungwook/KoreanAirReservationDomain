package com.koreanair.reservation.domain.event;

import com.koreanair.reservation.domain.flight.Seat;

/**
 * 좌석 hold 15분이 만료될 때 발행. Iteration 3.
 *
 * <p>발행자: SeatHoldMonitor. 구독자: ReservationHoldListener (좌석 해제 + Reservation 알림).
 */
public class SeatHoldExpiredEvent extends DomainEvent {

    private final Seat seat;
    private final String reservationPnr;

    public SeatHoldExpiredEvent(Seat seat, String reservationPnr) {
        super(seat != null ? seat.getSeatNumber() : "unknown");
        this.seat = seat;
        this.reservationPnr = reservationPnr;
    }

    public Seat getSeat() {
        return seat;
    }

    public String getReservationPnr() {
        return reservationPnr;
    }
}
