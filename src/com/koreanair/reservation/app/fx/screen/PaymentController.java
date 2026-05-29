package com.koreanair.reservation.app.fx.screen;

import com.koreanair.reservation.app.fx.AppContext;
import com.koreanair.reservation.app.fx.Navigator;
import com.koreanair.reservation.domain.bus.BusCity;
import com.koreanair.reservation.domain.bus.BusTicket;
import com.koreanair.reservation.domain.bus.BusTicketRequest;
import com.koreanair.reservation.domain.flight.FareRule;
import com.koreanair.reservation.domain.payment.Payment;
import com.koreanair.reservation.domain.payment.PaymentStatus;
import com.koreanair.reservation.domain.reservation.Reservation;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public final class PaymentController {

    @FXML private Label pnrLabel;
    @FXML private Label totalLabel;
    @FXML private ComboBox<String> methodCombo;
    @FXML private CheckBox busAddon;
    @FXML private ComboBox<BusCity> busCityCombo;
    @FXML private Label message;

    private Navigator nav;
    private AppContext ctx;
    private Reservation reservation;
    private FareRule fareRule;

    // PaymentPanel.DEFAULT_BASE_FARE / DEFAULT_TAX 와 동일.
    private static final long BASE_FARE = 320_000L;
    private static final long TAX = 32_000L;

    public void bind(Navigator nav, AppContext ctx) {
        this.nav = nav;
        this.ctx = ctx;
    }

    public void prepare(Reservation reservation, FareRule fareRule) {
        this.reservation = reservation;
        this.fareRule = fareRule;
        pnrLabel.setText(reservation != null ? reservation.getPnrNumber() : "-");
        totalLabel.setText(String.format("₩%,d", BASE_FARE + TAX));

        methodCombo.getItems().setAll("신용카드", "카카오페이", "애플페이", "계좌이체", "마일리지");
        methodCombo.getSelectionModel().selectFirst();

        busCityCombo.getItems().setAll(ctx.busTicketingService.supportedCities());
        busCityCombo.setDisable(true);
        busAddon.setSelected(false);
        busAddon.selectedProperty().addListener((o, was, now) -> busCityCombo.setDisable(!now));
    }

    @FXML
    private void onPay() {
        if (reservation == null || fareRule == null) {
            message.setText("결제 대상 예약이 없습니다.");
            return;
        }
        try {
            Payment payment = ctx.booking.confirmPayment(reservation, fareRule, BASE_FARE, TAX);
            if (payment == null || payment.getStatus() != PaymentStatus.PAID) {
                message.setText("결제가 실패했습니다. 다시 시도하세요.");
                return;
            }
            // 셔틀버스 연계 발권 (Observer 패턴 시연) — 선택 시에만.
            BusTicket bus = null;
            if (busAddon.isSelected() && busCityCombo.getValue() != null) {
                try {
                    bus = ctx.issueLinkedBusTicket(reservation,
                            new BusTicketRequest(busCityCombo.getValue(), null, null));
                } catch (Exception ex) {
                    System.out.println("[BUS] issue failed: " + ex.getMessage());
                }
            }
            nav.showConfirmation(reservation, payment, bus);
        } catch (Exception ex) {
            message.setText("결제 오류: " + ex.getMessage());
        }
    }

    @FXML
    private void onBack() { nav.showSeat(reservation); }
}
