package com.koreanair.reservation.domain.bus;

import java.util.ArrayList;
import java.util.List;

/**
 * 우등고속 프리미엄 셔틀 좌석 레이아웃 헬퍼.
 *
 * <p>1+2 배열, 9 또는 10 row. 통상 28석 (마지막 row 는 3석).
 */
public final class BusSeatLayout {

    private static final int ROWS = 10;

    private BusSeatLayout() {}

    /**
     * 28-seat 우등고속 1+2 배열 표준 레이아웃 생성.
     * <p>좌측: 통로 옆 1석 (A). 우측: 창가(B) + 통로(C). 마지막 row 는 3석(A·B·C 모두 창가/통로).
     */
    public static List<BusSeat> standardPremium() {
        List<BusSeat> seats = new ArrayList<>();
        for (int row = 1; row <= ROWS; row++) {
            seats.add(new BusSeat(row + "A", false, true));
            if (row == ROWS) {
                seats.add(new BusSeat(row + "B", false, true));
                seats.add(new BusSeat(row + "C", true, false));
            } else {
                seats.add(new BusSeat(row + "B", true, false));
                seats.add(new BusSeat(row + "C", false, true));
            }
        }
        return seats;
    }
}
