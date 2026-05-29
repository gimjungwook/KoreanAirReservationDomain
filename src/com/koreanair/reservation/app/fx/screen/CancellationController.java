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
            ctx.booking.processCancellation(pnr);   // State 전이
            nav.updateState(reservation);
            nav.showRefund(pnr, amount, policy);
        } catch (Exception ex) {
            message.setText("취소 오류: " + ex.getMessage());
        }
    }

    @FXML
    private void onBack() { nav.showLookup(); }
}
