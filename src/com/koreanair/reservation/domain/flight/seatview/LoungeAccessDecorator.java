package com.koreanair.reservation.domain.flight.seatview;

import java.math.BigDecimal;

/**
 * DP#9 Decorator — ConcreteDecorator: 라운지 액세스 (+80,000원).
 *
 * <p>비즈니스/퍼스트 좌석에는 자동 적용, 이코노미에는 옵션으로 추가.
 */
public class LoungeAccessDecorator extends AbstractSeatDecorator {

    private static final BigDecimal SURCHARGE = new BigDecimal("80000");

    public LoungeAccessDecorator(SeatView wrapped) {
        super(wrapped);
    }

    @Override
    protected String appendLabel() {
        return " · 라운지 이용 (+80,000)";
    }

    @Override
    protected BigDecimal extraSurcharge() {
        return SURCHARGE;
    }

    @Override
    protected String ownLabel() {
        return "Lounge";
    }
}
