package com.koreanair.reservation.domain.reservation;

/** DP#5 Factory Method — ConcreteProduct: 환승 여정. trip type 은 서브클래스가 고정한다. */
public class ConnectingItinerary extends Itinerary {
    public ConnectingItinerary() {
        super("CONNECTING");
    }
}
