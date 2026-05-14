package com.koreanair.reservation.domain.event;

/**
 * Reservation의 State 전이 시 발행. Iteration 3.
 *
 * <p>발행자: Reservation (setState 시점). 구독자: AuditLogListener 등.
 */
public class ReservationStateChangedEvent extends DomainEvent {

    private final String pnr;
    private final String previousState;
    private final String newState;

    public ReservationStateChangedEvent(String pnr, String previousState, String newState) {
        super(pnr);
        this.pnr = pnr;
        this.previousState = previousState;
        this.newState = newState;
    }

    public String getPnr() {
        return pnr;
    }

    public String getPreviousState() {
        return previousState;
    }

    public String getNewState() {
        return newState;
    }
}
