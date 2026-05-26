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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import com.koreanair.reservation.domain.bus.BusTicket;

/**
 * 우등고속 버스 좌석 선택 화면.
 *
 * <p>발표 데모용으로 2+2 배열, 12행 좌석을 제공한다.
 */
public class BusSeatSelectionPanel extends JPanel {

    private static final int ROWS = 12;
    private static final String[] COLS = { "A", "B", "C", "D" };

    private final MainFrame frame;
    private final JLabel infoLabel = new JLabel("버스 좌석을 선택해 주세요.");
    private final JButton confirmButton = new JButton("버스 좌석 저장");
    private final JButton backButton = new JButton("← 마이페이지");

    private BusTicket busTicket;
    private String selectedSeat;
    private JButton selectedButton;

    public BusSeatSelectionPanel(MainFrame frame) {
        super(new BorderLayout());
        this.frame = frame;
        setBackground(ModernUI.BACKGROUND);
        setOpaque(true);
        buildLayout();
    }

    private void buildLayout() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ModernUI.BACKGROUND);
        header.setBorder(BorderFactory.createEmptyBorder(20, 24, 12, 24));
        JLabel title = new JLabel("버스 좌석 선택");
        title.setFont(ModernUI.FONT_HEADING);
        title.setForeground(ModernUI.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);
        infoLabel.setFont(ModernUI.FONT_BODY);
        infoLabel.setForeground(ModernUI.TEXT_SECONDARY);
        header.add(infoLabel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(ModernUI.BACKGROUND);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        center.add(buildSeatGrid(), gc);
        add(new JScrollPane(center), BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        footer.setBackground(ModernUI.CARD_BG);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ModernUI.BORDER));
        ModernUI.styleButtonSecondary(backButton);
        ModernUI.styleButton(confirmButton);
        backButton.addActionListener(e -> frame.showMyPage());
        confirmButton.addActionListener(e -> doConfirm());
        footer.add(backButton);
        footer.add(confirmButton);
        footer.setPreferredSize(new Dimension(0, 60));
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel buildSeatGrid() {
        JPanel grid = new JPanel(new GridLayout(ROWS + 1, COLS.length + 2, 8, 8));
        grid.setBackground(ModernUI.CARD_BG);
        grid.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernUI.BORDER, 1),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)));

        grid.add(headerLabel(""));
        grid.add(headerLabel("A"));
        grid.add(headerLabel("B"));
        grid.add(headerLabel(""));
        grid.add(headerLabel("C"));
        grid.add(headerLabel("D"));

        for (int row = 1; row <= ROWS; row++) {
            grid.add(headerLabel(String.valueOf(row)));
            for (int col = 0; col < COLS.length; col++) {
                if (col == 2) {
                    grid.add(aisle());
                }
                grid.add(seatButton(row + COLS[col]));
            }
        }
        return grid;
    }

    private JLabel headerLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(ModernUI.FONT_SMALL);
        label.setForeground(ModernUI.TEXT_SECONDARY);
        label.setPreferredSize(new Dimension(42, 30));
        return label;
    }

    private JLabel aisle() {
        JLabel label = new JLabel("통로", SwingConstants.CENTER);
        label.setFont(ModernUI.FONT_SMALL);
        label.setForeground(ModernUI.TEXT_SECONDARY);
        label.setPreferredSize(new Dimension(32, 30));
        return label;
    }

    private JButton seatButton(String seatNo) {
        JButton button = new JButton(seatNo);
        button.setFont(ModernUI.FONT_SMALL);
        button.setForeground(ModernUI.TEXT_PRIMARY);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(ModernUI.BORDER, 1));
        button.setFocusPainted(false);
        button.setFocusable(false);
        button.setOpaque(true);
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(48, 34));
        button.addActionListener(e -> selectSeat(button, seatNo));
        return button;
    }

    private void selectSeat(JButton button, String seatNo) {
        if (selectedButton != null) {
            selectedButton.setBackground(Color.WHITE);
            selectedButton.setForeground(ModernUI.TEXT_PRIMARY);
        }
        button.setBackground(ModernUI.SUCCESS);
        button.setForeground(Color.WHITE);
        selectedButton = button;
        selectedSeat = seatNo;
        infoLabel.setText("선택한 버스 좌석: " + seatNo);
    }

    public void setBusTicket(BusTicket ticket) {
        this.busTicket = ticket;
        this.selectedSeat = null;
        if (selectedButton != null) {
            selectedButton.setBackground(Color.WHITE);
            selectedButton.setForeground(ModernUI.TEXT_PRIMARY);
            selectedButton = null;
        }
        String ticketNo = ticket != null ? ticket.getTicketNumber() : "-";
        String city = ticket != null && ticket.getDestinationCity() != null
                ? ticket.getDestinationCity().getDisplayName()
                : "-";
        infoLabel.setText("버스티켓: " + ticketNo + " · 목적지: " + city);
    }

    private void doConfirm() {
        if (busTicket == null) {
            JOptionPane.showMessageDialog(this,
                    "버스티켓이 없습니다.",
                    "안내",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (selectedSeat == null) {
            JOptionPane.showMessageDialog(this,
                    "버스 좌석을 먼저 선택하세요.",
                    "안내",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        frame.recordBusSeat(busTicket, selectedSeat);
        System.out.printf("[SWING][BUS-SEAT] ticket=%s city=%s seat=%s%n",
                busTicket.getTicketNumber(),
                busTicket.getDestinationCity() != null ? busTicket.getDestinationCity().getDisplayName() : "-",
                selectedSeat);
        JOptionPane.showMessageDialog(this,
                "버스 좌석이 저장되었습니다.\n버스티켓: "
                        + busTicket.getTicketNumber() + "\n좌석: " + selectedSeat,
                "버스 좌석 저장",
                JOptionPane.INFORMATION_MESSAGE);
        frame.showMyPage();
    }
}
