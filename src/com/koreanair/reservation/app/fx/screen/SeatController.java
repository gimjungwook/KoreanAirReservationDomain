package com.koreanair.reservation.app.fx.screen;

import com.koreanair.reservation.app.fx.AppContext;
import com.koreanair.reservation.app.fx.Navigator;
import com.koreanair.reservation.domain.reservation.Reservation;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public final class SeatController {

    @FXML private GridPane seatGrid;
    @FXML private Label selectedLabel;
    @FXML private Label message;

    private Navigator nav;
    private AppContext ctx;
    private Reservation reservation;

    private String selectedSeat;
    private Button selectedBtn;

    private static final int ROWS = 10;
    private static final char[] COLS = {'A', 'B', 'C', 'D', 'E', 'F'};

    public void bind(Navigator nav, AppContext ctx) {
        this.nav = nav;
        this.ctx = ctx;
    }

    public void prepare(Reservation reservation) {
        this.reservation = reservation;
        buildGrid();
        selectedSeat = null;
        selectedBtn = null;
        selectedLabel.setText("선택된 좌석 — 없음");
    }

    private void buildGrid() {
        seatGrid.getChildren().clear();
        for (int row = 1; row <= ROWS; row++) {
            Label rowNum = new Label(String.valueOf(row));
            rowNum.getStyleClass().add("flight-meta");
            seatGrid.add(rowNum, 0, row);
            int gridCol = 1;
            for (char c : COLS) {
                if (c == 'D') {
                    Region aisle = new Region();
                    aisle.setMinWidth(24);
                    seatGrid.add(aisle, gridCol++, row);
                }
                String label = row + String.valueOf(c);
                Button seat = new Button(label);
                seat.getStyleClass().add("seat");
                seat.setOnAction(e -> choose(label, seat));
                seatGrid.add(seat, gridCol++, row);
            }
        }
        // 컬럼 헤더
        int gc = 1;
        for (char c : COLS) {
            if (c == 'D') gc++;
            Label h = new Label(String.valueOf(c));
            h.getStyleClass().add("flight-meta");
            seatGrid.add(h, gc++, 0);
        }
    }

    private void choose(String label, Button btn) {
        if (selectedBtn != null) selectedBtn.getStyleClass().remove("seat-selected");
        selectedSeat = label;
        selectedBtn = btn;
        btn.getStyleClass().add("seat-selected");
        selectedLabel.setText("선택된 좌석 — " + label);
        message.setText("");
    }

    @FXML
    private void onNext() {
        if (reservation == null) { message.setText("예약 정보가 없습니다."); return; }
        if (selectedSeat == null) { message.setText("좌석을 선택하세요."); return; }
        try {
            ctx.booking.assignSeat(reservation, selectedSeat);
            nav.showPayment(reservation);
        } catch (Exception ex) {
            message.setText("좌석 배정 오류: " + ex.getMessage());
        }
    }

    @FXML
    private void onBack() { nav.showPassengerExisting(reservation); }
}
