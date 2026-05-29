package com.koreanair.reservation.app.fx.screen;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import com.koreanair.reservation.app.fx.AppContext;
import com.koreanair.reservation.app.fx.Navigator;
import com.koreanair.reservation.domain.flight.FlightSchedule;
import com.koreanair.reservation.domain.passenger.Passenger;
import com.koreanair.reservation.domain.passenger.PassengerType;
import com.koreanair.reservation.domain.reservation.Reservation;
import com.koreanair.reservation.domain.user.Member;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public final class PassengerController {

    @FXML private Label flightInfo;
    @FXML private TextField nameField;
    @FXML private TextField passportField;
    @FXML private TextField birthField;
    @FXML private Label message;

    private Navigator nav;
    private AppContext ctx;
    private Reservation reservation;

    public void bind(Navigator nav, AppContext ctx) {
        this.nav = nav;
        this.ctx = ctx;
    }

    private static String code(FlightSchedule s) {
        return s.getFlight().getRoute().getOrigin().getCode()
                + "→" + s.getFlight().getRoute().getDestination().getCode();
    }

    /** 생성된 예약 공통 후처리 — 요청자/세션/회원 동기화 + 승객명 prefill. */
    private void afterCreate(String infoText) {
        Member me = ctx.loggedInMember();
        if (me != null) reservation.setRequester(me);
        ctx.setCurrentReservation(reservation);
        nav.registerReservationToMember(reservation);
        nav.updateState(reservation);
        flightInfo.setText(infoText);
        if (me != null && me.getName() != null) nameField.setText(me.getName());
    }

    /** 편도 — 검색에서 선택한 항공편으로 예약 생성 (State: Initiated). */
    public void prepare(FlightSchedule selected) {
        reservation = ctx.booking.initiateBooking(selected);
        afterCreate("편도 · " + selected.getFlightNumber() + "  " + code(selected));
    }

    /** 왕복 — 가는 편 + 오는 편 2개 segment. */
    public void prepareRoundTrip(FlightSchedule outbound, FlightSchedule inbound) {
        reservation = ctx.booking.initiateRoundTripBooking(outbound, inbound);
        afterCreate("왕복 · " + code(outbound) + "  /  " + code(inbound));
    }

    /** 다구간 — N개 segment (MultiCityItineraryFactory). */
    public void prepareMultiCity(List<FlightSchedule> segments) {
        reservation = ctx.booking.initiateMultiCityBooking(segments);
        StringBuilder sb = new StringBuilder("다구간 · " + segments.size() + "개 구간  ");
        for (int i = 0; i < segments.size(); i++) {
            sb.append(i == 0 ? "" : " → ")
              .append(segments.get(i).getFlight().getRoute().getOrigin().getCode());
        }
        sb.append(" → ").append(segments.get(segments.size() - 1)
                .getFlight().getRoute().getDestination().getCode());
        afterCreate(sb.toString());
    }

    /** 예약 조회에서 "계속 진행"한 기존 예약. */
    public void prepareExisting(Reservation existing) {
        this.reservation = existing;
        ctx.setCurrentReservation(existing);
        flightInfo.setText("기존 예약 " + (existing != null ? existing.getPnrNumber() : "-"));
        if (existing != null && !existing.getPassengers().isEmpty()) {
            Passenger p = existing.getPassengers().get(0);
            if (p.getName() != null) nameField.setText(p.getName());
            if (p.getPassportNumber() != null) passportField.setText(p.getPassportNumber());
        } else {
            Member me = ctx.loggedInMember();
            if (me != null && me.getName() != null) nameField.setText(me.getName());
        }
    }

    @FXML
    private void onNext() {
        if (reservation == null) { message.setText("예약이 생성되지 않았습니다."); return; }
        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        if (name.isEmpty()) { message.setText("승객 이름을 입력하세요."); return; }
        try {
            LocalDate birth = LocalDate.parse(birthField.getText().trim());
            Member me = ctx.loggedInMember();
            Passenger passenger = Passenger.create(
                    name,
                    me != null ? me.getEmail() : null,
                    passportField.getText().trim(),
                    birth,
                    PassengerType.ADULT);
            ctx.booking.setPassengerInfo(reservation, passenger);  // State: Initiated → PendingPayment
            nav.showSeat(reservation);
        } catch (DateTimeParseException ex) {
            message.setText("생년월일 형식이 올바르지 않습니다. 예: 1999-01-31");
        } catch (IllegalArgumentException ex) {
            message.setText(ex.getMessage());
        }
    }

    @FXML
    private void onBack() { nav.showSearch(); }
}
