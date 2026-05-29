package com.koreanair.reservation.app.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import com.koreanair.reservation.app.AppConfig;
import com.koreanair.reservation.control.BusTicketingService;
import com.koreanair.reservation.domain.bus.BusCity;
import com.koreanair.reservation.domain.bus.BusSchedule;
import com.koreanair.reservation.domain.bus.BusSeat;

/**
 * 우등고속 프리미엄 셔틀 좌석 선택 다이얼로그 패널.
 *
 * <p>BusTicketingService 에서 도시별 운행 스케줄을 조회해 ComboBox 로 노출,
 * 선택한 BusSchedule 의 28좌석 (1+2 배열)을 차트로 그린다. 창가·통로 표시 + AppConfig
 * 의 isShowSeatMetadata() 가 켜져있으면 hover 메타 (창가/통로) 표시.
 */
public class BusSeatSelectionPanel extends JPanel {

    private final BusTicketingService busService;
    private final BusCity originCity;
    private final JComboBox<BusSchedule> scheduleCombo;
    private final JPanel chartPanel;
    private final JLabel infoLabel = new JLabel("좌석을 선택해 주세요.");
    private final JButton confirmButton = new JButton("선택 완료");

    private BusSchedule selectedSchedule;
    private BusSeat selectedSeat;
    private JButton selectedSeatButton;
    private Runnable onConfirm;

    public BusSeatSelectionPanel(BusTicketingService busService, BusCity originCity) {
        super(new BorderLayout(0, 8));
        this.busService = busService;
        this.originCity = originCity;
        setBackground(ModernUI.BACKGROUND);
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        this.scheduleCombo = new JComboBox<>();
        for (BusSchedule s : busService.schedulesFor(originCity)) {
            scheduleCombo.addItem(s);
        }
        scheduleCombo.addActionListener(e -> refreshChart());

        this.chartPanel = new JPanel();
        chartPanel.setBackground(ModernUI.CARD_BG);
        chartPanel.setOpaque(true);
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernUI.BORDER, 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));

        buildLayout();
        if (scheduleCombo.getItemCount() > 0) {
            scheduleCombo.setSelectedIndex(0);
            refreshChart();
        }
    }

    public void setOnConfirm(Runnable handler) {
        this.onConfirm = handler;
    }

    public BusSchedule getSelectedSchedule() {
        return selectedSchedule;
    }

    public BusSeat getSelectedSeat() {
        return selectedSeat;
    }

    private void buildLayout() {
        JPanel top = new JPanel(new BorderLayout(8, 4));
        top.setOpaque(false);
        JLabel title = new JLabel(originCity.getDisplayName() + " → 인천공항 셔틀 좌석 선택");
        title.setFont(ModernUI.FONT_HEADING);
        title.setForeground(ModernUI.TEXT_PRIMARY);
        top.add(title, BorderLayout.NORTH);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        row.setOpaque(false);
        JLabel scheduleLabel = new JLabel("출발 시간:");
        scheduleLabel.setFont(ModernUI.FONT_BODY);
        scheduleLabel.setForeground(ModernUI.TEXT_SECONDARY);
        row.add(scheduleLabel);
        row.add(scheduleCombo);
        top.add(row, BorderLayout.CENTER);
        top.add(buildLegend(), BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);

        JPanel chartWrap = new JPanel(new BorderLayout());
        chartWrap.setOpaque(false);
        chartWrap.add(new JScrollPane(chartPanel), BorderLayout.CENTER);
        add(chartWrap, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        infoLabel.setFont(ModernUI.FONT_BODY);
        infoLabel.setForeground(ModernUI.TEXT_PRIMARY);
        footer.add(infoLabel, BorderLayout.WEST);
        ModernUI.styleButton(confirmButton);
        confirmButton.addActionListener(e -> {
            if (selectedSeat == null) {
                javax.swing.JOptionPane.showMessageDialog(this, "좌석을 먼저 선택해주세요.",
                        "안내", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            if (onConfirm != null) {
                onConfirm.run();
            }
        });
        footer.add(confirmButton, BorderLayout.EAST);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel buildLegend() {
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        legend.setOpaque(false);
        legend.add(legendDot(Color.WHITE, ModernUI.BORDER, "선택 가능"));
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
        dot.setPreferredSize(new Dimension(16, 12));
        JLabel txt = new JLabel(label);
        txt.setFont(ModernUI.FONT_SMALL);
        txt.setForeground(ModernUI.TEXT_SECONDARY);
        row.add(dot);
        row.add(txt);
        return row;
    }

    private void refreshChart() {
        chartPanel.removeAll();
        selectedSeatButton = null;
        selectedSeat = null;
        infoLabel.setText("좌석을 선택해 주세요.");

        Object item = scheduleCombo.getSelectedItem();
        if (!(item instanceof BusSchedule)) {
            chartPanel.revalidate();
            chartPanel.repaint();
            return;
        }
        this.selectedSchedule = (BusSchedule) item;

        java.util.List<BusSeat> seats = selectedSchedule.getSeats();
        // 1 + (통로) + 2 배열: [행번호] [A] [통로] [B] [C]
        int rows = (seats.size() + 2) / 3;
        chartPanel.setLayout(new GridLayout(rows + 1, 5, 6, 6));
        chartPanel.add(headerLabel(""));
        chartPanel.add(headerLabel("A"));
        chartPanel.add(aisleHeader());
        chartPanel.add(headerLabel("B"));
        chartPanel.add(headerLabel("C"));

        java.util.Map<String, BusSeat> byName = new java.util.HashMap<>();
        for (BusSeat s : seats) {
            byName.put(s.getSeatNumber(), s);
        }
        for (int row = 1; row <= rows; row++) {
            chartPanel.add(headerLabel(String.valueOf(row)));
            BusSeat seatA = byName.get(row + "A");
            chartPanel.add(seatA != null ? seatButton(seatA) : new JLabel(""));
            chartPanel.add(aisleSpacer());
            BusSeat seatB = byName.get(row + "B");
            chartPanel.add(seatB != null ? seatButton(seatB) : new JLabel(""));
            BusSeat seatC = byName.get(row + "C");
            chartPanel.add(seatC != null ? seatButton(seatC) : new JLabel(""));
        }
        chartPanel.revalidate();
        chartPanel.repaint();
    }

    private JLabel headerLabel(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(ModernUI.FONT_SMALL);
        l.setForeground(ModernUI.TEXT_SECONDARY);
        l.setPreferredSize(new Dimension(44, 28));
        return l;
    }

    private JLabel aisleHeader() {
        JLabel l = new JLabel("통로", SwingConstants.CENTER);
        l.setFont(ModernUI.FONT_TINY);
        l.setForeground(ModernUI.TEXT_MUTED);
        l.setPreferredSize(new Dimension(20, 28));
        return l;
    }

    private JLabel aisleSpacer() {
        JLabel l = new JLabel("", SwingConstants.CENTER);
        l.setOpaque(true);
        l.setBackground(new java.awt.Color(0xF4, 0xF6, 0xF9));
        l.setPreferredSize(new Dimension(20, 36));
        return l;
    }

    private JButton seatButton(BusSeat seat) {
        JButton btn = new JButton(seat.getSeatNumber());
        btn.setFont(ModernUI.FONT_SMALL);
        btn.setForeground(ModernUI.TEXT_PRIMARY);
        btn.setBackground(seat.isAvailable() ? Color.WHITE : ModernUI.SEAT_OCCUPIED);
        btn.setBorder(BorderFactory.createLineBorder(ModernUI.BORDER, 1));
        btn.setFocusPainted(false);
        btn.setFocusable(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(false);
        btn.setEnabled(seat.isAvailable());
        btn.setPreferredSize(new Dimension(48, 36));
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn.putClientProperty("busSeat", seat);
        if (AppConfig.getInstance().isShowSeatMetadata()) {
            String meta = seat.isWindowSeat() ? "창가" : (seat.isAisleSeat() ? "통로" : "");
            btn.setToolTipText("<html><body style='font-family:sans-serif;padding:4px;'>"
                    + "<b>" + seat.getSeatNumber() + "</b><br/>" + meta + "</body></html>");
        }
        btn.addActionListener(e -> {
            if (selectedSeatButton != null) {
                BusSeat prev = (BusSeat) selectedSeatButton.getClientProperty("busSeat");
                selectedSeatButton.setBackground(prev.isAvailable() ? Color.WHITE : ModernUI.SEAT_OCCUPIED);
                selectedSeatButton.setForeground(ModernUI.TEXT_PRIMARY);
                selectedSeatButton.setBorder(BorderFactory.createLineBorder(ModernUI.BORDER, 1));
            }
            btn.setBackground(ModernUI.PRIMARY);
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createLineBorder(ModernUI.PRIMARY_HOVER, 2));
            selectedSeatButton = btn;
            selectedSeat = seat;
            infoLabel.setText("선택한 좌석: " + seat);
        });
        return btn;
    }
}
