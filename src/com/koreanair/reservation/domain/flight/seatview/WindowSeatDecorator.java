package com.koreanair.reservation.domain.flight.seatview;

import java.util.List;

/**
 * DP#8 Decorator — ConcreteDecorator: 창가 좌석 표시 (요금 0).
 *
 * <p>operation 을 override 하여 super(=component 로 전달) 호출 뒤 자신의 addedBehavior 를 더한다.
 */
public class WindowSeatDecorator extends AbstractSeatDecorator {

    public WindowSeatDecorator(SeatView component) {
        super(component);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + addedBehavior();
    }

    @Override
    public List<String> getMetadataLabels() {
        List<String> labels = super.getMetadataLabels();
        labels.add("Window");
        return labels;
    }

    /** addedBehavior — 창가 라벨. 요금은 0 이므로 getSurcharge 는 super 전달을 그대로 사용. */
    private String addedBehavior() {
        return " · 창가";
    }
}
