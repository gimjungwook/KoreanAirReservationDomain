package com.koreanair.reservation.domain.flight.seatview;

import java.math.BigDecimal;
import java.util.List;

import com.koreanair.reservation.domain.flight.Seat;

/**
 * DP#9 Decorator — Component.
 *
 * <p>좌석을 "표시/요금" 관점으로 다형화한다. 기본 SeatViewAdapter 가 Seat 본체를 감싸고,
 * Decorator 체인이 추가 라벨·추가 요금·부가 서비스(라운지 등)를 누적한다.
 */
public interface SeatView {

    Seat getSeat();

    String getDescription();

    BigDecimal getSurcharge();

    List<String> getMetadataLabels();
}
