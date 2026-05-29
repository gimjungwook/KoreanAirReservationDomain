package com.koreanair.reservation.control;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
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

    /**
     * Multi-city 검색. 각 도시쌍은 하루씩 이동하는 데모 일정으로 찾는다.
     *
     * <p>예: ICN → NRT → JFK → LAX, 시작일이 5/27이면
     * ICN-NRT는 5/27, NRT-JFK는 5/28, JFK-LAX는 5/29에서 조회한다.
     * 발표 데모에서는 3차 iteration의 "다도시 여행 + 버스 연계" 진입점으로 사용한다.
     */
    public List<Itinerary> searchMultiCity(List<String> airportCodes, LocalDate startDate, Duration mct) {
        List<Itinerary> result = new ArrayList<>();
        if (flightSearch == null || airportCodes == null || airportCodes.size() < 2 || startDate == null) {
            return result;
        }

        List<List<FlightSchedule>> legOptions = new ArrayList<>();
        for (int i = 0; i < airportCodes.size() - 1; i++) {
            String from = airportCodes.get(i);
            String to = airportCodes.get(i + 1);
            LocalDate legDate = startDate.plusDays(i);
            List<FlightSchedule> options = flightSearch.search(from, to, legDate);
            if (options == null || options.isEmpty()) {
                return result;
            }
            legOptions.add(options);
        }

        buildMultiCityCombinations(result, legOptions, new ArrayList<>(), 0, mct);
        return result;
    }

    /**
     * PPT 데모용 기본 다도시 코스.
     */
    public List<Itinerary> searchDemoMultiCity(LocalDate startDate) {
        return searchMultiCity(Arrays.asList("ICN", "NRT", "JFK", "LAX"), startDate,
                Itinerary.INTERNATIONAL_MCT);
    }

    private void buildMultiCityCombinations(List<Itinerary> result,
                                            List<List<FlightSchedule>> legOptions,
                                            List<FlightSchedule> selected,
                                            int depth,
                                            Duration mct) {
        if (result.size() >= 5) {
            return;
        }
        if (depth == legOptions.size()) {
            Itinerary candidate = Itinerary.multiCity(new ArrayList<>(selected));
            Duration min = mct != null ? mct : Itinerary.INTERNATIONAL_MCT;
            if (candidate.isConnectionTimeValid(min)) {
                result.add(candidate);
            }
            return;
        }
        for (FlightSchedule schedule : legOptions.get(depth)) {
            if (canFollow(selected, schedule, mct)) {
                selected.add(schedule);
                buildMultiCityCombinations(result, legOptions, selected, depth + 1, mct);
                selected.remove(selected.size() - 1);
            }
        }
    }

    private boolean canFollow(List<FlightSchedule> selected, FlightSchedule next, Duration mct) {
        if (selected.isEmpty()) {
            return true;
        }
        FlightSchedule previous = selected.get(selected.size() - 1);
        if (previous == null || next == null
                || previous.getArrivalDateTime() == null || next.getDepartureDateTime() == null) {
            return false;
        }
        Duration min = mct != null ? mct : Itinerary.INTERNATIONAL_MCT;
        return Duration.between(previous.getArrivalDateTime(), next.getDepartureDateTime())
                .compareTo(min) >= 0;
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

    /**
     * iter4: 다도시 검색 — 각 segment 별 가능한 schedule 후보 리스트 반환.
     * 사용자는 각 segment 별 1개씩 골라 multiCity Itinerary 를 만든다.
     */
    public List<List<FlightSchedule>> searchMultiCity(List<String[]> legs) {
        List<List<FlightSchedule>> out = new ArrayList<>();
        if (flightSearch == null) return out;
        for (String[] leg : legs) {
            if (leg == null || leg.length < 3) {
                out.add(new ArrayList<>());
                continue;
            }
            LocalDate d;
            try {
                d = LocalDate.parse(leg[2]);
            } catch (Exception ex) {
                d = null;
            }
            if (d == null) {
                out.add(new ArrayList<>());
                continue;
            }
            out.add(flightSearch.search(leg[0], leg[1], d));
        }
        return out;
    }
}
