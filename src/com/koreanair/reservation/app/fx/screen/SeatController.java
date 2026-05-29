package com.koreanair.reservation.app.fx.screen;

import com.koreanair.reservation.app.fx.AppContext;
import com.koreanair.reservation.app.fx.Navigator;
import com.koreanair.reservation.domain.flight.CabinClass;
import com.koreanair.reservation.domain.flight.Seat;
import com.koreanair.reservation.domain.flight.seatview.AisleSeatDecorator;
import com.koreanair.reservation.domain.flight.seatview.ExtraLegroomDecorator;
import com.koreanair.reservation.domain.flight.seatview.LoungeAccessDecorator;
import com.koreanair.reservation.domain.flight.seatview.SeatView;
import com.koreanair.reservation.domain.flight.seatview.SeatViewAdapter;
import com.koreanair.reservation.domain.flight.seatview.WindowSeatDecorator;
import com.koreanair.reservation.domain.reservation.Reservation;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public final class SeatController {

    @FXML private GridPane seatGrid;
    @FXML private Label selectedLabel;
    @FXML private Label seatDescLabel;     // Decorator 체인 설명
    @FXML private Label seatMetaLabel;     // 메타 라벨
    @FXML private Label surchargeLabel;    // 누적 부가요금
    @FXML private CheckBox legroomCheck;
    @FXML private CheckBox loungeCheck;
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
        legroomCheck.selectedProperty().addListener((o, a, b) -> refreshSeatView());
        loungeCheck.selectedProperty().addListener((o, a, b) -> refreshSeatView());
    }

    public void prepare(Reservation reservation) {
        this.reservation = reservation;
        buildGrid();
        selectedSeat = null;
        selectedBtn = null;
        legroomCheck.setSelected(false);
        loungeCheck.setSelected(false);
        selectedLabel.setText("선택된 좌석 — 없음");
        seatDescLabel.setText("");
        seatMetaLabel.setText("");
        surchargeLabel.setText("부가요금 ₩0");
        ctx.setSeatSurcharge(0);
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
    }

    private void choose(String label, Button btn) {
        if (selectedBtn != null) selectedBtn.getStyleClass().remove("seat-selected");
        selectedSeat = label;
        selectedBtn = btn;
        btn.getStyleClass().add("seat-selected");
        selectedLabel.setText("선택된 좌석 — " + label);
        message.setText("");
        refreshSeatView();
    }

    /** DP#9 Decorator — 좌석 위치 + 선택한 부가옵션으로 SeatView 데코레이터 체인을 조립. */
    private void refreshSeatView() {
        if (selectedSeat == null) return;
        Seat seat = new Seat(selectedSeat, CabinClass.ECONOMY);
        SeatView view = new SeatViewAdapter(seat);                       // ConcreteComponent
        char col = selectedSeat.charAt(selectedSeat.length() - 1);
        if (col == 'A' || col == 'F') view = new WindowSeatDecorator(view);   // 창측
        else if (col == 'C' || col == 'D') view = new AisleSeatDecorator(view); // 통로
        if (legroomCheck.isSelected()) view = new ExtraLegroomDecorator(view);  // +레그룸
        if (loungeCheck.isSelected()) view = new LoungeAccessDecorator(view);   // +라운지
        seatDescLabel.setText(view.getDescription());
        boolean showMeta = com.koreanair.reservation.app.AppConfig.getInstance().isShowSeatMetadata();
        seatMetaLabel.setVisible(showMeta);
        seatMetaLabel.setText(showMeta ? String.join(" · ", view.getMetadataLabels()) : "");
        long surcharge = view.getSurcharge().longValue();
        surchargeLabel.setText("부가요금 ₩" + String.format("%,d", surcharge));
        ctx.setSeatSurcharge(surcharge);
    }

    @FXML
    private void onNext() {
        if (reservation == null) { message.setText("예약 정보가 없습니다."); return; }
        if (selectedSeat == null) { message.setText("좌석을 선택하세요."); return; }
        try {
            Object assignment = ctx.booking.assignSeat(reservation, selectedSeat);
            if (assignment == null) {
                message.setText("좌석 배정에 실패했습니다. 다른 좌석을 선택하세요.");
                return;
            }
            nav.showPayment(reservation);
        } catch (Exception ex) {
            message.setText("좌석 배정 오류: " + ex.getMessage());
        }
    }

    @FXML
    private void onBack() { nav.showPassengerExisting(reservation); }
}
