package com.koreanair.reservation.domain.flight.seatview;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.koreanair.reservation.domain.flight.Seat;

/**
 * DP#8 Decorator — ConcreteComponent.
 *
 * <p>교과서 그림 10-7 의 ConcreteComponent 역할. Seat 도메인 객체를 SeatView(Component) 로
 * 노출하는 기본 구현이며 Decorator 체인의 시작점이 된다. (Adapter 패턴과 혼동을 피하기 위해
 * 이전 이름 SeatViewAdapter 를 BaseSeatView 로 정정 — 이 클래스는 순수 ConcreteComponent 다.)
 */
public class BaseSeatView extends SeatView {

    private final Seat seat;

    public BaseSeatView(Seat seat) {
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
