package com.koreanair.reservation.domain.flight.seatview;

import java.math.BigDecimal;

/**
 * DP#9 Decorator — ConcreteDecorator: 프리미엄 레그룸 (+50,000원).
 */
public class ExtraLegroomDecorator extends AbstractSeatDecorator {

    private static final BigDecimal SURCHARGE = new BigDecimal("50000");

    public ExtraLegroomDecorator(SeatView wrapped) {
        super(wrapped);
    }

    @Override
    protected String appendLabel() {
        return " · 프리미엄 레그룸 (+50,000)";
    }

    @Override
    protected BigDecimal extraSurcharge() {
        return SURCHARGE;
    }

    @Override
    protected String ownLabel() {
        return "ExtraLegroom";
    }
}
