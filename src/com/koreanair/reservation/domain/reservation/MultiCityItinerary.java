package com.koreanair.reservation.domain.reservation;

/** DP#6 Factory Method — ConcreteProduct: 다구간 여정. trip type 은 서브클래스가 고정한다. */
public class MultiCityItinerary extends Itinerary {
    public MultiCityItinerary() {
        super("MULTI_CITY");
    }
}
