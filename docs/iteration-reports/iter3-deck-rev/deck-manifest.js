// OODP Iter3 Revision Deck — shared manifest.
// index.html(청중) · presenter.html(발표자) · export-pdf.mjs · renumber.mjs 가 함께 소비.
// notes = 한국어 구두 발표 대본 (슬라이드/코드/다이어그램은 영어, 말은 한국어).
window.DECK_VER = 1;
window.DECK_WIDTH = 1920;
window.DECK_HEIGHT = 1080;
window.DECK_MANIFEST = [
  { file: "slides/01-title.html", label: "Cover",
    notes: "안녕하세요. A팀 대한항공 예약 시스템, Iteration 3 발표입니다.\n\n이번 반복의 핵심 패턴은 Observer입니다. 좌석 hold 만료, 결제 실패, 항공편 변경 — 서로 달라 보이는 세 사건이 사실 같은 구조를 가집니다. 발행자가 자기 상태 변화를 모르는 다른 객체들에게 통지한다. 그래서 Observer를 한 번 도입하면 세 곳에서 재사용됩니다.\n\n오늘은 다이어그램과 실제 코드를 나란히 보여드리면서, 왜 이렇게 설계했는지 방어 가능한 수준으로 설명하겠습니다." },

  { file: "slides/02-recap-iter2.html", label: "Iter2 요약 · 로드맵",
    notes: "먼저 지난 반복을 한 장으로 정리합니다.\n\nIteration 1은 State 패턴으로 walking skeleton을 만들었습니다. 예약의 생애주기 전이를 상태 객체로 분리했죠. Iteration 2는 그 위에 Strategy 패턴을 올려, 환불 정책 family — Full, Partial, No refund — 를 알고리즘으로 캡슐화했습니다.\n\n그래서 iter2까지는 happy path와 취소·환불 한 갈래가 끝까지 동작했습니다. 다만 부수효과 호출이 호출자 안에 박혀 있었습니다. 그 지점이 iter3가 푸는 문제입니다." },

  { file: "slides/03-scope.html", label: "Iter3 범위 · 변경분",
    notes: "Iteration 3 범위입니다. 표 오른쪽 빨간 칸이 iter2 대비 새로 채운 부분입니다. 교수님 피드백대로 변경분을 빨강으로 표기했습니다.\n\n부수효과 처리를 호출자 직접 호출에서 publisher-listener 분리로 바꿨고, 좌석 hold 만료 자동 해제, 결제 실패 자동 취소, 항공편 변경 전파를 활성화했습니다. 함께 Itinerary 환승과 multi-city, 마일리지 결제도 채워 walking skeleton의 마지막 빈 칸을 메웠습니다.\n\n총 수정 6개, 신규 13개, 약 19개 파일이 바뀌었습니다." },

  { file: "slides/div1-observer.html", label: "PART 1 · Observer",
    notes: "1부. 왜 Observer 패턴인가." },

  { file: "slides/04-motivation.html", label: "도입 동기 — 호출자 비대",
    notes: "동기부터 보겠습니다. iter2까지 호출자는 자기 책임이 아닌 후속 처리를 직접 호출했습니다. 화면 코드에서 결제 실패 시 BookingController가 직접 handlePaymentFailure를 부르죠.\n\n그런데 iter3에서 같은 자동 취소가 필요한 발생원이 셋으로 늘어납니다. 좌석 hold 만료, 결제 실패, 항공편 취소. 세 곳 모두 호출자에서 직접 부르면 호출자가 비대해지고, 같은 코드가 복제되고, 새 발생원이 생길 때마다 호출자를 고쳐야 합니다. OCP 위반이죠." },

  { file: "slides/05-observer-3d.html", label: "Observer broadcast (live)",
    notes: "해법은 각 발생원을 Subject로 격상하고 부수효과를 Listener로 분리하는 것입니다. 직접 보여드리겠습니다.\n\n왼쪽이 Subject, 오른쪽 네 개가 항상 구독 중인 Listener입니다. 위쪽 버튼으로 발행할 이벤트를 고르고 publish를 누르면, 이벤트가 모든 listener에게 broadcast됩니다. 핵심은 여기입니다 — 모두가 통지를 받지만, 자기 타입인 listener만 instanceof 검사를 통과해 실제로 처리하고, 나머지 셋은 조용히 무시합니다.\n\n이게 1대 N 통지이면서 Subject가 listener 구현 클래스를 전혀 모른다는 Observer의 두 성질입니다." },

  { file: "slides/06-why-observer.html", label: "왜 Observer인가",
    notes: "패턴 선택 근거입니다. 후보를 비교했습니다.\n\nMediator는 Subject가 셋뿐이라 중재자가 과잉입니다. Chain of Responsibility는 한 핸들러만 처리하는데, 우리는 N개 listener가 동시에 받아야 하니 맞지 않습니다. Command는 실행 단위 객체화가 목적이라 통지 목적과 다릅니다. 메시지 브로커 기반 Pub-Sub은 클래스 단위가 아니라 인프라 단위여서 학습 프로젝트 범위 밖입니다.\n\n그래서 1대 N 통지 + 느슨한 결합인 Observer를 선택했습니다." },

  { file: "slides/07-observer-code.html", label: "Observer 코드 (구조↔코드)",
    notes: "실제 코드입니다. 디펜스를 위해 구조도와 실제 소스를 토글로 같이 봅니다.\n\nEventPublisher가 Subject 베이스로 subscribe와 publish를 공통 처리합니다. publish는 등록된 listener를 순회하며 onEvent를 호출하는데, 한 listener가 예외를 던져도 try-catch로 다른 listener 통지가 끊기지 않게 했습니다. EventListener는 onEvent 하나짜리 인터페이스고, DomainEvent는 occurredAt과 sourceId를 가진 추상 베이스입니다.\n\nReservationAutoCancelListener를 보면 onEvent 첫 줄에서 instanceof PaymentFailedEvent로 가른 뒤, 자기 이벤트일 때만 PNR로 예약을 찾아 handlePaymentFailure를 호출합니다." },

  { file: "slides/07b-listeners.html", label: "Listener 구현 3종",
    notes: "Observer 처리부를 어떻게 구현했는지 실제 onEvent 본문 세 개를 나란히 보겠습니다.\n\n세 listener 모두 패턴이 같습니다. 첫 줄에서 instanceof로 자기 이벤트인지 가르고, 아니면 즉시 return. 그 다음 이벤트에서 payload를 꺼내고, 도메인 메서드를 호출합니다.\n\nHoldListener는 좌석을 release하고 예약을 취소합니다. AutoCancelListener는 PNR로 예약을 찾아 handlePaymentFailure를 부릅니다. AffectedListener는 영향받는 예약 리스트를 순회하며 1대 N으로 전파합니다. 같은 인터페이스, 각자 다른 부수효과 — 이게 Observer 구현의 핵심입니다." },

  { file: "slides/div2-consistency.html", label: "PART 2 · 정합성",
    notes: "2부. UC, Class, State, Sequence 네 다이어그램의 정합성." },

  { file: "slides/08-usecase.html", label: "Use Case Diagram",
    notes: "Use Case 다이어그램입니다. 빨간 노드가 iter3 신규 유스케이스입니다.\n\nSearch Connecting Flights와 Book Multi-city는 Book Flight를 include합니다 — 환승이든 다구간이든 결국 항공편 예약을 포함하니까요. Pay with Mileage와 Auto-cancel on Hold Expiry는 Make Payment를 extend합니다. 기본 결제 흐름의 선택적 확장이라는 뜻입니다. Notify Flight Schedule Change는 Manage Flight Schedule을 extend합니다.\n\ninclude와 extend를 구분해 그린 게 핵심입니다." },

  { file: "slides/09-class-ecb.html", label: "Class Diagram · ECB",
    notes: "클래스 다이어그램입니다. ECB로 색을 나눴습니다 — 파랑 Entity, 주황 Control, 황토 Boundary. 위 버튼으로 한 계층만 강조해 볼 수 있습니다.\n\nEntity는 Reservation, Seat, Itinerary, FlightSchedule, 그리고 DomainEvent family입니다. Control은 BookingController와 세 Subject, 네 Listener, 그리고 Registry와 검색 서비스죠. Boundary는 외부 Skypass mock입니다.\n\n주목할 점은 FlightSchedule이 Entity이면서 EventPublisher를 상속해 Subject 역할도 한다는 겁니다. 도메인 객체가 직접 발행자가 되는 사례입니다." },

  { file: "slides/10-state.html", label: "State Diagram",
    notes: "State 다이어그램입니다. 중요한 설계 판단 하나 — Reservation 상태는 iter3에서 추가하지 않았습니다.\n\n대신 Seat과 FlightSchedule의 전이에 이벤트 발행이라는 부수효과가 붙습니다. Seat의 Held에서 Available 전이는 SeatHoldMonitor의 sweep이 트리거하고 listener가 수행합니다. FlightSchedule의 모든 changeStatus 호출은 FlightStatusChangedEvent 발행을 동반합니다.\n\n즉 iter3는 새 상태를 만든 게 아니라, 기존 상태 전이에 통지를 얹은 반복입니다." },

  { file: "slides/11-consistency-map.html", label: "정합성 매트릭스",
    notes: "네 다이어그램이 어떻게 맞물리는지 한 장으로 보겠습니다. 교수님이 가장 중요하게 보시는 부분입니다.\n\nUse Case의 Auto-cancel은 Sequence의 PaymentFailed 흐름이 되고, 그 흐름은 Class의 ReservationAutoCancelListener가 담당하며, 결국 State의 PendingPayment에서 Cancelled 전이를 부릅니다. 하나의 요구사항이 네 다이어그램을 관통해 같은 코드로 떨어집니다.\n\nState에서 Class로, Sequence에서 Class로, Sequence에서 State로 — 세 축의 정합성을 모두 점검했습니다." },

  { file: "slides/div3-demo.html", label: "PART 3 · Sequence · 데모",
    notes: "3부. Sequence 흐름과 라이브 데모." },

  { file: "slides/12-seq-payfail.html", label: "Seq — 결제 실패 (다이어그램↔코드)",
    notes: "결제 실패 자동 취소 시퀀스입니다. 토글로 다이어그램과 실제 코드를 비교합니다.\n\nBookingController가 processPaymentCharge를 부르고, gateway가 거절하면 PaymentProcessor가 payment.fail 후 PaymentFailedEvent를 publish합니다. 그러면 publisher가 직접 호출하지 않아도 AutoCancelListener의 onEvent가 호출되고, PNR로 예약을 찾아 handlePaymentFailure로 PendingPayment에서 Cancelled로 전이시킵니다.\n\n시퀀스의 self-call publish 화살표가 코드의 publish 한 줄과 정확히 대응합니다." },

  { file: "slides/13-seq-connecting.html", label: "Seq — 환승 MCT (다이어그램↔코드)",
    notes: "환승 검색 시퀀스와 MCT 검증 코드입니다.\n\nItinerarySearchService가 카탈로그를 순회하며, 앞 구간 도착지가 뒤 구간 출발지와 같고 최종 목적지가 맞는 쌍을 찾습니다. 각 쌍을 Itinerary.connecting으로 묶고 isConnectionTimeValid로 검증합니다.\n\n코드를 보면 layover를 앞 구간 도착시각과 뒤 구간 출발시각의 차이로 계산하고, 최소 환승 시간 — 국내 60분, 국제 90분 — 보다 짧으면 false를 반환합니다. 데모에서 120분 환승은 통과, 20분 환승은 탈락하는 게 이 한 줄 때문입니다." },

  { file: "slides/14-demo-console.html", label: "라이브 데모 콘솔",
    notes: "실제 데모입니다. 이 출력은 목업이 아니라 Iter3DemoRunner를 실행한 실제 콘솔 캡처입니다. 같은 명령을 돌리면 같은 출력이 나옵니다.\n\n탭으로 시나리오를 고르세요. 좌석 hold 만료에서는 sweep이 한 건 발화해 좌석이 Available, 예약이 Cancelled가 됩니다. 결제 실패에서는 gateway 거절 후 자동 취소. 항공편 전파에서는 한 번의 관리자 명령이 두 예약에 통지됩니다. 버스 연계에서는 e-Ticket 발급이 부산행 우등고속 버스티켓을 자동 발매합니다.\n\n모두 listener가 처리하는 부수효과입니다." },

  { file: "slides/div4-defense.html", label: "PART 4 · 디펜스",
    notes: "4부. 패턴 협업, 예상 질문, 한계와 다음 반복." },

  { file: "slides/15-collab.html", label: "세 패턴 협업",
    notes: "세 패턴이 어떻게 협업하는지입니다. 서로 다른 축을 담당하기에 단일 책임이 유지됩니다.\n\nState는 예약이 어떤 전이를 할 수 있는가 — 상태 축. Strategy는 환불 금액을 어떻게 계산하는가 — 알고리즘 축. Observer는 상태 변화의 부수효과를 누구에게 통지하는가 — 통지 축입니다.\n\n실제로 iter3 listener가 부르는 handlePaymentFailure는 iter1 State 전이이고, 그 시점에 iter2 Strategy인 RefundPolicy가 잠재적으로 함께 동작합니다. 세 반복이 누적된 하나의 시스템이라는 증거입니다." },

  { file: "slides/16-defense-map.html", label: "예상 질문 ↔ 근거 위치",
    notes: "디펜스 맵입니다. 나올 만한 질문과, 그 답을 보여줄 코드·다이어그램 위치를 미리 짝지었습니다.\n\n왜 동기 broadcast냐 — EventPublisher.publish의 for 루프를 보여드립니다. listener 하나가 죽으면 어떻게 되냐 — 같은 메서드의 try-catch입니다. 왜 push 모델이냐 — DomainEvent 서브클래스가 payload를 들고 있어 listener가 subject를 재조회할 필요가 없습니다. 새 발생원을 추가하려면 — Subject는 EventPublisher 상속, listener는 subscribe 한 줄. 호출자는 손대지 않습니다.\n\n질문이 들어오면 해당 슬라이드로 바로 점프하겠습니다." },

  { file: "slides/17-limits-iter4.html", label: "한계 · Iteration 4",
    notes: "의도적으로 남긴 한계와 다음 반복입니다.\n\nsweep은 수동 호출이고, broadcast는 동기이며, Skypass는 in-memory mock이고, MCT는 동일 공항 환승만 봅니다. 모두 학습 프로젝트 범위를 위해 의식적으로 그은 선입니다.\n\nIteration 4에서는 AppConfig Singleton과 ItineraryFactory의 Factory Method를 도입하고, 관리자 예외 환불 검토 경로, e-Ticket PDF, 마일리지 적립, 그리고 iter3 Observer 위에 올린 실시간 예약 추적을 채울 계획입니다." },

  { file: "slides/18-thanks.html", label: "감사합니다",
    notes: "여기까지입니다. State, Strategy, Observer 세 패턴이 각자 다른 축을 담당하며 누적된 하나의 시스템을 만들었습니다.\n\n질문 주시면 해당 코드와 다이어그램으로 바로 보여드리겠습니다. 감사합니다." },
];
