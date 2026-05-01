package com.koreanair.reservation.domain.user;

import java.util.ArrayList;
import java.util.List;

import com.koreanair.reservation.domain.reservation.Reservation;

public class Member extends User {

    private String memberNumber;
    private List<Reservation> reservations = new ArrayList<>();

    public Member() {
    }

    public Member(String memberNumber) {
        this.memberNumber = memberNumber;
    }

    public String getMemberNumber() {
        return memberNumber;
    }

    public void setMemberNumber(String memberNumber) {
        this.memberNumber = memberNumber;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
    }
}
