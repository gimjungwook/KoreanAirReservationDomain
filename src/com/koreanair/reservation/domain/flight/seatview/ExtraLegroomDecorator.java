package com.koreanair.reservation.domain.flight.seatview;

import java.math.BigDecimal;
import java.util.List;

/**
 * DP#9 Decorator — ConcreteDecorator: 프리미엄 레그룸 (+50,000원).
 *
 * <p>operation override: super(=하위 체인) 호출 뒤 자신의 추가 요금/라벨(addedBehavior)을 더한다.
 */
public class ExtraLegroomDecorator extends AbstractSeatDecorator {

    private static final BigDecimal SURCHARGE = new BigDecimal("50000");

    public ExtraLegroomDecorator(SeatView component) {
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
        labels.add("ExtraLegroom");
        return labels;
    }

    private String addedLabel() {
        return " · 프리미엄 레그룸 (+50,000)";
    }
}
