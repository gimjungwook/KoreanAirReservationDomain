package com.koreanair.reservation.app.swing;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.math.BigDecimal;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.koreanair.reservation.app.sample.SampleData.SeedResult;
import com.koreanair.reservation.control.AuthService;
import com.koreanair.reservation.control.BookingController;
import com.koreanair.reservation.control.FlightSearchService;
import com.koreanair.reservation.control.PaymentProcessor;
import com.koreanair.reservation.control.RefundHandler;
import com.koreanair.reservation.control.ReservationLookupService;
import com.koreanair.reservation.control.TicketPurchasePublisher;
import com.koreanair.reservation.control.BusTicketingService;
import com.koreanair.reservation.domain.bus.BusCity;
import com.koreanair.reservation.domain.bus.BusTicket;
import com.koreanair.reservation.domain.flight.FlightSchedule;
import com.koreanair.reservation.domain.payment.Payment;
import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.reservation.Ticket;
import com.koreanair.reservation.domain.user.Member;

public class MainFrame extends JFrame {

    private static final String CARD_LOGIN = "login";
    private static final String CARD_SEARCH = "search";
    private static final String CARD_PASSENGER = "passenger";
    private static final String CARD_PAYMENT = "payment";
    private static final String CARD_CONFIRMATION = "confirmation";
    private static final String CARD_LOOKUP = "lookup";
    private static final String CARD_SEAT = "seatSelection";
    private static final String CARD_CANCELLATION = "cancellation";
    private static final String CARD_REFUND = "refund";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);
    private final StateBadge stateBadge = new StateBadge();
    private final JPanel headerPanel = new JPanel(new BorderLayout());
    private final StepIndicator stepIndicator = new StepIndicator();
    private final JPanel stepBar = new JPanel(new BorderLayout());

    {
        cards.setOpaque(true);
        cards.setBackground(ModernUI.BACKGROUND);
    }

    private final AuthService authService;
    private final FlightSearchService flightSearch;
    private final BookingController booking;
    private final RefundHandler refundHandler;
    private final ReservationLookupService lookupService;
    private final TicketPurchasePublisher ticketPublisher;
    private final BusTicketingService busTicketingService;
    private final SwingReservationUI ui;
    private final SeedResult seed;

    private final LoginPanel loginPanel;
    private final SearchPanel searchPanel;
    private final PassengerPanel passengerPanel;
    private final PaymentPanel paymentPanel;
    private final ConfirmationPanel confirmationPanel;
    private final LookupPanel lookupPanel;
    private final SeatSelectionPanel seatSelectionPanel;
    private final CancellationPanel cancellationPanel;
    private final RefundPanel refundPanel;

    private final JButton homeNavButton = new JButton("홈");
    private final JButton lookupNavButton = new JButton("예약 조회");
    private final JButton logoutNavButton = new JButton("로그아웃");
    private final JButton loginNavButton = new JButton("로그인");

    private Member loggedInMember;
    @SuppressWarnings("unused")
    private Reservation currentReservation;
    private com.koreanair.reservation.domain.flight.FlightSchedule pendingSchedule;

    public MainFrame(AuthService authService,
                     FlightSearchService flightSearch,
                     PaymentProcessor paymentProcessor,
                     BookingController booking,
                     RefundHandler refundHandler,
                     ReservationLookupService lookupService,
                     SwingReservationUI ui,
                     SeedResult seed,
                     TicketPurchasePublisher ticketPublisher,
                     BusTicketingService busTicketingService) {
        super("대한항공 예약 시스템");
        this.authService = authService;
        this.flightSearch = flightSearch;
        this.booking = booking;
        this.refundHandler = refundHandler;
        this.lookupService = lookupService;
        this.ticketPublisher = ticketPublisher;
        this.busTicketingService = busTicketingService;
        this.ui = ui;
        this.seed = seed;

        setLayout(new BorderLayout());

        buildHeader();
        buildStepBar();
        JPanel topStack = new JPanel(new BorderLayout());
        topStack.add(headerPanel, BorderLayout.NORTH);
        topStack.add(stepBar, BorderLayout.CENTER);
        add(topStack, BorderLayout.NORTH);
        add(cards, BorderLayout.CENTER);

        loginPanel = new LoginPanel(this, authService, ui);
        searchPanel = new SearchPanel(this, booking, ui);
        passengerPanel = new PassengerPanel(this, booking, ui);
        paymentPanel = new PaymentPanel(this, booking, ui);
        confirmationPanel = new ConfirmationPanel(this, busTicketingService);
        lookupPanel = new LookupPanel(this, booking, lookupService, authService);
        seatSelectionPanel = new SeatSelectionPanel(this, booking);
        cancellationPanel = new CancellationPanel(this, booking, refundHandler);
        refundPanel = new RefundPanel(this);

        cards.add(loginPanel, CARD_LOGIN);
        cards.add(searchPanel, CARD_SEARCH);
        cards.add(passengerPanel, CARD_PASSENGER);
        cards.add(paymentPanel, CARD_PAYMENT);
        cards.add(confirmationPanel, CARD_CONFIRMATION);
        cards.add(lookupPanel, CARD_LOOKUP);
        cards.add(seatSelectionPanel, CARD_SEAT);
        cards.add(cancellationPanel, CARD_CANCELLATION);
        cards.add(refundPanel, CARD_REFUND);

        stateBadge.reset();
        ui.setParent(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1080, 760));
        pack();
        setLocationRelativeTo(null);
    }

    private void buildStepBar() {
        stepBar.setBackground(Color.WHITE);
        stepBar.setOpaque(true);
        stepBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ModernUI.BORDER));
        stepBar.add(stepIndicator, BorderLayout.CENTER);
    }

    /** Iteration 1 backward-compat 생성자. RefundHandler / ReservationLookupService 미주입 시 사용. */
    public MainFrame(AuthService authService,
                     FlightSearchService flightSearch,
                     PaymentProcessor paymentProcessor,
                     BookingController booking,
                     SwingReservationUI ui,
                     SeedResult seed) {
        this(authService, flightSearch, paymentProcessor, booking,
                new RefundHandler(),
                new ReservationLookupService(authService),
                ui, seed,
                new TicketPurchasePublisher(),
                new BusTicketingService());
    }

    private void buildHeader() {
        headerPanel.setBackground(ModernUI.PRIMARY);
        headerPanel.setOpaque(true);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(ModernUI.PRIMARY);
        leftPanel.setOpaque(true);
        leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 0));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 0));

        JLabel logo = new JLabel("KOREAN AIR", SwingConstants.CENTER);
        logo.setFont(ModernUI.FONT_HEADING);
        logo.setForeground(Color.WHITE);
        logo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 0, Color.WHITE),
                BorderFactory.createEmptyBorder(0, 0, 0, 12)));

        JLabel title = new JLabel("예약");
        title.setFont(ModernUI.FONT_BODY_BOLD);
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Reservation");
        subtitle.setFont(ModernUI.FONT_SMALL);
        subtitle.setForeground(new Color(0xDF, 0xE8, 0xF7));

        leftPanel.add(logo);
        leftPanel.add(title);
        leftPanel.add(subtitle);

        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(ModernUI.PRIMARY);
        rightPanel.setOpaque(true);
        rightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 20));

        styleNavButton(homeNavButton);
        homeNavButton.addActionListener(e -> startNewBooking());
        rightPanel.add(homeNavButton);

        styleNavButton(lookupNavButton);
        lookupNavButton.setText("내 예약");
        lookupNavButton.addActionListener(e -> showLookup());
        rightPanel.add(lookupNavButton);

        styleNavButton(logoutNavButton);
        logoutNavButton.addActionListener(e -> doLogout());
        rightPanel.add(logoutNavButton);

        styleNavButton(loginNavButton);
        loginNavButton.addActionListener(e -> showLogin());
        rightPanel.add(loginNavButton);

        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        // 비로그인 상태: 내 예약·로그아웃 숨김
        refreshHeaderForAuth();
    }

    private void refreshHeaderForAuth() {
        boolean signedIn = loggedInMember != null;
        lookupNavButton.setVisible(signedIn);
        logoutNavButton.setVisible(signedIn);
        loginNavButton.setVisible(!signedIn);
    }

    private void doLogout() {
        this.loggedInMember = null;
        this.currentReservation = null;
        authService.logout();
        stateBadge.reset();
        refreshHeaderForAuth();
        showSearch();
    }

    private void styleNavButton(JButton btn) {
        btn.setFont(ModernUI.FONT_SMALL);
        btn.setForeground(Color.WHITE);
        btn.setBackground(ModernUI.PRIMARY_HOVER);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCC, 0xE4, 0xFF), 1),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        btn.setFocusPainted(false);
        btn.setFocusable(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void showLogin() {
        stepBar.setVisible(false);
        cardLayout.show(cards, CARD_LOGIN);
    }
    public void showSearch() {
        stepBar.setVisible(true);
        stepIndicator.setCurrentStep(StepIndicator.STEP_SEARCH);
        cardLayout.show(cards, CARD_SEARCH);
    }
    public void showPassenger() {
        stepBar.setVisible(true);
        stepIndicator.setCurrentStep(StepIndicator.STEP_PASSENGER);
        cardLayout.show(cards, CARD_PASSENGER);
    }
    public void showPayment() {
        stepBar.setVisible(true);
        stepIndicator.setCurrentStep(StepIndicator.STEP_PAYMENT);
        cardLayout.show(cards, CARD_PAYMENT);
    }
    public void showConfirmation() {
        stepBar.setVisible(true);
        stepIndicator.setCurrentStep(StepIndicator.STEP_DONE);
        cardLayout.show(cards, CARD_CONFIRMATION);
    }

    public void showLookup() {
        lookupPanel.refresh();
        cardLayout.show(cards, CARD_LOOKUP);
    }

    public void showSeatSelection(Reservation reservation) {
        seatSelectionPanel.setReservation(reservation);
        stepBar.setVisible(true);
        stepIndicator.setCurrentStep(StepIndicator.STEP_SEAT);
        cardLayout.show(cards, CARD_SEAT);
    }

    public void showCancellation(Reservation reservation) {
        this.currentReservation = reservation;
        if (reservation != null) stateBadge.setCurrentState(reservation.getStateName());
        cancellationPanel.setReservation(reservation);
        cardLayout.show(cards, CARD_CANCELLATION);
    }

    public void showRefund(String pnr, BigDecimal amount, String policyName) {
        refundPanel.setRefundInfo(pnr, amount, policyName);
        cardLayout.show(cards, CARD_REFUND);
    }

    public void onLoginSuccess(Member m) {
        this.loggedInMember = m;
        stateBadge.reset();
        refreshHeaderForAuth();
        if (pendingSchedule != null) {
            com.koreanair.reservation.domain.flight.FlightSchedule resume = pendingSchedule;
            pendingSchedule = null;
            onFlightSelected(resume);
        } else {
            showSearch();
        }
    }

    public boolean isSignedIn() {
        return loggedInMember != null;
    }

    public void requireSignIn(com.koreanair.reservation.domain.flight.FlightSchedule rememberSchedule) {
        this.pendingSchedule = rememberSchedule;
        showLogin();
    }

    public void onFlightSelected(FlightSchedule selected) {
        passengerPanel.prepare(selected, loggedInMember);
        showPassenger();
    }

    public void onRoundTripSelected(FlightSchedule outbound, FlightSchedule inbound) {
        // 왕복: 가는편 기준으로 passenger 준비. 두 segment는 reservation 만들 때 합쳐 사용.
        passengerPanel.prepareRoundTrip(outbound, inbound, loggedInMember);
        showPassenger();
    }

    public void onMultiCitySelected(java.util.List<FlightSchedule> segments) {
        passengerPanel.prepareMultiCity(segments, loggedInMember);
        showPassenger();
    }

    public void continueReservation(Reservation reservation) {
        this.currentReservation = reservation;
        if (reservation != null) {
            stateBadge.setCurrentState(reservation.getStateName());
        }
        passengerPanel.prepareExisting(reservation, loggedInMember);
        showPassenger();
    }

    public void onReservationCreated(Reservation reservation) {
        this.currentReservation = reservation;
        if (reservation != null) {
            stateBadge.setCurrentState(reservation.getStateName());
            // Iteration 2: PNR -> Reservation 레지스트리 + 회원의 예약 컬렉션에 동기화.
            // (BookingController.initiateBooking 은 setRequester 만 받으므로 여기서 보강.)
            if (loggedInMember != null && !loggedInMember.getReservations().contains(reservation)) {
                loggedInMember.addReservation(reservation);
            }
        }
    }

    public void onPassengerInfoEntered(Reservation reservation) {
        this.currentReservation = reservation;
        if (reservation != null) stateBadge.setCurrentState(reservation.getStateName());
        // Iteration 4: 결제 전 좌석 선택 단계 삽입.
        showSeatSelection(reservation);
    }

    /** Iteration 4: SeatSelectionPanel 에서 좌석 확정 후 호출. */
    public void onSeatAssigned(Reservation reservation) {
        this.currentReservation = reservation;
        if (reservation != null) stateBadge.setCurrentState(reservation.getStateName());
        paymentPanel.prepare(reservation, seed.defaultFareRule);
        showPayment();
    }

    public void onPaymentConfirmed(Reservation reservation, Payment payment) {
        this.currentReservation = reservation;
        if (reservation != null) stateBadge.setCurrentState(reservation.getStateName());
        // iter4: PaymentPanel에서 셔틀 add-on 선택했다면 발권 자동 트리거.
        com.koreanair.reservation.domain.bus.BusTicketRequest req = paymentPanel.getBusTicketRequest();
        com.koreanair.reservation.domain.bus.BusTicket bus = null;
        if (req != null && req.getOriginCity() != null) {
            try {
                bus = issueLinkedBusTicket(reservation, req);
            } catch (Exception ex) {
                System.out.println("[BUS] issue failed at payment confirm: " + ex.getMessage());
            }
        }
        confirmationPanel.prepare(reservation, payment, bus);
        showConfirmation();
    }

    public void syncReservationState(Reservation reservation) {
        this.currentReservation = reservation;
        if (reservation != null) {
            stateBadge.setCurrentState(reservation.getStateName());
        }
    }

    public BusTicket issueLinkedBusTicket(Reservation reservation, BusCity city) {
        return issueLinkedBusTicket(reservation,
                new com.koreanair.reservation.domain.bus.BusTicketRequest(city, null, null));
    }

    /** Iteration 4: 좌석·스케줄 포함 BusTicketRequest 발권. */
    public BusTicket issueLinkedBusTicket(Reservation reservation,
                                          com.koreanair.reservation.domain.bus.BusTicketRequest req) {
        if (reservation == null) {
            throw new IllegalArgumentException("발권 대상 예약이 없습니다.");
        }
        if (req == null || req.getOriginCity() == null) {
            throw new IllegalArgumentException("출발 도시를 선택하세요.");
        }
        if (reservation.getTickets().isEmpty()) {
            reservation.issueTicket();
            stateBadge.setCurrentState(reservation.getStateName());
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

    public BusTicketingService getBusTicketingService() {
        return busTicketingService;
    }

    public void reset() {
        this.currentReservation = null;
        this.loggedInMember = null;
        authService.logout();
        stateBadge.reset();
        showLogin();
        loginPanel.focusFirst();
    }

    public void startNewBooking() {
        this.currentReservation = null;
        stateBadge.reset();
        showSearch();
    }

    public SeedResult seed() { return seed; }
    public Reservation currentReservation() { return currentReservation; }
    public FlightSearchService flightSearch() { return flightSearch; }
    public BookingController booking() { return booking; }
    public RefundHandler refundHandler() { return refundHandler; }
    public ReservationLookupService lookupService() { return lookupService; }
}
