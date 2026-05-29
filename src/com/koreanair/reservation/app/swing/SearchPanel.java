package com.koreanair.reservation.app.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicScrollBarUI;

import com.koreanair.reservation.control.BookingController;
import com.koreanair.reservation.domain.flight.FlightSchedule;

/**
 * 항공편 검색 — iter4 KE 스타일.
 *
 * <p>모드 3종:
 * <ul>
 *   <li>편도 — 출발/도착/가는날</li>
 *   <li>왕복 — 출발/도착/가는날/오는날</li>
 *   <li>다도시 — segment 3개 동적</li>
 * </ul>
 * 결과는 검색 버튼 누른 후에만 표시.
 */
public class SearchPanel extends JPanel {

    private static final String[][] AIRPORTS = {
        {"NYC", "뉴욕 (다공항)"},
        {"TYO", "도쿄 (다공항)"},
        {"LON", "런던 (다공항)"},
        {"SEL", "서울 (다공항)"},
        {"ICN", "인천"},
        {"GMP", "김포"},
        {"NRT", "도쿄 나리타"},
        {"HND", "도쿄 하네다"},
        {"FUK", "후쿠오카"},
        {"SIN", "싱가포르"},
        {"BKK", "방콕"},
        {"HKG", "홍콩"},
        {"PVG", "상하이 푸동"},
        {"DEL", "델리"},
        {"LAX", "로스앤젤레스"},
        {"JFK", "뉴욕 JFK"},
        {"LGA", "뉴욕 라과디아"},
        {"EWR", "뉴어크"},
        {"SYD", "시드니"},
        {"CDG", "파리 샤를드골"},
        {"FRA", "프랑크푸르트"},
        {"LHR", "런던 히드로"},
        {"LGW", "런던 게트윅"},
        {"STN", "런던 스탠스테드"}
    };

    private enum Mode { ONE_WAY, ROUND_TRIP, MULTI_CITY }

    private Mode mode = Mode.ONE_WAY;

    private final JToggleButton oneWayBtn = new JToggleButton("편도");
    private final JToggleButton roundBtn = new JToggleButton("왕복");
    private final JToggleButton multiBtn = new JToggleButton("다도시");

    // 편도/왕복
    private final JComboBox<String> fromCombo = new JComboBox<>(displayOptions());
    private final JComboBox<String> toCombo = new JComboBox<>(displayOptions());
    private final DatePickerField depDate = new DatePickerField();
    private final DatePickerField retDate = new DatePickerField();
    private final JLabel retDateLabel = new JLabel("오는 날");
    private final JSpinner paxSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 9, 1));

    // 다도시 segments
    private final List<SegmentRow> segmentRows = new ArrayList<>();
    private final JPanel multiCityPanel = new JPanel();
    private final JButton addLegButton = new JButton("+ 구간 추가");

    private final JButton searchButton = new JButton("항공편 검색");
    private final JButton nextButton = new JButton("다음 단계 →");

    private final JPanel cardListPanel = new JPanel();
    private final JScrollPane scrollPane = new JScrollPane();
    private final JLabel resultCount = new JLabel("");
    private final JLabel emptyHint = new JLabel("검색 조건을 입력하고 [항공편 검색]을 눌러주세요.", SwingConstants.CENTER);

    private final MainFrame frame;
    private final BookingController booking;
    private final SwingReservationUI ui;
    private List<FlightSchedule> currentResults = new ArrayList<>();
    private FlightSchedule selectedOutbound;
    private FlightSchedule selectedInbound;
    private final List<FlightSchedule> multiCitySelected = new ArrayList<>();
    private FlightCard selectedCard;
    private JPanel inputCard;
    private JPanel oneWayInputs;

    public SearchPanel(MainFrame frame, BookingController booking, SwingReservationUI ui) {
        super(new BorderLayout(0, 16));
        this.frame = frame;
        this.booking = booking;
        this.ui = ui;
        setBackground(ModernUI.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        toCombo.setSelectedIndex(6);
        retDate.setSelectedDate(LocalDate.now().plusDays(8));

        // 기본 segments 3개 다도시용
        segmentRows.add(new SegmentRow("ICN", "NRT", LocalDate.now().plusDays(1)));
        segmentRows.add(new SegmentRow("NRT", "BKK", LocalDate.now().plusDays(5)));
        segmentRows.add(new SegmentRow("BKK", "ICN", LocalDate.now().plusDays(10)));

        add(buildSearchCard(), BorderLayout.NORTH);
        add(buildResultsArea(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        switchMode(Mode.ONE_WAY);
        showEmptyHint();
    }

    private String[] displayOptions() {
        String[] out = new String[AIRPORTS.length];
        for (int i = 0; i < AIRPORTS.length; i++) {
            out[i] = AIRPORTS[i][0] + " — " + AIRPORTS[i][1];
        }
        return out;
    }

    private String airportCodeOf(String displayed) {
        if (displayed == null) return null;
        int idx = displayed.indexOf(' ');
        return idx < 0 ? displayed : displayed.substring(0, idx);
    }

    private JPanel buildSearchCard() {
        inputCard = ModernUI.cardPanel();
        inputCard.setLayout(new BorderLayout(0, 16));

        // 헤더 + 토글
        JPanel head = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        head.setOpaque(false);
        ButtonGroup grp = new ButtonGroup();
        styleToggle(oneWayBtn, true, Mode.ONE_WAY);
        styleToggle(roundBtn, false, Mode.ROUND_TRIP);
        styleToggle(multiBtn, false, Mode.MULTI_CITY);
        grp.add(oneWayBtn);
        grp.add(roundBtn);
        grp.add(multiBtn);
        head.add(oneWayBtn);
        head.add(roundBtn);
        head.add(multiBtn);

        JLabel title = new JLabel("항공편 검색");
        title.setFont(ModernUI.FONT_HEADING);
        title.setForeground(ModernUI.KE_NAVY);
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.add(title, BorderLayout.WEST);
        headerRow.add(head, BorderLayout.EAST);

        // 편도/왕복 입력 그리드
        oneWayInputs = new JPanel(new GridBagLayout());
        oneWayInputs.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;
        gc.insets = new Insets(4, 0, 4, 12);

        gc.gridx = 0; gc.gridy = 0;
        oneWayInputs.add(fieldLabel("출발지"), gc);
        gc.gridy = 1;
        ModernUI.styleComboBox(fromCombo);
        oneWayInputs.add(fromCombo, gc);

        gc.gridx = 1; gc.gridy = 0;
        oneWayInputs.add(fieldLabel("도착지"), gc);
        gc.gridy = 1;
        ModernUI.styleComboBox(toCombo);
        oneWayInputs.add(toCombo, gc);

        gc.gridx = 2; gc.gridy = 0; gc.weightx = 0.8;
        oneWayInputs.add(fieldLabel("가는 날"), gc);
        gc.gridy = 1;
        oneWayInputs.add(depDate, gc);

        gc.gridx = 3; gc.gridy = 0; gc.weightx = 0.8;
        oneWayInputs.add(retDateLabel, gc);
        gc.gridy = 1;
        oneWayInputs.add(retDate, gc);
        retDateLabel.setFont(ModernUI.FONT_SMALL);
        retDateLabel.setForeground(ModernUI.TEXT_SECONDARY);

        gc.gridx = 4; gc.gridy = 0; gc.weightx = 0.4;
        oneWayInputs.add(fieldLabel("승객"), gc);
        gc.gridy = 1;
        stylePaxSpinner();
        oneWayInputs.add(paxSpinner, gc);

        // 다도시 입력
        multiCityPanel.setOpaque(false);
        multiCityPanel.setLayout(new BoxLayout(multiCityPanel, BoxLayout.Y_AXIS));
        refreshMultiCityRows();

        ModernUI.styleButtonSecondary(addLegButton);
        addLegButton.addActionListener(e -> {
            if (segmentRows.size() >= 5) return;
            segmentRows.add(new SegmentRow("ICN", "NRT", LocalDate.now().plusDays(15)));
            refreshMultiCityRows();
            revalidate();
            repaint();
        });

        JPanel multiWrap = new JPanel(new BorderLayout(0, 8));
        multiWrap.setOpaque(false);
        multiWrap.add(multiCityPanel, BorderLayout.CENTER);
        JPanel addRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        addRow.setOpaque(false);
        addRow.add(addLegButton);
        multiWrap.add(addRow, BorderLayout.SOUTH);

        // 검색 버튼
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actionRow.setOpaque(false);
        ModernUI.styleButtonAccent(searchButton);
        searchButton.setPreferredSize(new Dimension(180, 46));
        searchButton.addActionListener(e -> doSearch());
        actionRow.add(searchButton);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.add(headerRow);
        content.add(Box.createVerticalStrut(16));
        content.add(oneWayInputs);
        content.add(multiWrap);
        content.add(Box.createVerticalStrut(12));
        content.add(actionRow);
        inputCard.add(content, BorderLayout.CENTER);

        multiWrap.setVisible(false);
        return inputCard;
    }

    private void refreshMultiCityRows() {
        multiCityPanel.removeAll();
        for (int i = 0; i < segmentRows.size(); i++) {
            SegmentRow row = segmentRows.get(i);
            row.label.setText("구간 " + (i + 1));
            multiCityPanel.add(row.render());
            multiCityPanel.add(Box.createVerticalStrut(8));
        }
    }

    private void switchMode(Mode m) {
        this.mode = m;
        oneWayBtn.setSelected(m == Mode.ONE_WAY);
        roundBtn.setSelected(m == Mode.ROUND_TRIP);
        multiBtn.setSelected(m == Mode.MULTI_CITY);
        applyToggleStyles();

        retDate.setVisible(m == Mode.ROUND_TRIP);
        retDateLabel.setVisible(m == Mode.ROUND_TRIP);

        boolean isMulti = m == Mode.MULTI_CITY;
        oneWayInputs.setVisible(!isMulti);
        // multiCityPanel parent visibility
        java.awt.Component[] children = inputCard.getComponents();
        for (java.awt.Component c : children) {
            // skip
        }
        for (java.awt.Component c : ((JPanel)((BorderLayout)inputCard.getLayout()).getLayoutComponent(BorderLayout.CENTER)).getComponents()) {
            if (c instanceof JPanel) {
                JPanel p = (JPanel) c;
                if (p.getComponentCount() > 0 && p.getComponent(0) == multiCityPanel) {
                    p.setVisible(isMulti);
                }
            }
        }
        currentResults.clear();
        selectedOutbound = null;
        selectedInbound = null;
        multiCitySelected.clear();
        selectedCard = null;
        nextButton.setEnabled(false);
        showEmptyHint();
        revalidate();
        repaint();
    }

    private void applyToggleStyles() {
        applyToggle(oneWayBtn, mode == Mode.ONE_WAY);
        applyToggle(roundBtn, mode == Mode.ROUND_TRIP);
        applyToggle(multiBtn, mode == Mode.MULTI_CITY);
    }

    private void applyToggle(JToggleButton btn, boolean on) {
        btn.setBackground(on ? ModernUI.KE_NAVY : Color.WHITE);
        btn.setForeground(on ? Color.WHITE : ModernUI.KE_NAVY);
    }

    private void styleToggle(JToggleButton btn, boolean selected, Mode targetMode) {
        btn.setSelected(selected);
        btn.setFont(ModernUI.FONT_BODY_BOLD);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new ModernUI.RoundedBorder(ModernUI.KE_NAVY, 1, ModernUI.CORNER_RADIUS_SM),
                BorderFactory.createEmptyBorder(8, 18, 8, 18)));
        btn.setFocusPainted(false);
        btn.setFocusable(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        applyToggle(btn, selected);
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn.addActionListener(e -> switchMode(targetMode));
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(ModernUI.FONT_SMALL);
        l.setForeground(ModernUI.TEXT_SECONDARY);
        return l;
    }

    private void stylePaxSpinner() {
        paxSpinner.setFont(ModernUI.FONT_BODY);
        paxSpinner.setBorder(BorderFactory.createCompoundBorder(
                new ModernUI.RoundedBorder(ModernUI.BORDER, 1, ModernUI.CORNER_RADIUS_SM),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        ((JSpinner.DefaultEditor) paxSpinner.getEditor()).getTextField().setFont(ModernUI.FONT_BODY);
    }

    private JPanel buildResultsArea() {
        JPanel wrap = new JPanel(new BorderLayout(0, 8));
        wrap.setOpaque(false);

        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        JLabel title = new JLabel("검색 결과");
        title.setFont(ModernUI.FONT_HEADING);
        title.setForeground(ModernUI.TEXT_PRIMARY);
        resultCount.setFont(ModernUI.FONT_SMALL);
        resultCount.setForeground(ModernUI.TEXT_SECONDARY);
        head.add(title, BorderLayout.WEST);
        head.add(resultCount, BorderLayout.EAST);
        wrap.add(head, BorderLayout.NORTH);

        cardListPanel.setLayout(new BoxLayout(cardListPanel, BoxLayout.Y_AXIS));
        cardListPanel.setOpaque(false);

        scrollPane.setViewportView(cardListPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(ModernUI.BACKGROUND);
        scrollPane.getViewport().setOpaque(true);
        scrollPane.setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = ModernUI.BORDER_STRONG;
                this.trackColor = ModernUI.BACKGROUND;
            }
        });
        wrap.add(scrollPane, BorderLayout.CENTER);
        emptyHint.setFont(ModernUI.FONT_BODY);
        emptyHint.setForeground(ModernUI.TEXT_MUTED);
        return wrap;
    }

    private void showEmptyHint() {
        cardListPanel.removeAll();
        cardListPanel.add(Box.createVerticalStrut(48));
        cardListPanel.add(emptyHint);
        resultCount.setText("");
        cardListPanel.revalidate();
        cardListPanel.repaint();
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        JLabel hint = new JLabel("결과 카드에서 항공편을 선택한 뒤 '다음 단계' 클릭");
        hint.setFont(ModernUI.FONT_SMALL);
        hint.setForeground(ModernUI.TEXT_SECONDARY);
        footer.add(hint, BorderLayout.WEST);

        ModernUI.styleButtonAccent(nextButton);
        nextButton.setPreferredSize(new Dimension(160, 44));
        nextButton.setEnabled(false);
        nextButton.addActionListener(e -> proceedWithSelection());
        footer.add(nextButton, BorderLayout.EAST);
        return footer;
    }

    private void doSearch() {
        if (mode == Mode.MULTI_CITY) {
            doMultiCitySearch();
            return;
        }
        LocalDate date = depDate.getSelectedDate();
        String from = airportCodeOf((String) fromCombo.getSelectedItem());
        String to = airportCodeOf((String) toCombo.getSelectedItem());
        List<FlightSchedule> results = booking.processSearch(from, to, date);
        currentResults = results != null ? results : new ArrayList<>();
        if (mode == Mode.ROUND_TRIP) {
            // 결과 = 가는편만 표시, 선택 시 오는편 자동 검색
            refreshCardList("가는 편");
        } else {
            refreshCardList("항공편");
        }
        if (currentResults.isEmpty()) {
            showEmptyHint();
            emptyHint.setText("조건에 맞는 항공편이 없습니다.");
        }
    }

    private void doMultiCitySearch() {
        currentResults.clear();
        cardListPanel.removeAll();
        multiCitySelected.clear();

        List<List<FlightSchedule>> legs = new ArrayList<>();
        for (SegmentRow row : segmentRows) {
            String from = airportCodeOf((String) row.from.getSelectedItem());
            String to = airportCodeOf((String) row.to.getSelectedItem());
            LocalDate d = row.date.getSelectedDate();
            List<FlightSchedule> match = booking.processSearch(from, to, d);
            legs.add(match);
        }

        for (int i = 0; i < legs.size(); i++) {
            List<FlightSchedule> match = legs.get(i);
            JLabel sectionLabel = new JLabel("구간 " + (i + 1) + " · " + match.size() + "건");
            sectionLabel.setFont(ModernUI.FONT_SUBHEADING);
            sectionLabel.setForeground(ModernUI.KE_NAVY);
            sectionLabel.setBorder(BorderFactory.createEmptyBorder(8, 4, 4, 0));
            cardListPanel.add(sectionLabel);
            cardListPanel.add(Box.createVerticalStrut(6));
            if (match.isEmpty()) {
                JLabel none = new JLabel("매칭 없음");
                none.setFont(ModernUI.FONT_SMALL);
                none.setForeground(ModernUI.TEXT_MUTED);
                cardListPanel.add(none);
                cardListPanel.add(Box.createVerticalStrut(12));
                continue;
            }
            for (FlightSchedule fs : match) {
                FlightCard card = new FlightCard(fs, i);
                cardListPanel.add(card);
                cardListPanel.add(Box.createVerticalStrut(8));
            }
            cardListPanel.add(Box.createVerticalStrut(12));
        }

        // 각 leg 마다 첫 결과 기본 선택
        multiCitySelected.clear();
        for (List<FlightSchedule> match : legs) {
            multiCitySelected.add(match.isEmpty() ? null : match.get(0));
        }

        long totalCount = 0;
        for (List<FlightSchedule> m : legs) totalCount += m.size();
        resultCount.setText("다도시 " + segmentRows.size() + " 구간 · 총 " + totalCount + "건");
        cardListPanel.revalidate();
        cardListPanel.repaint();
        boolean allFilled = !multiCitySelected.contains(null);
        nextButton.setEnabled(allFilled);
    }

    private void refreshCardList(String labelPrefix) {
        cardListPanel.removeAll();
        selectedCard = null;
        selectedOutbound = null;
        selectedInbound = null;
        nextButton.setEnabled(false);

        resultCount.setText(currentResults.size() + "건");
        for (FlightSchedule s : currentResults) {
            FlightCard card = new FlightCard(s, -1);
            cardListPanel.add(card);
            cardListPanel.add(Box.createVerticalStrut(12));
        }
        cardListPanel.revalidate();
        cardListPanel.repaint();
    }

    private void proceedWithSelection() {
        if (!frame.isSignedIn()) {
            // 로그인 필요 시점: 1순위 선택을 가져가서 로그인 후 이어가야 한다.
            FlightSchedule first = null;
            if (mode == Mode.MULTI_CITY && !multiCitySelected.isEmpty()) {
                first = multiCitySelected.get(0);
            } else if (selectedOutbound != null) {
                first = selectedOutbound;
            }
            if (first == null) {
                ui.displayError("항공편을 선택하세요.");
                return;
            }
            frame.requireSignIn(first);
            return;
        }
        if (mode == Mode.ONE_WAY) {
            if (selectedOutbound == null) {
                ui.displayError("항공편을 선택하세요.");
                return;
            }
            frame.onFlightSelected(selectedOutbound);
        } else if (mode == Mode.ROUND_TRIP) {
            if (selectedOutbound == null) {
                ui.displayError("가는 편을 선택하세요.");
                return;
            }
            String from = airportCodeOf((String) toCombo.getSelectedItem());
            String to = airportCodeOf((String) fromCombo.getSelectedItem());
            List<FlightSchedule> inbound = booking.processSearch(from, to, retDate.getSelectedDate());
            if (inbound.isEmpty()) {
                ui.displayError("돌아오는 편을 찾을 수 없습니다.");
                return;
            }
            selectedInbound = inbound.get(0);
            frame.onRoundTripSelected(selectedOutbound, selectedInbound);
        } else {
            if (multiCitySelected.contains(null) || multiCitySelected.isEmpty()) {
                ui.displayError("모든 구간을 선택해야 합니다.");
                return;
            }
            frame.onMultiCitySelected(new ArrayList<>(multiCitySelected));
        }
    }

    /** 다도시 segment input row. */
    private class SegmentRow {
        final JLabel label = new JLabel("구간");
        final JComboBox<String> from = new JComboBox<>(displayOptions());
        final JComboBox<String> to = new JComboBox<>(displayOptions());
        final DatePickerField date = new DatePickerField();

        SegmentRow(String fromCode, String toCode, LocalDate d) {
            selectByCode(from, fromCode);
            selectByCode(to, toCode);
            date.setSelectedDate(d);
            ModernUI.styleComboBox(from);
            ModernUI.styleComboBox(to);
            label.setFont(ModernUI.FONT_SMALL);
            label.setForeground(ModernUI.TEXT_SECONDARY);
        }

        private void selectByCode(JComboBox<String> combo, String code) {
            for (int i = 0; i < combo.getItemCount(); i++) {
                String it = combo.getItemAt(i);
                if (it.startsWith(code + " ")) {
                    combo.setSelectedIndex(i);
                    return;
                }
            }
        }

        JPanel render() {
            JPanel row = new JPanel(new GridBagLayout());
            row.setOpaque(false);
            GridBagConstraints gc = new GridBagConstraints();
            gc.fill = GridBagConstraints.HORIZONTAL;
            gc.weightx = 1.0;
            gc.insets = new Insets(0, 0, 0, 8);

            gc.gridx = 0; gc.weightx = 0.12;
            row.add(label, gc);
            gc.gridx = 1; gc.weightx = 0.3;
            row.add(from, gc);
            gc.gridx = 2; gc.weightx = 0.3;
            row.add(to, gc);
            gc.gridx = 3; gc.weightx = 0.28; gc.insets = new Insets(0, 0, 0, 0);
            row.add(date, gc);
            return row;
        }
    }

    /** 검색 결과 카드. legIndex >= 0 이면 다도시 모드. */
    private class FlightCard extends ModernUI.CardPanel {
        private final FlightSchedule schedule;
        private final int legIndex;
        private boolean isSelected = false;

        FlightCard(FlightSchedule schedule, int legIndex) {
            this.schedule = schedule;
            this.legIndex = legIndex;
            setLayout(new BorderLayout(16, 0));
            setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
            setPreferredSize(new Dimension(0, 110));
            setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectThis();
                }
            });
            buildCardContent();
        }

        private void selectThis() {
            if (legIndex >= 0) {
                // 다도시 — 같은 leg 카드 중 하나만 선택
                java.awt.Component[] comps = cardListPanel.getComponents();
                for (java.awt.Component c : comps) {
                    if (c instanceof FlightCard) {
                        FlightCard fc = (FlightCard) c;
                        if (fc.legIndex == legIndex && fc != this) {
                            fc.isSelected = false;
                            fc.repaint();
                        }
                    }
                }
                isSelected = true;
                if (legIndex < multiCitySelected.size()) {
                    multiCitySelected.set(legIndex, schedule);
                }
                boolean all = !multiCitySelected.contains(null) && !multiCitySelected.isEmpty();
                nextButton.setEnabled(all);
                repaint();
            } else {
                if (selectedCard != null && selectedCard != this) {
                    selectedCard.isSelected = false;
                    selectedCard.repaint();
                }
                isSelected = true;
                selectedOutbound = schedule;
                selectedCard = this;
                nextButton.setEnabled(true);
                repaint();
            }
        }

        @Override
        protected void paintComponent(java.awt.Graphics g) {
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            int rad = ModernUI.CORNER_RADIUS * 2;
            g2.setColor(new Color(0, 0, 0, 8));
            g2.fillRoundRect(2, 4, w - 4 - ModernUI.SHADOW_OFFSET, h - 4 - ModernUI.SHADOW_OFFSET, rad, rad);
            g2.setColor(isSelected ? ModernUI.KE_NAVY_LIGHT : ModernUI.CARD_BG);
            g2.fillRoundRect(0, 0, w - ModernUI.SHADOW_OFFSET, h - ModernUI.SHADOW_OFFSET, rad, rad);
            g2.setColor(isSelected ? ModernUI.KE_NAVY : ModernUI.BORDER);
            g2.drawRoundRect(0, 0, w - ModernUI.SHADOW_OFFSET - 1, h - ModernUI.SHADOW_OFFSET - 1, rad, rad);
            if (isSelected) {
                g2.setColor(ModernUI.KE_NAVY);
                g2.fillRoundRect(0, 0, 6, h - ModernUI.SHADOW_OFFSET, rad, rad);
            }
            g2.dispose();
        }

        private void buildCardContent() {
            com.koreanair.reservation.domain.flight.Flight flight = schedule.getFlight();
            String flightNum = flight != null ? flight.getFlightNumber() : "-";
            String fromCode = "-", toCode = "-", fromTime = "-", toTime = "-", duration = "-";
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
                long m = Math.abs(mins);
                duration = (m / 60) + "h " + (m % 60) + "m";
            }

            JPanel left = new JPanel(new GridLayout(2, 1, 0, 4));
            left.setOpaque(false);
            JLabel airlineLbl = new JLabel("대한항공");
            airlineLbl.setFont(ModernUI.FONT_SMALL);
            airlineLbl.setForeground(ModernUI.TEXT_SECONDARY);
            JLabel flightLbl = new JLabel(flightNum);
            flightLbl.setFont(ModernUI.FONT_SUBHEADING);
            flightLbl.setForeground(ModernUI.TEXT_PRIMARY);
            left.add(airlineLbl);
            left.add(flightLbl);
            left.setPreferredSize(new Dimension(90, 0));
            add(left, BorderLayout.WEST);

            JPanel center = new JPanel(new GridBagLayout());
            center.setOpaque(false);
            GridBagConstraints gc = new GridBagConstraints();
            gc.insets = new Insets(0, 8, 0, 8);

            gc.gridx = 0; gc.anchor = GridBagConstraints.WEST;
            center.add(timeBlock(fromTime, fromCode), gc);

            gc.gridx = 1;
            JPanel mid = new JPanel();
            mid.setOpaque(false);
            mid.setLayout(new BoxLayout(mid, BoxLayout.Y_AXIS));
            JLabel dur = new JLabel(duration, SwingConstants.CENTER);
            dur.setFont(ModernUI.FONT_SMALL);
            dur.setForeground(ModernUI.TEXT_SECONDARY);
            dur.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel arrow = new JLabel("──────", SwingConstants.CENTER);
            arrow.setFont(ModernUI.FONT_SMALL);
            arrow.setForeground(ModernUI.KE_NAVY);
            arrow.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel stops = new JLabel("직항", SwingConstants.CENTER);
            stops.setFont(ModernUI.FONT_TINY);
            stops.setForeground(ModernUI.SUCCESS);
            stops.setAlignmentX(Component.CENTER_ALIGNMENT);
            mid.add(dur);
            mid.add(arrow);
            mid.add(stops);
            center.add(mid, gc);

            gc.gridx = 2;
            center.add(timeBlock(toTime, toCode), gc);
            add(center, BorderLayout.CENTER);

            add(priceBlock(flight), BorderLayout.EAST);
        }

        private JPanel timeBlock(String time, String code) {
            JPanel p = new JPanel();
            p.setOpaque(false);
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
            JLabel t = new JLabel(time);
            t.setFont(ModernUI.FONT_TITLE);
            t.setForeground(ModernUI.TEXT_PRIMARY);
            JLabel c = new JLabel(code);
            c.setFont(ModernUI.FONT_SMALL);
            c.setForeground(ModernUI.TEXT_SECONDARY);
            p.add(t);
            p.add(c);
            return p;
        }

        private JPanel priceBlock(com.koreanair.reservation.domain.flight.Flight flight) {
            JPanel p = new JPanel();
            p.setOpaque(false);
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
            BigDecimal lowest = null;
            String cabin = "이코노미";
            if (flight != null && flight.getFares() != null) {
                for (com.koreanair.reservation.domain.flight.Fare f : flight.getFares()) {
                    BigDecimal pp = f.getBasePrice();
                    if (pp != null && (lowest == null || pp.compareTo(lowest) < 0)) {
                        lowest = pp;
                    }
                    if (f.getCabinClass() != null) {
                        switch (f.getCabinClass()) {
                            case ECONOMY: cabin = "이코노미"; break;
                            case PREMIUM_ECONOMY: cabin = "프리미엄 이코노미"; break;
                            case BUSINESS: cabin = "비즈니스"; break;
                            case FIRST: cabin = "일등석"; break;
                        }
                    }
                }
            }
            NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
            String price = lowest != null ? nf.format(lowest.longValue()) + " KRW" : "---";

            JLabel cabinLbl = new JLabel(cabin);
            cabinLbl.setFont(ModernUI.FONT_SMALL);
            cabinLbl.setForeground(ModernUI.TEXT_SECONDARY);
            JLabel priceLbl = new JLabel(price);
            priceLbl.setFont(ModernUI.FONT_HEADING);
            priceLbl.setForeground(ModernUI.KE_RED);
            p.add(cabinLbl);
            p.add(priceLbl);
            return p;
        }
    }
}
