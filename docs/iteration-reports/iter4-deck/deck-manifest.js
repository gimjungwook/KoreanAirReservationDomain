// Iteration 4 deck manifest
// PDF 기반 29페이지 슬라이드 + 발표자 노트 동기화
window.DECK_WIDTH = 1920;
window.DECK_HEIGHT = 1080;
window.DECK_VER = "iter4-final-v1";

window.DECK_APP_FLOW = [
  { label: "01 홈 + 허브", idx: 7, note: "발표 인트로, 전체 흐름 오리엔테이션" },
  { label: "02 검색/예약", idx: 2, note: "기능 목록에서 검색/예약의 핵심 연결" },
  { label: "03 좌석/결제 준비", idx: 24, note: "좌석 데코레이터·적립/결제 준비 흐름" },
  { label: "04 결제/확정", idx: 8, note: "State + Strategy + Observer 동작 구간" },
  { label: "05 조회/환불", idx: 3, note: "조회·환불·버스 연계 연계 포인트" },
  { label: "06 패턴 가이드", idx: 4, note: "리팩터링/DP 정리 및 코드맵 마감" }
];

window.DECK_MANIFEST = [
  { file: "iter4-final-deck.pdf", page: 1, label: "01. Cover", notes: "이번 최종 발표는 JavaFX 전환한 대한항공 예약 시스템에서 9개 디자인 패턴 적용을 보여줍니다." },
  { file: "iter4-final-deck.pdf", page: 2, label: "02. 지침서 반영 원칙", notes: "양식 #1·#2·#3, 직전 대비 변경사항 표시, 교과서 비교를 우선 반영했습니다." },
  { file: "iter4-final-deck.pdf", page: 3, label: "03. 양식#1 전체 기능 - 검색/예약", notes: "검색/예약 기능: 도시 코드 검색, 다구간/왕복, 좌석 add-on, 결제 수단 확장을 정리했습니다." },
  { file: "iter4-final-deck.pdf", page: 4, label: "04. 양식#1 전체 기능 - 조회/환불/e-Ticket", notes: "예약 조회·취소·환불·e-Ticket·버스 연계 흐름을 최신 기능으로 통합 제시했습니다." },
  { file: "iter4-final-deck.pdf", page: 5, label: "05. 양식#3 Refactoring/DP 정리", notes: "9개 DP가 반복 사용 가능한 구조로 누적 적용되어 있음을 강조합니다." },
  { file: "iter4-final-deck.pdf", page: 6, label: "06. 직전 버전 대비 변경사항", notes: "JavaFX 마이그레이션, 패턴 가이드 화면, 조회 편의성 개선, 결제/버스 선택 개선이 핵심 변경입니다." },
  { file: "iter4-final-deck.pdf", page: 7, label: "07. 다이어그램 반영 방식", notes: "Use Case·Class·Sequence·State Diagram을 본편 구조에 맞춰 압축 정렬했습니다." },
  { file: "iter4-final-deck.pdf", page: 8, label: "08. 시연 순서(앱형 분할 제안)", notes: "Search → Seat → Payment/확정 → 조회/환불/버스 연계 → 설정/패턴가이드로 데모를 진행합니다. 화면 단위를 허브/검색/좌석준비/결제확정/조회환불/패턴가이드로 분할하세요." },
  { file: "iter4-final-deck.pdf", page: 9, label: "09. DP#1 State 비교", notes: "Context=Reservation, State=ReservationState, 구체 상태가 허용 전이만 override하고 나머지는 default 처리합니다." },
  { file: "iter4-final-deck.pdf", page: 10, label: "10. DP#1 State 코드", notes: "핵심은 processPayment이 현재 상태로 위임되고, 승인 시 다음 상태로 setState 전환되는 흐름입니다." },
  { file: "iter4-final-deck.pdf", page: 11, label: "11. DP#2 Strategy 비교", notes: "Context=RefundHandler, Strategy=RefundPolicy; Full/Partial/No refund 정책이 환불 규칙을 캡슐화합니다." },
  { file: "iter4-final-deck.pdf", page: 12, label: "12. DP#2 Strategy 코드", notes: "핵심은 if/else 대신 calculateRefundAmount(paid)를 policy에 위임하는 구조입니다." },
  { file: "iter4-final-deck.pdf", page: 13, label: "13. DP#3 Observer 비교", notes: "Subject=EventPublisher, Listener=EventListener. 이벤트는 publish 후 notifyObservers로 브로드캐스트됩니다." },
  { file: "iter4-final-deck.pdf", page: 14, label: "14. DP#3 Observer 코드", notes: "TicketPurchasePublisher는 event만 발행하고, BusTicketPurchaseListener가 관심 이벤트만 update로 처리합니다." },
  { file: "iter4-final-deck.pdf", page: 15, label: "15. DP#4 Composite 비교", notes: "공항-도시를 AirportLocation으로 통합하고 Airport는 leaf, AirportCity는 composite로 처리합니다." },
  { file: "iter4-final-deck.pdf", page: 16, label: "16. DP#4 Composite 코드", notes: "getAirports()에서 leaf/composite의 동일한 반환 타입 처리로 검색 분기 코드를 줄입니다." },
  { file: "iter4-final-deck.pdf", page: 17, label: "17. DP#5 Singleton 비교", notes: "AppConfig를 전역 단일 인스턴스로 사용해 테마·통화·마일리지 정책 값의 화면 간 일치성을 유지합니다." },
  { file: "iter4-final-deck.pdf", page: 18, label: "18. DP#5 Singleton 코드", notes: "private ctor + getInstance 중심, 설정 변경 시 listener/바인딩으로 화면 동기화합니다." },
  { file: "iter4-final-deck.pdf", page: 19, label: "19. DP#6 Factory Method 비교", notes: "핵심 정정: PaymentProcessorFactory는 helper이고, 실제 GoF Creator는 PaymentMethodProcessor/ItineraryFactory입니다." },
  { file: "iter4-final-deck.pdf", page: 20, label: "20. DP#6 Factory Method 코드", notes: "processCharge 공통 흐름 안에서 createPayment()가 결제수단별 concrete creator로 대체됩니다." },
  { file: "iter4-final-deck.pdf", page: 21, label: "21. DP#7 Template Method 비교", notes: "TicketRenderer가 header/body/footer 템플릿을 고정하고 종류별 formatter만 교체합니다." },
  { file: "iter4-final-deck.pdf", page: 22, label: "22. DP#7 Template Method 코드", notes: "render() template 실행과 hook/override 동작을 짚어 코드와 화면 동작을 일치시킵니다." },
  { file: "iter4-final-deck.pdf", page: 23, label: "23. DP#8 Adapter 비교", notes: "SkypassAdapter가 외부 API 응답을 내부 UI 친화 형식으로 변환해 연동 경계를 분리합니다." },
  { file: "iter4-final-deck.pdf", page: 24, label: "24. DP#8 Adapter 코드", notes: "remote.getMileage()를 Adapter에서 내부형 balance로 정규화한 뒤 시스템이 소비합니다." },
  { file: "iter4-final-deck.pdf", page: 25, label: "25. DP#9 Decorator 비교", notes: "SeatView를 decorator chain으로 감싸며 선택 옵션별 기능과 surcharge를 누적합니다." },
  { file: "iter4-final-deck.pdf", page: 26, label: "26. DP#9 Decorator 코드", notes: "BaseSeatView 위에 Window/Aisle/ExtraLegroom/Lounge decorator를 조립해 확장합니다." },
  { file: "iter4-final-deck.pdf", page: 27, label: "27. 검증 및 수정 이력", notes: "double refund, fare hardcoding, 조회 흐름, 버스 연계 선택 이슈를 수정했습니다." },
  { file: "iter4-final-deck.pdf", page: 28, label: "28. 의도적 한계", notes: "admin auth, PDF download, 실 HTTP 연동은 후속으로 분리한 범위임을 명확히 합니다." },
  { file: "iter4-final-deck.pdf", page: 29, label: "29. 결론", notes: "if/else가 아니라 DP 역할 분리를 통해 변화 대응력을 높인 구조를 마무리합니다." }
];
