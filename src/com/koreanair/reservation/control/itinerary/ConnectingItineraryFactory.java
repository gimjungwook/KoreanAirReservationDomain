package com.koreanair.reservation.control.itinerary;

import java.time.Duration;
import java.util.List;

import com.koreanair.reservation.domain.flight.FlightSchedule;
import com.koreanair.reservation.domain.reservation.Itinerary;
import com.koreanair.reservation.domain.reservation.ConnectingItinerary;

/**
 * DP#5 Factory Method — ConcreteCreator: 환승편 (Connecting).
 *
 * <p>최소 2 segment, 첫 도착 == 다음 출발 동일 공항, MCT 충족 검증.
 */
public class ConnectingItineraryFactory extends ItineraryFactory {

    private final Duration mct;

    public ConnectingItineraryFactory() {
        this(Itinerary.INTERNATIONAL_MCT);
    }

    public ConnectingItineraryFactory(Duration mct) {
        this.mct = mct != null ? mct : Itinerary.INTERNATIONAL_MCT;
    }

    @Override
    protected Itinerary createItinerary() {
        return new ConnectingItinerary();
    }

    @Override
    protected void validate(List<FlightSchedule> schedules) {
        if (schedules.size() < 2) {
            throw new IllegalArgumentException("Connecting itinerary requires 2+ segments");
        }
        for (int i = 0; i < schedules.size() - 1; i++) {
            FlightSchedule a = schedules.get(i);
            FlightSchedule b = schedules.get(i + 1);
            String aArr = a.getFlight().getRoute().getDestination().getAirportCode();
            String bDep = b.getFlight().getRoute().getOrigin().getAirportCode();
            if (!aArr.equalsIgnoreCase(bDep)) {
                throw new IllegalArgumentException("Segment " + i + " arrival " + aArr
                        + " != next departure " + bDep);
            }
        }
    }

    @Override
    protected void postCheck(Itinerary itinerary) {
        if (!itinerary.isConnectionTimeValid(mct)) {
            throw new IllegalStateException("MCT violated (< " + mct.toMinutes() + " min)");
        }
    }
}
