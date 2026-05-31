package com.koreanair.reservation.domain.flight.seatview;

import java.math.BigDecimal;
import java.util.List;

/**
 * DP#9 Decorator — ConcreteDecorator: 라운지 액세스 (+80,000원).
 *
 * <p>비즈니스/퍼스트 좌석에는 자동 적용, 이코노미에는 옵션으로 추가.
 */
public class LoungeAccessDecorator extends AbstractSeatDecorator {

    private static final BigDecimal SURCHARGE = new BigDecimal("80000");

    public LoungeAccessDecorator(SeatView component) {
        super(component);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + addedLabel();
    }

    @Override
    public BigDecimal getSurcharge() {
        return super.getSurcharge().add(SURCHARGE);
    }

    @Override
    public List<String> getMetadataLabels() {
        List<String> labels = super.getMetadataLabels();
        labels.add("Lounge");
        return labels;
    }

    private String addedLabel() {
        return " · 라운지 이용 (+80,000)";
    }
}
