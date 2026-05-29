package com.koreanair.reservation.control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.koreanair.reservation.domain.flight.FlightSchedule;
import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.reservation.Segment;

/**
 * Reservation 인메모리 레지스트리.
 *
 * <p>Iteration 3: 활성 Reservation 전수 순회용 컬렉션 도입 (FlightStatus 전파 Observer 사용).
 * <p>Iteration 4 Refactoring#5: flightId → reservation set 보조 인덱스 추가.
 * FlightStatusChangedEvent 전파 시 O(n) 전수 스캔 대신 O(1) 인덱스 조회로 대체할 수 있다.
 * <p>register/unregister 시 두 자료구조를 동기화한다.
 */
public class ReservationRegistry {

    public static final ReservationRegistry DEFAULT = new ReservationRegistry();

    private final Map<String, Reservation> map = new LinkedHashMap<>();

    /** Refactoring#5: 보조 인덱스 — flightId → Set<PNR>. */
    private final Map<String, Set<String>> byFlightId = new HashMap<>();

    public void register(Reservation reservation) {
        if (reservation == null || reservation.getPnrNumber() == null) {
            return;
        }
        String pnr = reservation.getPnrNumber();
        Reservation prev = map.put(pnr, reservation);
        if (prev != null) {
            removeFromFlightIndex(prev);
        }
        addToFlightIndex(reservation);
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

    /**
     * Refactoring#5: O(1) 인덱스 조회. flightId 가 변경된 항공편을 점유한 Reservation 집합 반환.
     * <p>AffectedReservationListener 에서 전수 스캔 대신 본 메서드 호출.
     */
    public List<Reservation> findByFlightId(String flightId) {
        if (flightId == null) {
            return Collections.emptyList();
        }
        Set<String> pnrs = byFlightId.get(flightId);
        if (pnrs == null || pnrs.isEmpty()) {
            return Collections.emptyList();
        }
        List<Reservation> result = new ArrayList<>(pnrs.size());
        for (String pnr : pnrs) {
            Reservation r = map.get(pnr);
            if (r != null) {
                result.add(r);
            }
        }
        return result;
    }

    public void clear() {
        map.clear();
        byFlightId.clear();
    }

    public int size() {
        return map.size();
    }

    private void addToFlightIndex(Reservation reservation) {
        for (String fid : extractFlightIds(reservation)) {
            byFlightId.computeIfAbsent(fid, k -> new HashSet<>()).add(reservation.getPnrNumber());
        }
    }

    private void removeFromFlightIndex(Reservation reservation) {
        for (String fid : extractFlightIds(reservation)) {
            Set<String> set = byFlightId.get(fid);
            if (set != null) {
                set.remove(reservation.getPnrNumber());
                if (set.isEmpty()) {
                    byFlightId.remove(fid);
                }
            }
        }
    }

    private Set<String> extractFlightIds(Reservation reservation) {
        Set<String> ids = new HashSet<>();
        if (reservation == null || reservation.getItinerary() == null) {
            return ids;
        }
        for (Segment s : reservation.getItinerary().getSegments()) {
            FlightSchedule fs = s != null ? s.getFlightSchedule() : null;
            if (fs != null && fs.getFlight() != null && fs.getFlight().getFlightNumber() != null) {
                ids.add(fs.getFlight().getFlightNumber());
            }
        }
        return ids;
    }
}
