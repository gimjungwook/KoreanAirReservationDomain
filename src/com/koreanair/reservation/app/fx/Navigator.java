package com.koreanair.reservation.app.fx;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import com.koreanair.reservation.app.fx.screen.CancellationController;
import com.koreanair.reservation.app.fx.screen.ConfirmationController;
import com.koreanair.reservation.app.fx.screen.LoginController;
import com.koreanair.reservation.app.fx.screen.LookupController;
import com.koreanair.reservation.app.fx.screen.PassengerController;
import com.koreanair.reservation.app.fx.screen.PaymentController;
import com.koreanair.reservation.app.fx.screen.SearchController;
import com.koreanair.reservation.app.fx.screen.SeatController;
import com.koreanair.reservation.domain.bus.BusTicket;
import com.koreanair.reservation.domain.flight.FlightSchedule;
import com.koreanair.reservation.domain.payment.Payment;
import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.user.Member;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * 화면 전환의 단일 진입점. Swing 의 MainFrame + CardLayout 역할을 대체한다.
 *
 * <p>각 화면은 FXML(레이아웃) + Controller(로직) 으로 분리되어 있고, 여기서 로드한 뒤
 * {@code bind(nav, ctx)} 로 의존성을 주입하고 화면별 {@code prepare(...)} 로 데이터를 넘긴다.
 * 단계 표시(StepIndicator)와 상태 배지(State 패턴 시연)는 ShellController 가 담당한다.
 */
public final class Navigator {

    public static final int STEP_SEARCH = 0;
    public static final int STEP_PASSENGER = 1;
    public static final int STEP_SEAT = 2;
    public static final int STEP_PAYMENT = 3;
    public static final int STEP_DONE = 4;

    private final AppContext ctx;
    private final ShellController shell;

    public Navigator(AppContext ctx, ShellController shell) {
        this.ctx = ctx;
        this.shell = shell;
        shell.setNavigator(this);
    }

    private <T> Loaded<T> load(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent node = loader.load();
            return new Loaded<>(node, loader.getController());
        } catch (IOException e) {
            throw new IllegalStateException("FXML 로드 실패: " + fxml, e);
        }
    }

    private record Loaded<T>(Parent node, T controller) {}

    // ---- 상태 배지 (State 패턴 시연) ----
    public void updateState(Reservation r) {
        shell.setStateBadge(r != null ? r.getStateName() : null);
    }

    // ---- 화면 전환 ----

    public void showLogin() {
        Loaded<LoginController> l = load("login.fxml");
        l.controller().bind(this, ctx);
        shell.showStep(false);
        shell.setContent(l.node());
    }

    public void showSearch() {
        ctx.setCurrentReservation(null);
        shell.setStateBadge(null);
        Loaded<SearchController> l = load("search.fxml");
        l.controller().bind(this, ctx);
        shell.showStep(true);
        shell.setStep(STEP_SEARCH);
        shell.setContent(l.node());
    }

    public void showPassenger(FlightSchedule schedule) {
        Loaded<PassengerController> l = load("passenger.fxml");
        l.controller().bind(this, ctx);
        l.controller().prepare(schedule);
        shell.showStep(true);
        shell.setStep(STEP_PASSENGER);
        shell.setContent(l.node());
    }

    public void showPassengerExisting(Reservation reservation) {
        Loaded<PassengerController> l = load("passenger.fxml");
        l.controller().bind(this, ctx);
        l.controller().prepareExisting(reservation);
        updateState(reservation);
        shell.showStep(true);
        shell.setStep(STEP_PASSENGER);
        shell.setContent(l.node());
    }

    public void showSeat(Reservation reservation) {
        Loaded<SeatController> l = load("seat.fxml");
        l.controller().bind(this, ctx);
        l.controller().prepare(reservation);
        updateState(reservation);
        shell.showStep(true);
        shell.setStep(STEP_SEAT);
        shell.setContent(l.node());
    }

    public void showPayment(Reservation reservation) {
        Loaded<PaymentController> l = load("payment.fxml");
        l.controller().bind(this, ctx);
        l.controller().prepare(reservation, ctx.seed.defaultFareRule);
        updateState(reservation);
        shell.showStep(true);
        shell.setStep(STEP_PAYMENT);
        shell.setContent(l.node());
    }

    public void showConfirmation(Reservation reservation, Payment payment, BusTicket busTicket) {
        Loaded<ConfirmationController> l = load("confirmation.fxml");
        l.controller().bind(this, ctx);
        l.controller().prepare(reservation, payment, busTicket);
        updateState(reservation);
        shell.showStep(true);
        shell.setStep(STEP_DONE);
        shell.setContent(l.node());
    }

    public void showLookup() {
        Loaded<LookupController> l = load("lookup.fxml");
        l.controller().bind(this, ctx);
        l.controller().refresh();
        shell.showStep(false);
        shell.setContent(l.node());
    }

    public void showCancellation(Reservation reservation) {
        ctx.setCurrentReservation(reservation);
        Loaded<CancellationController> l = load("cancellation.fxml");
        l.controller().bind(this, ctx);
        l.controller().prepare(reservation);
        updateState(reservation);
        shell.showStep(false);
        shell.setContent(l.node());
    }

    public void showRefund(String pnr, BigDecimal amount, String policyName) {
        Loaded<com.koreanair.reservation.app.fx.screen.RefundController> l = load("refund.fxml");
        l.controller().bind(this, ctx);
        l.controller().prepare(pnr, amount, policyName);
        shell.showStep(false);
        shell.setContent(l.node());
    }

    // ---- 세션 / 헤더 콜백 ----

    public void onLoginSuccess(Member m) {
        ctx.setLoggedInMember(m);
        shell.refreshAuth(true);
        FlightSchedule resume = ctx.pendingSchedule();
        if (resume != null) {
            ctx.setPendingSchedule(null);
            showPassenger(resume);
        } else {
            showSearch();
        }
    }

    public void requireSignIn(FlightSchedule rememberSchedule) {
        ctx.setPendingSchedule(rememberSchedule);
        showLogin();
    }

    public void doLogout() {
        ctx.logout();
        shell.refreshAuth(false);
        shell.setStateBadge(null);
        showSearch();
    }

    public void startNewBooking() {
        ctx.setCurrentReservation(null);
        shell.setStateBadge(null);
        showSearch();
    }

    // 회원 예약 목록에 동기화 (Swing MainFrame.onReservationCreated 보강 로직)
    public void registerReservationToMember(Reservation reservation) {
        Member m = ctx.loggedInMember();
        if (m != null && reservation != null && !m.getReservations().contains(reservation)) {
            m.addReservation(reservation);
        }
    }

    public List<FlightSchedule> noop() { return List.of(); }
}
