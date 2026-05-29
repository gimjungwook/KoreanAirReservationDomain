package com.koreanair.reservation.app.fx.screen;

import java.math.BigDecimal;

import com.koreanair.reservation.app.fx.AppContext;
import com.koreanair.reservation.app.fx.Navigator;
import com.koreanair.reservation.domain.flight.FareRule;
import com.koreanair.reservation.domain.reservation.Reservation;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public final class CancellationController {

    @FXML private Label pnrLabel;
    @FXML private Label stateLabel;
    @FXML private Label previewLabel;
    @FXML private Label message;

    private Navigator nav;
    private AppContext ctx;
    private Reservation reservation;

    public void bind(Navigator nav, AppContext ctx) {
        this.nav = nav;
        this.ctx = ctx;
    }

    public void prepare(Reservation reservation) {
        this.reservation = reservation;
        pnrLabel.setText(reservation != null ? reservation.getPnrNumber() : "-");
        stateLabel.setText(reservation != null ? reservation.getStateName() : "-");
        previewLabel.setText("환불 예상액을 미리 확인하세요.");
        message.setText("");
    }

    private String fareClass() {
        try {
            FareRule fr = reservation.getItinerary().getSegments().get(0)
                    .getFlightSchedule().getFareRule();
            if (fr != null && fr.getFareClass() != null) return fr.getFareClass();
        } catch (Exception ignore) { /* fall through */ }
        return ctx.seed.defaultFareRule.getFareClass();
    }

    @FXML
    private void onPreview() {
        if (reservation == null) return;
        String pnr = reservation.getPnrNumber();
        String fc = fareClass();
        try {
            BigDecimal amount = ctx.refundHandler.previewRefund(pnr, fc);
            String policy = ctx.refundHandler.previewPolicyName(pnr, fc);
            previewLabel.setText(String.format("환불 정책: %s · 예상 환불액: ₩%,d", policy, amount.longValue()));
        } catch (Exception ex) {
            message.setText("미리보기 오류: " + ex.getMessage());
        }
    }

    @FXML
    private void onCancel() {
        if (reservation == null) return;
        String pnr = reservation.getPnrNumber();
        String fc = fareClass();
        try {
            BigDecimal amount = ctx.refundHandler.previewRefund(pnr, fc);
            String policy = ctx.refundHandler.previewPolicyName(pnr, fc);
            ctx.booking.processCancellation(pnr);   // 즉시 취소 + 자동 환불(State 전이)
            nav.updateState(reservation);
            nav.showRefund(pnr, amount, policy);
        } catch (Exception ex) {
            message.setText("취소 오류: " + ex.getMessage());
        }
    }

    /**
     * 담당자 검토 경로 — 즉시 환불하지 않고 예약을 RefundRequested 상태로만 전이한 뒤
     * 환불 요청(REQUESTED)을 생성해 검토 대기열로 보낸다. 승인/거절은 검토 화면에서 결정.
     */
    /**
     * 담당자 검토 경로 — 예약 상태는 그대로 두고 환불 요청(REQUESTED)만 생성해 검토 대기열로 보낸다.
     * 승인 시 검토 화면에서 실제 취소+환불이 일어나고, 거절 시 예약은 그대로 유지된다.
     */
    @FXML
    private void onRequestReview() {
        if (reservation == null) return;
        try {
            com.koreanair.reservation.domain.payment.RefundRequest req =
                    ctx.refundHandler.evaluateRefund(reservation.getPnrNumber(), fareClass());  // Strategy → 대기 요청
            if (req != null) ctx.refundReview.put(req.getRequestId(), reservation);
            nav.showRefundReview();
        } catch (Exception ex) {
            message.setText("검토 요청 오류: " + ex.getMessage());
        }
    }

    @FXML
    private void onBack() { nav.showLookup(); }
}
