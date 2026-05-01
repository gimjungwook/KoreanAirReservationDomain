package com.koreanair.reservation.control;

import java.util.ArrayList;
import java.util.List;

import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.user.Member;

/**
 * 예약 조회 — 회원 / 비회원 분기.
 * Iteration 2: in-memory Reservation registry 기반.
 * Iteration 3+ : DB / 외부 GDS 연동.
 */
public class ReservationLookupService {

    private final AuthService authService;

    public ReservationLookupService(AuthService authService) {
        this.authService = authService;
    }

    /** 회원의 모든 예약 조회. */
    public List<Reservation> findByMember(Member member) {
        if (member == null) return new ArrayList<>();
        return new ArrayList<>(member.getReservations());
    }

    /**
     * 비회원 단건 조회 — PNR + 이름 + 이메일 검증 후 Reservation 반환.
     * 검증 실패 시 null.
     */
    public Reservation findByGuestPnr(String pnr, String name, String email) {
        if (authService == null) return null;
        if (!authService.verifyGuest(pnr, name, email)) return null;
        return Reservation.findByPnr(pnr);
    }
}
