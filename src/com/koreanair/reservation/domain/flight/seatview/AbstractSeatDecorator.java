package com.koreanair.reservation.domain.flight.seatview;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.koreanair.reservation.domain.flight.Seat;

/**
 * DP#8 Decorator — abstract Decorator.
 *
 * <p>교과서 Decorator 그림과 동일하게 Component(SeatView)를 참조(-component)하고,
 * 모든 연산을 component 로 그대로 전달(forward)한다. 구체 Decorator 는 operation 을
 * override 하여 super 호출 뒤 자신의 addedBehavior 를 더한다(super.operation(); addedBehavior();).
 */
public abstract class AbstractSeatDecorator extends SeatView {

    protected final SeatView component;

    protected AbstractSeatDecorator(SeatView component) {
        this.component = Objects.requireNonNull(component);
    }

    @Override
    public Seat getSeat() {
        return component.getSeat();
    }

    @Override
    public String getDescription() {
        return component.getDescription();
    }

    @Override
    public BigDecimal getSurcharge() {
        return component.getSurcharge();
    }

    @Override
    public List<String> getMetadataLabels() {
        return new ArrayList<>(component.getMetadataLabels());
    }
}
