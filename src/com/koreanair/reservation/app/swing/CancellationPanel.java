package com.koreanair.reservation.app.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.koreanair.reservation.control.BookingController;
import com.koreanair.reservation.control.RefundHandler;
import com.koreanair.reservation.domain.flight.FareRule;
import com.koreanair.reservation.domain.flight.FlightSchedule;
import com.koreanair.reservation.domain.reservation.Itinerary;
import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.reservation.Segment;

/**
 * 예약 취소 화면 — 예약 요약 + 사유 + 환불 미리보기 + 확정.
 *
 * <p>흐름:
 * <ol>
 *   <li>예약 요약 (PNR / 출발 / 도착 / 상태) 표시.</li>
 *   <li>사유 입력 (JTextArea).</li>
 *   <li>"환불 미리보기" → RefundHandler.evaluateRefund 결과를 메시지 라벨에 표기.</li>
 *   <li>"취소 확정" → BookingController.processCancellation(pnr) 호출 후 RefundPanel 로 전이.</li>
 * </ol>
 */
public class CancellationPanel extends JPanel {

    private final MainFrame frame;
    private final BookingController booking;
    private final RefundHandler refundHandler;

    private final JLabel pnrLabel = new JLabel(" ");
    private final JLabel routeLabel = new JLabel(" ");
    private final JLabel stateLabel = new JLabel(" ");

    private final JTextArea reasonArea = new JTextArea(4, 30);
    private final JLabel previewLabel = new JLabel(" ");

    private final JButton previewButton = new JButton("환불 미리보기");
    private final JButton confirmButton = new JButton("취소 확정");
    private final JButton backButton = new JButton("← 뒤로");

    private Reservation reservation;
    // preview 결과 캐시 — RefundPanel 표시용.
    private BigDecimal lastRefundAmount = BigDecimal.ZERO;
    private String lastPolicyName = "-";

    public CancellationPanel(MainFrame parent,
                             BookingController bookingController,
                             RefundHandler refundHandler) {
        super(new BorderLayout());
        this.frame = parent;
        this.booking = bookingController;
        this.refundHandler = refundHandler;
        setBackground(ModernUI.BACKGROUND);
        setOpaque(true);
        buildLayout();
    }

    private void buildLayout() {
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(ModernUI.BACKGROUND);
        center.setOpaque(true);
        center.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 1.0;

        JLabel title = new JLabel("예약 취소");
        title.setFont(ModernUI.FONT_HEADING);
        title.setForeground(ModernUI.TEXT_PRIMARY);
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        center.add(title, c);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(ModernUI.CARD_BG);
        card.setOpaque(true);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernUI.BORDER, 1),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)));

        c.gridwidth = 1;
        c.gridy = 1; c.gridx = 0;
        card.add(makeKey("PNR"), c);
        c.gridx = 1;
        pnrLabel.setFont(new Font("Monaco", Font.PLAIN, 15));
        pnrLabel.setForeground(ModernUI.PRIMARY);
        card.add(pnrLabel, c);

        c.gridy = 2; c.gridx = 0;
        card.add(makeKey("구간"), c);
        c.gridx = 1;
        routeLabel.setFont(ModernUI.FONT_BODY);
        routeLabel.setForeground(ModernUI.TEXT_PRIMARY);
        card.add(routeLabel, c);

        c.gridy = 3; c.gridx = 0;
        card.add(makeKey("상태"), c);
        c.gridx = 1;
        stateLabel.setFont(ModernUI.FONT_BODY);
        stateLabel.setForeground(ModernUI.TEXT_PRIMARY);
        card.add(stateLabel, c);

        c.gridy = 4; c.gridx = 0;
        card.add(makeKey("취소 사유"), c);
        c.gridx = 1;
        reasonArea.setFont(ModernUI.FONT_BODY);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        reasonArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernUI.BORDER, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        JScrollPane reasonScroll = new JScrollPane(reasonArea);
        reasonScroll.setBorder(BorderFactory.createEmptyBorder());
        reasonScroll.setPreferredSize(new Dimension(360, 100));
        card.add(reasonScroll, c);

        c.gridy = 5; c.gridx = 0; c.gridwidth = 2;
        previewLabel.setFont(ModernUI.FONT_SMALL);
        previewLabel.setForeground(ModernUI.TEXT_SECONDARY);
        previewLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        card.add(previewLabel, c);

        c.gridy = 1; c.gridx = 0; c.gridwidth = 1;
        center.add(card, c);

        add(center, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        footer.setBackground(ModernUI.CARD_BG);
        footer.setOpaque(true);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ModernUI.BORDER));
        ModernUI.styleButtonSecondary(backButton);
        ModernUI.styleButtonSecondary(previewButton);
        ModernUI.styleButton(confirmButton);
        backButton.addActionListener(e -> frame.showLookup());
        previewButton.addActionListener(e -> doPreview());
        confirmButton.addActionListener(e -> doConfirm());
        footer.add(backButton);
        footer.add(previewButton);
        footer.add(confirmButton);
        footer.setPreferredSize(new Dimension(0, 60));
        add(footer, BorderLayout.SOUTH);
    }

    private JLabel makeKey(String text) {
        JLabel l = new JLabel(text);
        l.setFont(ModernUI.FONT_SMALL);
        l.setForeground(ModernUI.TEXT_SECONDARY);
        return l;
    }

    /** 취소 대상 예약을 호출자가 지정. */
    public void setReservation(Reservation r) {
        this.reservation = r;
        if (r == null) {
            pnrLabel.setText("-");
            routeLabel.setText("-");
            stateLabel.setText("-");
            previewLabel.setText(" ");
            reasonArea.setText("");
            return;
        }
        pnrLabel.setText(r.getPnrNumber() != null ? r.getPnrNumber() : "-");
        stateLabel.setText(r.getStateName());
        routeLabel.setText(routeText(r));
        previewLabel.setText(" ");
        reasonArea.setText("");
        lastRefundAmount = BigDecimal.ZERO;
        lastPolicyName = "-";
    }

    private static String routeText(Reservation r) {
        Itinerary it = r.getItinerary();
        if (it == null || it.getSegments() == null || it.getSegments().isEmpty()) {
            return "(구간 정보 없음)";
        }
        Segment first = it.getSegments().get(0);
        FlightSchedule fs = first != null ? first.getFlightSchedule() : null;
        if (fs == null || fs.getFlight() == null || fs.getFlight().getRoute() == null) {
            return "(구간 정보 없음)";
        }
        String origin = fs.getFlight().getRoute().getOrigin() != null
                ? fs.getFlight().getRoute().getOrigin().getAirportCode() : "?";
        String dest = fs.getFlight().getRoute().getDestination() != null
                ? fs.getFlight().getRoute().getDestination().getAirportCode() : "?";
        return origin + " → " + dest;
    }

    private void doPreview() {
        if (reservation == null) {
            previewLabel.setForeground(ModernUI.ERROR);
            previewLabel.setText("예약이 지정되지 않았습니다.");
            return;
        }
        String pnr = reservation.getPnrNumber();
        String fareClass = resolveFareClass(reservation);
        // 미리보기는 pending 큐에 등록하지 않는 read-only 경로로 처리한다.
        BigDecimal amount = refundHandler.previewRefund(pnr, fareClass);
        String policyName = refundHandler.previewPolicyName(pnr, fareClass);
        lastRefundAmount = amount;
        lastPolicyName = policyName;
        previewLabel.setForeground(ModernUI.TEXT_PRIMARY);
        previewLabel.setText(String.format(
                "예상 환불 금액: %,d KRW  /  적용 정책: %s",
                amount.longValue(), policyName));
    }

    private void doConfirm() {
        if (reservation == null) {
            JOptionPane.showMessageDialog(this,
                    "예약이 지정되지 않았습니다.",
                    "오류",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        String pnr = reservation.getPnrNumber();
        try {
            if ("-".equals(lastPolicyName)) {
                previewRefund();
            }
            booking.processCancellation(pnr);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "취소 실패",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "취소 처리 중 오류: " + ex.getMessage(),
                    "취소 실패",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        // BookingController.processCancellation 이 evaluate + processRefund 를 다시 수행하므로
        // 여기서 표시할 환불 금액은 미리보기 결과를 그대로 사용한다.
        frame.syncReservationState(reservation);
        frame.showRefund(pnr, lastRefundAmount, lastPolicyName);
    }

    private static String resolveFareClass(Reservation reservation) {
        Itinerary it = reservation.getItinerary();
        if (it == null || it.getSegments() == null || it.getSegments().isEmpty()) return "Y";
        Segment first = it.getSegments().get(0);
        if (first == null || first.getFlightSchedule() == null) return "Y";
        FareRule rule = first.getFlightSchedule().getFareRule();
        return (rule != null && rule.getFareClass() != null) ? rule.getFareClass() : "Y";
    }

    private void previewRefund() {
        String pnr = reservation.getPnrNumber();
        String fareClass = resolveFareClass(reservation);
        lastRefundAmount = refundHandler.previewRefund(pnr, fareClass);
        lastPolicyName = refundHandler.previewPolicyName(pnr, fareClass);
    }

}
