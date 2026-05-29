package com.koreanair.reservation.domain.flight.seatview;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.koreanair.reservation.domain.flight.Seat;

/**
 * DP#9 Decorator — abstract Decorator.
 *
 * <p>하위 클래스는 한 가지 메타(창가/통로/추가 레그룸/라운지 등)를 누적한다.
 */
public abstract class AbstractSeatDecorator implements SeatView {

    protected final SeatView wrapped;

    protected AbstractSeatDecorator(SeatView wrapped) {
        this.wrapped = Objects.requireNonNull(wrapped);
    }

    @Override
    public Seat getSeat() {
        return wrapped.getSeat();
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription() + appendLabel();
    }

    @Override
    public BigDecimal getSurcharge() {
        return wrapped.getSurcharge().add(extraSurcharge());
    }

    @Override
    public List<String> getMetadataLabels() {
        List<String> labels = new ArrayList<>(wrapped.getMetadataLabels());
        String mine = ownLabel();
        if (mine != null && !mine.isEmpty()) {
            labels.add(mine);
        }
        return labels;
    }

    protected abstract String appendLabel();

    protected abstract BigDecimal extraSurcharge();

    protected abstract String ownLabel();
}
