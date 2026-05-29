package com.koreanair.reservation.app.fx.screen;

import java.util.List;

import com.koreanair.reservation.app.fx.AppContext;
import com.koreanair.reservation.app.fx.Navigator;
import com.koreanair.reservation.domain.payment.RefundRequest;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * 환불 검토 대기열 (담당자용) — RefundHandler.getPendingRequests / processRefund / denyRefund.
 * 환불 정책(Strategy)으로 계산된 요청을 사람이 승인/거절하는 수동 검토 단계.
 */
public final class RefundReviewController {

    @FXML private VBox queue;
    @FXML private Label message;

    private Navigator nav;
    private AppContext ctx;

    public void bind(Navigator nav, AppContext ctx) {
        this.nav = nav;
        this.ctx = ctx;
    }

    public void refresh() {
        queue.getChildren().clear();
        List<RefundRequest> pending = ctx.refundHandler.getPendingRequests();
        if (pending.isEmpty()) {
            queue.getChildren().add(new Label("검토 대기 중인 환불 요청이 없습니다."));
            return;
        }
        for (RefundRequest r : pending) queue.getChildren().add(row(r));
    }

    private HBox row(RefundRequest r) {
        VBox left = new VBox(2);
        Label id = new Label(r.getRequestId());
        id.getStyleClass().add("flight-route");
        Label meta = new Label(String.format("환불액 ₩%,d · 상태 %s",
                r.getRefundAmount() != null ? r.getRefundAmount().longValue() : 0, r.getStatus()));
        meta.getStyleClass().add("flight-meta");
        left.getChildren().addAll(id, meta);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button approve = new Button("승인");
        approve.getStyleClass().add("btn-primary");
        approve.setOnAction(e -> {
            // 승인 = 취소 상태전이를 직접 수행하고 환불은 이 요청 1건만 처리(중복 환불 방지).
            com.koreanair.reservation.domain.reservation.Reservation res = ctx.refundReview.remove(r.getRequestId());
            try {
                if (res != null) {
                    res.requestCancellation();
                    res.confirmCancellation();
                    res.requestRefund();                                       // → RefundRequested
                    ctx.refundHandler.processRefund(r.getRequestId(), r.getRefundAmount());  // 환불 1회
                    res.processRefundDecision(true);                           // → Refunded
                } else {
                    ctx.refundHandler.processRefund(r.getRequestId(), r.getRefundAmount());
                }
                message.setText(r.getRequestId() + " 승인 완료 (₩"
                        + String.format("%,d", r.getRefundAmount().longValue()) + ")");
            } catch (com.koreanair.reservation.domain.reservation.state.InvalidStateTransitionException ex) {
                ctx.refundHandler.denyRefund(r.getRequestId(), "환불 불가 운임 — 취소만 처리");
                message.setText(r.getRequestId() + ": 환불 불가 운임이라 취소만 처리되었습니다.");
            } catch (Exception ex) {
                message.setText("승인 처리 오류: " + ex.getMessage());
            }
            refresh();
        });

        Button deny = new Button("거절");
        deny.getStyleClass().add("btn-ghost");
        deny.setOnAction(e -> {
            // 거절 = 환불 요청만 거절, 예약은 그대로 유지.
            ctx.refundReview.remove(r.getRequestId());
            ctx.refundHandler.denyRefund(r.getRequestId(), "담당자 검토 후 거절");
            message.setText(r.getRequestId() + " 거절 처리 (예약 유지)");
            refresh();
        });

        HBox row = new HBox(10, left, sp, approve, deny);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("flight-row");
        return row;
    }

    @FXML
    private void onBack() { nav.showSearch(); }
}
