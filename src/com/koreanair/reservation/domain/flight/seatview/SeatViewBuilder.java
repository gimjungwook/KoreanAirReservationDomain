package com.koreanair.reservation.domain.flight.seatview;

import com.koreanair.reservation.domain.flight.CabinClass;
import com.koreanair.reservation.domain.flight.Seat;

/**
 * DP#8 Decorator — Client 헬퍼.
 *
 * <p>Seat 의 boolean 필드를 보고 적절한 Decorator 체인을 자동 조립한다.
 * 비즈니스/퍼스트 좌석에는 LoungeAccess 가 기본 포함된다.
 */
public final class SeatViewBuilder {

    private SeatViewBuilder() {}

    public static SeatView decorate(Seat seat) {
        SeatView view = new BaseSeatView(seat);
        if (seat.isWindowSeat()) {
            view = new WindowSeatDecorator(view);
        } else if (seat.isAisleSeat()) {
            view = new AisleSeatDecorator(view);
        }
        if (seat.isExtraLegroom()) {
            view = new ExtraLegroomDecorator(view);
        }
        CabinClass cc = seat.getCabinClass();
        if (cc == CabinClass.BUSINESS || cc == CabinClass.FIRST) {
            view = new LoungeAccessDecorator(view);
        }
        return view;
    }
}
