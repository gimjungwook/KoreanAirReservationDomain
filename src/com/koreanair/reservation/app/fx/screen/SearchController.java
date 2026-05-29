package com.koreanair.reservation.app.fx.screen;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.koreanair.reservation.app.fx.AppContext;
import com.koreanair.reservation.app.fx.Navigator;
import com.koreanair.reservation.domain.flight.FlightSchedule;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public final class SearchController {

    @FXML private ImageView heroImage;
    @FXML private HBox tripTabs;
    @FXML private HBox simpleFields;
    @FXML private TextField fromField;
    @FXML private TextField toField;
    @FXML private DatePicker datePicker;     // 가는 날
    @FXML private VBox returnDateBox;
    @FXML private DatePicker returnDate;      // 오는 날 (왕복)
    @FXML private ComboBox<String> passengersCombo;
    @FXML private VBox multiBox;
    @FXML private VBox legsBox;
    @FXML private Button proceedBtn;
    @FXML private Label resultsTitle;
    @FXML private VBox results;
    @FXML private Label message;

    private Navigator nav;
    private AppContext ctx;

    private enum Mode { ONE_WAY, ROUND_TRIP, MULTI_CITY }
    private Mode mode = Mode.ONE_WAY;
    private FlightSchedule pendingOutbound;          // 왕복: 선택된 가는 편
    private FlightSchedule[] multiSelected;          // 다구간: 구간별 선택
    private final List<LegRow> legRows = new ArrayList<>();

    private static final DateTimeFormatter T = DateTimeFormatter.ofPattern("MM/dd HH:mm");

    /** 다구간 한 구간의 입력 행. */
    private static final class LegRow {
        final TextField from = new TextField();
        final TextField to = new TextField();
        final DatePicker date = new DatePicker();
    }

    public void bind(Navigator nav, AppContext ctx) {
        this.nav = nav;
        this.ctx = ctx;
        if (datePicker.getValue() == null) datePicker.setValue(LocalDate.now().plusDays(1));
        if (returnDate.getValue() == null) returnDate.setValue(LocalDate.now().plusDays(5));
        if (passengersCombo.getItems().isEmpty()) {
            passengersCombo.getItems().setAll(
                    "성인 1명, 일반석", "성인 2명, 일반석", "성인 1명, 비즈니스", "성인 2명, 비즈니스");
            passengersCombo.getSelectionModel().selectFirst();
        }
        if (heroImage != null && heroImage.getParent() instanceof Region hero) {
            heroImage.fitWidthProperty().bind(hero.widthProperty());
        }
        ensureDefaultLegs();
        applyMode();
    }

    // ---------- 모드 ----------

    private void ensureDefaultLegs() {
        if (!legRows.isEmpty()) return;
        // 실제 존재하는 노선으로 기본 3구간: 서울→도쿄→뉴욕→서울
        legRows.add(makeLeg("ICN", "NRT", 1));
        legRows.add(makeLeg("NRT", "JFK", 3));
        legRows.add(makeLeg("JFK", "ICN", 6));
    }

    private LegRow makeLeg(String f, String t, int plusDays) {
        LegRow lr = new LegRow();
        lr.from.setText(f);
        lr.to.setText(t);
        lr.date.setValue(LocalDate.now().plusDays(plusDays));
        return lr;
    }

    @FXML
    private void onTripTab(MouseEvent e) {
        Object m = ((Label) e.getSource()).getUserData();
        setMode("round".equals(m) ? Mode.ROUND_TRIP : "multi".equals(m) ? Mode.MULTI_CITY : Mode.ONE_WAY);
    }

    private void setMode(Mode m) {
        this.mode = m;
        // 탭 활성 표시
        for (Node n : tripTabs.getChildren()) {
            n.getStyleClass().remove("trip-tab-active");
            Object ud = n.getUserData();
            boolean active = (m == Mode.ONE_WAY && "oneway".equals(ud))
                    || (m == Mode.ROUND_TRIP && "round".equals(ud))
                    || (m == Mode.MULTI_CITY && "multi".equals(ud));
            if (active) n.getStyleClass().add("trip-tab-active");
        }
        applyMode();
    }

    private void applyMode() {
        boolean multi = mode == Mode.MULTI_CITY;
        boolean round = mode == Mode.ROUND_TRIP;
        toggle(simpleFields, !multi);
        toggle(returnDateBox, round);
        toggle(multiBox, multi);
        toggle(proceedBtn, false);
        pendingOutbound = null;
        multiSelected = null;
        results.getChildren().clear();
        if (multi) {
            buildLegsBox();
            resultsTitle.setText("구간별 항공편");
            message.setText("구간을 입력하고 ‘항공권 검색’을 누르세요.");
        } else if (round) {
            resultsTitle.setText("가는 편 선택");
            message.setText("가는 편을 검색하세요.");
        } else {
            resultsTitle.setText("검색된 항공편");
            message.setText("");
            renderCards(ctx.search.getCatalog(), nav::showPassenger);
        }
    }

    private static void toggle(Node n, boolean on) {
        n.setVisible(on);
        n.setManaged(on);
    }

    // ---------- 검색 ----------

    @FXML
    private void onSearch() {
        try {
            switch (mode) {
                case ONE_WAY -> searchOneWay();
                case ROUND_TRIP -> searchRoundOutbound();
                case MULTI_CITY -> searchMulti();
            }
        } catch (Exception ex) {
            message.setText("검색 오류: " + ex.getMessage());
        }
    }

    private void searchOneWay() {
        String from = safe(fromField.getText()), to = safe(toField.getText());
        List<FlightSchedule> list = datedOrRoute(from, to, datePicker.getValue());
        resultsTitle.setText("검색된 항공편");
        renderCards(list, nav::showPassenger);
        message.setText(list.isEmpty() ? from + " → " + to + " 직항편이 없습니다." : from + " → " + to + " · " + list.size() + "편");
    }

    private void searchRoundOutbound() {
        pendingOutbound = null;
        String from = safe(fromField.getText()), to = safe(toField.getText());
        List<FlightSchedule> out = datedOrRoute(from, to, datePicker.getValue());
        resultsTitle.setText("가는 편 선택  (" + from + " → " + to + ")");
        renderCards(out, this::pickOutbound);
        message.setText(out.isEmpty() ? "가는 편 직항이 없습니다." : "가는 편을 선택하면 오는 편을 보여드립니다.");
    }

    /** 왕복 — 가는 편 선택 후 오는 편(도착→출발) 목록 표시. */
    private void pickOutbound(FlightSchedule out) {
        pendingOutbound = out;
        String from = safe(fromField.getText()), to = safe(toField.getText());
        List<FlightSchedule> inbound = datedOrRoute(to, from, returnDate.getValue());
        resultsTitle.setText("오는 편 선택  (가는 편: " + code(out) + ")");
        renderCards(inbound, in -> nav.showPassengerRoundTrip(pendingOutbound, in));
        message.setText(inbound.isEmpty() ? "오는 편 직항이 없습니다." : "오는 편을 선택하면 예약을 진행합니다.");
    }

    private void searchMulti() {
        results.getChildren().clear();
        int n = legRows.size();
        multiSelected = new FlightSchedule[n];
        resultsTitle.setText("구간별 항공편");
        boolean any = false;
        for (int i = 0; i < n; i++) {
            LegRow lr = legRows.get(i);
            String f = safe(lr.from.getText()), t = safe(lr.to.getText());
            List<FlightSchedule> legRes = datedOrRoute(f, t, lr.date.getValue());
            Label header = new Label("구간 " + (i + 1) + " · " + f + " → " + t);
            header.getStyleClass().add("kv-val");
            results.getChildren().add(header);
            if (legRes.isEmpty()) {
                Label none = new Label("직항편 없음 — 도시 코드를 확인하세요.");
                none.getStyleClass().add("flight-meta");
                results.getChildren().add(none);
                multiSelected[i] = null;
                continue;
            }
            any = true;
            final int leg = i;
            List<HBox> cards = new ArrayList<>();
            for (FlightSchedule s : legRes) {
                HBox card = flightRow(s);
                cards.add(card);
                card.setOnMouseClicked(e -> {
                    cards.forEach(c -> c.getStyleClass().remove("flight-row-selected"));
                    card.getStyleClass().add("flight-row-selected");
                    multiSelected[leg] = s;
                    refreshProceed();
                });
                results.getChildren().add(card);
            }
            cards.get(0).getStyleClass().add("flight-row-selected");  // 첫 편 기본 선택
            multiSelected[i] = legRes.get(0);
        }
        message.setText(any ? "각 구간의 항공편을 선택한 뒤 ‘다음 단계’로 진행하세요."
                            : "구간 직항편이 없습니다. 도시 코드를 확인하세요.");
        refreshProceed();
    }

    private void refreshProceed() {
        boolean show = mode == Mode.MULTI_CITY && multiSelected != null && multiSelected.length > 0;
        boolean all = show;
        if (show) for (FlightSchedule s : multiSelected) if (s == null) { all = false; break; }
        toggle(proceedBtn, show);
        proceedBtn.setDisable(!all);
    }

    @FXML
    private void onProceed() {
        if (mode != Mode.MULTI_CITY || multiSelected == null) return;
        List<FlightSchedule> segs = new ArrayList<>();
        for (FlightSchedule s : multiSelected) {
            if (s == null) { message.setText("모든 구간의 항공편을 선택하세요."); return; }
            segs.add(s);
        }
        if (segs.size() < 2) { message.setText("다구간은 2개 이상 구간이 필요합니다."); return; }
        nav.showPassengerMultiCity(segs);
    }

    // ---------- 다구간 구간 행 ----------

    @FXML
    private void onAddLeg() {
        if (legRows.size() >= 5) { message.setText("구간은 최대 5개까지 추가할 수 있습니다."); return; }
        LegRow prev = legRows.get(legRows.size() - 1);
        legRows.add(makeLeg(safe(prev.to.getText()), "ICN", legRows.size() * 2 + 1));
        buildLegsBox();
    }

    private void buildLegsBox() {
        legsBox.getChildren().clear();
        for (int i = 0; i < legRows.size(); i++) {
            LegRow lr = legRows.get(i);
            lr.from.setMaxWidth(Double.MAX_VALUE);
            lr.to.setMaxWidth(Double.MAX_VALUE);
            lr.date.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(lr.from, Priority.ALWAYS);
            HBox.setHgrow(lr.to, Priority.ALWAYS);
            HBox.setHgrow(lr.date, Priority.ALWAYS);
            Label idx = new Label("구간 " + (i + 1));
            idx.getStyleClass().add("search-field-label");
            idx.setMinWidth(48);
            Label arrow = new Label("→");
            HBox row = new HBox(8, idx, lr.from, arrow, lr.to, lr.date);
            row.setAlignment(Pos.CENTER_LEFT);
            if (legRows.size() > 2) {
                Button rm = new Button("✕");
                rm.getStyleClass().add("btn-ghost");
                final int li = i;
                rm.setOnAction(e -> { legRows.remove(li); buildLegsBox(); });
                row.getChildren().add(rm);
            }
            legsBox.getChildren().add(row);
        }
    }

    // ---------- 인기 여행지 ----------

    @FXML
    private void onDestClick(MouseEvent e) {
        Object ud = ((Node) e.getSource()).getUserData();
        if (!(ud instanceof String s) || !s.contains(">")) return;
        String[] od = s.split(">", 2);
        setMode(Mode.ONE_WAY);
        fromField.setText(od[0]);
        toField.setText(od[1]);
        List<FlightSchedule> route = routeOnly(od[0], od[1]);
        resultsTitle.setText("검색된 항공편");
        renderCards(route, nav::showPassenger);
        message.setText(route.isEmpty()
                ? od[0] + " → " + od[1] + " 직항 준비 중입니다."
                : od[0] + " → " + od[1] + " 직항편 " + route.size() + "편");
    }

    @FXML
    private void onSwap() {
        String f = fromField.getText();
        fromField.setText(toField.getText());
        toField.setText(f);
    }

    // ---------- 검색 헬퍼 ----------

    /** 날짜 검색 → 결과 없으면 노선 전체(날짜 무관)로 폴백. */
    private List<FlightSchedule> datedOrRoute(String from, String to, LocalDate date) {
        List<FlightSchedule> dated = ctx.booking.processSearch(from, to, date);
        return dated.isEmpty() ? routeOnly(from, to) : dated;
    }

    private List<FlightSchedule> routeOnly(String from, String to) {
        List<FlightSchedule> out = new ArrayList<>();
        if (from.isEmpty() || to.isEmpty()) return out;
        for (FlightSchedule s : ctx.search.getCatalog()) {
            String o = s.getFlight().getRoute().getOrigin().getCode();
            String d = s.getFlight().getRoute().getDestination().getCode();
            if (o.equalsIgnoreCase(from) && d.equalsIgnoreCase(to)) out.add(s);
        }
        return out;
    }

    // ---------- 렌더링 ----------

    private void renderCards(List<FlightSchedule> list, Consumer<FlightSchedule> onClick) {
        results.getChildren().clear();
        for (FlightSchedule s : list) {
            HBox card = flightRow(s);
            card.setOnMouseClicked(e -> onClick.accept(s));
            results.getChildren().add(card);
        }
    }

    private HBox flightRow(FlightSchedule s) {
        String origin = s.getFlight().getRoute().getOrigin().getCode();
        String dest = s.getFlight().getRoute().getDestination().getCode();
        VBox left = new VBox(2);
        Label route = new Label(origin + "  →  " + dest);
        route.getStyleClass().add("flight-route");
        Label meta = new Label(s.getFlightNumber() + " · " + s.getDepartureDateTime().format(T)
                + " 출발 · " + s.getAircraftType());
        meta.getStyleClass().add("flight-meta");
        left.getChildren().addAll(route, meta);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label fare = new Label("₩320,000~");
        fare.getStyleClass().add("flight-fare");
        HBox r = new HBox(14, left, spacer, fare);
        r.setAlignment(Pos.CENTER_LEFT);
        r.getStyleClass().add("flight-row");
        return r;
    }

    private static String safe(String s) { return s == null ? "" : s.trim(); }

    private static String code(FlightSchedule s) {
        return s.getFlight().getRoute().getOrigin().getCode()
                + "→" + s.getFlight().getRoute().getDestination().getCode();
    }
}
