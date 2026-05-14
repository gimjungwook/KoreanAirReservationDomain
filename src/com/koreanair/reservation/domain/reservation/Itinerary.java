package com.koreanair.reservation.domain.reservation;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import com.koreanair.reservation.domain.flight.FlightSchedule;

/**
 * Itinerary — 여정. Iteration 3에서 connecting/multi-city 구조 활성화.
 *
 * <p>Trip 종류:
 * <ul>
 *   <li>DIRECT — 1 segment</li>
 *   <li>CONNECTING — 2개 이상 segment, 같은 OD의 환승</li>
 *   <li>MULTI_CITY — 2개 이상 segment, 서로 다른 OD</li>
 * </ul>
 *
 * <p>MCT(Minimum Connection Time, 국내 60분 / 국제 90분)는 connecting trip 생성 시 검증한다.
 */
public class Itinerary {

    public static final Duration DOMESTIC_MCT = Duration.ofMinutes(60);
    public static final Duration INTERNATIONAL_MCT = Duration.ofMinutes(90);

    private String tripType;
    private List<Segment> segments = new ArrayList<>();

    public String getTripType() {
        return tripType;
    }

    public void setTripType(String tripType) {
        this.tripType = tripType;
    }

    public List<Segment> getSegments() {
        return segments;
    }

    public void addSegment(Segment segment) {
        segments.add(segment);
        if (tripType == null) {
            tripType = "DIRECT";
        } else if (segments.size() >= 2) {
            tripType = inferTripType();
        }
    }

    private String inferTripType() {
        if (segments.size() <= 1) {
            return "DIRECT";
        }
        String origin = firstOriginCode();
        String finalDest = lastDestinationCode();
        if (origin != null && origin.equals(finalDest)) {
            return "MULTI_CITY";
        }
        if (isPureConnecting()) {
            return "CONNECTING";
        }
        return "MULTI_CITY";
    }

    private boolean isPureConnecting() {
        for (int i = 0; i < segments.size() - 1; i++) {
            Segment a = segments.get(i);
            Segment b = segments.get(i + 1);
            String aArr = destinationCode(a);
            String bDep = originCode(b);
            if (aArr == null || !aArr.equalsIgnoreCase(bDep)) {
                return false;
            }
        }
        return true;
    }

    private String firstOriginCode() {
        return segments.isEmpty() ? null : originCode(segments.get(0));
    }

    private String lastDestinationCode() {
        return segments.isEmpty() ? null : destinationCode(segments.get(segments.size() - 1));
    }

    private String originCode(Segment s) {
        FlightSchedule fs = s != null ? s.getFlightSchedule() : null;
        if (fs == null || fs.getFlight() == null || fs.getFlight().getRoute() == null
                || fs.getFlight().getRoute().getOrigin() == null) {
            return null;
        }
        return fs.getFlight().getRoute().getOrigin().getAirportCode();
    }

    private String destinationCode(Segment s) {
        FlightSchedule fs = s != null ? s.getFlightSchedule() : null;
        if (fs == null || fs.getFlight() == null || fs.getFlight().getRoute() == null
                || fs.getFlight().getRoute().getDestination() == null) {
            return null;
        }
        return fs.getFlight().getRoute().getDestination().getAirportCode();
    }

    /**
     * Iteration 3: 환승 시간이 MCT 이상인지 검증한다. CONNECTING trip 한정.
     */
    public boolean isConnectionTimeValid(Duration minimumConnectionTime) {
        if (segments.size() < 2 || minimumConnectionTime == null) {
            return true;
        }
        for (int i = 0; i < segments.size() - 1; i++) {
            Segment a = segments.get(i);
            Segment b = segments.get(i + 1);
            if (a.getArrivalTime() == null || b.getDepartureTime() == null) {
                return false;
            }
            Duration layover = Duration.between(a.getArrivalTime(), b.getDepartureTime());
            if (layover.compareTo(minimumConnectionTime) < 0) {
                return false;
            }
        }
        return true;
    }

    public Duration getTotalDuration() {
        if (segments.isEmpty()) {
            return Duration.ZERO;
        }
        Segment first = segments.get(0);
        Segment last = segments.get(segments.size() - 1);
        if (first.getDepartureTime() == null || last.getArrivalTime() == null) {
            return Duration.ZERO;
        }
        return Duration.between(first.getDepartureTime(), last.getArrivalTime());
    }

    public static Itinerary direct(FlightSchedule schedule) {
        Itinerary it = new Itinerary();
        it.tripType = "DIRECT";
        it.addSegment(new Segment(schedule));
        return it;
    }

    public static Itinerary connecting(FlightSchedule first, FlightSchedule second) {
        Itinerary it = new Itinerary();
        it.addSegment(new Segment(first));
        it.addSegment(new Segment(second));
        it.tripType = "CONNECTING";
        return it;
    }

    public static Itinerary multiCity(List<FlightSchedule> schedules) {
        Itinerary it = new Itinerary();
        for (FlightSchedule s : schedules) {
            it.addSegment(new Segment(s));
        }
        it.tripType = "MULTI_CITY";
        return it;
    }
}
