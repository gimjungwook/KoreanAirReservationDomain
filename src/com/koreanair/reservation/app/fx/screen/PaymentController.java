package com.koreanair.reservation.app.fx.screen;

import com.koreanair.reservation.app.fx.AppContext;
import com.koreanair.reservation.app.fx.Navigator;
import com.koreanair.reservation.domain.bus.BusCity;
import com.koreanair.reservation.domain.bus.BusSchedule;
import com.koreanair.reservation.domain.bus.BusSeat;
import com.koreanair.reservation.domain.bus.BusTicket;
import com.koreanair.reservation.domain.bus.BusTicketRequest;
import com.koreanair.reservation.domain.flight.FareRule;
import com.koreanair.reservation.domain.payment.Payment;
import com.koreanair.reservation.domain.payment.PaymentMethod;
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
    @FXML private Label mileageLabel;
    @FXML private Label projectedMileageLabel;
    @FXML private CheckBox busAddon;
    @FXML private ComboBox<BusCity> busCityCombo;
    @FXML private ComboBox<BusSchedule> busScheduleCombo;
    @FXML private ComboBox<BusSeat> busSeatCombo;
    @FXML private Label busHelpLabel;
    @FXML private Label message;

    private Navigator nav;
    private AppContext ctx;
    private Reservation reservation;
    private FareRule fareRule;

    // 구간(segment)당 기본 운임/세금 — 왕복(2)·다구간(N)은 구간 수만큼 합산.
    private static final long FARE_PER_SEGMENT = 320_000L;
    private static final long TAX_PER_SEGMENT = 32_000L;

    private int segmentCount() {
        try {
            int n = reservation.getItinerary().getSegments().size();
            return Math.max(1, n);
        } catch (Exception e) {
            return 1;
        }
    }
    private long baseFare() { return FARE_PER_SEGMENT * segmentCount(); }
    private long tax() { return TAX_PER_SEGMENT * segmentCount(); }
    private long totalAmount() { return baseFare() + tax() + ctx.seatSurcharge(); }

    public void bind(Navigator nav, AppContext ctx) {
        this.nav = nav;
        this.ctx = ctx;
    }

    public void prepare(Reservation reservation, FareRule fareRule) {
        this.reservation = reservation;
        this.fareRule = fareRule;
        pnrLabel.setText(reservation != null ? reservation.getPnrNumber() : "-");
        totalLabel.setText(String.format("₩%,d (%d개 구간)", totalAmount(), segmentCount()));

        methodCombo.getItems().setAll("신용카드", "카카오페이", "애플페이", "계좌이체", "마일리지");
        methodCombo.getSelectionModel().selectFirst();
        methodCombo.valueProperty().addListener((o, a, b) -> refreshMileageLabel());
        refreshMileageLabel();

        busCityCombo.getItems().setAll(ctx.busTicketingService.supportedCities());
        if (!busCityCombo.getItems().isEmpty()) {
            busCityCombo.getSelectionModel().selectFirst();
        }
        busCityCombo.setDisable(true);
        busScheduleCombo.setDisable(true);
        busSeatCombo.setDisable(true);
        busAddon.setSelected(false);
        busAddon.selectedProperty().addListener((o, was, now) -> {
            busCityCombo.setDisable(!now);
            busScheduleCombo.setDisable(!now);
            busSeatCombo.setDisable(!now);
            refreshBusOptions();
        });
        busCityCombo.valueProperty().addListener((o, a, b) -> refreshBusOptions());
        busScheduleCombo.valueProperty().addListener((o, a, b) -> refreshBusSeats());
        refreshBusOptions();
    }

    private void refreshMileageLabel() {
        if (!(ctx.isSignedIn() && ctx.loggedInMember() != null)) {
            mileageLabel.setText("마일리지 결제는 회원 로그인 후 사용할 수 있습니다.");
            if (projectedMileageLabel != null) {
                projectedMileageLabel.setText("");
            }
            return;
        }
        try {
            int bal = ctx.skypass.getMileageBalance(ctx.loggedInMember().getMemberNumber());
            boolean mileage = "마일리지".equals(methodCombo.getValue());
            if (mileage) {
                long remaining = Math.max(0L, bal - totalAmount());
                mileageLabel.setText(String.format("마일리지 결제 사용 · 현재 보유 %,d M", bal));
                if (projectedMileageLabel != null) {
                    projectedMileageLabel.setText(String.format("차감 예정 %,d M → 예상 잔액 %,d M", totalAmount(), remaining));
                }
            } else {
                mileageLabel.setText(String.format("보유 마일리지 %,d M", bal));
                if (projectedMileageLabel != null) {
                    projectedMileageLabel.setText("마일리지 결제 시 차감 예상치가 표시됩니다.");
                }
            }
        } catch (Exception ignore) {
            mileageLabel.setText("");
            if (projectedMileageLabel != null) {
                projectedMileageLabel.setText("");
            }
        }
    }

    private void refreshBusOptions() {
        boolean enabled = busAddon.isSelected();
        BusCity city = busCityCombo.getValue();
        busScheduleCombo.getItems().clear();
        busSeatCombo.getItems().clear();
        if (!enabled || city == null) {
            busHelpLabel.setText("셔틀 연계를 선택하면 출발 도시별 운행 시간과 좌석을 고를 수 있습니다.");
            return;
        }
        busScheduleCombo.getItems().setAll(ctx.busTicketingService.schedulesFor(city));
        if (!busScheduleCombo.getItems().isEmpty()) {
            busScheduleCombo.getSelectionModel().selectFirst();
        }
        refreshBusSeats();
        busHelpLabel.setText(city.getDisplayName() + " → ICN 우등고속 셔틀 · 운임 "
                + String.format("₩%,d", city.getPremiumFare()));
    }

    private void refreshBusSeats() {
        busSeatCombo.getItems().clear();
        BusSchedule schedule = busScheduleCombo.getValue();
        if (schedule == null) {
            return;
        }
        busSeatCombo.getItems().setAll(schedule.availableSeats());
        if (!busSeatCombo.getItems().isEmpty()) {
            busSeatCombo.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void onPay() {
        if (reservation == null || fareRule == null) {
            message.setText("결제 대상 예약이 없습니다.");
            return;
        }
        if (busAddon.isSelected() && (busCityCombo.getValue() == null
                || busScheduleCombo.getValue() == null || busSeatCombo.getValue() == null)) {
            message.setText("셔틀버스 연계를 선택했습니다. 출발 도시/운행/좌석을 골라주세요.");
            return;
        }
        try {
            // DP#5 Factory Method — 선택한 결제 수단에 맞는 PaymentMethodProcessor 를 팩토리가 생성.
            Payment payment = ctx.booking.confirmPaymentWith(
                    reservation, fareRule, baseFare(), tax() + ctx.seatSurcharge(),
                    methodOf(methodCombo.getValue()), ctx.gateway, ctx.sessionMileage);
            if (payment == null || payment.getStatus() != PaymentStatus.PAID) {
                message.setText("결제가 실패했습니다. (잔액/한도 확인) 다시 시도하세요.");
                return;
            }
            // 셔틀버스 연계 발권 (Observer 패턴 시연) — 선택 시에만.
            BusTicket bus = null;
            if (busAddon.isSelected() && busCityCombo.getValue() != null) {
                try {
                    bus = ctx.issueLinkedBusTicket(reservation,
                            new BusTicketRequest(busCityCombo.getValue(),
                                    busScheduleCombo.getValue(), busSeatCombo.getValue()));
                } catch (Exception ex) {
                    System.out.println("[BUS] issue failed: " + ex.getMessage());
                }
            }
            nav.showConfirmation(reservation, payment, bus);
        } catch (Exception ex) {
            message.setText("결제 오류: " + ex.getMessage());
        }
    }

    private static PaymentMethod methodOf(String label) {
        if (label == null) return PaymentMethod.CREDIT_CARD;
        return switch (label) {
            case "카카오페이" -> PaymentMethod.KAKAO_PAY;
            case "애플페이" -> PaymentMethod.APPLE_PAY;
            case "계좌이체" -> PaymentMethod.BANK_TRANSFER;
            case "마일리지" -> PaymentMethod.MILEAGE;
            default -> PaymentMethod.CREDIT_CARD;
        };
    }

    @FXML
    private void onBack() { nav.showSeat(reservation); }

    public void setContinueFromLookup(boolean continueFromLookup) {
        ctx.setContinueFromLookup(continueFromLookup);
    }
}
