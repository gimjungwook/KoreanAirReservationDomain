package com.koreanair.reservation.control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.koreanair.reservation.domain.reservation.Reservation;

/**
 * Reservation 인메모리 레지스트리. Iteration 3 신규.
 *
 * <p>Iteration 2까지는 {@link Reservation#findByPnr(String)}이 static 맵을 직접 보유했다.
 * iter3에서는 FlightStatus 전파 listener가 "활성 Reservation 전수 순회"가 필요해서
 * 별도 레지스트리로 분리한다. PNR 단건 조회는 기존 API를 유지한다.
 */
public class ReservationRegistry {

    public static final ReservationRegistry DEFAULT = new ReservationRegistry();

    private final Map<String, Reservation> map = new LinkedHashMap<>();

    public void register(Reservation reservation) {
        if (reservation == null || reservation.getPnrNumber() == null) {
            return;
        }
        map.put(reservation.getPnrNumber(), reservation);
    }

    public Reservation findByPnr(String pnr) {
        if (pnr == null) {
            return null;
        }
        return map.get(pnr);
    }

    public Collection<Reservation> all() {
        return new ArrayList<>(map.values());
    }

    public void clear() {
        map.clear();
    }

    public int size() {
        return map.size();
    }
}
