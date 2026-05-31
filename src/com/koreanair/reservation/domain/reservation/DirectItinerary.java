package com.koreanair.reservation.domain.reservation;

/** DP#6 Factory Method — ConcreteProduct: 직항 여정. trip type 은 서브클래스가 고정한다. */
public class DirectItinerary extends Itinerary {
    public DirectItinerary() {
        super("DIRECT");
    }
}
