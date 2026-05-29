package com.koreanair.reservation.app.fx.screen;

import com.koreanair.reservation.app.fx.AppContext;
import com.koreanair.reservation.app.fx.Navigator;
import com.koreanair.reservation.domain.bus.BusCity;
import com.koreanair.reservation.domain.bus.BusTicket;
import com.koreanair.reservation.domain.bus.BusTicketRequest;
import com.koreanair.reservation.domain.payment.Payment;
import com.koreanair.reservation.domain.reservation.Reservation;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public final class ConfirmationController {

    @FXML private Label pnrLabel;
    @FXML private Label stateLabel;
    @FXML private Label amountLabel;
    @FXML private Label busLabel;
    @FXML private ComboBox<BusCity> busCityCombo;
    @FXML private Button lookupBtn;
    @FXML private Label message;

    private Navigator nav;
    private AppContext ctx;
    private Reservation reservation;

    public void bind(Navigator nav, AppContext ctx) {
        this.nav = nav;
        this.ctx = ctx;
    }

    public void prepare(Reservation reservation, Payment payment, BusTicket bus) {
        this.reservation = reservation;
        pnrLabel.setText(reservation != null ? reservation.getPnrNumber() : "-");
        stateLabel.setText(reservation != null ? reservation.getStateName() : "-");
        amountLabel.setText(payment != null ? String.format("₩%,d", payment.getAmount().longValue()) : "-");

        busCityCombo.getItems().setAll(ctx.busTicketingService.supportedCities());
        if (bus != null) showBus(bus);
        else busLabel.setText("셔틀버스 미발권");

        boolean signedIn = ctx.isSignedIn();
        lookupBtn.setVisible(signedIn);
        lookupBtn.setManaged(signedIn);
    }

    private void showBus(BusTicket bus) {
        busLabel.setText("셔틀버스 발권 완료 — " + bus.getTicketNumber()
                + " (" + bus.getOriginCity().getDisplayName() + ")");
    }

    @FXML
    private void onIssueTicket() {
        if (reservation == null) return;
        try {
            reservation.issueTicket();                 // State: Confirmed → Ticketed
            stateLabel.setText(reservation.getStateName());
            nav.updateState(reservation);
            message.setText("e-Ticket 발급 완료");
        } catch (Exception ex) {
            message.setText("발권 오류: " + ex.getMessage());
        }
    }

    @FXML
    private void onLinkBus() {
        if (reservation == null) return;
        BusCity city = busCityCombo.getValue();
        if (city == null) { message.setText("출발 도시를 선택하세요."); return; }
        try {
            BusTicket bus = ctx.issueLinkedBusTicket(reservation, new BusTicketRequest(city, null, null));
            showBus(bus);
            stateLabel.setText(reservation.getStateName());
            nav.updateState(reservation);
            message.setText("");
        } catch (Exception ex) {
            message.setText("셔틀 발권 오류: " + ex.getMessage());
        }
    }

    @FXML
    private void onLookup() { nav.showLookup(); }

    @FXML
    private void onNew() { nav.startNewBooking(); }
}
