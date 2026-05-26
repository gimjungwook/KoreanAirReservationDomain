package com.koreanair.reservation.app.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.koreanair.reservation.control.BookingController;
import com.koreanair.reservation.domain.reservation.Reservation;

/**
 * 좌석 선택 화면 (iter 2 — 정적 6열 × 30행 더미 그리드).
 *
 * <p>실제 SeatInventory 연동은 iter 3 범위. 여기서는 그리드 버튼만 표시하고
 * 사용자가 클릭하면 정보 라벨에 좌석번호를 표기한다. "확인" 버튼은
 * BookingController.assignSeat(reservationId, seatNumber) 를 호출한 뒤 결제 화면으로 진입한다.
 */
public class SeatSelectionPanel extends JPanel {

    private static final int ROWS = 30;
    private static final char[] COLS = { 'A', 'B', 'C', 'D', 'E', 'F' };

    private final MainFrame frame;
    private final BookingController booking;

    private final JLabel infoLabel = new JLabel("좌석을 선택해 주세요.");
    private final JButton confirmButton = new JButton("확인");
    private final JButton backButton = new JButton("← 뒤로");

    private Reservation reservation;
    private String selectedSeat;
    private JButton selectedButton;
    private boolean managementMode;

    public SeatSelectionPanel(MainFrame parent, BookingController bookingController) {
        super(new BorderLayout());
        this.frame = parent;
        this.booking = bookingController;
        setBackground(ModernUI.BACKGROUND);
        setOpaque(true);
        buildLayout();
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

        JPanel seatGrid = buildSeatGrid();
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(ModernUI.BACKGROUND);
        center.setOpaque(true);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        center.add(seatGrid, gc);
        add(new javax.swing.JScrollPane(center), BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        footer.setBackground(ModernUI.CARD_BG);
        footer.setOpaque(true);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ModernUI.BORDER));
        ModernUI.styleButtonSecondary(backButton);
        ModernUI.styleButton(confirmButton);
        backButton.addActionListener(e -> {
            if (managementMode) {
                frame.showMyPage();
            } else {
                frame.showPassenger();
            }
        });
        confirmButton.addActionListener(e -> doConfirm());
        footer.add(backButton);
        footer.add(confirmButton);
        footer.setPreferredSize(new Dimension(0, 60));
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel buildSeatGrid() {
        // 헤더: 빈칸 + A B C [통로] D E F
        JPanel grid = new JPanel(new GridLayout(ROWS + 1, COLS.length + 2, 6, 6));
        grid.setBackground(ModernUI.CARD_BG);
        grid.setOpaque(true);
        grid.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernUI.BORDER, 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));

        // header row.
        grid.add(makeHeaderLabel(""));
        grid.add(makeHeaderLabel("A"));
        grid.add(makeHeaderLabel("B"));
        grid.add(makeHeaderLabel("C"));
        grid.add(makeHeaderLabel(""));
        grid.add(makeHeaderLabel("D"));
        grid.add(makeHeaderLabel("E"));
        grid.add(makeHeaderLabel("F"));

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
        JButton btn = new JButton(seatNo);
        btn.setFont(ModernUI.FONT_SMALL);
        btn.setForeground(ModernUI.TEXT_PRIMARY);
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(ModernUI.BORDER, 1));
        btn.setFocusPainted(false);
        btn.setFocusable(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(false);
        btn.setPreferredSize(new Dimension(40, 32));
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn.addActionListener(e -> selectSeat(btn, seatNo));
        return btn;
    }

    private void selectSeat(JButton btn, String seatNo) {
        if (selectedButton != null) {
            selectedButton.setBackground(Color.WHITE);
            selectedButton.setForeground(ModernUI.TEXT_PRIMARY);
        }
        btn.setBackground(ModernUI.PRIMARY);
        btn.setForeground(Color.WHITE);
        selectedButton = btn;
        selectedSeat = seatNo;
        infoLabel.setText("선택한 좌석: " + seatNo);
    }

    /** 호출자(MainFrame)가 어떤 예약에 좌석을 부여할지 지정. */
    public void setReservation(Reservation r) {
        setReservation(r, false);
    }

    /** 호출자(MainFrame)가 어떤 예약에 좌석을 부여할지 지정. */
    public void setReservation(Reservation r, boolean managementMode) {
        this.reservation = r;
        this.managementMode = managementMode;
        this.selectedSeat = null;
        if (selectedButton != null) {
            selectedButton.setBackground(Color.WHITE);
            selectedButton.setForeground(ModernUI.TEXT_PRIMARY);
            selectedButton = null;
        }
        confirmButton.setText(managementMode ? "좌석 변경 저장" : "확인");
        backButton.setText(managementMode ? "← 마이페이지" : "← 뒤로");
        infoLabel.setText((managementMode ? "변경할 항공 좌석을 선택해 주세요. (PNR: " : "좌석을 선택해 주세요. (PNR: ")
                + (r != null && r.getPnrNumber() != null ? r.getPnrNumber() : "-") + ")");
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
            frame.recordAirSeat(reservation, selectedSeat);
        }
        if (managementMode) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "항공 좌석이 변경되었습니다.\nPNR: "
                            + (reservation != null ? reservation.getPnrNumber() : "-")
                            + "\n좌석: " + selectedSeat,
                    "좌석 변경 완료",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
            frame.showMyPage();
        } else {
            frame.showPayment();
        }
    }
}
