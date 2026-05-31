package com.koreanair.reservation.control;

import java.util.ArrayList;
import java.util.List;

import com.koreanair.reservation.domain.event.DomainEvent;
import com.koreanair.reservation.domain.event.EventListener;
import com.koreanair.reservation.domain.event.FlightStatusChangedEvent;
import com.koreanair.reservation.domain.flight.FlightSchedule;
import com.koreanair.reservation.domain.flight.FlightStatus;
import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.reservation.Segment;

/**
 * {@link FlightStatusChangedEvent} 구독자. Iteration 3.
 *
 * <p>FlightSchedule 상태 변경을 받아 해당 schedule을 itinerary에 포함하는 Reservation 모두에게
 * 알림 + 자동 처리를 수행한다. CANCELLED 전이는 자동 환불 안내, DELAYED는 통지만.
 */
public class AffectedReservationListener implements EventListener {

    private final ReservationRegistry registry;

    public AffectedReservationListener() {
        this(ReservationRegistry.DEFAULT);
    }

    public AffectedReservationListener(ReservationRegistry registry) {
        this.registry = registry != null ? registry : ReservationRegistry.DEFAULT;
    }

    @Override
    public void update(DomainEvent event) {
        if (!(event instanceof FlightStatusChangedEvent)) {
            return;
        }
        FlightStatusChangedEvent e = (FlightStatusChangedEvent) event;
        FlightSchedule schedule = e.getSchedule();
        if (schedule == null) {
            return;
        }
        List<Reservation> affected = findAffected(schedule);
        for (Reservation r : affected) {
            r.evaluateImpactOfFlightStatusChange();
            String tag = e.getNewStatus() == FlightStatus.CANCELLED ? "CANCEL" : "STATUS";
            System.out.println("[FLIGHT-" + tag + "] PNR=" + r.getPnrNumber()
                    + " flight=" + schedule.getFlightNumber()
                    + " " + e.getPreviousStatus() + " -> " + e.getNewStatus());
        }
    }

    private List<Reservation> findAffected(FlightSchedule schedule) {
        // Refactoring#5: 보조 인덱스 O(1) 조회 (이전 iter3 의 O(n) 전수 스캔 대체).
        String flightId = schedule.getFlightNumber();
        if (flightId != null) {
            List<Reservation> indexed = registry.findByFlightId(flightId);
            if (!indexed.isEmpty()) {
                List<Reservation> result = new ArrayList<>();
                for (Reservation r : indexed) {
                    if (r.getItinerary() == null) {
                        continue;
                    }
                    for (Segment s : r.getItinerary().getSegments()) {
                        if (s != null && s.getFlightSchedule() == schedule) {
                            result.add(r);
                            break;
                        }
                    }
                }
                return result;
            }
        }
        // Fallback (인덱스 누락 시 안전망).
        List<Reservation> result = new ArrayList<>();
        for (Reservation r : registry.all()) {
            if (r.getItinerary() == null) {
                continue;
            }
            for (Segment s : r.getItinerary().getSegments()) {
                if (s != null && s.getFlightSchedule() == schedule) {
                    result.add(r);
                    break;
                }
            }
        }
        return result;
    }
}
