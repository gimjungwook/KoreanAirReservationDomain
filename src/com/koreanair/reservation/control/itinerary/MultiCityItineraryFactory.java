package com.koreanair.reservation.control.itinerary;

import java.util.List;

import com.koreanair.reservation.domain.flight.FlightSchedule;
import com.koreanair.reservation.domain.reservation.Itinerary;
import com.koreanair.reservation.domain.reservation.MultiCityItinerary;

/**
 * DP#6 Factory Method — ConcreteCreator: Multi-city (서로 다른 OD 다중 segment).
 *
 * <p>예: ICN → NRT → BKK → ICN. Connecting 처럼 동일 공항 환승 제약은 없음.
 */
public class MultiCityItineraryFactory extends ItineraryFactory {

    @Override
    protected Itinerary createItinerary() {
        return new MultiCityItinerary();
    }

    @Override
    protected void validate(List<FlightSchedule> schedules) {
        if (schedules.size() < 2) {
            throw new IllegalArgumentException("Multi-city itinerary requires 2+ segments");
        }
    }
}
