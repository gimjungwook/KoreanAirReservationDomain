package com.koreanair.reservation.app;

import java.time.LocalDate;
import java.util.List;

import com.koreanair.reservation.app.sample.SampleData;
import com.koreanair.reservation.app.sample.SampleData.SeedResult;
import com.koreanair.reservation.boundary.PaymentGatewayInterface;
import com.koreanair.reservation.boundary.ReservationUI;
import com.koreanair.reservation.control.AuthService;
import com.koreanair.reservation.control.BookingController;
import com.koreanair.reservation.control.FlightSearchService;
import com.koreanair.reservation.control.PaymentProcessor;
import com.koreanair.reservation.control.RefundHandler;
import com.koreanair.reservation.control.ReservationLookupService;
import com.koreanair.reservation.domain.flight.FlightSchedule;
import com.koreanair.reservation.domain.passenger.Passenger;
import com.koreanair.reservation.domain.passenger.PassengerType;
import com.koreanair.reservation.domain.payment.Payment;
import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.user.Member;

/**
 * Iteration 1 Walking Skeleton + Iteration 2 취소/환불 데모 드라이버.
 *
 * <p>Iteration 1 Happy Path: 로그인 → 검색 → 선택 → 승객 정보 → 결제 → 확정.
 *   콘솔에 State 전이 2건 (Initiated → PendingPayment, PendingPayment → Confirmed) 이
 *   {@code [STATE] X -> Y} 로 출력된다.
 *
 * <p>Iteration 2 추가 흐름: e-Ticket 발권 → 취소 → 자동 환불.
 *   {@code Confirmed → Ticketed → CancellationRequested → Cancelled → RefundRequested → Refunded}
 *   까지 전이가 모두 출력된다.
 *
 * <p>TODO(iter1 발표 준비): 실패 경로 시연 1건 추가 — gateway 를 fail 로 바꿔
 * {@code PendingPayment → Cancelled} 전이도 보여주면 State 패턴 설명 서사가 완성된다.
 */
public final class App {

    private App() {}

    public static void main(String[] args) {
        // --- 1) 의존성 주입 ---
        AuthService auth = new AuthService();
        FlightSearchService search = new FlightSearchService();
        PaymentGatewayInterface gateway = new MockPaymentGateway();
        PaymentProcessor paymentProcessor = new PaymentProcessor(gateway);
        RefundHandler refundHandler = new RefundHandler(gateway);
        ReservationLookupService lookupService = new ReservationLookupService(auth);
        BookingController booking = new BookingController(
                auth, search, paymentProcessor, refundHandler, lookupService);
        ReservationUI ui = new ConsoleReservationUI();

        // --- 2) 샘플 데이터 seed (회원·공항·항공편·운임 규칙) ---
        SeedResult seed = SampleData.seedAll(auth, search);

        // --- 3) 로그인 ---
        Member me = auth.login("SKY-000-001", "pw1234");
        if (me == null) {
            ui.displayError("로그인 실패");
            return;
        }
        System.out.println("[LOGIN] " + me.getName());

        // --- 4) 검색 ---
        List<FlightSchedule> results = booking.processSearch("ICN", "NRT", LocalDate.now().plusDays(1));
        ui.displaySearchResults(results);
        if (results.isEmpty()) return;

        // --- 5) 선택 (첫 번째 결과) ---
        FlightSchedule selected = results.get(0);
        ui.displayItineraryDetail(selected);
        Reservation reservation = booking.initiateBooking(selected);
        reservation.setRequester(me);
        me.addReservation(reservation);
        System.out.println("[BOOK] 예약 개시: PNR=" + reservation.getPnrNumber()
                + " state=" + reservation.getStateName());

        // --- 6) 승객 정보 입력 ---     (State: Initiated → PendingPayment)
        Passenger passenger = Passenger.create(
                me.getName(), me.getEmail(), "M12345678", LocalDate.of(1999, 1, 1), PassengerType.ADULT);
        booking.setPassengerInfo(reservation, passenger);

        // --- 7) 결제 ---               (State: PendingPayment → Confirmed)
        Payment payment = booking.confirmPayment(reservation, selected.getFareRule(), 450_000L, 50_000L);

        // --- 8) 확정 화면 ---
        ui.displayBookingConfirmation(reservation, payment);

        // --- Iteration 2: e-Ticket 발권 ---
        System.out.println();
        System.out.println("=== Iteration 2: e-Ticket 발권 ===");
        try {
            reservation.issueTicket();   // Confirmed -> Ticketed
        } catch (RuntimeException ex) {
            System.out.println("[TICKET] 발권 실패: " + ex.getMessage());
        }

        // --- Iteration 2: 취소 + 자동 환불 ---
        System.out.println();
        System.out.println("=== Iteration 2: 취소 + 자동 환불 ===");
        try {
            booking.processCancellation(reservation.getReservationNumber());
            // processCancellation 내부 흐름:
            //   requestCancellation -> confirmCancellation -> requestRefund
            //   -> RefundHandler.evaluateRefund -> processRefund -> processRefundDecision(true) -> Refunded
        } catch (RuntimeException ex) {
            System.out.println("[CANCEL] 처리 실패: " + ex.getMessage());
        }
    }
}
