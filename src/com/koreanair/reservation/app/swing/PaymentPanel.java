package com.koreanair.reservation.app.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.koreanair.reservation.control.BookingController;
import com.koreanair.reservation.domain.flight.FareRule;
import com.koreanair.reservation.domain.passenger.MileageAccount;
import com.koreanair.reservation.domain.payment.Payment;
import com.koreanair.reservation.domain.payment.PaymentMethod;
import com.koreanair.reservation.domain.payment.PaymentStatus;
import com.koreanair.reservation.domain.reservation.Reservation;

public class PaymentPanel extends JPanel {

    private static final long DEFAULT_BASE_FARE = 450_000L;
    private static final long DEFAULT_TAX = 50_000L;
    private static final long DEMO_MILEAGE_BALANCE = 800_000L;

    private final JTextField pnrLabel = new JTextField(" ");
    private final JLabel amountLabel = new JLabel(" ");
    private final JLabel mileageBalanceLabel = new JLabel(" ");
    private final JLabel methodHintLabel = new JLabel("신용카드는 PG 승인, 마일리지는 MileageAccount 차감 흐름으로 진행됩니다.");
    private final JComboBox<String> methodCombo = new JComboBox<>(new String[] {
            "신용카드 (PG 데모 승인)",
            "마일리지 전액 결제 (500,000 차감)"
    });
    private final JButton payButton = new JButton("결제하기");
    private final JButton backButton = new JButton("← 뒤로");

    private final MainFrame frame;
    private final BookingController booking;
    private final SwingReservationUI ui;

    private Reservation reservation;
    private FareRule fareRule;
    private MileageAccount mileageAccount;

    public PaymentPanel(MainFrame frame, BookingController booking, SwingReservationUI ui) {
        super(new BorderLayout());
        this.frame = frame;
        this.booking = booking;
        this.ui = ui;
        setBackground(ModernUI.BACKGROUND);
        setOpaque(true);
        buildContent();
    }

    private void buildContent() {
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(ModernUI.BACKGROUND);
        centerPanel.setOpaque(true);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 1.0;

        JLabel stepLabel = new JLabel("STEP 3");
        stepLabel.setFont(ModernUI.FONT_SMALL);
        stepLabel.setForeground(ModernUI.PRIMARY);
        stepLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        stepLabel.setOpaque(false);
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        centerPanel.add(stepLabel, c);

        JLabel title = new JLabel("결제");
        title.setFont(ModernUI.FONT_HEADING);
        title.setForeground(ModernUI.TEXT_PRIMARY);
        title.setOpaque(false);
        c.gridy = 1; c.gridwidth = 2;
        centerPanel.add(title, c);

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(ModernUI.CARD_BG);
        formCard.setOpaque(true);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernUI.BORDER, 1),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)));

        c.gridwidth = 1;
        c.gridy = 2; c.gridx = 0;
        c.insets = new Insets(4, 4, 4, 4);
        JLabel pnrH = new JLabel("예약 번호 (PNR)");
        pnrH.setFont(ModernUI.FONT_SMALL);
        pnrH.setForeground(ModernUI.TEXT_SECONDARY);
        pnrH.setOpaque(false);
        formCard.add(pnrH, c);

        c.gridx = 1;
        ModernUI.styleSelectableValue(pnrLabel,
                new java.awt.Font("Monaco", java.awt.Font.PLAIN, 15),
                ModernUI.PRIMARY,
                ModernUI.CARD_BG);
        pnrLabel.setToolTipText("마우스로 드래그하거나 Cmd/Ctrl+C로 PNR을 복사할 수 있습니다.");
        formCard.add(pnrLabel, c);

        c.gridy = 3; c.gridx = 0;
        JLabel amountH = new JLabel("결제 금액");
        amountH.setFont(ModernUI.FONT_SMALL);
        amountH.setForeground(ModernUI.TEXT_SECONDARY);
        amountH.setOpaque(false);
        formCard.add(amountH, c);

        c.gridx = 1;
        amountLabel.setFont(ModernUI.FONT_HEADING);
        amountLabel.setForeground(ModernUI.TEXT_PRIMARY);
        amountLabel.setOpaque(false);
        formCard.add(amountLabel, c);

        c.gridy = 4; c.gridx = 0;
        JLabel mileageH = new JLabel("마일리지 잔액");
        mileageH.setFont(ModernUI.FONT_SMALL);
        mileageH.setForeground(ModernUI.TEXT_SECONDARY);
        mileageH.setOpaque(false);
        formCard.add(mileageH, c);

        c.gridx = 1;
        mileageBalanceLabel.setFont(ModernUI.FONT_BODY);
        mileageBalanceLabel.setForeground(ModernUI.PRIMARY);
        mileageBalanceLabel.setOpaque(false);
        formCard.add(mileageBalanceLabel, c);

        c.gridy = 5; c.gridx = 0;
        JLabel methodH = new JLabel("결제 수단");
        methodH.setFont(ModernUI.FONT_SMALL);
        methodH.setForeground(ModernUI.TEXT_SECONDARY);
        methodH.setOpaque(false);
        formCard.add(methodH, c);

        c.gridx = 1;
        methodCombo.setFont(ModernUI.FONT_BODY);
        methodCombo.addActionListener(e -> updateMileageBalance());
        formCard.add(methodCombo, c);

        c.gridy = 6; c.gridx = 0; c.gridwidth = 2;
        methodHintLabel.setFont(ModernUI.FONT_SMALL);
        methodHintLabel.setForeground(ModernUI.TEXT_SECONDARY);
        formCard.add(methodHintLabel, c);

        c.gridy = 7; c.gridx = 0; c.gridwidth = 2;
        c.anchor = GridBagConstraints.EAST;
        ModernUI.styleButtonSuccess(payButton);
        payButton.addActionListener(e -> doPay());
        formCard.add(payButton, c);

        c.gridy = 2; c.gridx = 0; c.gridwidth = 1; c.anchor = GridBagConstraints.NORTHWEST;
        centerPanel.add(formCard, c);

        add(centerPanel, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(ModernUI.CARD_BG);
        footer.setOpaque(true);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ModernUI.BORDER));

        JPanel rightBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightBtns.setBackground(ModernUI.CARD_BG);
        rightBtns.setOpaque(true);

        ModernUI.styleButtonSecondary(backButton);
        rightBtns.add(backButton);

        footer.add(rightBtns, BorderLayout.EAST);
        footer.setPreferredSize(new Dimension(0, 52));
        add(footer, BorderLayout.SOUTH);

        backButton.addActionListener(e -> frame.showPassenger());
    }

    public void prepare(Reservation reservation, FareRule fareRule) {
        this.reservation = reservation;
        this.fareRule = fareRule;
        pnrLabel.setText(reservation != null ? reservation.getPnrNumber() : "-");
        long total = DEFAULT_BASE_FARE + DEFAULT_TAX;
        amountLabel.setText(String.format("%,d KRW", total));
        mileageAccount = new MileageAccount();
        mileageAccount.deposit(BigDecimal.valueOf(DEMO_MILEAGE_BALANCE));
        methodCombo.setSelectedIndex(0);
        updateMileageBalance();
    }

    private void doPay() {
        if (reservation == null || fareRule == null) {
            ui.displayError("결제 대상 예약이 없습니다.");
            return;
        }
        try {
            Payment payment;
            long total = DEFAULT_BASE_FARE + DEFAULT_TAX;
            if (isMileageSelected()) {
                System.out.printf("[SWING][MILEAGE] before=%,d cost=%,d pnr=%s%n",
                        mileageAccount.getBalance().longValue(), total, reservation.getPnrNumber());
                payment = booking.confirmMileagePayment(reservation, mileageAccount, total);
                System.out.printf("[SWING][MILEAGE] after=%,d status=%s method=%s%n",
                        mileageAccount.getBalance().longValue(),
                        payment.getStatus(),
                        payment.getPaymentMethod());
                updateMileageBalance();
            } else {
                payment = booking.confirmPayment(
                        reservation, fareRule, DEFAULT_BASE_FARE, DEFAULT_TAX);
            }
            if (payment != null && payment.getStatus() == PaymentStatus.PAID) {
                frame.onPaymentConfirmed(reservation, payment);
            } else {
                ui.displayError("결제가 실패했습니다. 다시 시도하세요.");
            }
        } catch (Exception ex) {
            ui.displayError("결제 처리 중 오류: " + ex.getMessage());
        }
    }

    private boolean isMileageSelected() {
        Object selected = methodCombo.getSelectedItem();
        return selected != null && selected.toString().contains("마일리지");
    }

    private void updateMileageBalance() {
        if (mileageAccount == null) {
            mileageBalanceLabel.setText("-");
            return;
        }
        mileageBalanceLabel.setText(String.format("%,d miles", mileageAccount.getBalance().longValue()));
        PaymentMethod method = isMileageSelected() ? PaymentMethod.MILEAGE : PaymentMethod.CREDIT_CARD;
        methodHintLabel.setText(method == PaymentMethod.MILEAGE
                ? "마일리지 결제 선택: 잔액에서 500,000 miles가 차감되고 콘솔에 before/after 로그가 출력됩니다."
                : "신용카드 선택: PG 승인 후 PendingPayment → Confirmed 상태로 전이됩니다.");
    }
}
