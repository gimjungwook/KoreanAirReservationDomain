package com.koreanair.reservation.app.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.koreanair.reservation.control.BusTicketingService;
import com.koreanair.reservation.domain.bus.BusTicket;
import com.koreanair.reservation.domain.flight.FlightSchedule;
import com.koreanair.reservation.domain.reservation.Itinerary;
import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.reservation.Segment;
import com.koreanair.reservation.domain.user.Member;

/**
 * 회원 예약/좌석 관리 화면.
 */
public class MyPagePanel extends JPanel {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final MainFrame frame;
    private final BusTicketingService busTicketingService;

    private final JLabel summaryLabel = new JLabel(" ");
    private final DefaultTableModel reservationModel =
            new DefaultTableModel(new Object[] { "PNR", "여정", "출발일", "상태", "항공 좌석" }, 0) {
                @Override public boolean isCellEditable(int row, int column) { return false; }
            };
    private final JTable reservationTable = new JTable(reservationModel);
    private final DefaultTableModel busModel =
            new DefaultTableModel(new Object[] { "버스티켓", "도시", "항공권", "버스 좌석" }, 0) {
                @Override public boolean isCellEditable(int row, int column) { return false; }
            };
    private final JTable busTable = new JTable(busModel);

    private final JButton refreshButton = new JButton("새로고침");
    private final JButton airSeatButton = new JButton("항공 좌석 변경");
    private final JButton busSeatButton = new JButton("버스 좌석 선택");
    private final JButton lookupButton = new JButton("예약 조회로");
    private final JButton homeButton = new JButton("항공 예약 홈");

    private List<Reservation> reservations = java.util.Collections.emptyList();
    private List<BusTicket> busTickets = java.util.Collections.emptyList();

    public MyPagePanel(MainFrame frame, BusTicketingService busTicketingService) {
        super(new BorderLayout());
        this.frame = frame;
        this.busTicketingService = busTicketingService;
        setBackground(ModernUI.BACKGROUND);
        setOpaque(true);
        buildLayout();
    }

    private void buildLayout() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ModernUI.BACKGROUND);
        header.setBorder(BorderFactory.createEmptyBorder(20, 24, 12, 24));
        JLabel title = new JLabel("마이페이지");
        title.setFont(ModernUI.FONT_HEADING);
        title.setForeground(ModernUI.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);
        summaryLabel.setFont(ModernUI.FONT_BODY);
        summaryLabel.setForeground(ModernUI.TEXT_SECONDARY);
        header.add(summaryLabel, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(ModernUI.FONT_BODY);
        tabs.addTab("예약/항공 좌석", wrapTable(reservationTable));
        tabs.addTab("버스티켓/버스 좌석", wrapTable(busTable));
        add(tabs, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        footer.setBackground(ModernUI.CARD_BG);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ModernUI.BORDER));
        ModernUI.styleButtonSecondary(refreshButton);
        ModernUI.styleButtonSecondary(airSeatButton);
        ModernUI.styleButtonSecondary(busSeatButton);
        ModernUI.styleButtonSecondary(lookupButton);
        ModernUI.styleButton(homeButton);
        refreshButton.addActionListener(e -> refresh());
        airSeatButton.addActionListener(e -> changeAirSeat());
        busSeatButton.addActionListener(e -> chooseBusSeat());
        lookupButton.addActionListener(e -> frame.showLookup());
        homeButton.addActionListener(e -> frame.startNewBooking());
        footer.add(refreshButton);
        footer.add(airSeatButton);
        footer.add(busSeatButton);
        footer.add(lookupButton);
        footer.add(homeButton);
        footer.setPreferredSize(new Dimension(0, 62));
        add(footer, BorderLayout.SOUTH);
    }

    private JScrollPane wrapTable(JTable table) {
        table.setFont(ModernUI.FONT_BODY);
        table.setRowHeight(32);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(ModernUI.BORDER);
        table.setBackground(ModernUI.CARD_BG);
        table.setShowVerticalLines(false);
        JTableHeader head = table.getTableHeader();
        head.setFont(ModernUI.FONT_SUBHEADING);
        head.setBackground(ModernUI.PRIMARY_LIGHT);
        head.setForeground(ModernUI.TEXT_PRIMARY);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        scroll.getViewport().setBackground(ModernUI.CARD_BG);
        return scroll;
    }

    public void refresh() {
        reservationModel.setRowCount(0);
        busModel.setRowCount(0);
        Member member = frame.currentMember();
        reservations = member != null ? member.getReservations() : java.util.Collections.emptyList();
        busTickets = busTicketingService.getIssuedTickets();
        for (Reservation reservation : reservations) {
            reservationModel.addRow(new Object[] {
                    reservation.getPnrNumber(),
                    routeText(reservation),
                    departureDate(reservation),
                    reservation.getStateName(),
                    frame.airSeatFor(reservation)
            });
        }
        for (BusTicket ticket : busTickets) {
            busModel.addRow(new Object[] {
                    ticket.getTicketNumber(),
                    ticket.getDestinationCity() != null ? ticket.getDestinationCity().toString() : "-",
                    ticket.getAirTicketNumber(),
                    frame.busSeatFor(ticket)
            });
        }
        if (!reservations.isEmpty()) {
            reservationTable.setRowSelectionInterval(0, 0);
        }
        if (!busTickets.isEmpty()) {
            busTable.setRowSelectionInterval(0, 0);
        }
        summaryLabel.setText(String.format("%s · 예약 %d건 · 버스티켓 %d건",
                member != null ? member.getName() : "로그인 필요",
                reservations.size(),
                busTickets.size()));
        airSeatButton.setEnabled(!reservations.isEmpty());
        busSeatButton.setEnabled(!busTickets.isEmpty());
    }

    private void changeAirSeat() {
        Reservation reservation = selectedReservation();
        if (reservation == null) {
            JOptionPane.showMessageDialog(this, "항공 좌석을 변경할 예약을 선택하세요.");
            return;
        }
        frame.showSeatManagement(reservation);
    }

    private void chooseBusSeat() {
        BusTicket ticket = selectedBusTicket();
        if (ticket == null) {
            JOptionPane.showMessageDialog(this, "좌석을 선택할 버스티켓을 선택하세요.");
            return;
        }
        frame.showBusSeatSelection(ticket);
    }

    private Reservation selectedReservation() {
        int row = reservationTable.getSelectedRow();
        if (row >= 0 && row < reservations.size()) {
            return reservations.get(row);
        }
        return null;
    }

    private BusTicket selectedBusTicket() {
        int row = busTable.getSelectedRow();
        if (row >= 0 && row < busTickets.size()) {
            return busTickets.get(row);
        }
        return null;
    }

    private String routeText(Reservation reservation) {
        Itinerary itinerary = reservation != null ? reservation.getItinerary() : null;
        if (itinerary == null || itinerary.getSegments() == null || itinerary.getSegments().isEmpty()) {
            return "-";
        }
        Segment first = itinerary.getSegments().get(0);
        Segment last = itinerary.getSegments().get(itinerary.getSegments().size() - 1);
        return airportCode(first, true) + " → " + airportCode(last, false);
    }

    private String departureDate(Reservation reservation) {
        Itinerary itinerary = reservation != null ? reservation.getItinerary() : null;
        if (itinerary == null || itinerary.getSegments() == null || itinerary.getSegments().isEmpty()) {
            return "-";
        }
        FlightSchedule schedule = itinerary.getSegments().get(0).getFlightSchedule();
        return schedule != null && schedule.getDepartureDateTime() != null
                ? schedule.getDepartureDateTime().toLocalDate().format(DATE_FMT)
                : "-";
    }

    private String airportCode(Segment segment, boolean origin) {
        FlightSchedule schedule = segment != null ? segment.getFlightSchedule() : null;
        if (schedule == null || schedule.getFlight() == null || schedule.getFlight().getRoute() == null) {
            return "-";
        }
        if (origin) {
            return schedule.getFlight().getRoute().getOrigin() != null
                    ? schedule.getFlight().getRoute().getOrigin().getAirportCode()
                    : "-";
        }
        return schedule.getFlight().getRoute().getDestination() != null
                ? schedule.getFlight().getRoute().getDestination().getAirportCode()
                : "-";
    }
}
