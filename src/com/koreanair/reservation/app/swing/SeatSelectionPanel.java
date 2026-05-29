package com.koreanair.reservation.app.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.lang.reflect.Field;
import java.math.BigDecimal;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.koreanair.reservation.app.AppConfig;
import com.koreanair.reservation.control.BookingController;
import com.koreanair.reservation.domain.flight.CabinClass;
import com.koreanair.reservation.domain.flight.Seat;
import com.koreanair.reservation.domain.flight.seatview.SeatView;
import com.koreanair.reservation.domain.flight.seatview.SeatViewBuilder;
import com.koreanair.reservation.domain.reservation.Reservation;

/**
 * 좌석 선택 — iter4 강화.
 *
 * <p>차트 시각화: 1~2행 First(골드), 3~6행 Business(진청), 7~10행 Premium Economy(퍼플),
 * 11~30행 Economy(그레이). A·F 창가, C·D 통로, 14행 비상구 추가 레그룸.
 * <p>DP#9 Decorator 활용: SeatViewBuilder 가 Seat 메타에 맞춰 Decorator 체인 조립,
 * tooltip 에 description + 추가요금 + 라벨 노출. AppConfig Singleton 의 isShowSeatMetadata()
 * 토글로 hover 메타 ON/OFF.
 */
public class SeatSelectionPanel extends JPanel {

    private static final int ROWS = 30;
    private static final char[] COLS = { 'A', 'B', 'C', 'D', 'E', 'F' };
    private static final int EXIT_ROW = 14;

    private final MainFrame frame;
    private final BookingController booking;

    private final JLabel infoLabel = new JLabel("좌석을 선택해 주세요.");
    private final JLabel surchargeLabel = new JLabel("추가 요금 0원");
    private final JButton confirmButton = new JButton("확인");
    private final JButton backButton = new JButton("← 뒤로");

    private Reservation reservation;
    private String selectedSeat;
    private JButton selectedButton;
    private CabinClass selectedClass;
    private BigDecimal selectedSurcharge = BigDecimal.ZERO;

    public SeatSelectionPanel(MainFrame parent, BookingController bookingController) {
        super(new BorderLayout());
        this.frame = parent;
        this.booking = bookingController;
        setBackground(ModernUI.BACKGROUND);
        setOpaque(true);
        buildLayout();
        AppConfig.getInstance().addChangeListener(cfg -> refreshTooltips());
    }

    private void buildLayout() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ModernUI.BACKGROUND);
        header.setOpaque(true);
        header.setBorder(BorderFactory.createEmptyBorder(20, 24, 12, 24));
        JLabel title = new JLabel("좌석 선택");
        title.setFont(ModernUI.FONT_HEADING);
        title.setForeground(ModernUI.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);
        infoLabel.setFont(ModernUI.FONT_BODY);
        infoLabel.setForeground(ModernUI.TEXT_SECONDARY);
        header.add(infoLabel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel chartWrap = new JPanel(new BorderLayout(0, 12));
        chartWrap.setBackground(ModernUI.BACKGROUND);
        chartWrap.setOpaque(true);
        chartWrap.add(buildLegend(), BorderLayout.NORTH);

        JPanel seatGrid = buildSeatGrid();
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(ModernUI.BACKGROUND);
        center.setOpaque(true);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        center.add(seatGrid, gc);
        chartWrap.add(new javax.swing.JScrollPane(center), BorderLayout.CENTER);
        add(chartWrap, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        footer.setBackground(ModernUI.CARD_BG);
        footer.setOpaque(true);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ModernUI.BORDER));
        surchargeLabel.setFont(ModernUI.FONT_BODY);
        surchargeLabel.setForeground(ModernUI.TEXT_PRIMARY);
        footer.add(surchargeLabel);
        footer.add(javax.swing.Box.createHorizontalStrut(24));
        ModernUI.styleButtonSecondary(backButton);
        ModernUI.styleButton(confirmButton);
        backButton.addActionListener(e -> frame.showPassenger());
        confirmButton.addActionListener(e -> doConfirm());
        footer.add(backButton);
        footer.add(confirmButton);
        footer.setPreferredSize(new Dimension(0, 60));
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel buildLegend() {
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 6));
        legend.setBackground(ModernUI.BACKGROUND);
        legend.setOpaque(true);
        legend.add(legendDot(ModernUI.SEAT_FIRST_BG, ModernUI.SEAT_FIRST, "First"));
        legend.add(legendDot(ModernUI.SEAT_BUSINESS_BG, ModernUI.SEAT_BUSINESS, "Business"));
        legend.add(legendDot(ModernUI.SEAT_PREMIUM_BG, ModernUI.SEAT_PREMIUM, "Premium Economy"));
        legend.add(legendDot(ModernUI.SEAT_ECONOMY_BG, ModernUI.SEAT_ECONOMY, "Economy"));
        legend.add(legendDot(ModernUI.SEAT_OCCUPIED, ModernUI.SEAT_OCCUPIED, "점유"));
        legend.add(legendDot(ModernUI.PRIMARY, ModernUI.PRIMARY, "선택"));
        return legend;
    }

    private JPanel legendDot(Color bg, Color border, String label) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        row.setOpaque(false);
        JLabel dot = new JLabel("  ");
        dot.setOpaque(true);
        dot.setBackground(bg);
        dot.setBorder(BorderFactory.createLineBorder(border, 1));
        dot.setPreferredSize(new Dimension(18, 14));
        JLabel txt = new JLabel(label);
        txt.setFont(ModernUI.FONT_SMALL);
        txt.setForeground(ModernUI.TEXT_SECONDARY);
        row.add(dot);
        row.add(txt);
        return row;
    }

    private JPanel buildSeatGrid() {
        JPanel grid = new JPanel(new GridLayout(ROWS + 1, COLS.length + 2, 6, 6));
        grid.setBackground(ModernUI.CARD_BG);
        grid.setOpaque(true);
        grid.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernUI.BORDER, 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));

        grid.add(makeHeaderLabel(""));
        for (char c : new char[]{'A','B','C'}) {
            grid.add(makeHeaderLabel(String.valueOf(c)));
        }
        grid.add(makeHeaderLabel(""));
        for (char c : new char[]{'D','E','F'}) {
            grid.add(makeHeaderLabel(String.valueOf(c)));
        }

        for (int row = 1; row <= ROWS; row++) {
            grid.add(makeHeaderLabel(String.valueOf(row)));
            for (int col = 0; col < COLS.length; col++) {
                if (col == 3) {
                    grid.add(makeAisleSpacer());
                }
                grid.add(makeSeatButton(row, COLS[col]));
            }
        }
        return grid;
    }

    private JLabel makeHeaderLabel(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(ModernUI.FONT_SMALL);
        l.setForeground(ModernUI.TEXT_SECONDARY);
        l.setPreferredSize(new Dimension(36, 28));
        return l;
    }

    private JLabel makeAisleSpacer() {
        JLabel l = new JLabel(" ", SwingConstants.CENTER);
        l.setPreferredSize(new Dimension(20, 28));
        return l;
    }

    private JButton makeSeatButton(int row, char col) {
        String seatNo = row + String.valueOf(col);
        CabinClass cc = classOfRow(row);
        boolean window = (col == 'A' || col == 'F');
        boolean aisle = (col == 'C' || col == 'D');
        boolean extraLegroom = (row == EXIT_ROW);
        Seat seat = buildSampleSeat(seatNo, cc, window, aisle, extraLegroom);
        SeatView view = SeatViewBuilder.decorate(seat);

        JButton btn = new JButton(seatNo);
        btn.setFont(ModernUI.FONT_SMALL);
        btn.setForeground(textColorFor(cc));
        btn.setBackground(bgColorFor(cc));
        btn.setBorder(BorderFactory.createLineBorder(seatBorderFor(cc), 1));
        btn.setFocusPainted(false);
        btn.setFocusable(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(false);
        btn.setPreferredSize(new Dimension(40, 32));
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn.putClientProperty("seat", seat);
        btn.putClientProperty("seatView", view);
        btn.putClientProperty("cabinClass", cc);
        btn.setToolTipText(buildTooltip(view));
        btn.addActionListener(e -> selectSeat(btn, seatNo, view, cc));
        return btn;
    }

    private CabinClass classOfRow(int row) {
        if (row <= 2) {
            return CabinClass.FIRST;
        } else if (row <= 6) {
            return CabinClass.BUSINESS;
        } else if (row <= 10) {
            return CabinClass.PREMIUM_ECONOMY;
        }
        return CabinClass.ECONOMY;
    }

    private Color bgColorFor(CabinClass cc) {
        switch (cc) {
            case FIRST: return ModernUI.SEAT_FIRST_BG;
            case BUSINESS: return ModernUI.SEAT_BUSINESS_BG;
            case PREMIUM_ECONOMY: return ModernUI.SEAT_PREMIUM_BG;
            case ECONOMY:
            default: return ModernUI.SEAT_ECONOMY_BG;
        }
    }

    private Color textColorFor(CabinClass cc) {
        switch (cc) {
            case FIRST: return ModernUI.SEAT_FIRST;
            case BUSINESS: return ModernUI.SEAT_BUSINESS;
            case PREMIUM_ECONOMY: return ModernUI.SEAT_PREMIUM;
            case ECONOMY:
            default: return ModernUI.SEAT_ECONOMY;
        }
    }

    private Color seatBorderFor(CabinClass cc) {
        switch (cc) {
            case FIRST: return ModernUI.SEAT_FIRST;
            case BUSINESS: return ModernUI.SEAT_BUSINESS;
            case PREMIUM_ECONOMY: return ModernUI.SEAT_PREMIUM;
            case ECONOMY:
            default: return ModernUI.BORDER;
        }
    }

    private String buildTooltip(SeatView view) {
        if (!AppConfig.getInstance().isShowSeatMetadata()) {
            return null;
        }
        StringBuilder sb = new StringBuilder("<html><body style='font-family:sans-serif;padding:4px;'>");
        sb.append("<b>").append(view.getDescription()).append("</b><br/>");
        BigDecimal surcharge = view.getSurcharge();
        sb.append("추가 요금: ").append(formatKrw(surcharge)).append("<br/>");
        for (String label : view.getMetadataLabels()) {
            sb.append("· ").append(label).append("<br/>");
        }
        sb.append("</body></html>");
        return sb.toString();
    }

    private void refreshTooltips() {
        java.awt.Component[] comps = getComponentsAll(this);
        for (java.awt.Component c : comps) {
            if (c instanceof JButton) {
                JButton btn = (JButton) c;
                Object v = btn.getClientProperty("seatView");
                if (v instanceof SeatView) {
                    btn.setToolTipText(buildTooltip((SeatView) v));
                }
            }
        }
    }

    private java.awt.Component[] getComponentsAll(java.awt.Container container) {
        java.util.List<java.awt.Component> result = new java.util.ArrayList<>();
        collect(container, result);
        return result.toArray(new java.awt.Component[0]);
    }

    private void collect(java.awt.Container container, java.util.List<java.awt.Component> out) {
        for (java.awt.Component c : container.getComponents()) {
            out.add(c);
            if (c instanceof java.awt.Container) {
                collect((java.awt.Container) c, out);
            }
        }
    }

    private String formatKrw(BigDecimal amount) {
        if (amount == null) {
            return "0원";
        }
        return String.format("%,d원", amount.longValue());
    }

    private Seat buildSampleSeat(String seatNo, CabinClass cc, boolean win, boolean aisle, boolean extra) {
        Seat seat = new Seat(seatNo, cc);
        setField(seat, "windowSeat", win);
        setField(seat, "aisleSeat", aisle);
        setField(seat, "extraLegroom", extra);
        return seat;
    }

    private void setField(Object target, String name, Object value) {
        try {
            Field f = target.getClass().getDeclaredField(name);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception ignored) {
        }
    }

    private void selectSeat(JButton btn, String seatNo, SeatView view, CabinClass cc) {
        if (selectedButton != null) {
            CabinClass prevCc = (CabinClass) selectedButton.getClientProperty("cabinClass");
            selectedButton.setBackground(bgColorFor(prevCc));
            selectedButton.setForeground(textColorFor(prevCc));
            selectedButton.setBorder(BorderFactory.createLineBorder(seatBorderFor(prevCc), 1));
        }
        btn.setBackground(ModernUI.PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(ModernUI.PRIMARY_HOVER, 2));
        selectedButton = btn;
        selectedSeat = seatNo;
        selectedClass = cc;
        selectedSurcharge = view.getSurcharge();
        infoLabel.setText("선택한 좌석: " + view.getDescription());
        surchargeLabel.setText("추가 요금 " + formatKrw(selectedSurcharge));
    }

    public void setReservation(Reservation r) {
        this.reservation = r;
        this.selectedSeat = null;
        if (selectedButton != null) {
            CabinClass prevCc = (CabinClass) selectedButton.getClientProperty("cabinClass");
            selectedButton.setBackground(bgColorFor(prevCc));
            selectedButton.setForeground(textColorFor(prevCc));
            selectedButton.setBorder(BorderFactory.createLineBorder(seatBorderFor(prevCc), 1));
            selectedButton = null;
        }
        infoLabel.setText("좌석을 선택해 주세요. (PNR: "
                + (r != null && r.getPnrNumber() != null ? r.getPnrNumber() : "-") + ")");
        surchargeLabel.setText("추가 요금 0원");
    }

    private void doConfirm() {
        if (selectedSeat == null) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "좌석을 먼저 선택하세요.",
                    "안내",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (reservation != null) {
            booking.assignSeat(reservation, selectedSeat);
        }
        frame.onSeatAssigned(reservation);
    }
}
