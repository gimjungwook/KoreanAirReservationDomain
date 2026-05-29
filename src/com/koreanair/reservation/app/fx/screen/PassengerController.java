package com.koreanair.reservation.app.fx.screen;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

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

    /** 검색에서 선택한 항공편으로 예약 생성 (State: Initiated). */
    public void prepare(FlightSchedule selected) {
        reservation = ctx.booking.initiateBooking(selected);
        Member me = ctx.loggedInMember();
        if (me != null) reservation.setRequester(me);
        ctx.setCurrentReservation(reservation);
        nav.registerReservationToMember(reservation);
        nav.updateState(reservation);

        String origin = selected.getFlight().getRoute().getOrigin().getCode();
        String dest = selected.getFlight().getRoute().getDestination().getCode();
        flightInfo.setText(selected.getFlightNumber() + "  " + origin + " → " + dest);
        if (me != null && me.getName() != null) nameField.setText(me.getName());
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
