package com.koreanair.reservation.app.fx.screen;

import java.util.List;

import com.koreanair.reservation.app.fx.AppContext;
import com.koreanair.reservation.app.fx.Navigator;
import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.user.Member;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public final class LookupController {

    @FXML private VBox memberList;
    @FXML private TextField pnrField;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private Label message;

    private Navigator nav;
    private AppContext ctx;

    public void bind(Navigator nav, AppContext ctx) {
        this.nav = nav;
        this.ctx = ctx;
    }

    public void refresh() {
        memberList.getChildren().clear();
        Member m = ctx.loggedInMember();
        if (m == null) {
            memberList.getChildren().add(new Label("비회원은 아래 PNR 조회를 사용하세요."));
            return;
        }
        List<Reservation> list = ctx.lookupService.findByMember(m);
        if (list.isEmpty()) {
            memberList.getChildren().add(new Label("예약 내역이 없습니다."));
            return;
        }
        for (Reservation r : list) memberList.getChildren().add(row(r));
    }

    private HBox row(Reservation r) {
        VBox left = new VBox(2);
        Label pnr = new Label(r.getPnrNumber());
        pnr.getStyleClass().add("flight-route");
        pnr.getStyleClass().add("copy-target");
        pnr.setTooltip(new Tooltip("클릭하면 PNR이 복사됩니다"));
        pnr.setOnMouseClicked(e -> copyPnr(r));
        String stateName = r.getStateName();
        Label state = new Label(formatStateName(stateName));
        state.getStyleClass().add("flight-meta");
        state.getStyleClass().add("state-pill");
        state.getStyleClass().add(stateClass(stateName));
        Label route = new Label(routeSummary(r));
        route.getStyleClass().add("flight-meta");
        left.getChildren().addAll(pnr, state, route);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        HBox row = new HBox(10, left, sp);
        HBox actions = new HBox(8);
        actions.getStyleClass().add("row-actions");
        Button detail = new Button("상세/e-Ticket");
        detail.getStyleClass().add("btn-ghost");
        detail.setOnAction(e -> nav.showReservationDetail(r));
        actions.getChildren().add(detail);

        Button copy = new Button("PNR 복사");
        copy.getStyleClass().add("btn-ghost");
        copy.setOnAction(e -> copyPnr(r));
        actions.getChildren().add(copy);

        // 미완료(Initiated/PendingPayment)만 이어서 진행 — 완료 상태는 잘못된 전이 방지.
        if ("Initiated".equals(stateName)) {
            Button cont = new Button("승객정보 이어서 입력");
            cont.getStyleClass().add("btn-ghost");
            cont.setOnAction(e -> nav.showPassengerExisting(r));   // 승객정보부터
            actions.getChildren().add(cont);
        } else if ("PendingPayment".equals(stateName)) {
            Button cont = new Button("좌석/결제 이어서 진행");
            cont.getStyleClass().add("btn-ghost");
            cont.setOnAction(e -> nav.showSeatFromExisting(r));   // 승객정보 입력 완료 → 좌석/결제부터
            actions.getChildren().add(cont);
        }
        // 확정/발권 상태만 취소·환불 가능.
        if ("Confirmed".equals(stateName) || "Ticketed".equals(stateName)) {
            Button cancel = new Button("취소/환불");
            cancel.getStyleClass().add("btn-ghost");
            cancel.setOnAction(e -> nav.showCancellation(r));
            actions.getChildren().add(cancel);
        }
        row.getChildren().add(actions);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("flight-row");
        return row;
    }

    private static String formatStateName(String raw) {
        if (raw == null || raw.isBlank()) {
            return "상태 · -";
        }
        return "상태 · " + switch (raw) {
            case "Initiated" -> "예약 개시";
            case "PendingPayment" -> "결제 대기";
            case "Confirmed" -> "예약 확정";
            case "Ticketed" -> "발권 완료";
            case "CancellationRequested" -> "취소 요청";
            case "RefundRequested" -> "환불 요청";
            case "Refunded" -> "환불 완료";
            case "Cancelled" -> "예약 취소";
            case "Paid" -> "결제 완료";
            default -> raw;
        };
    }

    private static String stateClass(String raw) {
        if (raw == null || raw.isBlank()) {
            return "state-pill-default";
        }
        return switch (raw) {
            case "Initiated" -> "state-pill-dim";
            case "PendingPayment" -> "state-pill-warning";
            case "Confirmed" -> "state-pill-info";
            case "Ticketed" -> "state-pill-success";
            case "CancellationRequested" -> "state-pill-warning";
            case "RefundRequested" -> "state-pill-warning";
            case "Refunded" -> "state-pill-success";
            case "Cancelled" -> "state-pill-danger";
            default -> "state-pill-default";
        };
    }

    private void copyPnr(Reservation r) {
        if (r == null || r.getPnrNumber() == null) return;
        ClipboardContent content = new ClipboardContent();
        content.putString(r.getPnrNumber());
        Clipboard.getSystemClipboard().setContent(content);
        message.setText("PNR을 복사했습니다: " + r.getPnrNumber());
    }

    private static String routeSummary(Reservation r) {
        try {
            java.util.List<com.koreanair.reservation.domain.reservation.Segment> segs =
                    r.getItinerary().getSegments();
            if (segs.isEmpty()) {
                return "여정 정보 없음";
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < segs.size(); i++) {
                com.koreanair.reservation.domain.flight.FlightSchedule fs = segs.get(i).getFlightSchedule();
                if (i == 0) {
                    sb.append(fs.getFlight().getRoute().getOrigin().getCode());
                }
                sb.append(" → ").append(fs.getFlight().getRoute().getDestination().getCode());
            }
            sb.append(" · ").append(segs.size()).append("구간");
            return sb.toString();
        } catch (Exception ex) {
            return "여정 정보 확인 불가";
        }
    }

    @FXML
    private void onGuestLookup() {
        String pnr = txt(pnrField), name = txt(nameField), email = txt(emailField);
        if (pnr.isEmpty()) { message.setText("PNR을 입력하세요."); return; }
        Reservation r = ctx.lookupService.findByGuestPnr(pnr, name, email);
        if (r == null) {
            message.setText("일치하는 예약을 찾지 못했습니다. (PNR/이름/이메일 확인)");
            return;
        }
        message.setText("");
        memberList.getChildren().setAll(row(r));
    }

    private static String txt(TextField f) { return f.getText() == null ? "" : f.getText().trim(); }
}
