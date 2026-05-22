package com.koreanair.reservation.app.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.koreanair.reservation.control.BusTicketingService;
import com.koreanair.reservation.domain.bus.BusCity;
import com.koreanair.reservation.domain.bus.BusTicket;
import com.koreanair.reservation.domain.payment.Payment;
import com.koreanair.reservation.domain.reservation.Reservation;

public class ConfirmationPanel extends JPanel {

    private final JLabel pnrLabel = new JLabel(" ");
    private final JLabel stateLabel = new JLabel(" ");
    private final JLabel amountLabel = new JLabel(" ");
    private final JLabel paymentStatusLabel = new JLabel(" ");
    private final JLabel busTicketStatusLabel = new JLabel("미발매");
    private final JComboBox<BusCity> busCityCombo;
    private final JButton lookupButton = new JButton("예약 조회로");
    private final JButton homeButton = new JButton("다른 항공편 예약");
    private final JButton ticketButton = new JButton("e-Ticket + 우등고속 발매");

    private final MainFrame frame;
    private Reservation reservation;

    public ConfirmationPanel(MainFrame frame, BusTicketingService busTicketingService) {
        super(new BorderLayout());
        this.frame = frame;
        this.busCityCombo = new JComboBox<>(
                busTicketingService.supportedCities().toArray(new BusCity[0]));
        setBackground(ModernUI.BACKGROUND);
        setOpaque(true);
        buildContent();
    }

    private void buildContent() {
        setBackground(ModernUI.BACKGROUND);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(ModernUI.BACKGROUND);
        centerPanel.setOpaque(true);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 1.0;

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(ModernUI.CARD_BG);
        card.setOpaque(true);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernUI.SUCCESS, 2),
                BorderFactory.createEmptyBorder(24, 32, 24, 32)));
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;

        JLabel checkmark = new JLabel("✓", SwingConstants.CENTER);
        checkmark.setFont(new Font("System", Font.BOLD, 48));
        checkmark.setForeground(ModernUI.SUCCESS);
        checkmark.setPreferredSize(new Dimension(80, 80));
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        card.add(checkmark, c);

        JLabel title = new JLabel("예약 확정");
        title.setFont(ModernUI.FONT_TITLE);
        title.setForeground(ModernUI.TEXT_PRIMARY);
        c.gridy = 1;
        card.add(title, c);

        JLabel subtitle = new JLabel("예약이 성공적으로 완료되었습니다.");
        subtitle.setFont(ModernUI.FONT_BODY);
        subtitle.setForeground(ModernUI.TEXT_SECONDARY);
        c.gridy = 2;
        card.add(subtitle, c);

        c.gridwidth = 1;
        c.gridy = 3; c.gridx = 0;
        JLabel pnrH = new JLabel("예약 번호 (PNR)");
        pnrH.setFont(ModernUI.FONT_SMALL);
        pnrH.setForeground(ModernUI.TEXT_SECONDARY);
        card.add(pnrH, c);
        c.gridx = 1;
        pnrLabel.setFont(new Font("Monaco", Font.PLAIN, 16));
        pnrLabel.setForeground(ModernUI.PRIMARY);
        card.add(pnrLabel, c);

        c.gridy = 4; c.gridx = 0;
        JLabel stateH = new JLabel("예약 상태");
        stateH.setFont(ModernUI.FONT_SMALL);
        stateH.setForeground(ModernUI.TEXT_SECONDARY);
        card.add(stateH, c);
        c.gridx = 1;
        stateLabel.setFont(ModernUI.FONT_BODY);
        stateLabel.setForeground(ModernUI.SUCCESS);
        card.add(stateLabel, c);

        c.gridy = 5; c.gridx = 0;
        JLabel payH = new JLabel("결제 상태");
        payH.setFont(ModernUI.FONT_SMALL);
        payH.setForeground(ModernUI.TEXT_SECONDARY);
        card.add(payH, c);
        c.gridx = 1;
        paymentStatusLabel.setFont(ModernUI.FONT_BODY);
        paymentStatusLabel.setForeground(ModernUI.TEXT_PRIMARY);
        card.add(paymentStatusLabel, c);

        c.gridy = 6; c.gridx = 0;
        JLabel amtH = new JLabel("결제 금액");
        amtH.setFont(ModernUI.FONT_SMALL);
        amtH.setForeground(ModernUI.TEXT_SECONDARY);
        card.add(amtH, c);
        c.gridx = 1;
        amountLabel.setFont(ModernUI.FONT_HEADING);
        amountLabel.setForeground(ModernUI.TEXT_PRIMARY);
        card.add(amountLabel, c);

        c.gridy = 7; c.gridx = 0;
        JLabel busCityH = new JLabel("연계 버스 목적지");
        busCityH.setFont(ModernUI.FONT_SMALL);
        busCityH.setForeground(ModernUI.TEXT_SECONDARY);
        card.add(busCityH, c);
        c.gridx = 1;
        busCityCombo.setFont(ModernUI.FONT_BODY);
        card.add(busCityCombo, c);

        c.gridy = 8; c.gridx = 0;
        JLabel busStatusH = new JLabel("버스티켓 상태");
        busStatusH.setFont(ModernUI.FONT_SMALL);
        busStatusH.setForeground(ModernUI.TEXT_SECONDARY);
        card.add(busStatusH, c);
        c.gridx = 1;
        busTicketStatusLabel.setFont(ModernUI.FONT_BODY);
        busTicketStatusLabel.setForeground(ModernUI.TEXT_PRIMARY);
        card.add(busTicketStatusLabel, c);

        c.gridy = 2; c.gridx = 0; c.gridwidth = 1; c.anchor = GridBagConstraints.NORTHWEST;
        centerPanel.add(card, c);

        add(centerPanel, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(ModernUI.CARD_BG);
        footer.setOpaque(true);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ModernUI.BORDER));

        JPanel rightBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightBtns.setBackground(ModernUI.CARD_BG);
        rightBtns.setOpaque(true);

        ticketButton.setFont(ModernUI.FONT_SMALL);
        ModernUI.styleButtonSuccess(ticketButton);
        ticketButton.addActionListener(e -> issueLinkedTicket());
        rightBtns.add(ticketButton);

        ModernUI.styleButtonSecondary(lookupButton);
        lookupButton.addActionListener(e -> frame.showLookup());
        rightBtns.add(lookupButton);

        ModernUI.styleButton(homeButton);
        homeButton.addActionListener(e -> frame.startNewBooking());
        rightBtns.add(homeButton);

        footer.add(rightBtns, BorderLayout.EAST);
        footer.setPreferredSize(new Dimension(0, 52));
        add(footer, BorderLayout.SOUTH);
    }

    public void prepare(Reservation reservation, Payment payment) {
        this.reservation = reservation;
        pnrLabel.setText(reservation != null ? reservation.getPnrNumber() : "-");
        stateLabel.setText(reservation != null ? reservation.getStateName() : "-");
        busTicketStatusLabel.setText("미발매");
        ticketButton.setEnabled(reservation != null);
        busCityCombo.setEnabled(reservation != null);
        if (payment != null) {
            paymentStatusLabel.setText(String.valueOf(payment.getStatus()));
            amountLabel.setText(payment.getAmount() != null
                    ? String.format("%,d KRW", payment.getAmount().longValueExact())
                    : "-");
        } else {
            paymentStatusLabel.setText("-");
            amountLabel.setText("-");
        }
    }

    private void issueLinkedTicket() {
        try {
            BusCity city = (BusCity) busCityCombo.getSelectedItem();
            BusTicket busTicket = frame.issueLinkedBusTicket(reservation, city);
            stateLabel.setText(reservation != null ? reservation.getStateName() : "-");
            busTicketStatusLabel.setText(String.format("%s · %s · %,d KRW",
                    busTicket.getTicketNumber(),
                    busTicket.getDestinationCity().getDisplayName(),
                    busTicket.getFare()));
            ticketButton.setEnabled(false);
            busCityCombo.setEnabled(false);
            JOptionPane.showMessageDialog(this,
                    "e-Ticket 발급 후 우등고속 버스티켓이 연계 발매되었습니다.\n"
                            + "버스티켓: " + busTicket.getTicketNumber() + "\n"
                            + "도시: " + busTicket.getDestinationCity().getDisplayName()
                            + " / 요금: " + String.format("%,d KRW", busTicket.getFare()),
                    "연계 발매 완료",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "연계 발매 실패: " + ex.getMessage(),
                    "오류",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
