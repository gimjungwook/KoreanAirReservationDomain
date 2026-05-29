package com.koreanair.reservation.app.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.koreanair.reservation.control.BookingController;
import com.koreanair.reservation.domain.bus.BusCity;
import com.koreanair.reservation.domain.bus.BusTicketRequest;
import com.koreanair.reservation.domain.flight.FareRule;
import com.koreanair.reservation.domain.payment.Payment;
import com.koreanair.reservation.domain.payment.PaymentStatus;
import com.koreanair.reservation.domain.reservation.Reservation;

/**
 * 결제 화면 — iter4: Bus add-on offer 통합.
 *
 * <p>KE 부킹 패턴: 결제 직전에 부가 서비스(셔틀)를 add-on 으로 권유, 동의 시 좌석 선택 →
 * 항공 결제 금액 + 셔틀 금액 합산.
 */
public class PaymentPanel extends JPanel {

    private static final long DEFAULT_BASE_FARE = 450_000L;
    private static final long DEFAULT_TAX = 50_000L;

    private final JTextField pnrLabel = new JTextField(" ");
    private final JLabel amountLabel = new JLabel(" ");
    private final JLabel airFareLabel = new JLabel(" ");
    private final JLabel busFareLabel = new JLabel("0 KRW");
    private final JComboBox<String> methodCombo = new JComboBox<>(new String[] {
            "신용카드", "Apple Pay", "Kakao Pay", "마일리지"
    });
    private final JCheckBox busAddonCheck = new JCheckBox("공항까지 KAL 프리미엄 셔틀 추가 (대도시 출발)");
    private final JComboBox<BusCity> busCityCombo = new JComboBox<>(BusCity.values());
    private final JButton selectBusSeatButton = new JButton("좌석 선택");
    private final JLabel busSeatStatusLabel = new JLabel("미선택");

    private final JButton payButton = new JButton("결제하기");
    private final JButton backButton = new JButton("← 뒤로");

    private final MainFrame frame;
    private final BookingController booking;
    private final SwingReservationUI ui;

    private Reservation reservation;
    private FareRule fareRule;
    private BusTicketRequest busTicketRequest;

    public PaymentPanel(MainFrame frame, BookingController booking, SwingReservationUI ui) {
        super(new BorderLayout());
        this.frame = frame;
        this.booking = booking;
        this.ui = ui;
        setBackground(ModernUI.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));
        buildContent();
    }

    public BusTicketRequest getBusTicketRequest() {
        return busTicketRequest;
    }

    private void buildContent() {
        JLabel title = new JLabel("결제");
        title.setFont(ModernUI.FONT_TITLE);
        title.setForeground(ModernUI.TEXT_PRIMARY);

        JPanel container = new JPanel(new BorderLayout(0, 16));
        container.setOpaque(false);
        container.add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 16, 16);
        c.weightx = 1.0;
        c.gridy = 0;

        c.gridx = 0; c.weightx = 0.55;
        grid.add(buildPaymentCard(), c);

        c.gridx = 1; c.weightx = 0.45; c.insets = new Insets(0, 0, 16, 0);
        grid.add(buildBusOfferCard(), c);

        container.add(grid, BorderLayout.CENTER);
        container.add(buildFooter(), BorderLayout.SOUTH);
        add(container, BorderLayout.CENTER);
    }

    private JPanel buildPaymentCard() {
        JPanel card = ModernUI.cardPanel();
        card.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.insets = new Insets(4, 0, 4, 0);

        c.gridx = 0; c.gridy = 0;
        JLabel h = new JLabel("예약 번호 (PNR)");
        h.setFont(ModernUI.FONT_SMALL);
        h.setForeground(ModernUI.TEXT_SECONDARY);
        card.add(h, c);

        c.gridy = 1; c.insets = new Insets(0, 0, 16, 0);
        ModernUI.styleSelectableValue(pnrLabel, ModernUI.FONT_MONO, ModernUI.KE_NAVY, ModernUI.CARD_BG);
        card.add(pnrLabel, c);

        c.gridy = 2; c.insets = new Insets(0, 0, 4, 0);
        JLabel airH = new JLabel("항공 운임");
        airH.setFont(ModernUI.FONT_SMALL);
        airH.setForeground(ModernUI.TEXT_SECONDARY);
        card.add(airH, c);

        c.gridy = 3; c.insets = new Insets(0, 0, 12, 0);
        airFareLabel.setFont(ModernUI.FONT_BODY);
        airFareLabel.setForeground(ModernUI.TEXT_PRIMARY);
        card.add(airFareLabel, c);

        c.gridy = 4; c.insets = new Insets(0, 0, 4, 0);
        JLabel busH = new JLabel("셔틀 추가");
        busH.setFont(ModernUI.FONT_SMALL);
        busH.setForeground(ModernUI.TEXT_SECONDARY);
        card.add(busH, c);

        c.gridy = 5; c.insets = new Insets(0, 0, 16, 0);
        busFareLabel.setFont(ModernUI.FONT_BODY);
        busFareLabel.setForeground(ModernUI.TEXT_PRIMARY);
        card.add(busFareLabel, c);

        c.gridy = 6; c.insets = new Insets(8, 0, 4, 0);
        JLabel divider = new JLabel(" ");
        divider.setOpaque(true);
        divider.setBackground(ModernUI.BORDER);
        divider.setPreferredSize(new Dimension(0, 1));
        card.add(divider, c);

        c.gridy = 7; c.insets = new Insets(8, 0, 4, 0);
        JLabel totalH = new JLabel("총 결제 금액");
        totalH.setFont(ModernUI.FONT_SUBHEADING);
        totalH.setForeground(ModernUI.TEXT_PRIMARY);
        card.add(totalH, c);

        c.gridy = 8; c.insets = new Insets(0, 0, 16, 0);
        amountLabel.setFont(ModernUI.FONT_TITLE);
        amountLabel.setForeground(ModernUI.KE_RED);
        card.add(amountLabel, c);

        c.gridy = 9; c.insets = new Insets(0, 0, 4, 0);
        JLabel mH = new JLabel("결제 수단");
        mH.setFont(ModernUI.FONT_SMALL);
        mH.setForeground(ModernUI.TEXT_SECONDARY);
        card.add(mH, c);

        c.gridy = 10; c.insets = new Insets(0, 0, 16, 0);
        ModernUI.styleComboBox(methodCombo);
        card.add(methodCombo, c);

        c.gridy = 11;
        ModernUI.styleButtonAccent(payButton);
        payButton.setPreferredSize(new Dimension(0, 48));
        payButton.addActionListener(e -> doPay());
        card.add(payButton, c);
        return card;
    }

    private JPanel buildBusOfferCard() {
        JPanel card = ModernUI.cardPanel();
        card.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.insets = new Insets(4, 0, 4, 0);

        c.gridy = 0;
        JLabel offerTag = new JLabel("부가 서비스");
        offerTag.setFont(ModernUI.FONT_SMALL);
        offerTag.setForeground(ModernUI.KE_RED);
        card.add(offerTag, c);

        c.gridy = 1; c.insets = new Insets(0, 0, 6, 0);
        JLabel title = new JLabel("공항까지 편하게");
        title.setFont(ModernUI.FONT_HEADING);
        title.setForeground(ModernUI.TEXT_PRIMARY);
        card.add(title, c);

        c.gridy = 2; c.insets = new Insets(0, 0, 12, 0);
        JLabel sub = new JLabel("<html>대도시 거주지에서 인천공항까지 KAL 프리미엄<br/>우등고속 셔틀로 이동하세요. 좌석 지정 가능.</html>");
        sub.setFont(ModernUI.FONT_SMALL);
        sub.setForeground(ModernUI.TEXT_SECONDARY);
        card.add(sub, c);

        c.gridy = 3;
        busAddonCheck.setFont(ModernUI.FONT_BODY_BOLD);
        busAddonCheck.setForeground(ModernUI.KE_NAVY);
        busAddonCheck.setOpaque(false);
        busAddonCheck.setFocusable(false);
        card.add(busAddonCheck, c);

        c.gridy = 4; c.insets = new Insets(8, 0, 4, 0);
        JLabel cityH = new JLabel("출발 도시");
        cityH.setFont(ModernUI.FONT_SMALL);
        cityH.setForeground(ModernUI.TEXT_SECONDARY);
        card.add(cityH, c);

        c.gridy = 5; c.insets = new Insets(0, 0, 12, 0);
        ModernUI.styleComboBox(busCityCombo);
        busCityCombo.setEnabled(false);
        card.add(busCityCombo, c);

        c.gridy = 6;
        ModernUI.styleButtonSecondary(selectBusSeatButton);
        selectBusSeatButton.setEnabled(false);
        card.add(selectBusSeatButton, c);

        c.gridy = 7; c.insets = new Insets(8, 0, 4, 0);
        busSeatStatusLabel.setFont(ModernUI.FONT_SMALL);
        busSeatStatusLabel.setForeground(ModernUI.TEXT_SECONDARY);
        card.add(busSeatStatusLabel, c);

        busAddonCheck.addItemListener(e -> {
            boolean on = busAddonCheck.isSelected();
            busCityCombo.setEnabled(on);
            selectBusSeatButton.setEnabled(on);
            if (!on) {
                busTicketRequest = null;
                busSeatStatusLabel.setText("미선택");
                busSeatStatusLabel.setForeground(ModernUI.TEXT_SECONDARY);
                refreshTotal();
            }
        });
        selectBusSeatButton.addActionListener(e -> openBusSeatDialog());
        return card;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        footer.setOpaque(false);
        ModernUI.styleButtonSecondary(backButton);
        backButton.addActionListener(e -> frame.showPassenger());
        footer.add(backButton);
        return footer;
    }

    private void openBusSeatDialog() {
        BusCity city = (BusCity) busCityCombo.getSelectedItem();
        if (city == null) return;
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                city.getDisplayName() + " → 인천공항 셔틀 좌석",
                java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        BusSeatSelectionPanel selector = new BusSeatSelectionPanel(
                frame.getBusTicketingService(), city);
        dlg.setContentPane(selector);
        dlg.setSize(560, 640);
        dlg.setLocationRelativeTo(this);
        final boolean[] ok = {false};
        selector.setOnConfirm(() -> {
            ok[0] = true;
            dlg.dispose();
        });
        dlg.setVisible(true);
        if (!ok[0]) return;
        busTicketRequest = new BusTicketRequest(city,
                selector.getSelectedSchedule(),
                selector.getSelectedSeat());
        String seatNum = busTicketRequest.getSeat() != null
                ? busTicketRequest.getSeat().getSeatNumber() : "-";
        busSeatStatusLabel.setText("선택: " + city.getDisplayName() + " · 좌석 " + seatNum);
        busSeatStatusLabel.setForeground(ModernUI.SUCCESS);
        refreshTotal();
    }

    private void refreshTotal() {
        long air = DEFAULT_BASE_FARE + DEFAULT_TAX;
        long bus = busTicketRequest != null && busTicketRequest.getOriginCity() != null
                ? busTicketRequest.getOriginCity().getPremiumFare() : 0;
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        airFareLabel.setText(nf.format(air) + " KRW");
        busFareLabel.setText(bus > 0 ? "+ " + nf.format(bus) + " KRW" : "0 KRW");
        amountLabel.setText(nf.format(air + bus) + " KRW");
    }

    public void prepare(Reservation reservation, FareRule fareRule) {
        this.reservation = reservation;
        this.fareRule = fareRule;
        this.busTicketRequest = null;
        busAddonCheck.setSelected(false);
        busCityCombo.setEnabled(false);
        selectBusSeatButton.setEnabled(false);
        busSeatStatusLabel.setText("미선택");
        busSeatStatusLabel.setForeground(ModernUI.TEXT_SECONDARY);
        pnrLabel.setText(reservation != null ? reservation.getPnrNumber() : "-");
        refreshTotal();
    }

    private void doPay() {
        if (reservation == null || fareRule == null) {
            ui.displayError("결제 대상 예약이 없습니다.");
            return;
        }
        try {
            Payment payment = booking.confirmPayment(
                    reservation, fareRule, DEFAULT_BASE_FARE, DEFAULT_TAX);
            if (payment != null && payment.getStatus() == PaymentStatus.PAID) {
                frame.onPaymentConfirmed(reservation, payment);
            } else {
                ui.displayError("결제가 실패했습니다. 다시 시도하세요.");
            }
        } catch (Exception ex) {
            ui.displayError("결제 처리 중 오류: " + ex.getMessage());
        }
    }

    @SuppressWarnings("unused")
    private final Color reservedColor = Color.WHITE;
}
