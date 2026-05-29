package com.koreanair.reservation.domain.flight.seatview;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.koreanair.reservation.domain.flight.Seat;

/**
 * DP#9 Decorator — ConcreteComponent.
 *
 * <p>Seat 도메인 객체를 SeatView 인터페이스에 맞춰 노출하는 기본 어댑터.
 * Decorator 체인의 시작점.
 */
public class SeatViewAdapter implements SeatView {

    private final Seat seat;

    public SeatViewAdapter(Seat seat) {
        this.seat = Objects.requireNonNull(seat);
    }

    @Override
    public Seat getSeat() {
        return seat;
    }

    @Override
    public String getDescription() {
        return seat.getSeatNumber() + " (" + seat.getCabinClass() + ")";
    }

    @Override
    public BigDecimal getSurcharge() {
        return BigDecimal.ZERO;
    }

    @Override
    public List<String> getMetadataLabels() {
        return new ArrayList<>();
    }
}
