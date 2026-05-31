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
public abstract class Itinerary {

    public static final Duration DOMESTIC_MCT = Duration.ofMinutes(60);
    public static final Duration INTERNATIONAL_MCT = Duration.ofMinutes(90);

    private String tripType;
    private List<Segment> segments = new ArrayList<>();

    /** ConcreteProduct(Direct/Connecting/MultiCity Itinerary)가 자기 trip type 을 고정하는 생성자. */
    protected Itinerary(String tripType) {
        this.tripType = tripType;
    }

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
        // tripType 은 ConcreteProduct(Direct/Connecting/MultiCity Itinerary)가 생성자에서 고정한다.
        // 여기서 재추론하면 MULTI_CITY 가 CONNECTING 으로 덮여쓰일 수 있어 재추론하지 않는다.
        segments.add(segment);
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
        Itinerary it = new DirectItinerary();
        it.addSegment(new Segment(schedule));
        return it;
    }

    public static Itinerary connecting(FlightSchedule first, FlightSchedule second) {
        Itinerary it = new ConnectingItinerary();
        it.addSegment(new Segment(first));
        it.addSegment(new Segment(second));
        return it;
    }

    public static Itinerary multiCity(List<FlightSchedule> schedules) {
        Itinerary it = new MultiCityItinerary();
        for (FlightSchedule s : schedules) {
            it.addSegment(new Segment(s));
        }
        return it;
    }
}
