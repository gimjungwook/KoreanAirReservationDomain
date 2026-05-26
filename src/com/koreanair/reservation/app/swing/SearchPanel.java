package com.koreanair.reservation.app.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicScrollBarUI;

import com.koreanair.reservation.control.BookingController;
import com.koreanair.reservation.domain.flight.Fare;
import com.koreanair.reservation.domain.flight.Flight;
import com.koreanair.reservation.domain.flight.FlightSchedule;
import com.koreanair.reservation.domain.reservation.Itinerary;
import com.koreanair.reservation.domain.reservation.Segment;

public class SearchPanel extends JPanel {

    private static final String MODE_DIRECT = "직항";
    private static final String MODE_CONNECTING = "환승";
    private static final String MODE_MULTI_CITY = "다도시 추천";

    private static final String[] AIRPORT_CODES = {
        "ICN", "NRT", "HND", "FUK", "GMP",
        "SIN", "BKK", "HKG", "PVG", "DEL",
        "LAX", "JFK", "SYD", "CDG", "FRA"
    };

    private final JComboBox<String> modeCombo;
    private final JComboBox<String> fromCombo;
    private final JComboBox<String> toCombo;
    private final JSpinner dateSpinner;
    private final JButton searchButton = new JButton("검색");
    private final JButton nextButton = new JButton("다음 단계 →");

    private final JPanel cardListPanel = new JPanel();
    private final JScrollPane scrollPane = new JScrollPane();
    private final JLabel hint = new JLabel("직항/환승/다도시 추천을 선택해 발표 흐름 그대로 예약할 수 있습니다.");

    private final MainFrame frame;
    private final BookingController booking;
    private final SwingReservationUI ui;
    private List<Itinerary> currentResults = new ArrayList<>();
    private int selectedIndex = -1;

    public SearchPanel(MainFrame frame, BookingController booking, SwingReservationUI ui) {
        super(new BorderLayout());
        this.frame = frame;
        this.booking = booking;
        this.ui = ui;
        setBackground(ModernUI.BACKGROUND);

        modeCombo = new JComboBox<>(new String[] { MODE_DIRECT, MODE_CONNECTING, MODE_MULTI_CITY });
        fromCombo = new JComboBox<>(AIRPORT_CODES);
        toCombo = new JComboBox<>(AIRPORT_CODES);
        toCombo.setSelectedItem("NRT");

        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateModel.setValue(Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        dateSpinner = new JSpinner(dateModel);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));

        buildSearchBar();
        buildCardList();
        buildFooter();
        loadAllSchedules();
    }

    private void buildSearchBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(ModernUI.BACKGROUND);

        JPanel headline = new JPanel(new BorderLayout());
        headline.setBackground(ModernUI.NAVY);
        headline.setBorder(BorderFactory.createEmptyBorder(18, 28, 18, 28));
        JLabel title = new JLabel("항공편 검색");
        title.setFont(ModernUI.FONT_TITLE);
        title.setForeground(Color.WHITE);
        headline.add(title, BorderLayout.WEST);
        JLabel subtitle = new JLabel("직항 · 환승 · 다도시 여정을 한 흐름으로 예약합니다");
        subtitle.setFont(ModernUI.FONT_BODY);
        subtitle.setForeground(new Color(0xC8, 0xE6, 0xFF));
        headline.add(subtitle, BorderLayout.SOUTH);
        top.add(headline, BorderLayout.NORTH);

        JPanel searchBar = new JPanel(new GridBagLayout());
        searchBar.setBackground(ModernUI.CARD_BG);
        searchBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ModernUI.BORDER),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 12, 8, 12);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel logo = ModernUI.pill("Search", ModernUI.SKY, ModernUI.PRIMARY_LIGHT);
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridx = 0;
        c.weightx = 0.05;
        searchBar.add(logo, c);

        c.gridx = 1;
        c.weightx = 0.14;
        styleCombo(modeCombo);
        searchBar.add(wrap(modeCombo, "검색 방식"), c);

        c.gridx = 2;
        c.weightx = 0.18;
        styleCombo(fromCombo);
        searchBar.add(wrap(fromCombo, "출발"), c);

        JLabel arrow = new JLabel("→", SwingConstants.CENTER);
        arrow.setFont(new Font("System", Font.BOLD, 16));
        arrow.setForeground(ModernUI.PRIMARY);
        c.gridx = 3;
        c.weightx = 0.03;
        searchBar.add(arrow, c);

        c.gridx = 4;
        c.weightx = 0.18;
        styleCombo(toCombo);
        searchBar.add(wrap(toCombo, "도착"), c);

        c.gridx = 5;
        c.weightx = 0.16;
        dateSpinner.setFont(ModernUI.FONT_BODY);
        dateSpinner.setBackground(Color.WHITE);
        dateSpinner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernUI.BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        searchBar.add(wrap(dateSpinner, "날짜"), c);

        c.gridx = 6;
        c.weightx = 0.12;
        ModernUI.styleButton(searchButton);
        searchButton.setPreferredSize(new Dimension(90, 38));
        searchBar.add(searchButton, c);

        modeCombo.addActionListener(e -> applyModeDefaults());
        searchButton.addActionListener(e -> doSearch());
        top.add(searchBar, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setFont(ModernUI.FONT_BODY);
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernUI.BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        combo.setFocusable(false);
    }

    private JPanel wrap(JComponent component, String label) {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(ModernUI.CARD_BG);
        JLabel ph = new JLabel(label, SwingConstants.CENTER);
        ph.setFont(ModernUI.FONT_SMALL);
        ph.setForeground(ModernUI.TEXT_SECONDARY);
        ph.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        wrap.add(ph, BorderLayout.NORTH);
        wrap.add(component, BorderLayout.CENTER);
        return wrap;
    }

    private void buildCardList() {
        cardListPanel.setLayout(new BoxLayout(cardListPanel, BoxLayout.Y_AXIS));
        cardListPanel.setBackground(ModernUI.BACKGROUND);
        cardListPanel.setOpaque(true);
        cardListPanel.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        scrollPane.setViewportView(cardListPanel);
        scrollPane.setBackground(ModernUI.BACKGROUND);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(ModernUI.BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = ModernUI.BORDER;
                this.trackColor = ModernUI.BACKGROUND;
            }
        });
        add(scrollPane, BorderLayout.CENTER);
    }

    private void buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(ModernUI.CARD_BG);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ModernUI.BORDER));

        JPanel leftHint = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        leftHint.setBackground(ModernUI.CARD_BG);
        hint.setFont(ModernUI.FONT_SMALL);
        hint.setForeground(ModernUI.TEXT_SECONDARY);
        leftHint.add(hint);

        JPanel rightBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightBtns.setBackground(ModernUI.CARD_BG);

        JButton detailBtn = new JButton("상세 보기");
        styleSolidButton(detailBtn, ModernUI.CARD_BG, ModernUI.PRIMARY, ModernUI.PRIMARY);
        detailBtn.addActionListener(e -> showSelectedDetail());
        rightBtns.add(detailBtn);

        styleSolidButton(nextButton, ModernUI.PRIMARY, Color.WHITE, ModernUI.PRIMARY);
        nextButton.addActionListener(e -> proceedWithSelection());
        rightBtns.add(nextButton);

        footer.add(leftHint, BorderLayout.WEST);
        footer.add(rightBtns, BorderLayout.EAST);
        footer.setPreferredSize(new Dimension(0, 52));
        add(footer, BorderLayout.SOUTH);
    }

    private void applyModeDefaults() {
        String mode = (String) modeCombo.getSelectedItem();
        if (MODE_MULTI_CITY.equals(mode)) {
            fromCombo.setSelectedItem("ICN");
            toCombo.setSelectedItem("LAX");
            hint.setText("다도시 추천: ICN → NRT → JFK → LAX, 3일 일정으로 버스 연계 데모까지 이어집니다.");
        } else if (MODE_CONNECTING.equals(mode)) {
            fromCombo.setSelectedItem("ICN");
            toCombo.setSelectedItem("LAX");
            hint.setText("환승: 1-stop 조합만 표시하고 MCT 90분 이상인 여정만 통과합니다.");
        } else {
            hint.setText("직항: 선택한 출발/도착/날짜의 단일 항공편을 예약합니다.");
        }
    }

    private void loadAllSchedules() {
        currentResults = new ArrayList<>();
        for (FlightSchedule schedule : booking.getAllSchedules()) {
            currentResults.add(Itinerary.direct(schedule));
        }
        selectedIndex = currentResults.isEmpty() ? -1 : 0;
        refreshCardList();
    }

    private void doSearch() {
        Date selectedDate = (Date) dateSpinner.getValue();
        LocalDate date = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        String from = (String) fromCombo.getSelectedItem();
        String to = (String) toCombo.getSelectedItem();
        String mode = (String) modeCombo.getSelectedItem();

        List<Itinerary> results;
        if (MODE_CONNECTING.equals(mode)) {
            results = booking.searchConnectingItineraries(from, to, date);
        } else if (MODE_MULTI_CITY.equals(mode)) {
            results = booking.searchDemoMultiCityItineraries(date);
        } else {
            results = booking.searchDirectItineraries(from, to, date);
        }
        currentResults = results != null ? results : new ArrayList<>();
        selectedIndex = currentResults.isEmpty() ? -1 : 0;
        refreshCardList();
        if (currentResults.isEmpty()) {
            ui.displayError("검색 결과가 없습니다. 날짜 또는 검색 방식을 바꿔보세요.");
        }
    }

    private void refreshCardList() {
        cardListPanel.removeAll();
        for (int i = 0; i < currentResults.size(); i++) {
            ItineraryCard card = new ItineraryCard(currentResults.get(i), i);
            card.setSelected(i == selectedIndex);
            cardListPanel.add(card);
            cardListPanel.add(Box.createVerticalStrut(10));
        }
        cardListPanel.revalidate();
        cardListPanel.repaint();
    }

    private void proceedWithSelection() {
        Itinerary selected = selectedItinerary();
        if (selected == null) {
            return;
        }
        logSelection(selected);
        frame.onItinerarySelected(selected);
    }

    private void showSelectedDetail() {
        Itinerary selected = selectedItinerary();
        if (selected == null) {
            return;
        }
        JOptionPane.showMessageDialog(this, detailText(selected), "여정 상세", JOptionPane.INFORMATION_MESSAGE);
    }

    private Itinerary selectedItinerary() {
        if (currentResults.isEmpty() || selectedIndex < 0 || selectedIndex >= currentResults.size()) {
            ui.displayError("여정을 선택하세요.");
            return null;
        }
        return currentResults.get(selectedIndex);
    }

    private void selectIndex(int index) {
        selectedIndex = index;
        refreshCardList();
    }

    private void logSelection(Itinerary itinerary) {
        System.out.println("[SWING][SEARCH] " + summaryLine(itinerary)
                + " / tripType=" + itinerary.getTripType()
                + " / segments=" + itinerary.getSegments().size());
    }

    private String detailText(Itinerary itinerary) {
        StringBuilder sb = new StringBuilder();
        sb.append("TripType: ").append(itinerary.getTripType()).append("\n");
        sb.append("총 구간: ").append(itinerary.getSegments().size()).append("개\n\n");
        for (Segment segment : itinerary.getSegments()) {
            FlightSchedule schedule = segment.getFlightSchedule();
            Flight flight = schedule.getFlight();
            sb.append(flight.getFlightNumber()).append("  ")
                    .append(routeText(schedule)).append("  ")
                    .append(timeRange(schedule)).append("\n");
        }
        return sb.toString();
    }

    private String summaryLine(Itinerary itinerary) {
        if (itinerary == null || itinerary.getSegments().isEmpty()) {
            return "선택 여정 없음";
        }
        Segment first = itinerary.getSegments().get(0);
        Segment last = itinerary.getSegments().get(itinerary.getSegments().size() - 1);
        FlightSchedule firstSchedule = first.getFlightSchedule();
        FlightSchedule lastSchedule = last.getFlightSchedule();
        return routeOrigin(firstSchedule) + " → " + routeDestination(lastSchedule)
                + " · " + tripLabel(itinerary)
                + " · " + flightNumbers(itinerary);
    }

    private String tripLabel(Itinerary itinerary) {
        int segments = itinerary.getSegments().size();
        if ("MULTI_CITY".equals(itinerary.getTripType())) {
            return "다도시 " + segments + "구간";
        }
        if (segments >= 2) {
            return "환승 " + (segments - 1) + "회";
        }
        return "직항";
    }

    private String flightNumbers(Itinerary itinerary) {
        List<String> numbers = new ArrayList<>();
        for (Segment segment : itinerary.getSegments()) {
            FlightSchedule schedule = segment.getFlightSchedule();
            if (schedule != null && schedule.getFlight() != null) {
                numbers.add(schedule.getFlight().getFlightNumber());
            }
        }
        return String.join(" + ", numbers);
    }

    private String routeText(FlightSchedule schedule) {
        return routeOrigin(schedule) + " → " + routeDestination(schedule);
    }

    private String routeOrigin(FlightSchedule schedule) {
        if (schedule == null || schedule.getFlight() == null || schedule.getFlight().getRoute() == null
                || schedule.getFlight().getRoute().getOrigin() == null) {
            return "-";
        }
        return schedule.getFlight().getRoute().getOrigin().getAirportCode();
    }

    private String routeDestination(FlightSchedule schedule) {
        if (schedule == null || schedule.getFlight() == null || schedule.getFlight().getRoute() == null
                || schedule.getFlight().getRoute().getDestination() == null) {
            return "-";
        }
        return schedule.getFlight().getRoute().getDestination().getAirportCode();
    }

    private String timeRange(FlightSchedule schedule) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM-dd HH:mm");
        if (schedule == null || schedule.getDepartureDateTime() == null || schedule.getArrivalDateTime() == null) {
            return "-";
        }
        return schedule.getDepartureDateTime().format(fmt) + " ~ " + schedule.getArrivalDateTime().format(fmt);
    }

    private String durationText(Itinerary itinerary) {
        Duration d = itinerary.getTotalDuration();
        long minutes = Math.max(0, d.toMinutes());
        return (minutes / 60) + "h " + (minutes % 60) + "m";
    }

    private String priceText(Itinerary itinerary) {
        BigDecimal total = BigDecimal.ZERO;
        for (Segment segment : itinerary.getSegments()) {
            BigDecimal p = lowestPrice(segment.getFlightSchedule());
            if (p != null) {
                total = total.add(p);
            }
        }
        return NumberFormat.getNumberInstance(Locale.US).format(total.longValue());
    }

    private BigDecimal lowestPrice(FlightSchedule schedule) {
        Flight flight = schedule != null ? schedule.getFlight() : null;
        if (flight == null || flight.getFares() == null || flight.getFares().isEmpty()) {
            return null;
        }
        BigDecimal lowest = null;
        for (Fare fare : flight.getFares()) {
            BigDecimal p = fare.getBasePrice();
            if (p != null && (lowest == null || p.compareTo(lowest) < 0)) {
                lowest = p;
            }
        }
        return lowest;
    }

    private class ItineraryCard extends JPanel {
        private final Itinerary itinerary;
        private final int index;
        private boolean selected;

        ItineraryCard(Itinerary itinerary, int index) {
            super(new BorderLayout());
            this.itinerary = itinerary;
            this.index = index;
            setOpaque(true);
            setPreferredSize(new Dimension(0, 118));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 118));
            setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectIndex(index);
                }
            });
            build();
        }

        private void build() {
            JPanel center = new JPanel(new GridBagLayout());
            center.setOpaque(false);
            GridBagConstraints gc = new GridBagConstraints();
            gc.insets = new Insets(0, 18, 0, 0);
            gc.anchor = GridBagConstraints.WEST;

            gc.gridx = 0;
            JLabel trip = ModernUI.pill(tripLabel(itinerary), ModernUI.SKY, ModernUI.PRIMARY_LIGHT);
            center.add(trip, gc);

            gc.gridx = 1;
            JLabel route = new JLabel(summaryLine(itinerary));
            route.setFont(new Font("System", Font.BOLD, 20));
            route.setForeground(ModernUI.TEXT_PRIMARY);
            center.add(route, gc);

            gc.gridx = 2;
            JLabel duration = new JLabel(durationText(itinerary));
            duration.setFont(ModernUI.FONT_SMALL);
            duration.setForeground(ModernUI.TEXT_SECONDARY);
            center.add(duration, gc);

            add(center, BorderLayout.CENTER);

            JPanel right = new JPanel();
            right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
            right.setBorder(BorderFactory.createEmptyBorder(22, 0, 0, 26));
            right.setOpaque(false);
            JLabel price = new JLabel(priceText(itinerary));
            price.setFont(new Font("System", Font.BOLD, 24));
            price.setForeground(ModernUI.SKY);
            JLabel currency = new JLabel("KRW");
            currency.setFont(ModernUI.FONT_SMALL);
            currency.setForeground(ModernUI.PRIMARY);
            right.add(price);
            right.add(currency);
            add(right, BorderLayout.EAST);
        }

        private void setSelected(boolean value) {
            selected = value;
            setBackground(selected ? ModernUI.PRIMARY_LIGHT : ModernUI.CARD_BG);
            setBorder(selected
                    ? BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(ModernUI.SKY, 2),
                            BorderFactory.createEmptyBorder(0, 0, 0, 0))
                    : BorderFactory.createLineBorder(ModernUI.BORDER, 1));
        }
    }

    private void styleSolidButton(JButton btn, Color bg, Color fg, Color border) {
        btn.setFont(ModernUI.FONT_BODY);
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border, 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        btn.setFocusPainted(false);
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setFocusable(false);
    }
}
