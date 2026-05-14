package com.koreanair.reservation.app;

import com.koreanair.reservation.boundary.MockSkypassInterface;
import com.koreanair.reservation.boundary.PaymentGatewayInterface;
import com.koreanair.reservation.boundary.SkypassInterface;
import com.koreanair.reservation.control.AffectedReservationListener;
import com.koreanair.reservation.control.PaymentProcessor;
import com.koreanair.reservation.control.ReservationAutoCancelListener;
import com.koreanair.reservation.control.ReservationHoldListener;
import com.koreanair.reservation.control.ReservationRegistry;
import com.koreanair.reservation.control.SeatHoldMonitor;
import com.koreanair.reservation.domain.flight.CabinClass;
import com.koreanair.reservation.domain.flight.FlightSchedule;
import com.koreanair.reservation.domain.flight.FlightStatus;
import com.koreanair.reservation.domain.flight.Seat;
import com.koreanair.reservation.domain.passenger.MileageAccount;
import com.koreanair.reservation.domain.payment.Payment;
import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.reservation.Segment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Iteration 3 라이브 시연 러너. Observer 5개 시나리오를 순차 실행하여
 * 실제 콘솔 출력을 만들어 발표용 스크린샷의 근거로 사용한다.
 *
 * <p>실행:
 * <pre>
 *   javac -sourcepath src -d bin $(find src -name "*.java" | grep -v "tools/")
 *   java -cp bin com.koreanair.reservation.app.Iter3DemoRunner
 * </pre>
 */
public class Iter3DemoRunner {

    public static void main(String[] args) {
        System.out.println("==== Iter3 demo runner ====");
        bootSubscribers();
        System.out.println();
        demoSeatHoldExpiry();
        System.out.println();
        demoPaymentFailureAutoCancel();
        System.out.println();
        demoFlightStatusPropagation();
        System.out.println();
        demoMileagePayment();
        System.out.println();
        demoConnectingItinerary();
        System.out.println();
        System.out.println("==== Iter3 demo runner done ====");
    }

    // ─────────────────────────────────────────────
    // Boot — wire Subjects to Listeners
    // ─────────────────────────────────────────────
    private static SeatHoldMonitor monitor;
    private static PaymentProcessor payProc;
    private static FailingGateway failingGateway;

    private static void bootSubscribers() {
        System.out.println("[BOOT] event subscribers loaded");
        monitor = new SeatHoldMonitor();
        monitor.subscribe(new ReservationHoldListener());

        failingGateway = new FailingGateway();
        payProc = new PaymentProcessor(failingGateway);
        payProc.subscribe(new ReservationAutoCancelListener());

        System.out.printf("  SeatHoldMonitor    .subscriberCount = %d%n", monitor.subscriberCount());
        System.out.printf("  PaymentProcessor   .subscriberCount = %d%n", payProc.subscriberCount());
        System.out.println("  → ReservationHoldListener         registered");
        System.out.println("  → ReservationAutoCancelListener   registered");
        System.out.println("  → AffectedReservationListener     attached per FlightSchedule");
    }

    // ─────────────────────────────────────────────
    // SC-01 — Seat hold expiry
    // ─────────────────────────────────────────────
    private static void demoSeatHoldExpiry() {
        System.out.println("--- SC-01 Seat hold expiry (HoldMonitor.sweep) ---");
        Reservation r = newReservation("PNR-DEMO01");
        r.enterPassengerInfo();   // → PendingPayment

        Seat seat = new Seat("12A", CabinClass.ECONOMY);
        seat.hold(15, r.getPnrNumber());
        // 시연용으로 만료 시각을 과거로 강제. 실제 만료까지 기다리지 않는다.
        forceHoldExpiry(seat);
        monitor.track(seat, r.getPnrNumber());

        System.out.printf("[INIT] tracked seat %s (PNR=%s, holdExpiresAt=%s)%n",
                seat.getSeatNumber(), r.getPnrNumber(), seat.getHoldExpiresAt());
        int fired = monitor.sweep();
        System.out.printf("[SWEEP] fired=%d events%n", fired);
        System.out.printf("[RESULT] seat=%s state=%s reservation=%s%n",
                seat.getSeatNumber(), seat.getStatus(), r.getStateName());
    }

    // ─────────────────────────────────────────────
    // SC-02 — Payment failure → auto cancel
    // ─────────────────────────────────────────────
    private static void demoPaymentFailureAutoCancel() {
        System.out.println("--- SC-02 Payment failure auto-cancel ---");
        Reservation r = newReservation("PNR-DEMO02");
        r.enterPassengerInfo();   // → PendingPayment

        failingGateway.declineNext = true;
        Payment p = payProc.processPaymentCharge(320_000L, r.getPnrNumber());
        System.out.printf("[RESULT] payment.status=%s reservation=%s%n",
                p.getStatus(), r.getStateName());
    }

    // ─────────────────────────────────────────────
    // SC-03 — FlightSchedule propagation
    // ─────────────────────────────────────────────
    private static void demoFlightStatusPropagation() {
        System.out.println("--- SC-03 FlightSchedule status propagation ---");
        ReservationRegistry registry = ReservationRegistry.DEFAULT;

        FlightSchedule schedule = new FlightSchedule();
        forceField(schedule, "scheduleId", 9001L);
        com.koreanair.reservation.domain.flight.Flight flight =
                new com.koreanair.reservation.domain.flight.Flight();
        forceField(flight, "flightNumber", "KE001");
        forceField(schedule, "flight", flight);
        schedule.changeStatus(FlightStatus.SCHEDULED);
        schedule.subscribe(new AffectedReservationListener(registry));

        Reservation a = newReservation("PNR-DEMO03A");
        Reservation b = newReservation("PNR-DEMO03B");
        a.getItinerary().addSegment(new Segment(schedule));
        b.getItinerary().addSegment(new Segment(schedule));
        registry.register(a);
        registry.register(b);

        System.out.printf("[ADMIN] changeFlightStatus(%s, CANCELLED)%n", flight.getFlightNumber());
        schedule.changeStatus(FlightStatus.CANCELLED);
        System.out.printf("[RESULT] schedule.status=%s%n", schedule.getStatus());
    }

    // ─────────────────────────────────────────────
    // SC-04 — Mileage payment (success)
    // ─────────────────────────────────────────────
    private static void demoMileagePayment() {
        System.out.println("--- SC-04 Mileage payment ---");
        SkypassInterface skypass = newSkypassMock("SK-DEMO", 50_000);
        MileageAccount acct = new MileageAccount();
        acct.deposit(BigDecimal.valueOf(50_000));

        Reservation r = newReservation("PNR-DEMO04");
        r.enterPassengerInfo();

        System.out.printf("[MILEAGE] balance before = %s%n", acct.getBalance());
        Payment p = payProc.processMileagePayment(acct, 30_000L, r.getPnrNumber());
        System.out.printf("[MILEAGE] balance after  = %s%n", acct.getBalance());
        System.out.printf("[MILEAGE] external skypass.deduct = %s%n",
                skypass.deductMileage("SK-DEMO", 30_000));
        if (p.getStatus() == com.koreanair.reservation.domain.payment.PaymentStatus.PAID) {
            r.addPayment(p);
            r.processPayment();
        }
        System.out.printf("[RESULT] payment=%s reservation=%s%n",
                p.getStatus(), r.getStateName());
    }

    // ─────────────────────────────────────────────
    // SC-05 — Connecting itinerary (MCT 검증)
    // ─────────────────────────────────────────────
    private static void demoConnectingItinerary() {
        System.out.println("--- SC-05 Connecting itinerary (MCT check) ---");
        FlightSchedule legA = leg("KE091", "ICN", "NRT",
                LocalDateTime.of(2026, 6, 12, 9, 0),
                LocalDateTime.of(2026, 6, 12, 11, 30));
        FlightSchedule legB = leg("KE002", "NRT", "JFK",
                LocalDateTime.of(2026, 6, 12, 13, 30),
                LocalDateTime.of(2026, 6, 12, 13, 0));
        FlightSchedule legBClose = leg("KE002X", "NRT", "JFK",
                LocalDateTime.of(2026, 6, 12, 11, 50),
                LocalDateTime.of(2026, 6, 12, 11, 30));

        com.koreanair.reservation.domain.reservation.Itinerary itinOk =
                com.koreanair.reservation.domain.reservation.Itinerary.connecting(legA, legB);
        com.koreanair.reservation.domain.reservation.Itinerary itinTooClose =
                com.koreanair.reservation.domain.reservation.Itinerary.connecting(legA, legBClose);

        java.time.Duration mct = java.time.Duration.ofMinutes(90);
        System.out.printf("[ICN→NRT→JFK 120m layover] valid=%s%n",
                itinOk.isConnectionTimeValid(mct));
        System.out.printf("[ICN→NRT→JFK 20m layover ] valid=%s%n",
                itinTooClose.isConnectionTimeValid(mct));
    }

    // ─────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────
    private static Reservation newReservation(String pnr) {
        Reservation r = new Reservation();
        r.setReservationNumber(pnr);
        return r;
    }

    private static FlightSchedule leg(String flightNumber, String origin, String dest,
                                      LocalDateTime dep, LocalDateTime arr) {
        FlightSchedule s = new FlightSchedule();
        com.koreanair.reservation.domain.flight.Flight flight =
                new com.koreanair.reservation.domain.flight.Flight();
        com.koreanair.reservation.domain.flight.Route route =
                new com.koreanair.reservation.domain.flight.Route();
        com.koreanair.reservation.domain.flight.Airport o =
                new com.koreanair.reservation.domain.flight.Airport();
        com.koreanair.reservation.domain.flight.Airport d =
                new com.koreanair.reservation.domain.flight.Airport();
        forceField(o, "airportCode", origin);
        forceField(d, "airportCode", dest);
        forceField(route, "origin", o);
        forceField(route, "destination", d);
        forceField(flight, "flightNumber", flightNumber);
        forceField(flight, "route", route);
        forceField(s, "flight", flight);
        forceField(s, "departureDateTime", dep);
        forceField(s, "arrivalDateTime", arr);
        forceField(s, "status", FlightStatus.SCHEDULED);
        return s;
    }

    private static SkypassInterface newSkypassMock(String skypassNumber, int balance) {
        MockSkypassInterface m = new MockSkypassInterface();
        m.seed(skypassNumber, "pw", balance);
        return m;
    }

    private static void forceHoldExpiry(Seat seat) {
        try {
            java.lang.reflect.Field f = Seat.class.getDeclaredField("holdExpiresAt");
            f.setAccessible(true);
            f.set(seat, LocalDateTime.now().minusSeconds(1));
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void forceField(Object target, String name, Object value) {
        try {
            java.lang.reflect.Field f = target.getClass().getDeclaredField(name);
            f.setAccessible(true);
            f.set(target, value);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 테스트용 결제 게이트웨이. declineNext=true 한 번만 거절 후 자동 false 리셋.
     */
    static class FailingGateway implements PaymentGatewayInterface {
        boolean declineNext = false;

        @Override
        public Object sendAuthorizationRequest(BigDecimal amount, Object info) { return null; }
        @Override
        public Object receiveTransactionResult() { return null; }
        @Override
        public Object sendRefund(String paymentId, BigDecimal amount) { return null; }

        @Override
        public boolean authorize(Payment payment) {
            if (declineNext) {
                declineNext = false;
                System.out.printf("[GATEWAY] authorize -> false (amount=%s)%n", payment.getAmount());
                return false;
            }
            System.out.printf("[GATEWAY] authorize -> true (amount=%s)%n", payment.getAmount());
            return true;
        }

        @Override
        public boolean refund(Payment payment, BigDecimal amount) { return true; }
    }
}
