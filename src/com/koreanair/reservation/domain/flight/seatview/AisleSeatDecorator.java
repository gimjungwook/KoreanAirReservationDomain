package com.koreanair.reservation.domain.flight.seatview;

import java.math.BigDecimal;

/**
 * DP#9 Decorator — ConcreteDecorator: 통로 좌석 표시 (요금 0).
 */
public class AisleSeatDecorator extends AbstractSeatDecorator {

    public AisleSeatDecorator(SeatView wrapped) {
        super(wrapped);
    }

    @Override
    protected String appendLabel() {
        return " · 통로";
    }

    @Override
    protected BigDecimal extraSurcharge() {
        return BigDecimal.ZERO;
    }

    @Override
    protected String ownLabel() {
        return "Aisle";
    }
}
