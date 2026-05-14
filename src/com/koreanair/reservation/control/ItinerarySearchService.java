package com.koreanair.reservation.control;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.koreanair.reservation.domain.flight.FlightSchedule;
import com.koreanair.reservation.domain.reservation.Itinerary;

/**
 * Iteration 3 신규 — 환승 일정 + multi-city 검색.
 *
 * <p>학습 프로젝트이므로 catalog 전수를 순회해 단순 1-stop connecting 조합만 만든다.
 * 실서비스라면 GDS shopping API를 호출한다.
 */
public class ItinerarySearchService {

    private final FlightSearchService flightSearch;

    public ItinerarySearchService(FlightSearchService flightSearch) {
        this.flightSearch = flightSearch;
    }

    /**
     * 직항 결과를 Itinerary 형태로 래핑.
     */
    public List<Itinerary> searchDirect(String from, String to, LocalDate date) {
        List<Itinerary> result = new ArrayList<>();
        if (flightSearch == null) {
            return result;
        }
        for (FlightSchedule fs : flightSearch.search(from, to, date)) {
            result.add(Itinerary.direct(fs));
        }
        return result;
    }

    /**
     * 1-stop connecting 검색. MCT 검증을 통과한 조합만 반환.
     */
    public List<Itinerary> searchConnecting(String from, String to, LocalDate date, Duration mct) {
        List<Itinerary> result = new ArrayList<>();
        if (flightSearch == null) {
            return result;
        }
        List<FlightSchedule> catalog = flightSearch.getCatalog();
        Duration min = mct != null ? mct : Itinerary.INTERNATIONAL_MCT;
        for (FlightSchedule a : catalog) {
            if (!isFirstLegOf(a, from, date)) {
                continue;
            }
            String hub = destinationCode(a);
            if (hub == null) {
                continue;
            }
            for (FlightSchedule b : catalog) {
                if (a == b) {
                    continue;
                }
                if (!hub.equalsIgnoreCase(originCode(b))) {
                    continue;
                }
                if (!to.equalsIgnoreCase(destinationCode(b))) {
                    continue;
                }
                Itinerary candidate = Itinerary.connecting(a, b);
                if (candidate.isConnectionTimeValid(min)) {
                    result.add(candidate);
                }
            }
        }
        return result;
    }

    private boolean isFirstLegOf(FlightSchedule fs, String from, LocalDate date) {
        if (fs == null || fs.getDepartureDateTime() == null || fs.getFlight() == null
                || fs.getFlight().getRoute() == null
                || fs.getFlight().getRoute().getOrigin() == null) {
            return false;
        }
        String code = fs.getFlight().getRoute().getOrigin().getAirportCode();
        return from.equalsIgnoreCase(code)
                && fs.getDepartureDateTime().toLocalDate().equals(date);
    }

    private String destinationCode(FlightSchedule fs) {
        if (fs == null || fs.getFlight() == null || fs.getFlight().getRoute() == null
                || fs.getFlight().getRoute().getDestination() == null) {
            return null;
        }
        return fs.getFlight().getRoute().getDestination().getAirportCode();
    }

    private String originCode(FlightSchedule fs) {
        if (fs == null || fs.getFlight() == null || fs.getFlight().getRoute() == null
                || fs.getFlight().getRoute().getOrigin() == null) {
            return null;
        }
        return fs.getFlight().getRoute().getOrigin().getAirportCode();
    }
}
