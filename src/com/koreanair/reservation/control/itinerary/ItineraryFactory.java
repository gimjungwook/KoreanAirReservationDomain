package com.koreanair.reservation.control.itinerary;

import java.util.List;

import com.koreanair.reservation.domain.flight.FlightSchedule;
import com.koreanair.reservation.domain.reservation.Itinerary;
import com.koreanair.reservation.domain.reservation.Segment;

/**
 * DP#5 Factory Method — Creator (abstract).
 *
 * <p>Concrete Creator (Direct/Connecting/MultiCity)가 createItinerary() 와 validate() 를
 * 오버라이드해서 각 trip 종류의 Itinerary 객체를 만든다. build() 는 템플릿 흐름이다.
 */
public abstract class ItineraryFactory {

    /** Template Method: validate → createItinerary → addSegments → postCheck */
    public final Itinerary build(List<FlightSchedule> schedules) {
        if (schedules == null || schedules.isEmpty()) {
            throw new IllegalArgumentException("schedules must not be empty");
        }
        validate(schedules);
        Itinerary itinerary = createItinerary();
        for (FlightSchedule fs : schedules) {
            itinerary.addSegment(new Segment(fs));
        }
        postCheck(itinerary);
        return itinerary;
    }

    /** Factory Method: 서브클래스가 자기 trip type Itinerary 생성. */
    protected abstract Itinerary createItinerary();

    protected abstract void validate(List<FlightSchedule> schedules);

    /** Hook (default no-op). */
    protected void postCheck(Itinerary itinerary) {
    }
}
