package com.koreanair.reservation.app.fx.screen;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.koreanair.reservation.app.fx.AppContext;
import com.koreanair.reservation.app.fx.Navigator;
import com.koreanair.reservation.domain.flight.FlightSchedule;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;

public final class SearchController {

    @FXML private TextField fromField;
    @FXML private TextField toField;
    @FXML private DatePicker datePicker;
    @FXML private VBox results;
    @FXML private Label message;

    private Navigator nav;
    private AppContext ctx;

    private static final DateTimeFormatter T = DateTimeFormatter.ofPattern("MM/dd HH:mm");

    public void bind(Navigator nav, AppContext ctx) {
        this.nav = nav;
        this.ctx = ctx;
        if (datePicker.getValue() == null) {
            datePicker.setValue(LocalDate.now().plusDays(7));
        }
        showAll();
    }

    @FXML
    private void onSearch() {
        String from = safe(fromField.getText());
        String to = safe(toField.getText());
        LocalDate date = datePicker.getValue();
        try {
            List<FlightSchedule> list = ctx.booking.processSearch(from, to, date);
            render(list);
            if (list.isEmpty()) message.setText("해당 조건의 직항편이 없습니다.");
            else message.setText("");
        } catch (Exception ex) {
            message.setText("검색 오류: " + ex.getMessage());
        }
    }

    @FXML
    private void onShowAll() { showAll(); }

    private void showAll() {
        render(ctx.search.getCatalog());
        message.setText("");
    }

    private void render(List<FlightSchedule> list) {
        results.getChildren().clear();
        for (FlightSchedule s : list) {
            results.getChildren().add(row(s));
        }
    }

    private HBox row(FlightSchedule s) {
        String origin = s.getFlight().getRoute().getOrigin().getCode();
        String dest = s.getFlight().getRoute().getDestination().getCode();

        VBox left = new VBox(2);
        Label route = new Label(origin + "  →  " + dest);
        route.getStyleClass().add("flight-route");
        Label meta = new Label(s.getFlightNumber() + " · "
                + s.getDepartureDateTime().format(T) + " 출발 · "
                + s.getAircraftType());
        meta.getStyleClass().add("flight-meta");
        left.getChildren().addAll(route, meta);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label fare = new Label("₩320,000~");
        fare.getStyleClass().add("flight-fare");

        HBox r = new HBox(14, left, spacer, fare);
        r.setAlignment(Pos.CENTER_LEFT);
        r.getStyleClass().add("flight-row");
        r.setOnMouseClicked(e -> select(s));
        return r;
    }

    private void select(FlightSchedule s) {
        if (ctx.isSignedIn()) {
            nav.showPassenger(s);
        } else {
            nav.requireSignIn(s);
        }
    }

    private static String safe(String s) { return s == null ? "" : s.trim(); }
}
