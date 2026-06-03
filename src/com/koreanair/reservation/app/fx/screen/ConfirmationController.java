package com.koreanair.reservation.app.fx.screen;

import com.koreanair.reservation.app.fx.AppContext;
import com.koreanair.reservation.app.fx.Navigator;
import com.koreanair.reservation.domain.bus.BusCity;
import com.koreanair.reservation.domain.bus.BusTicket;
import com.koreanair.reservation.domain.bus.BusTicketRequest;
import com.koreanair.reservation.domain.payment.Payment;
import com.koreanair.reservation.domain.reservation.Reservation;

import com.koreanair.reservation.control.render.BoardingPassRenderer;
import com.koreanair.reservation.control.render.HtmlTicketRenderer;
import com.koreanair.reservation.control.render.PlainTextTicketRenderer;
import com.koreanair.reservation.control.render.TicketRenderer;
import com.koreanair.reservation.domain.reservation.Ticket;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public final class ConfirmationController {

    @FXML private Label pnrLabel;
    @FXML private Label stateLabel;
    @FXML private Label amountLabel;
    @FXML private Label busLabel;
    @FXML private ComboBox<BusCity> busCityCombo;
    @FXML private ComboBox<String> ticketFormatCombo;
    @FXML private TextArea ticketArea;
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

        if (ticketFormatCombo.getItems().isEmpty()) {
            ticketFormatCombo.getItems().setAll("일반 텍스트", "HTML", "보딩패스");
            ticketFormatCombo.getSelectionModel().selectFirst();
            ticketFormatCombo.valueProperty().addListener((o, a, b) -> renderTicket());
        }
        ticketArea.setText("");

        boolean signedIn = ctx.isSignedIn();
        lookupBtn.setVisible(signedIn);
        lookupBtn.setManaged(signedIn);
    }

    @FXML
    private void onCopyPnr() {
        if (reservation == null || reservation.getPnrNumber() == null) return;
        ClipboardContent content = new ClipboardContent();
        content.putString(reservation.getPnrNumber());
        Clipboard.getSystemClipboard().setContent(content);
        message.setText("PNR이 클립보드에 복사되었습니다.");
    }

    private void showBus(BusTicket bus) {
        busLabel.setText("셔틀버스 발권 완료 — " + bus.getTicketNumber()
                + " (" + bus.getOriginCity().getDisplayName() + ")");
    }

    @FXML
    private void onIssueTicket() {
        if (reservation == null) return;
        try {
            if (reservation.getTickets().isEmpty()) {
                reservation.issueTicket();             // State: Confirmed → Ticketed
            }
            stateLabel.setText(reservation.getStateName());
            nav.updateState(reservation);
            renderTicket();
            message.setText("e-Ticket 발급 완료");
        } catch (Exception ex) {
            message.setText("발권 오류: " + ex.getMessage());
        }
    }

    /** DP#6 Template Method — 선택한 포맷의 TicketRenderer 로 동일 데이터를 다른 매체로 렌더. */
    private void renderTicket() {
        if (reservation == null || reservation.getTickets().isEmpty()) return;
        Ticket ticket = reservation.getTickets().get(reservation.getTickets().size() - 1);
        String fmt = ticketFormatCombo.getValue();
        TicketRenderer renderer = switch (fmt == null ? "" : fmt) {
            case "HTML" -> new HtmlTicketRenderer();
            case "보딩패스" -> new BoardingPassRenderer();
            default -> new PlainTextTicketRenderer();
        };
        ticketArea.setText(renderer.render(reservation, ticket));
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
