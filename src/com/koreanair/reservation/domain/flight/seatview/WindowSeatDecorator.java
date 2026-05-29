package com.koreanair.reservation.domain.flight.seatview;

import java.math.BigDecimal;

/**
 * DP#9 Decorator — ConcreteDecorator: 창가 좌석 표시 (요금 0).
 */
public class WindowSeatDecorator extends AbstractSeatDecorator {

    public WindowSeatDecorator(SeatView wrapped) {
        super(wrapped);
    }

    @Override
    protected String appendLabel() {
        return " · 창가";
    }

    @Override
    protected BigDecimal extraSurcharge() {
        return BigDecimal.ZERO;
    }

    @Override
    protected String ownLabel() {
        return "Window";
    }
}
