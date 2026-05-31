package com.koreanair.reservation.app.fx.screen;

import java.util.List;

import com.koreanair.reservation.app.fx.AppContext;
import com.koreanair.reservation.app.fx.Navigator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * 발표 중 바로 열어 설명하는 DP 코드 가이드.
 *
 * <p>교과서 클래스다이어그램의 추상 역할을 우리 코드의 실제 클래스, 메서드,
 * attribute, 호출 흐름에 대응시킨다. 앱 시연 화면에서 "이 버튼을 누르면 어떤
 * 패턴의 어떤 메서드가 어떤 순서로 호출되는가"를 설명하기 위한 Boundary 화면이다.
 */
public final class PatternGuideController {

    @FXML private VBox patternList;

    @SuppressWarnings("unused")
    private Navigator nav;
    @SuppressWarnings("unused")
    private AppContext ctx;

    public void bind(Navigator nav, AppContext ctx) {
        this.nav = nav;
        this.ctx = ctx;
        render();
    }

    private void render() {
        if (patternList.getChildren().size() > 2) {
            patternList.getChildren().remove(2, patternList.getChildren().size());
        }
        for (PatternNote note : notes()) {
            patternList.getChildren().add(card(note));
        }
    }

    private VBox card(PatternNote n) {
        VBox box = new VBox(10);
        box.getStyleClass().add("pattern-card");

        Label title = new Label(n.title());
        title.getStyleClass().add("pattern-title");

        Label intent = new Label(n.intent());
        intent.getStyleClass().add("screen-sub");
        intent.setWrapText(true);

        HBox grid = new HBox(12);
        grid.getChildren().addAll(kv("GoF 역할", n.gof()),
                kv("우리 코드", n.team()),
                kv("핵심 흐름", n.flow()));

        TextArea code = new TextArea(n.skeleton());
        code.setEditable(false);
        code.setWrapText(false);
        code.getStyleClass().add("pattern-code");
        code.setPrefRowCount(Math.max(5, Math.min(11, n.skeleton().split("\n").length + 1)));

        Label demo = new Label("시연 포인트: " + n.demo());
        demo.getStyleClass().add("pattern-demo");
        demo.setWrapText(true);

        box.getChildren().addAll(title, intent, grid, code, demo);
        return box;
    }

    private VBox kv(String key, String value) {
        VBox box = new VBox(4);
        box.getStyleClass().add("pattern-kv");
        Label k = new Label(key);
        k.getStyleClass().add("kv-key");
        Label v = new Label(value);
        v.getStyleClass().add("kv-val");
        v.setWrapText(true);
        box.getChildren().addAll(k, v);
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }

    private record PatternNote(String title,
                               String intent,
                               String gof,
                               String team,
                               String flow,
                               String skeleton,
                               String demo) {}

    private List<PatternNote> notes() {
        return List.of(
                new PatternNote(
                        "DP#1 State - Reservation lifecycle",
                        "예약 객체가 상태명을 enum으로 검사하지 않고, 현재 State 객체에게 가능한 전이를 위임한다.",
                        "Context = Reservation, State = ReservationState, Default = AbstractReservationState, ConcreteState = 8개 상태",
                        "Reservation.currentState, setState(), requestPayment(), issueTicket(), AbstractReservationState 기본 거부",
                        "UI 버튼 -> Reservation 메서드 -> 현재 State가 검증 -> setState(next)",
                        """
                        class Reservation {
                            private ReservationState currentState;
                            void processPayment() {
                                currentState.processPayment(this);
                            }
                            void setState(ReservationState next) {
                                currentState = next;
                            }
                        }
                        abstract class AbstractReservationState implements ReservationState {
                            void processPayment(Reservation r) {
                                throw new InvalidStateTransitionException(...);
                            }
                        }
                        class PendingPaymentState extends AbstractReservationState {
                            void processPayment(Reservation r) {
                                r.setState(new ConfirmedState());
                            }
                        }
                        """,
                        "헤더 STATE 배지를 보며 검색 -> 승객정보 -> 결제 -> 발권 단계에서 상태가 어떻게 바뀌는지 설명한다."),
                new PatternNote(
                        "DP#2 Strategy - RefundPolicy family",
                        "환불 계산 if/else를 RefundHandler 밖으로 빼고, 운임 규칙이 적절한 정책 객체를 고르게 한다.",
                        "Context = RefundHandler, Strategy = RefundPolicy, ConcreteStrategy = Full/Partial/No",
                        "FareRule.checkRefundPolicy(), RefundHandler.previewRefund(), RefundRequest",
                        "취소 화면 -> fareClass 확인 -> FareRule이 policy 선택 -> policy.calculateRefundAmount(paid)",
                        """
                        interface RefundPolicy {
                            BigDecimal calculateRefundAmount(BigDecimal paid);
                        }
                        class FareRule {
                            RefundPolicy checkRefundPolicy() {
                                if (!refundable) return new NoRefundPolicy();
                                if (fareClass.equals(\"Y\")) return new FullRefundPolicy();
                                return new PartialRefundPolicy();
                            }
                        }
                        class RefundHandler {
                            BigDecimal previewRefund(String pnr, String fareClass) {
                                RefundPolicy p = fareRule.checkRefundPolicy();
                                return p.calculateRefundAmount(paid);
                            }
                        }
                        """,
                        "취소 화면에서 미리보기를 누르고 Full/Partial/No 정책명이 바뀌는 구조를 보여준다."),
                new PatternNote(
                        "DP#3 Observer - Domain event broadcast",
                        "발권, 결제 실패, 좌석 홀드 만료 같은 부수효과를 호출자에게 직접 묶지 않고 이벤트로 전파한다.",
                        "Subject = EventPublisher, Observer = EventListener, ConcreteEvent = DomainEvent",
                        "TicketPurchasePublisher, BusTicketPurchaseListener, TicketIssuedEvent",
                        "e-Ticket 발권 -> publish(event) -> listeners 순회 -> bus service 실행",
                        """
                        class TicketPurchasePublisher extends EventPublisher {
                            void publishTicketIssued(Reservation r, Ticket t, BusCity city) {
                                publish(new TicketIssuedEvent(r, t, city));
                            }
                        }
                        class EventPublisher {
                            void publish(DomainEvent e) {
                                for (EventListener l : listeners) l.onEvent(e);
                            }
                        }
                        class BusTicketPurchaseListener implements EventListener {
                            void onEvent(DomainEvent e) {
                                if (e instanceof TicketIssuedEvent t)
                                    busTicketingService.issuePremiumTicket(...);
                            }
                        }
                        """,
                        "확인 화면에서 e-Ticket/셔틀 연계를 실행하고 콘솔의 BUS 로그와 화면의 버스티켓 상태를 같이 보여준다."),
                new PatternNote(
                        "DP#4 Composite - Airport city search",
                        "도시 코드와 공항 코드를 같은 타입으로 다루어 SEL, TYO, NYC 검색을 공항 목록 검색으로 확장한다.",
                        "Component = AirportLocation, Composite = AirportCity, Leaf = Airport",
                        "AirportLocation.getAirports(), AirportCity.members, AirportCatalog.resolve()",
                        "검색어 -> AirportCatalog.resolve(code) -> getAirports() -> 모든 공항 조합 검색",
                        """
                        interface AirportLocation {
                            String getCode();
                            List<Airport> getAirports();
                        }
                        class Airport implements AirportLocation {
                            List<Airport> getAirports() { return List.of(this); }
                        }
                        class AirportCity implements AirportLocation {
                            private final List<Airport> airports;
                            List<Airport> getAirports() { return airports; }
                        }
                        // SearchController.expand(\"TYO\") => NRT, HND
                        """,
                        "검색창에 TYO, NYC, SEL 같은 도시 코드를 넣고 여러 공항 노선이 함께 잡히는 점을 설명한다."),
                new PatternNote(
                        "DP#5 Singleton - AppConfig",
                        "글꼴 크기, 통화, 테마 설정을 화면마다 따로 들고 있지 않고 앱 전체가 한 객체를 공유한다.",
                        "Singleton = AppConfig, private constructor, getInstance()",
                        "AppConfig.instance, listeners, setFontSize(), setCurrency()",
                        "설정 화면 -> AppConfig.getInstance() -> 값 변경 -> listener가 Scene 재렌더",
                        """
                        public final class AppConfig {
                            private static volatile AppConfig instance;
                            private final List<Consumer<AppConfig>> listeners;
                            private AppConfig() {}
                            public static AppConfig getInstance() {
                                if (instance == null) synchronized (AppConfig.class) {
                                    if (instance == null) instance = new AppConfig();
                                }
                                return instance;
                            }
                            void setFontSize(int size) {
                                this.fontSize = size;
                                notifyListeners();
                            }
                        }
                        """,
                        "설정 화면에서 글꼴 크기를 바꾸고 전체 UI가 같은 설정 객체를 바라본다는 점을 보여준다."),
                new PatternNote(
                        "DP#6 Factory Method - Payment and itinerary creation",
                        "호출자가 구체 결제/여정 클래스를 직접 new 하지 않고, 선택값에 맞는 Product 생성을 팩토리에 맡긴다.",
                        "Creator = PaymentMethodProcessor / ItineraryFactory, Factory Method = createPayment() / createItinerary()",
                        "ConcreteCreator = 결제수단별 Processor + Direct/Connecting/MultiCityFactory, 선택 헬퍼 = PaymentProcessorFactory",
                        "콤보 선택 -> forMethod가 ConcreteCreator 선택 -> processCharge/build 안에서 Factory Method 호출",
                        """
                        abstract class PaymentMethodProcessor {
                            final Payment processCharge(long amount, String pnr) {
                                Payment p = createPayment(amount); // factory method
                                if (authorize(p)) p.pay();
                                else { p.fail(); publish(new PaymentFailedEvent(...)); }
                                return p;
                            }
                            protected abstract Payment createPayment(long amount);
                        }
                        class KakaoPayPaymentProcessor extends PaymentMethodProcessor {
                            protected Payment createPayment(long amount) {
                                return basePayment(amount, PaymentMethod.KAKAO_PAY);
                            }
                        }
                        abstract class ItineraryFactory {
                            final Itinerary build(List<FlightSchedule> fs) {
                                validate(fs);
                                Itinerary it = createItinerary(); // factory method
                                fs.forEach(s -> it.addSegment(new Segment(s)));
                                return it;
                            }
                            protected abstract Itinerary createItinerary();
                        }
                        """,
                        "결제 화면에서 결제 수단을 바꾸거나 검색에서 환승/다구간을 골라 Factory가 ConcreteProduct를 고르는 장면을 설명한다."),
                new PatternNote(
                        "DP#7 Template Method - TicketRenderer",
                        "전자항공권 출력은 header/body/footer 흐름이 같고 표현만 다르므로, 알고리즘 골격을 상위 클래스에 고정한다.",
                        "AbstractClass = TicketRenderer, templateMethod = render(), primitiveOperation = header/body/footer",
                        "PlainTextTicketRenderer, HtmlTicketRenderer, BoardingPassRenderer",
                        "포맷 콤보 -> renderer 선택 -> final render() -> subclass step 호출",
                        """
                        abstract class TicketRenderer {
                            public final String render(Reservation r, Ticket t) {
                                return header(r, t) + body(r, t) + footer(r, t);
                            }
                            protected abstract String header(...);
                            protected abstract String body(...);
                            protected abstract String footer(...);
                        }
                        class BoardingPassRenderer extends TicketRenderer {
                            protected String body(...) { return boardingPassLayout; }
                        }
                        """,
                        "확인 화면에서 일반 텍스트/HTML/보딩패스를 토글해 같은 데이터가 다른 템플릿 step으로 렌더됨을 보여준다."),
                new PatternNote(
                        "DP#8 Adapter - Skypass mileage API",
                        "외부 Skypass API의 Map 기반 응답을 우리 도메인이 기대하는 SkypassInterface 형태로 변환한다.",
                        "Target = SkypassInterface, Adapter = SkypassAdapter, Adaptee = RemoteSkypassApi",
                        "SkypassAdapter.remote, getMileageBalance(), verifyAndDeduct()",
                        "결제 화면 -> Target 호출 -> Adapter가 remote API 호출/변환 -> int/Object 반환",
                        """
                        interface SkypassInterface {
                            int getMileageBalance(String memberNo);
                            Object verifyAndDeduct(String memberNo, int amount);
                        }
                        class SkypassAdapter implements SkypassInterface {
                            private final RemoteSkypassApi remote;
                            int getMileageBalance(String no) {
                                Map<String,Object> res = remote.getMileage(no);
                                return (Integer) res.get(\"balance\");
                            }
                        }
                        """,
                        "회원 로그인 후 결제 화면의 마일리지 잔액 표시와 마일리지 결제 흐름을 Target 인터페이스 기준으로 설명한다."),
                new PatternNote(
                        "DP#9 Decorator - Seat add-on chain",
                        "창측/통로/레그룸/라운지 부가옵션을 조합별 클래스로 만들지 않고 런타임 wrapper 체인으로 쌓는다.",
                        "Component = SeatView, ConcreteComponent = SeatViewAdapter, Decorator = AbstractSeatDecorator",
                        "SeatController.refreshSeatView(), getDescription(), getSurcharge(), metadataLabels",
                        "좌석 선택 -> base SeatView -> 위치 decorator -> 체크박스 decorator -> surcharge 합산",
                        """
                        SeatView view = new SeatViewAdapter(seat);
                        if (col == 'A' || col == 'F')
                            view = new WindowSeatDecorator(view);
                        else if (col == 'C' || col == 'D')
                            view = new AisleSeatDecorator(view);
                        if (legroomCheck.isSelected())
                            view = new ExtraLegroomDecorator(view);
                        if (loungeCheck.isSelected())
                            view = new LoungeAccessDecorator(view);
                        ctx.setSeatSurcharge(view.getSurcharge().longValue());
                        """,
                        "좌석 화면에서 A/F, C/D 좌석과 레그룸/라운지 체크를 바꾸며 설명/부가요금이 누적되는 것을 보여준다.")
        );
    }
}
