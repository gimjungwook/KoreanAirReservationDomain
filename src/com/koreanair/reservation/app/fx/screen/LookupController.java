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
        Label state = new Label("상태 · " + r.getStateName());
        state.getStyleClass().add("flight-meta");
        left.getChildren().addAll(pnr, state);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button cont = new Button("계속 진행");
        cont.getStyleClass().add("btn-ghost");
        cont.setOnAction(e -> nav.showPassengerExisting(r));

        Button cancel = new Button("취소/환불");
        cancel.getStyleClass().add("btn-ghost");
        cancel.setOnAction(e -> nav.showCancellation(r));

        HBox row = new HBox(10, left, sp, cont, cancel);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("flight-row");
        return row;
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
