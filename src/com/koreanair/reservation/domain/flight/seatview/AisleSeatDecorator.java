package com.koreanair.reservation.domain.flight.seatview;

import java.util.List;

/**
 * DP#9 Decorator — ConcreteDecorator: 통로 좌석 표시 (요금 0).
 */
public class AisleSeatDecorator extends AbstractSeatDecorator {

    public AisleSeatDecorator(SeatView component) {
        super(component);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + addedBehavior();
    }

    @Override
    public List<String> getMetadataLabels() {
        List<String> labels = super.getMetadataLabels();
        labels.add("Aisle");
        return labels;
    }

    private String addedBehavior() {
        return " · 통로";
    }
}
