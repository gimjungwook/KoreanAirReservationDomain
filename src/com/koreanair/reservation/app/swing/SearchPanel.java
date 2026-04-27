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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicScrollBarUI;

import com.koreanair.reservation.control.BookingController;
import com.koreanair.reservation.domain.flight.FlightSchedule;

public class SearchPanel extends JPanel {

    private static final String[] AIRPORT_CODES = {
        "ICN", "NRT", "HND", "FUK", "GMP",
        "SIN", "BKK", "HKG", "PVG", "DEL",
        "LAX", "JFK", "SYD", "CDG", "FRA"
    };

    private final JComboBox<String> fromCombo;
    private final JComboBox<String> toCombo;
    private final JSpinner dateSpinner;
    private final JButton searchButton = new JButton("검색");
    private final JButton nextButton = new JButton("다음 단계 →");

    private final JPanel cardListPanel = new JPanel();
    private final JScrollPane scrollPane = new JScrollPane();

    private final MainFrame frame;
    private final BookingController booking;
    private final SwingReservationUI ui;
    private List<FlightSchedule> currentResults = new ArrayList<>();

    public SearchPanel(MainFrame frame, BookingController booking, SwingReservationUI ui) {
        super(new BorderLayout());
        this.frame = frame;
        this.booking = booking;
        this.ui = ui;
        setBackground(ModernUI.BACKGROUND);

        fromCombo = new JComboBox<>(AIRPORT_CODES);
        toCombo = new JComboBox<>(AIRPORT_CODES);

        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateModel.setValue(Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);

        buildSearchBar();
        buildCardList();
        buildFooter();
        loadAllSchedules();
    }

    private void buildSearchBar() {
        JPanel searchBar = new JPanel(new GridBagLayout());
        searchBar.setBackground(ModernUI.CARD_BG);
        searchBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ModernUI.BORDER));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 16, 8, 16);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel logo = new JLabel("🔍", SwingConstants.CENTER);
        logo.setFont(new Font("System", Font.PLAIN, 20));
        c.gridx = 0;
        searchBar.add(logo, c);

        c.gridx = 1;
        c.weightx = 0.25;
        fromCombo.setFont(ModernUI.FONT_BODY);
        fromCombo.setBackground(Color.WHITE);
        fromCombo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernUI.BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        fromCombo.setFocusable(false);
        JPanel fromWrap = wrapCombo(fromCombo, "출발 공항");
        searchBar.add(fromWrap, c);

        JLabel arrow = new JLabel("→", SwingConstants.CENTER);
        arrow.setFont(new Font("System", Font.BOLD, 16));
        arrow.setForeground(ModernUI.PRIMARY);
        c.gridx = 2;
        searchBar.add(arrow, c);

        c.gridx = 3;
        c.weightx = 0.25;
        toCombo.setFont(ModernUI.FONT_BODY);
        toCombo.setBackground(Color.WHITE);
        toCombo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernUI.BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        toCombo.setFocusable(false);
        toCombo.setSelectedItem("NRT");
        JPanel toWrap = wrapCombo(toCombo, "도착 공항");
        searchBar.add(toWrap, c);

        JLabel divider = new JLabel("|", SwingConstants.CENTER);
        divider.setFont(new Font("System", Font.PLAIN, 16));
        divider.setForeground(ModernUI.BORDER);
        c.gridx = 4;
        searchBar.add(divider, c);

        c.gridx = 5;
        c.weightx = 0.2;
        dateSpinner.setFont(ModernUI.FONT_BODY);
        dateSpinner.setBackground(Color.WHITE);
        dateSpinner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernUI.BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        JComponent dateEditorComponent = dateSpinner.getEditor();
        dateEditorComponent.setBackground(Color.WHITE);
        JPanel dateWrap = wrapSpinner(dateSpinner, "날짜");
        searchBar.add(dateWrap, c);

        c.gridx = 6;
        c.weightx = 0.15;
        ModernUI.styleButton(searchButton);
        searchButton.setPreferredSize(new Dimension(90, 38));
        searchBar.add(searchButton, c);

        add(searchBar, BorderLayout.NORTH);
    }

    private JPanel wrapCombo(JComboBox<String> combo, String placeholder) {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(ModernUI.CARD_BG);
        wrap.add(combo, BorderLayout.CENTER);
        JLabel ph = new JLabel(placeholder, SwingConstants.CENTER);
        ph.setFont(ModernUI.FONT_SMALL);
        ph.setForeground(ModernUI.TEXT_SECONDARY);
        ph.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        return wrap;
    }

    private JPanel wrapSpinner(JSpinner spinner, String placeholder) {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(ModernUI.CARD_BG);
        wrap.add(spinner, BorderLayout.CENTER);
        JLabel ph = new JLabel(placeholder, SwingConstants.CENTER);
        ph.setFont(ModernUI.FONT_SMALL);
        ph.setForeground(ModernUI.TEXT_SECONDARY);
        ph.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        return wrap;
    }

    private void buildCardList() {
        cardListPanel.setLayout(new BoxLayout(cardListPanel, BoxLayout.Y_AXIS));
        cardListPanel.setBackground(ModernUI.BACKGROUND);
        cardListPanel.setOpaque(true);

        scrollPane.setViewportView(cardListPanel);
        scrollPane.setBackground(ModernUI.BACKGROUND);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(ModernUI.BACKGROUND);
        scrollPane.getViewport().setOpaque(true);
        scrollPane.setOpaque(true);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = ModernUI.BORDER;
                this.trackColor = ModernUI.BACKGROUND;
            }
        });

        add(scrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(e -> doSearch());
    }

    private void buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(ModernUI.CARD_BG);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ModernUI.BORDER));

        JPanel leftHint = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        leftHint.setBackground(ModernUI.CARD_BG);
        JLabel hint = new JLabel("날짜를 비워두면 전체 항공편이 표시됩니다");
        hint.setFont(ModernUI.FONT_SMALL);
        hint.setForeground(ModernUI.TEXT_SECONDARY);
        leftHint.add(hint);

        JPanel rightBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightBtns.setBackground(ModernUI.CARD_BG);
        rightBtns.setOpaque(true);

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
        footer.setOpaque(true);
        add(footer, BorderLayout.SOUTH);
    }

    private void loadAllSchedules() {
        currentResults = booking.getAllSchedules();
        refreshCardList();
    }

    private void doSearch() {
        Date selectedDate = (Date) dateSpinner.getValue();
        LocalDate date = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        String from = (String) fromCombo.getSelectedItem();
        String to = (String) toCombo.getSelectedItem();

        List<FlightSchedule> results = booking.processSearch(from, to, date);
        currentResults = results != null ? results : new ArrayList<>();
        refreshCardList();
        if (currentResults.isEmpty()) {
            ui.displayError("검색 결과가 없습니다.");
        }
    }

    private void refreshCardList() {
        cardListPanel.removeAll();

        for (int i = 0; i < currentResults.size(); i++) {
            FlightSchedule s = currentResults.get(i);
            FlightCard card = new FlightCard(s, i + 1);
            cardListPanel.add(card);
            cardListPanel.add(Box.createVerticalStrut(10));
        }

        cardListPanel.revalidate();
        cardListPanel.repaint();
        scrollPane.revalidate();
        scrollPane.repaint();
    }

    private void proceedWithSelection() {
        FlightSchedule selected = selectedSchedule();
        if (selected == null) return;
        ui.displayItineraryDetail(selected);
        frame.onFlightSelected(selected);
    }

    private void showSelectedDetail() {
        FlightSchedule selected = selectedSchedule();
        if (selected != null) {
            ui.displayItineraryDetail(selected);
        }
    }

    private FlightSchedule selectedSchedule() {
        if (currentResults.isEmpty()) {
            ui.displayError("항공편을 선택하세요.");
            return null;
        }
        return currentResults.get(0);
    }

    private class FlightCard extends JPanel {
        private final FlightSchedule schedule;
        private boolean isSelected = false;

        FlightCard(FlightSchedule schedule, int index) {
            super(new BorderLayout());
            this.schedule = schedule;
            setBackground(ModernUI.CARD_BG);
            setOpaque(true);
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ModernUI.BORDER));
            setPreferredSize(new Dimension(0, 88));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 88));
            setMinimumSize(new Dimension(0, 88));
            setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!isSelected) {
                        setBackground(new Color(0xF8, 0xFA, 0xFC));
                        repaintCard();
                    }
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    if (!isSelected) {
                        setBackground(ModernUI.CARD_BG);
                        repaintCard();
                    }
                }
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectCard();
                }
            });

            buildCard();
        }

        private void repaintCard() {
            invalidate();
            repaint();
        }

        private void buildCard() {
            com.koreanair.reservation.domain.flight.Flight flight = schedule.getFlight();
            String flightNum = flight != null ? flight.getFlightNumber() : "-";
            String airline = "대한항공";

            String fromCode = "-";
            String toCode = "-";
            String fromTime = "-";
            String toTime = "-";
            String duration = "-";

            if (flight != null && flight.getRoute() != null) {
                fromCode = flight.getRoute().getOrigin() != null
                        ? flight.getRoute().getOrigin().getAirportCode() : fromCode;
                toCode = flight.getRoute().getDestination() != null
                        ? flight.getRoute().getDestination().getAirportCode() : toCode;
            }

            if (schedule.getDepartureDateTime() != null) {
                fromTime = schedule.getDepartureDateTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            }
            if (schedule.getArrivalDateTime() != null) {
                toTime = schedule.getArrivalDateTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            }
            if (schedule.getDepartureDateTime() != null && schedule.getArrivalDateTime() != null) {
                long mins = ChronoUnit.MINUTES.between(
                        schedule.getDepartureDateTime(), schedule.getArrivalDateTime());
                long h = mins / 60;
                long m = mins % 60;
                duration = h > 0 ? h + "h " + m + "m" : m + "m";
            }

            setLayout(new BorderLayout());
            setBackground(ModernUI.CARD_BG);

            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new GridBagLayout());
            centerPanel.setBackground(ModernUI.CARD_BG);
            centerPanel.setOpaque(true);
            GridBagConstraints gc = new GridBagConstraints();
            gc.insets = new Insets(0, 16, 0, 0);
            gc.anchor = GridBagConstraints.WEST;
            gc.fill = GridBagConstraints.VERTICAL;

            gc.gridx = 0;
            gc.gridy = 0;
            gc.gridheight = 2;
            JPanel airlinePanel = new JPanel();
            airlinePanel.setLayout(new BoxLayout(airlinePanel, BoxLayout.Y_AXIS));
            airlinePanel.setBackground(ModernUI.CARD_BG);
            airlinePanel.setOpaque(true);
            JLabel airlineLbl = new JLabel(airline);
            airlineLbl.setFont(ModernUI.FONT_SMALL);
            airlineLbl.setForeground(ModernUI.TEXT_SECONDARY);
            JLabel flightNumLbl = new JLabel(flightNum);
            flightNumLbl.setFont(new Font("System", Font.BOLD, 13));
            flightNumLbl.setForeground(ModernUI.TEXT_PRIMARY);
            airlinePanel.add(airlineLbl);
            airlinePanel.add(flightNumLbl);
            centerPanel.add(airlinePanel, gc);

            gc.gridx = 1;
            gc.gridheight = 1;
            gc.insets = new Insets(0, 24, 0, 0);
            JPanel depPanel = new JPanel();
            depPanel.setLayout(new BoxLayout(depPanel, BoxLayout.Y_AXIS));
            depPanel.setBackground(ModernUI.CARD_BG);
            depPanel.setOpaque(true);
            JLabel depTime = new JLabel(fromTime);
            depTime.setFont(new Font("System", Font.BOLD, 22));
            depTime.setForeground(ModernUI.TEXT_PRIMARY);
            JLabel depCode = new JLabel(fromCode);
            depCode.setFont(ModernUI.FONT_SMALL);
            depCode.setForeground(ModernUI.TEXT_SECONDARY);
            depPanel.add(depTime);
            depPanel.add(depCode);
            centerPanel.add(depPanel, gc);

            gc.gridx = 2;
            gc.gridheight = 2;
            gc.insets = new Insets(0, 16, 0, 0);
            JPanel durationPanel = new JPanel();
            durationPanel.setLayout(new BoxLayout(durationPanel, BoxLayout.Y_AXIS));
            durationPanel.setBackground(ModernUI.CARD_BG);
            durationPanel.setOpaque(true);
            JLabel durLabel = new JLabel(duration);
            durLabel.setFont(ModernUI.FONT_SMALL);
            durLabel.setForeground(ModernUI.TEXT_SECONDARY);
            durLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
            JLabel stopsLabel = new JLabel("직항");
            stopsLabel.setFont(ModernUI.FONT_SMALL);
            stopsLabel.setForeground(ModernUI.SUCCESS);
            stopsLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
            durationPanel.add(durLabel);
            durationPanel.add(stopsLabel);
            centerPanel.add(durationPanel, gc);

            gc.gridx = 3;
            gc.gridheight = 1;
            gc.insets = new Insets(0, 16, 0, 0);
            JPanel arrPanel = new JPanel();
            arrPanel.setLayout(new BoxLayout(arrPanel, BoxLayout.Y_AXIS));
            arrPanel.setBackground(ModernUI.CARD_BG);
            arrPanel.setOpaque(true);
            JLabel arrTime = new JLabel(toTime);
            arrTime.setFont(new Font("System", Font.BOLD, 22));
            arrTime.setForeground(ModernUI.TEXT_PRIMARY);
            JLabel arrCode = new JLabel(toCode);
            arrCode.setFont(ModernUI.FONT_SMALL);
            arrCode.setForeground(ModernUI.TEXT_SECONDARY);
            arrPanel.add(arrTime);
            arrPanel.add(arrCode);
            centerPanel.add(arrPanel, gc);

            add(centerPanel, BorderLayout.CENTER);

            BigDecimal lowestPrice = null;
            String cabinLabel = "이코노미";
            NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);

            if (flight != null) {
                List<com.koreanair.reservation.domain.flight.Fare> fares = flight.getFares();
                if (fares != null && !fares.isEmpty()) {
                    for (com.koreanair.reservation.domain.flight.Fare f : fares) {
                        BigDecimal p = f.getBasePrice();
                        if (p != null && (lowestPrice == null || p.compareTo(lowestPrice) < 0)) {
                            lowestPrice = p;
                        }
                        if (f.getCabinClass() != null) {
                            switch (f.getCabinClass()) {
                                case ECONOMY: cabinLabel = "이코노미"; break;
                                case PREMIUM_ECONOMY: cabinLabel = "프리미엄 이코노미"; break;
                                case BUSINESS: cabinLabel = "비즈니스"; break;
                                case FIRST: cabinLabel = "일등"; break;
                            }
                        }
                    }
                }
            }

            String priceText = lowestPrice != null ? nf.format(lowestPrice.longValue()) : "---";

            JPanel pricePanel = new JPanel();
            pricePanel.setLayout(new BoxLayout(pricePanel, BoxLayout.Y_AXIS));
            pricePanel.setBackground(ModernUI.CARD_BG);
            pricePanel.setOpaque(true);
            pricePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 16));
            JLabel priceLbl = new JLabel(priceText);
            priceLbl.setFont(new Font("System", Font.BOLD, 20));
            priceLbl.setForeground(ModernUI.PRIMARY);
            JLabel currencyLbl = new JLabel("KRW");
            currencyLbl.setFont(ModernUI.FONT_SMALL);
            currencyLbl.setForeground(ModernUI.PRIMARY);
            JLabel cabinLbl = new JLabel(cabinLabel);
            cabinLbl.setFont(ModernUI.FONT_SMALL);
            cabinLbl.setForeground(ModernUI.TEXT_SECONDARY);
            pricePanel.add(priceLbl);
            pricePanel.add(currencyLbl);
            pricePanel.add(cabinLbl);

            add(pricePanel, BorderLayout.EAST);
        }

        private void selectCard() {
            for (java.awt.Component c : cardListPanel.getComponents()) {
                if (c instanceof FlightCard) {
                    ((FlightCard) c).setSelected(false);
                }
            }
            setSelected(true);
        }

        private void setSelected(boolean selected) {
            isSelected = selected;
            if (selected) {
                setBackground(ModernUI.PRIMARY_LIGHT);
                setBorder(BorderFactory.createMatteBorder(2, 0, 2, 0, ModernUI.PRIMARY));
            } else {
                setBackground(ModernUI.CARD_BG);
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ModernUI.BORDER));
            }
            repaintCard();
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