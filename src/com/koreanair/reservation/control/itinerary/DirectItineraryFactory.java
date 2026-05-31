package com.koreanair.reservation.control.itinerary;

import java.util.List;

import com.koreanair.reservation.domain.flight.FlightSchedule;
import com.koreanair.reservation.domain.reservation.Itinerary;
import com.koreanair.reservation.domain.reservation.DirectItinerary;

/** DP#6 Factory Method — ConcreteCreator: 직항. */
public class DirectItineraryFactory extends ItineraryFactory {

    @Override
    protected Itinerary createItinerary() {
        return new DirectItinerary();
    }

    @Override
    protected void validate(List<FlightSchedule> schedules) {
        if (schedules.size() != 1) {
            throw new IllegalArgumentException("Direct itinerary requires exactly 1 segment");
        }
    }
}
