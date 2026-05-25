package com.koreanair.reservation.app.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.koreanair.reservation.boundary.MockSkypassInterface;
import com.koreanair.reservation.boundary.PaymentGatewayInterface;
import com.koreanair.reservation.boundary.SkypassInterface;
import com.koreanair.reservation.control.AffectedReservationListener;
import com.koreanair.reservation.control.BusTicketPurchaseListener;
import com.koreanair.reservation.control.BusTicketingService;
import com.koreanair.reservation.control.PaymentProcessor;
import com.koreanair.reservation.control.ReservationAutoCancelListener;
import com.koreanair.reservation.control.ReservationHoldListener;
import com.koreanair.reservation.control.ReservationRegistry;
import com.koreanair.reservation.control.SeatHoldMonitor;
import com.koreanair.reservation.control.TicketPurchasePublisher;
import com.koreanair.reservation.domain.bus.BusCity;
import com.koreanair.reservation.domain.flight.CabinClass;
import com.koreanair.reservation.domain.flight.Flight;
import com.koreanair.reservation.domain.flight.FlightSchedule;
import com.koreanair.reservation.domain.flight.FlightStatus;
import com.koreanair.reservation.domain.flight.Seat;
import com.koreanair.reservation.domain.passenger.MileageAccount;
import com.koreanair.reservation.domain.passenger.Passenger;
import com.koreanair.reservation.domain.passenger.PassengerType;
import com.koreanair.reservation.domain.payment.Payment;
import com.koreanair.reservation.domain.payment.PaymentStatus;
import com.koreanair.reservation.domain.reservation.Itinerary;
import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.reservation.Segment;
import com.koreanair.reservation.domain.reservation.Ticket;

public class Iter3DemoPanel extends JPanel {

    private final MainFrame frame;
    private final JTextArea logArea = new JTextArea();
    private final SeatHoldMonitor monitor = new SeatHoldMonitor();
    private final DemoGateway gateway = new DemoGateway();
    private final PaymentProcessor paymentProcessor = new PaymentProcessor(gateway);
    private final TicketPurchasePublisher ticketPublisher = new TicketPurchasePublisher();
    private final BusTicketingService busTicketingService = new BusTicketingService();

    public Iter3DemoPanel(MainFrame frame) {
        super(new BorderLayout());
        this.frame = frame;
        setBackground(ModernUI.BACKGROUND);
        setOpaque(true);
        bootSubscribers();
        buildLayout();
        resetLog();
    }

    private void bootSubscribers() {
        monitor.subscribe(new ReservationHoldListener());
        paymentProcessor.subscribe(new ReservationAutoCancelListener());
        ticketPublisher.subscribe(new BusTicketPurchaseListener(busTicketingService));
    }

    private void buildLayout() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ModernUI.BACKGROUND);
        header.setOpaque(true);
        header.setBorder(BorderFactory.createEmptyBorder(20, 24, 12, 24));

        JLabel title = new JLabel("Iteration 3 발표 데모");
        title.setFont(ModernUI.FONT_HEADING);
        title.setForeground(ModernUI.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);

        JLabel hint = new JLabel("PPT의 Observer · MCT · Mileage 흐름을 Swing에서 버튼별로 실행합니다.");
        hint.setFont(ModernUI.FONT_SMALL);
        hint.setForeground(ModernUI.TEXT_SECONDARY);
        header.add(hint, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        JPanel actions = new JPanel(new GridLayout(0, 2, 12, 12));
        actions.setBackground(ModernUI.BACKGROUND);
        actions.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));
        addAction(actions, "SC-01 좌석 hold 만료", this::demoSeatHoldExpiry);
        addAction(actions, "SC-02 결제 실패 자동 취소", this::demoPaymentFailureAutoCancel);
        addAction(actions, "SC-03 운항 변경 전파", this::demoFlightStatusPropagation);
        addAction(actions, "SC-04 e-Ticket + 우등고속", this::demoLinkedBusTicketIssue);
        addAction(actions, "SC-05 환승 MCT 검증", this::demoConnectingItinerary);
        addAction(actions, "SC-06 마일리지 결제", this::demoMileagePayment);

        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBackground(ModernUI.BACKGROUND);
        logPanel.setBorder(BorderFactory.createEmptyBorder(0, 24, 16, 24));
        logArea.setEditable(false);
        logArea.setFont(ModernUI.FONT_MONO);
        logArea.setForeground(ModernUI.TEXT_PRIMARY);
        logArea.setBackground(Color.WHITE);
        logArea.setLineWrap(false);
        logArea.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createLineBorder(ModernUI.BORDER, 1));
        logPanel.add(scroll, BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(ModernUI.BACKGROUND);
        center.add(actions, BorderLayout.NORTH);
        center.add(logPanel, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        footer.setBackground(ModernUI.CARD_BG);
        footer.setOpaque(true);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ModernUI.BORDER));

        JButton clearButton = new JButton("로그 초기화");
        ModernUI.styleButtonSecondary(clearButton);
        clearButton.addActionListener(e -> resetLog());
        footer.add(clearButton);

        JButton homeButton = new JButton("항공 예약으로");
        ModernUI.styleButton(homeButton);
        homeButton.addActionListener(e -> frame.startNewBooking());
        footer.add(homeButton);
        footer.setPreferredSize(new Dimension(0, 60));
        add(footer, BorderLayout.SOUTH);
    }

    private void addAction(JPanel parent, String title, Runnable action) {
        JButton button = new JButton(title);
        ModernUI.styleButtonSecondary(button);
        button.setFont(ModernUI.FONT_SMALL);
        button.addActionListener(e -> runDemo(title, action));
        parent.add(button);
    }

    private void runDemo(String title, Runnable action) {
        line("");
        line("==== " + title + " ====");
        try {
            action.run();
        } catch (RuntimeException ex) {
            line("[ERROR] " + ex.getMessage());
        }
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void resetLog() {
        logArea.setText("");
        line("==== Iter3 Swing demo console ====");
        line("[BOOT] SeatHoldMonitor.subscriberCount = " + monitor.subscriberCount());
        line("[BOOT] PaymentProcessor.subscriberCount = " + paymentProcessor.subscriberCount());
        line("[BOOT] TicketPublisher.subscriberCount = " + ticketPublisher.subscriberCount());
        line("[GUIDE] 발표자료 14번 콘솔 데모와 같은 흐름을 버튼별로 실행합니다.");
    }

    private void line(String text) {
        logArea.append(text + "\n");
        System.out.println(text);
    }

    private void demoSeatHoldExpiry() {
        Reservation r = newReservation("PNR-SWING-HOLD");
        r.enterPassengerInfo();
        Seat seat = new Seat("12A", CabinClass.ECONOMY);
        seat.hold(15, r.getPnrNumber());
        forceHoldExpiry(seat);
        monitor.track(seat, r.getPnrNumber());
        line("[INIT] tracked seat " + seat.getSeatNumber()
                + " PNR=" + r.getPnrNumber()
                + " holdExpiresAt=" + seat.getHoldExpiresAt());
        int fired = monitor.sweep();
        line("[SWEEP] fired=" + fired + " events");
        line("[RESULT] seat=" + seat.getSeatNumber()
                + " state=" + seat.getStatus()
                + " reservation=" + r.getStateName());
    }

    private void demoPaymentFailureAutoCancel() {
        Reservation r = newReservation("PNR-SWING-PAYFAIL");
        r.enterPassengerInfo();
        gateway.declineNext = true;
        Payment p = paymentProcessor.processPaymentCharge(320_000L, r.getPnrNumber());
        line("[RESULT] payment.status=" + p.getStatus() + " reservation=" + r.getStateName());
    }

    private void demoFlightStatusPropagation() {
        ReservationRegistry registry = new ReservationRegistry();
        FlightSchedule schedule = new FlightSchedule();
        Flight flight = new Flight();
        forceField(flight, "flightNumber", "KE001");
        forceField(schedule, "scheduleId", 9001L);
        forceField(schedule, "flight", flight);
        forceField(schedule, "status", FlightStatus.SCHEDULED);
        schedule.subscribe(new AffectedReservationListener(registry));

        Reservation a = newReservation("PNR-SWING-FLIGHT-A");
        Reservation b = newReservation("PNR-SWING-FLIGHT-B");
        a.getItinerary().addSegment(new Segment(schedule));
        b.getItinerary().addSegment(new Segment(schedule));
        registry.register(a);
        registry.register(b);

        line("[ADMIN] changeFlightStatus(" + flight.getFlightNumber() + ", CANCELLED)");
        schedule.changeStatus(FlightStatus.CANCELLED);
        line("[RESULT] schedule.status=" + schedule.getStatus() + " affected=2");
    }

    private void demoLinkedBusTicketIssue() {
        Reservation r = newReservation("PNR-SWING-BUS");
        Passenger passenger = Passenger.create("Kim Jaeho", "jaeho@example.com",
                "M12345678", LocalDate.of(1999, 1, 1), PassengerType.ADULT);
        r.enterPassengerInfo(passenger);
        r.processPayment();
        r.issueTicket();
        Ticket ticket = r.getTickets().isEmpty() ? null : r.getTickets().get(0);
        int before = busTicketingService.getIssuedTickets().size();
        ticketPublisher.publishTicketIssued(r, ticket, BusCity.BUSAN);
        int issued = busTicketingService.getIssuedTickets().size() - before;
        line("[RESULT] linkedBusTickets=+" + issued + " reservation=" + r.getStateName());
    }

    private void demoConnectingItinerary() {
        FlightSchedule legA = leg("KE091", "ICN", "NRT",
                LocalDateTime.of(2026, 6, 12, 9, 0),
                LocalDateTime.of(2026, 6, 12, 11, 30));
        FlightSchedule legB = leg("KE002", "NRT", "JFK",
                LocalDateTime.of(2026, 6, 12, 13, 30),
                LocalDateTime.of(2026, 6, 12, 23, 0));
        FlightSchedule legClose = leg("KE002X", "NRT", "JFK",
                LocalDateTime.of(2026, 6, 12, 11, 50),
                LocalDateTime.of(2026, 6, 12, 21, 30));
        Itinerary ok = Itinerary.connecting(legA, legB);
        Itinerary tooClose = Itinerary.connecting(legA, legClose);
        Duration mct = Duration.ofMinutes(90);
        line("[ICN->NRT->JFK 120m layover] valid=" + ok.isConnectionTimeValid(mct));
        line("[ICN->NRT->JFK 20m layover ] valid=" + tooClose.isConnectionTimeValid(mct));
    }

    private void demoMileagePayment() {
        SkypassInterface skypass = newSkypassMock("SK-DEMO", 50_000);
        MileageAccount account = new MileageAccount();
        account.deposit(BigDecimal.valueOf(50_000));
        Reservation r = newReservation("PNR-SWING-MILEAGE");
        r.enterPassengerInfo();
        line("[MILEAGE] balance before = " + account.getBalance());
        Payment p = paymentProcessor.processMileagePayment(account, 30_000L, r.getPnrNumber());
        line("[MILEAGE] balance after  = " + account.getBalance());
        line("[MILEAGE] external skypass.deduct = " + skypass.deductMileage("SK-DEMO", 30_000));
        if (p.getStatus() == PaymentStatus.PAID) {
            r.addPayment(p);
            r.processPayment();
        }
        line("[RESULT] payment=" + p.getStatus() + " reservation=" + r.getStateName());
    }

    private Reservation newReservation(String pnr) {
        Reservation r = new Reservation();
        r.setReservationNumber(pnr + "-" + System.currentTimeMillis());
        return r;
    }

    private FlightSchedule leg(String flightNumber, String origin, String dest,
                               LocalDateTime dep, LocalDateTime arr) {
        FlightSchedule schedule = new FlightSchedule();
        Flight flight = new Flight();
        com.koreanair.reservation.domain.flight.Route route =
                new com.koreanair.reservation.domain.flight.Route();
        com.koreanair.reservation.domain.flight.Airport originAirport =
                new com.koreanair.reservation.domain.flight.Airport();
        com.koreanair.reservation.domain.flight.Airport destAirport =
                new com.koreanair.reservation.domain.flight.Airport();
        forceField(originAirport, "airportCode", origin);
        forceField(destAirport, "airportCode", dest);
        forceField(route, "origin", originAirport);
        forceField(route, "destination", destAirport);
        forceField(flight, "flightNumber", flightNumber);
        forceField(flight, "route", route);
        forceField(schedule, "flight", flight);
        forceField(schedule, "departureDateTime", dep);
        forceField(schedule, "arrivalDateTime", arr);
        forceField(schedule, "status", FlightStatus.SCHEDULED);
        return schedule;
    }

    private SkypassInterface newSkypassMock(String skypassNumber, int balance) {
        MockSkypassInterface mock = new MockSkypassInterface();
        mock.seed(skypassNumber, "pw", balance);
        return mock;
    }

    private void forceHoldExpiry(Seat seat) {
        forceField(seat, "holdExpiresAt", LocalDateTime.now().minusSeconds(1));
    }

    private void forceField(Object target, String name, Object value) {
        try {
            Field f = target.getClass().getDeclaredField(name);
            f.setAccessible(true);
            f.set(target, value);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    private final class DemoGateway implements PaymentGatewayInterface {
        private boolean declineNext;

        @Override
        public Object sendAuthorizationRequest(BigDecimal amount, Object info) {
            return null;
        }

        @Override
        public Object receiveTransactionResult() {
            return null;
        }

        @Override
        public Object sendRefund(String paymentId, BigDecimal amount) {
            return null;
        }

        @Override
        public boolean authorize(Payment payment) {
            if (declineNext) {
                declineNext = false;
                line("[GATEWAY] authorize -> false (amount=" + payment.getAmount() + ")");
                return false;
            }
            line("[GATEWAY] authorize -> true (amount=" + payment.getAmount() + ")");
            return true;
        }

        @Override
        public boolean refund(Payment payment, BigDecimal amount) {
            return true;
        }
    }
}
