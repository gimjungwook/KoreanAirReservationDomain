package com.koreanair.reservation.app.fx;

import com.koreanair.reservation.app.sample.SampleData.SeedResult;
import com.koreanair.reservation.control.AuthService;
import com.koreanair.reservation.control.BookingController;
import com.koreanair.reservation.control.BusTicketingService;
import com.koreanair.reservation.control.FlightSearchService;
import com.koreanair.reservation.control.PaymentProcessor;
import com.koreanair.reservation.control.RefundHandler;
import com.koreanair.reservation.control.ReservationLookupService;
import com.koreanair.reservation.control.TicketPurchasePublisher;
import com.koreanair.reservation.domain.bus.BusTicket;
import com.koreanair.reservation.domain.bus.BusTicketRequest;
import com.koreanair.reservation.domain.flight.FlightSchedule;
import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.reservation.Ticket;
import com.koreanair.reservation.domain.user.Member;

/**
 * JavaFX UI 전 화면이 공유하는 의존성 + 세션 상태 보관소.
 *
 * <p>Boundary(JavaFX) 만 교체했을 뿐, Control/Domain 인프라는 Swing 버전과 100% 동일하다.
 * 9개 디자인 패턴(State/Strategy/Observer/Composite/Singleton/Factory/Template/Adapter/Decorator)은
 * 모두 control/domain 계층에 그대로 살아 있고, 이 클래스는 그 서비스들을 호출만 한다.
 */
public final class AppContext {

    // --- Control 서비스 (Swing 버전과 동일 wiring) ---
    public final AuthService auth;
    public final FlightSearchService search;
    public final PaymentProcessor paymentProcessor;
    public final RefundHandler refundHandler;
    public final ReservationLookupService lookupService;
    public final BookingController booking;
    public final TicketPurchasePublisher ticketPublisher;
    public final BusTicketingService busTicketingService;
    public final SeedResult seed;

    // --- 세션 상태 ---
    private Member loggedInMember;
    private Reservation currentReservation;
    private FlightSchedule pendingSchedule;

    public AppContext(AuthService auth,
                      FlightSearchService search,
                      PaymentProcessor paymentProcessor,
                      RefundHandler refundHandler,
                      ReservationLookupService lookupService,
                      BookingController booking,
                      TicketPurchasePublisher ticketPublisher,
                      BusTicketingService busTicketingService,
                      SeedResult seed) {
        this.auth = auth;
        this.search = search;
        this.paymentProcessor = paymentProcessor;
        this.refundHandler = refundHandler;
        this.lookupService = lookupService;
        this.booking = booking;
        this.ticketPublisher = ticketPublisher;
        this.busTicketingService = busTicketingService;
        this.seed = seed;
    }

    public Member loggedInMember() { return loggedInMember; }
    public void setLoggedInMember(Member m) { this.loggedInMember = m; }
    public boolean isSignedIn() { return loggedInMember != null; }

    public Reservation currentReservation() { return currentReservation; }
    public void setCurrentReservation(Reservation r) { this.currentReservation = r; }

    public FlightSchedule pendingSchedule() { return pendingSchedule; }
    public void setPendingSchedule(FlightSchedule s) { this.pendingSchedule = s; }

    public void logout() {
        loggedInMember = null;
        currentReservation = null;
        auth.logout();
    }

    /**
     * Iteration 3 Observer 시연 — e-Ticket 발권 후 TicketPurchasePublisher 로 우등고속 버스티켓을 연계 발매.
     * MainFrame.issueLinkedBusTicket 과 동일 로직.
     */
    public BusTicket issueLinkedBusTicket(Reservation reservation, BusTicketRequest req) {
        if (reservation == null) {
            throw new IllegalArgumentException("발권 대상 예약이 없습니다.");
        }
        if (req == null || req.getOriginCity() == null) {
            throw new IllegalArgumentException("출발 도시를 선택하세요.");
        }
        if (reservation.getTickets().isEmpty()) {
            reservation.issueTicket();
        }
        if (reservation.getTickets().isEmpty()) {
            throw new IllegalStateException("e-Ticket 발급 결과가 없습니다.");
        }
        Ticket airTicket = reservation.getTickets().get(reservation.getTickets().size() - 1);
        int before = busTicketingService.getIssuedTickets().size();
        ticketPublisher.publishTicketIssued(reservation, airTicket, req);
        if (busTicketingService.getIssuedTickets().size() <= before) {
            throw new IllegalStateException("버스티켓 listener가 발매 결과를 만들지 못했습니다.");
        }
        return busTicketingService.getIssuedTickets()
                .get(busTicketingService.getIssuedTickets().size() - 1);
    }
}
