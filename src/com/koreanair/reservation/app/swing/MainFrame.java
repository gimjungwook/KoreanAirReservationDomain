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
import com.koreanair.reservation.domain.flight.FlightSchedule;
import com.koreanair.reservation.domain.payment.Payment;
import com.koreanair.reservation.domain.reservation.Reservation;
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

    {
        cards.setOpaque(true);
        cards.setBackground(ModernUI.BACKGROUND);
    }

    private final AuthService authService;
    private final FlightSearchService flightSearch;
    private final BookingController booking;
    private final RefundHandler refundHandler;
    private final ReservationLookupService lookupService;
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

    private final JButton lookupNavButton = new JButton("예약 조회");

    private Member loggedInMember;
    @SuppressWarnings("unused")
    private Reservation currentReservation;

    public MainFrame(AuthService authService,
                     FlightSearchService flightSearch,
                     PaymentProcessor paymentProcessor,
                     BookingController booking,
                     RefundHandler refundHandler,
                     ReservationLookupService lookupService,
                     SwingReservationUI ui,
                     SeedResult seed) {
        super("대한항공 예약 시스템");
        this.authService = authService;
        this.flightSearch = flightSearch;
        this.booking = booking;
        this.refundHandler = refundHandler;
        this.lookupService = lookupService;
        this.ui = ui;
        this.seed = seed;

        setLayout(new BorderLayout());

        buildHeader();
        add(headerPanel, BorderLayout.NORTH);
        add(cards, BorderLayout.CENTER);

        loginPanel = new LoginPanel(this, authService, ui);
        searchPanel = new SearchPanel(this, booking, ui);
        passengerPanel = new PassengerPanel(this, booking, ui);
        paymentPanel = new PaymentPanel(this, booking, ui);
        confirmationPanel = new ConfirmationPanel(this);
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
        setPreferredSize(new Dimension(900, 640));
        pack();
        setLocationRelativeTo(null);
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
                ui, seed);
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

        JLabel logo = new JLabel("✈", SwingConstants.CENTER);
        logo.setFont(new Font("System", Font.PLAIN, 26));
        logo.setForeground(Color.WHITE);

        JLabel title = new JLabel("대한항공");
        title.setFont(new Font("System", Font.BOLD, 18));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("항공 예약");
        subtitle.setFont(new Font("System", Font.PLAIN, 13));
        subtitle.setForeground(new Color(0xCC, 0xE4, 0xFF));

        leftPanel.add(logo);
        leftPanel.add(title);
        leftPanel.add(subtitle);

        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(ModernUI.PRIMARY);
        rightPanel.setOpaque(true);
        rightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 20));

        // Iteration 2: 헤더에 "예약 조회" 진입 버튼 추가.
        styleNavButton(lookupNavButton);
        lookupNavButton.addActionListener(e -> showLookup());
        rightPanel.add(lookupNavButton);

        rightPanel.add(stateBadge);

        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
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

    public void showLogin() { cardLayout.show(cards, CARD_LOGIN); }
    public void showSearch() { cardLayout.show(cards, CARD_SEARCH); }
    public void showPassenger() { cardLayout.show(cards, CARD_PASSENGER); }
    public void showPayment() { cardLayout.show(cards, CARD_PAYMENT); }
    public void showConfirmation() { cardLayout.show(cards, CARD_CONFIRMATION); }

    public void showLookup() {
        lookupPanel.refresh();
        cardLayout.show(cards, CARD_LOOKUP);
    }

    public void showSeatSelection(Reservation reservation) {
        seatSelectionPanel.setReservation(reservation);
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
        showSearch();
    }

    public void onFlightSelected(FlightSchedule selected) {
        passengerPanel.prepare(selected, loggedInMember);
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
        paymentPanel.prepare(reservation, seed.defaultFareRule);
        showPayment();
    }

    public void onPaymentConfirmed(Reservation reservation, Payment payment) {
        this.currentReservation = reservation;
        if (reservation != null) stateBadge.setCurrentState(reservation.getStateName());
        confirmationPanel.prepare(reservation, payment);
        showConfirmation();
    }

    public void reset() {
        this.currentReservation = null;
        this.loggedInMember = null;
        authService.logout();
        stateBadge.reset();
        showLogin();
        loginPanel.focusFirst();
    }

    public SeedResult seed() { return seed; }
    public FlightSearchService flightSearch() { return flightSearch; }
    public BookingController booking() { return booking; }
    public RefundHandler refundHandler() { return refundHandler; }
    public ReservationLookupService lookupService() { return lookupService; }
}
