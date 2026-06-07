# Iteration 4 발표 덱 제작 목표 (DECK_GOAL)

## 0. 목적

- `docs/REPORT_GOAL.md` 와 GWS 보고서(Google Docs ID `1Zd53XHVxSU6cINaN2GWbcmjpsSHpXIDW-PRpDFFVeq8`)에 이미 들어 있는 내용을 발표용 슬라이드 덱으로 옮긴다.
- 덱은 보고서의 핵심을 장표 단위로 재배치한 것이다. 새 사실을 창작하지 않고, 보고서에 있는 내용(기능, 디자인 패턴, 다이어그램, 코드 흐름, 부록 A1-A11)과 실제 소스만 사용한다.
- **핵심: 이 덱은 보고서를 그대로 발표용 장표로 옮긴 것이다 — "보고서를 장표화해서 그냥 발표한다"고 생각한다. 보고서에 있는 표(핵심 본문 표 3개 + Team Contribution 표), 코드 블록, 다이어그램, 설명을 빠짐없이 장표화한다. 표/다이어그램/코드는 새로 만들지 않고 보고서와 실제 소스의 것을 그대로 쓰고, 설명만 장표용으로 짧게 줄인다.**
- **표 3개 필수: Feature 표, Iteration Progress 표, Extension Feature 표는 보고서처럼 반드시 장표로 포함한다(표 형태 그대로). Team Contribution 표도 포함하며 Iteration 4 작업은 빨간색으로 표시한다.**
- **코드 필수: 각 디자인 패턴은 실제 소스 코드를 보여주는 코드 장표를 포함한다(아래 4번 코드 규칙 참고).**
- 보고서와 덱은 서로 모순되지 않아야 한다(DP 번호, pull 모델, 클래스/메서드 이름, State 미번호 규칙 등 모두 보고서와 동일).
- **덱을 고치면 보고서(GWS 3탭)도 같이 고친다. 덱과 보고서는 항상 같은 사실을 담아야 한다(아래 6번 동기화 규칙).**

## 1. 스타일 규칙 (최소, 디자인 없음)

- 정교한 디자인, 그래픽 레이아웃, 배경 이미지, 아이콘 세트 같은 것은 만들지 않는다. (단 Team Contribution 표는 카드 형태로 한다 — 2번 3장 참고.)
- 각 장표에는 명확한 타이틀을 둔다. 타이틀 아래 본문은 내용에 맞게 자유롭게 배치한다. 장표마다 배치가 달라도 된다.
- 무조건 다 불릿일 필요는 없다. 내용에 따라 형태를 고른다: 불릿, 짧은 산문 한두 문장, 핵심 문장 한 줄, 2단 비교, 강조 숫자/콜아웃, 이미지 + 캡션, 표, 카드 그리드 등. 단조로운 불릿 도배는 피한다.
- 한 장표가 단조로우면(예: 같은 모양 셀 3개만) 보강한다 — 요약 문장, 구체 예시, 핵심 키워드 밴드 등을 더해 밀도와 가독성을 높인다.
- 서브타이틀은 필요할 때만 넣는다(필수 아님).
- 이모지는 절제해서 쓴다. 장표당 하나 정도 포인트 강조용.
- 한 장표에 내용을 과밀하게 넣지 않는다. 핵심만, 가독성 우선.
- 슬라이드 텍스트는 한국어로 작성한다. 단, 클래스명, 메서드명, 코드 식별자, 디자인 패턴명(예: DP#1 Strategy, EventPublisher, getInstance)은 영어 원어를 그대로 유지한다.
- 보고서에 있는 다이어그램 이미지(Use Case, Sequence, State, DP#1-8 구현 다이어그램, 교과서 사진)는 필요한 장표에 그대로 삽입한다. 다이어그램을 새로 그리지 않고 보고서 것을 재사용한다.
- 색, 폰트, 애니메이션 같은 정교한 서식은 신경 쓰지 않는다. 읽히기만 하면 된다.

## 2. 장표 구성 (현재 덱 = 45장 기준)

각 줄은 한 장표 또는 한 묶음이다. 실제 장표 제목/본문은 한국어, 배치는 1번 규칙대로 자유롭게.

1. Title — Korean Air Reservation System, OODP Iteration 4 (팀원, 날짜 placeholder)
2. Agenda / Overview — 다룰 내용 개요
3. **Team Contribution (카드)** — 보고서 Team Contribution 표를 카드로. 이름은 줄바꿈 금지(nowrap). Iteration 4 작업은 빨강.
4. **Feature 표** — 보고서 Feature 표 그대로(Feature / Sub-feature / Implementation Iteration). Iteration 4 항목 빨강. **`DP#n` 은 `DP#n (PatternName)` 으로 패턴명 병기**(예: `DP#5 (Factory Method)`). 셀 안 줄별 부분 빨강 유지.
5. **Iteration Progress 표** — 보고서 표 그대로. Iteration 4 행 빨강. 열이 많으면 글씨/패딩을 줄여 한 장에 들어오게 한다(열은 빼지 않는다).
6. **Extension Feature ① 이미 적용된 확장** — iteration 1 계획 외로 추가·적용 완료된 확장(Refactoring applied). Iteration 4 추가분 빨강, 이전 iteration(예: 버스티켓 iter3)은 검정.
7. **Extension Feature ② 향후 확장 가능성** — 아직 미구현, 기존 패턴 재사용/추가 리팩토링 필요한 후보(Refactoring required).
8. Behavioral — Use Case (설명) — 액터, iter4 신규 유스케이스 빨강, 다이어그램 삽입.
9. Behavioral — Use Case **집중(순수)** — 다이어그램만 화면 가득(타이틀/헤더/푸터/설명 없음).
10. Behavioral — Sequence (예약/결제/발권, 설명).
11. Behavioral — Sequence **집중(순수)**.
12. Behavioral — Sequence (Adapter & Decorator, 설명, 두 이미지).
13. **Adapter Sequence 집중(순수)** — Adapter 시퀀스만 화면 가득.
14. **Decorator Sequence 집중(순수)** — Decorator 시퀀스만 화면 가득.
15. Behavioral — Reservation State Machine (설명).
16. State **집중(순수)**.
17. State Implementation (교과서 사진 + 구현 다이어그램 비교) — **State = DP#1**(초기 제외 → 재지정).
17-2. **전체 클래스 다이어그램 (full) — 덱·보고서 둘 다 1장 필수**: ECB(Boundary/Control/Domain) 3계층 + 9 GoF 를 담되 **"전체"는 실제 소스의 모든 클래스를 의미한다 — `tools/`(빌드 제외)만 빼고 146개 클래스 전부**를 패키지별로 그린다(큐레이션 27박스는 "전체"가 아니다). `class-full.puml`(소스 파싱 자동 생성) → `dg-class-full.png`. 박스가 많아 크므로(세로 김) 집중(순수) 풀폭. Amateras UML 스타일, 박스에 실제 필드/메서드, **코드와 1:1**(유령 클래스/메서드 금지). 위치는 구조 개관용으로 State Implementation 다음, DP 상세 앞. 보고서(GWS 3탭)에도 같은 도면을 동일 위치에 넣는다.
18-41. **DP#1 - DP#8 (패턴당 3장)**:
    - (a) 설명 장표: DP#n PatternName, "무엇을 위해 적용했나" 한 줄 + 역할 대응(교과서 role → 구현 클래스) + 적용 이득(SOLID). 교과서 사진 + 구현 다이어그램(가로로 길면 풀폭).
    - (b) 비교 전용 장표(순수): 교과서 사진과 구현 다이어그램만 좌우로 크게. 타이틀/설명/캡션/푸터 없음.
    - (c) **코드 장표(multi-class)**: 그 패턴의 **관련 역할 클래스 전부**를 카드 그리드로(4번 코드 규칙).
42. **DP Application Strength (적용 강도, 보강형)** — 단조로운 3열 나열이 아니라: 요약 한 줄(sub) + 3개 강도(기능 완결도/재사용성/테스트 적합성, 각 설명 + 구체 예시) + 하단 8개 DP 칩 밴드.
43. Status & Next Steps (현황과 다음) — 구현 완료 / 보완 필요(빨강) / 다음 단계(Extract Class). **이 장표는 "현황" 뷰이며 Extension 표(미래 확장)와 목적이 다르다 — 둘을 섞지 않는다.**
44. Closing / Q&A.
45. **(부록, Closing 이후) Reservation 설계 방어(Defense) 장표** — "왜 `Reservation` 이 비대한가"에 대한 예상 질문 대비용 backup 슬라이드. Q&A(Closing) **다음**에 둔다. 내용은 8번 섹션 기준: Aggregate Root + State Context 겸임은 의도된 설계(DDD/GoF 정석)이고 God class 가 아니라는 방어 + 영속 책임은 `ReservationRegistry` Extract Class 로 이미 분리(완료) + 잔여(이중 상태 단일화·중복 API 정리)는 향후. 한 장에 "정당한 이유 / 이미 한 것 / 향후" 3블록으로 구성하고 과장(전면 분해) 표현 금지.

> 장표 번호는 편집에 따라 바뀐다. 덱 산출물의 푸터 페이지 번호(`n / total`)는 자동 생성이라 항상 정확하다(따로 안 채워도 됨). 보고서 표의 페이지/섹션 참조는 별도 — 6번 참고.

## 3. 글로벌 규칙

- **DP 번호(확정): State=DP#1 / DP#2 Strategy / DP#3 Observer / DP#4 Composite / DP#5 Singleton / DP#6 Factory Method / DP#7 Template Method / DP#8 Adapter / DP#9 Decorator (총 9).** State는 초기 교과서 규칙상 제외했다가 팀 결정으로 DP#1 재지정(변경이력 표기). 덱·로컬 md·GWS 3탭 모두 이 스킴.
- iteration 4 신규/변경 항목은 보고서처럼 빨강 텍스트로 표시(테두리/박스 불필요). 이전 iteration 누적분은 검정.
- 보고서에 있는 사실만 사용. 새 클래스/메서드/숫자 창작 금지. 불확실하면 보고서 본문/부록(A1-A11)과 실제 소스를 출처로 확인. **소스 grep 단독 판정 금지 — 무엇이 어느 iteration에 추가/구현됐는지는 iteration 보고서 문서(`docs/iteration-reports/iteration-1~4-ko.md`)로 확인한다.**
- 행동 다이어그램(Use Case, Sequence(예약/결제/발권, Adapter, Decorator), State)과 표는 각각 "그것만" 크게 집중해서 보여주는 독립 장표를 둔다. Sequence는 메인 흐름뿐 아니라 **Adapter, Decorator도 각각** 독립 집중 장표를 갖는다.
- **집중(순수) 장표 규칙: 타이틀, 서브타이틀, 설명, 헤더, 푸터를 전부 제거하고 다이어그램(또는 표)만 화면 가득.** 다이어그램 이미지에 박힌 PlantUML title 줄도 제거한 버전을 쓴다(title 없는 렌더본).
- 표는 보고서(docs) 구성과 정확히 일치시킨다 — 모든 컬럼 포함, 셀 안 줄별 부분 빨강 보존, 임의로 열을 빼지 않는다.
- **클래스 다이어그램 완전성(필수): 패턴별 구현 클래스 다이어그램은 실제 소스의 해당 패턴 역할 클래스를 빠짐없이 포함하고, 클래스 개수가 소스와 일치해야 한다.** 다이어그램 박스에 쓰인 클래스명은 실제 소스 타입명과 정확히 같아야 한다(존재하지 않는 클래스명 금지). 누락/오타 점검 대상:
  - DP#2 Observer: EventListener 구현 전부(`BusTicketPurchaseListener`, `ReservationAutoCancelListener`, `ReservationHoldListener`, `AffectedReservationListener`), EventPublisher 서브클래스 전부(`TicketPurchasePublisher`, `PaymentProcessor`, `SeatHoldMonitor`, `FlightSchedule`), DomainEvent 서브클래스 전부(`TicketIssuedEvent`, `PaymentFailedEvent`, `ReservationStateChangedEvent`, `FlightStatusChangedEvent`, `SeatHoldExpiredEvent`).
  - **버스 연계 도메인 포함(필수): `BusTicket`, `BusTicketingService`, `BusTicketRequest`, `BusCity`, `BusSchedule`, `BusSeat` 를 Observer 클래스 다이어그램(또는 버스 도메인 전용 보조 다이어그램)에 박스로 그린다.** 현재 `BusTicketPurchaseListener` 만 보이고 발권 결과물(`BusTicket`)·요청·도시·스케줄·좌석 구조가 다이어그램에 없다 — `BusTicketPurchaseListener → BusTicketingService → BusTicket` 연계를 보이게 한다.
  - DP#8 Decorator: ConcreteComponent 박스명은 실제 소스인 `BaseSeatView` 다(`SeatViewAdapter` 는 소스에 없는 잘못된 이름 — 정정).
  - DP#1 Strategy 다이어그램에 `RefundPolicyResolver` 포함(코드 장표에는 이미 있음).
- **이미지 렌더(PNG)는 시각 검증용으로만 쓴다** — 오버플로우, 잘림, 과밀, 사진 회전, 레이아웃 확인용. 코드/표/텍스트의 내용 대조는 이미지가 아니라 텍스트·소스로 한다(이미지 대조는 오버헤드).
- 산출물 형식은 단순 HTML 덱(각 장표 독립 HTML + index.html 집계, 방향키로 넘김). 빌드 도구(`build.py`, `decktables.json`)는 작업용이며 repo에는 생성된 슬라이드/assets/index만 둔다.
- 학교 제출 repo이므로 커밋/푸시에 Claude/AI 흔적을 남기지 않는다(Co-Author·문구·README 어디에도). 커밋 메시지는 변경 목적만 담는다.

## 4. 코드 장표 규칙 (multi-class, 필수)

- **패턴당 한 클래스만 보여주지 않는다 — 그 패턴을 이루는 관련 역할 클래스를 전부 보여준다.** 인터페이스/추상(역할 타입), 구상 클래스, Context, Adaptee 등 패턴 역할에 해당하는 클래스를 모두 카드로 둔다.
  - DP#1 Strategy: RefundPolicy «Strategy» / No·Partial·FullRefundPolicy «ConcreteStrategy» / RefundHandler «Context» / RefundPolicyResolver «Selector»
  - DP#2 Observer: EventListener «Observer» / EventPublisher «Subject» / BusTicketPurchaseListener «ConcreteObserver» / BusTicketingService «연계 발권 서비스» / BusTicket «발권 결과물» (버스 발권 체인 BusTicketPurchaseListener → BusTicketingService → BusTicket 가 보이게)
  - DP#3 Composite: AirportLocation «Component» / Airport «Leaf» / AirportCity «Composite»
  - DP#4 Singleton: AppConfig «골격» / AppConfig «공유 상태 + 변경 통지»
  - DP#5 Factory Method: PaymentMethodProcessor «Creator» / CreditCardPaymentProcessor «ConcreteCreator» / Payment «Product» / CreditCardPayment «ConcreteProduct»
  - DP#6 Template Method: TicketRenderer «AbstractClass» / PlainTextTicketRenderer «ConcreteClass»
  - DP#7 Adapter: SkypassInterface «Target» / SkypassAdapter «Adapter» / RemoteSkypassApi «Adaptee»
  - DP#8 Decorator: SeatView «Component» / AbstractSeatDecorator «Decorator» / ExtraLegroomDecorator «ConcreteDecorator»
- 각 카드: 파일명 + 역할 라벨(«Strategy», «Context» 등) + 실제 소스 발췌.
- **코드는 실제 소스 그대로**(클래스명/메서드명/시그니처 일치). 역할이 드러나는 핵심 부분만 트림하되, **역할과 동작을 설명하는 상세 주석(한국어)을 충분히 단다.**
- 레이아웃: dark(IDE 다크) 카드 그리드(2열), syntax highlight. 카드가 홀수면 마지막 카드는 전폭(span). 한 장에 안 들어오면 카드를 더 트림하거나 패턴당 2장으로 나눈다.
- 모든 코드 장표는 PNG로 렌더해 오버플로우/잘림이 없는지 확인한다.

## 5. Extension Feature 표 규칙 (필수)

- **정의(REPORT_GOAL 4-4): Extension Feature = iteration 1 원래 계획에 없던 "추가사항 + 변경사항"과 "향후 확장 가능성".** 기존 Base Feature와 연결하고, Notes에 `Refactoring applied`(이미 함) / `Refactoring required`(향후) / 재사용 DP 번호를 적는다.
- **무엇이 "추가된 확장"인지는 iteration 1 계획(iter1 보고서 Feature Inventory + Pattern Roadmap)과 실제 산출물을 대조해 판정한다.** iter1 원래 패턴 계획 = State / Strategy / Observer / Singleton / Factory Method. 계획 외로 추가된 것:
  - DP#3 Composite(공항 계층), DP#6 Template Method(e-Ticket 렌더러), DP#7 Adapter(Skypass 마일리지), DP#8 Decorator(좌석 부가옵션) — 전부 Iteration 4.
  - 연계 버스 티켓 자동 발권(DP#2 Observer 활용) — **교수님 지시로 추가**, Iteration 3.
  - 환불 정책 자동 선택(RefundPolicyResolver, DP#1 Strategy) — Iteration 4.
- 덱은 두 장으로 나눈다: ① 이미 적용된 확장(applied) / ② 향후 확장 가능성(future).
- 빨강 규칙: **Iteration 4에서 추가·적용된 항목만 빨강**. 이전 iteration(버스티켓 iter3 등)과 미구현 항목은 검정.
- 보고서(GWS 3탭)는 단일 표에 적용행을 추가하고 iter4 추가분을 빨강 처리한다(6번 동기화).

## 6. 보고서 동기화 + 페이지 번호 규칙 (필수)

- **GWS 보고서는 3개 탭이다**: `t.0` 전체 한국어 / `t.91s3pcxxbpdg` 전체 영어 / `t.696nkrorir6j` 혼합. 표 변경은 **세 탭 모두**에 반영하고, 언어(한국어/영어)는 탭에 맞춘다.
- 덱 내용을 고치면 같은 사실을 보고서에도 반영한다(표 행, 빨강, 코드 설명 등). 덱과 보고서가 어긋나면 안 된다.
- 보고서의 iteration 4 신규/변경분만 빨강(`#C00000`), 이전 누적분은 검정. 표 행 삽입은 인덱스 시프트에 주의하고(행 삽입 → 텍스트 → 스타일 순), Google Docs 버전 히스토리로 복구 가능함을 전제로 신중히 한다.
- **페이지/섹션 참조 채우기(현재 비어 있음): 보고서 표의 페이지·섹션 참조 컬럼을 빠짐없이 채운다.**
  - Team Contribution 표의 "관련 섹션 / 페이지"(Related Section / Page) 컬럼.
  - Iteration Progress 표의 "기술 페이지"(Described Page) 컬럼.
  - 각 행이 가리키는 내용이 보고서 어느 섹션/페이지에 기술됐는지로 채운다.
- **수정 시 동기화: 내용을 추가/삭제해 페이지가 밀리면 위 페이지·섹션 참조도 같이 갱신한다.** 표의 페이지 번호가 실제 위치와 어긋난 채로 두지 않는다.
- 참고: 덱 슬라이드 푸터의 `n / total` 페이지 번호는 자동 생성이라 따로 채우거나 맞출 필요 없다. 페이지 번호 작업 대상은 "보고서 표의 페이지·섹션 참조 컬럼"이다.

## 7. 완료 기준

- 보고서가 그대로 장표로 옮겨졌다(보고서 장표화 발표본). 명확한 타이틀 + 내용에 맞는 자유 배치 + 보고서 다이어그램 이미지.
- 핵심 본문 표 3개(Feature, Iteration Progress, Extension Feature)와 Team Contribution 표(카드)가 포함되었고, Iteration 4 작업/항목은 빨강이다.
- Feature 표의 DP 번호는 `DP#n (PatternName)` 으로 패턴명을 병기한다.
- Extension Feature 표가 ① 적용 완료 / ② 향후 확장 두 장으로 나뉘고, 계획 외 추가분(Composite/Template Method/Adapter/Decorator/버스티켓/환불 resolver)이 정확히 반영됐으며 iter4 추가분만 빨강이다.
- 각 디자인 패턴이 설명 장표 + 비교 전용 장표 + **관련 역할 클래스 전부를 담은 multi-class 코드 장표**(상세 주석 포함)를 갖는다.
- **패턴별 클래스 다이어그램의 클래스 개수·이름이 실제 소스와 일치한다(3번 클래스 다이어그램 완전성 규칙 통과): Observer 다이어그램에 listener 4 / publisher 4 / event 5 + 버스 도메인(BusTicket 등)이 들어가고, Decorator ConcreteComponent 가 `BaseSeatView` 로 정정됐으며, 소스에 없는 클래스명(`SeatViewAdapter` 등)이 없다.**
- 행동 다이어그램 집중 장표가 Use Case / Sequence(메인) / **Sequence Adapter / Sequence Decorator** / State 각각 존재하고, 전부 순수(타이틀/헤더/푸터/설명 제거)다.
- 적용 강도 장표가 단조롭지 않게 보강됐다(요약 + 강도별 예시 + DP 칩 밴드).
- 슬라이드 텍스트는 한국어다(코드·식별자·패턴명은 영어). 교과서 사진은 모두 올바른 방향(회전 없음).
- 덱과 보고서(GWS 3탭)가 같은 사실을 담고 서로 모순되지 않는다. 보고서 표의 페이지·섹션 참조가 채워져 있고 실제 위치와 일치한다.
- 각 장표를 PNG로 렌더해 오버플로우/잘림/과밀/사진 회전/가독성을 눈으로 검증해 통과했다.
- 커밋/푸시에 Claude/AI 흔적이 없다.

## 8. Reservation 책임(SRP) 입장 + 정리 계획

`Reservation`(253줄)의 책임이 많다는 지적에 대한 팀 입장과 발표/보고서 narrative 기준. 덱 슬라이드 44("Reservation SRP 분리(Extract Class)") 및 보고서가 이 입장과 일치해야 한다.

### 책임 분석 (현재 소스 기준)
- **(1) Aggregate Root** — `passengers`/`reservationItems`/`payments`/`tickets`/`itinerary` 보유 + `addX()`. → DDD Aggregate Root 의 정당한 책임. 한 예약 트리의 일관성 경계.
- **(2) State Context** — `currentState` + `setState()` + 생애주기 위임 메서드(`processPayment`/`issueTicket`/`requestCancellation`/`confirmCancellation`/`requestRefund`/`processRefundDecision`/`handlePaymentFailure` 등). → GoF State 의 Context 역할. State 구현체가 `Reservation` 을 인자로 받으므로 Context = `Reservation` 이 자연스럽다.
- **(3) 영속/조회** — `findByPnr`. → **이미 해소됨**: iteration 4 에서 `ReservationRegistry` 로 Extract Class 완료, `Reservation.findByPnr` 는 호환용 얇은 위임 shim 으로만 남음. (가장 큰 SRP 위반이었고 이미 분리됨.)

### 입장: "지금 상태가 대체로 좋다 — God class 아님, 단 잔여 정리 권장"
- Aggregate Root + State Context 를 한 클래스가 겸하는 것은 DDD/GoF 정석이며 응집적이다. 데이터-보유와 행위를 강제로 분리하면 anemic domain model(안티패턴)이 된다. **공격적 분해는 하지 않는다.**
- 가장 큰 책임(영속)은 iter4 에서 이미 `ReservationRegistry` 로 분리했다 — 발표/보고서의 핵심 SRP 근거로 쓴다.

### 잔여 smell (저위험 정리 — 향후 확장 후보로 기재, 강행 아님)
- **이중 상태 표현**: `status`(ReservationStatus enum) 와 `currentState`(ReservationState 객체) 동시 보유 → 동기화 위험. 정리 방향: `getStatus()` 를 `currentState` 에서 파생(매핑)해 단일 진실원으로.
- **중복 레거시 위임 쌍**: `confirmReservation`↔`processPayment`, `issueTickets`↔`issueTicket`, `cancelReservation`↔`requestCancellation` 등 같은 State 호출을 하는 중복 API → 하나로 단일화.
- **dead/empty code**: `requestRefund(Payment, String)`(항상 null 반환), `evaluateImpactOfFlightStatusChange()`(빈 메서드) → 제거 또는 구현.

### 덱/보고서 반영 기준
- 슬라이드 44 및 보고서 "보완 필요/다음 단계" 에: **"Reservation 영속 책임은 `ReservationRegistry` Extract Class 로 이미 분리(완료), 잔여로 이중 상태 표현 단일화 + 중복 생애주기 API 정리는 향후"** 로 정확히 표기한다 (이미 한 것과 향후 할 것을 구분).
- "God class 라서 전면 분해" 식 과장 금지. Aggregate Root + State Context 는 의도된 설계임을 명시한다.
- 6번 동기화 규칙대로 보고서(GWS 3탭)에도 같은 입장을 반영한다.

### 부록 방어 장표 (Closing 이후, 필수)
- Closing/Q&A **다음**에 `Reservation` 설계 방어 backup 슬라이드를 1장 둔다(2번 장표 구성 45 항목). 예상 질문 "왜 `Reservation` 이 이렇게 비대한가?" 에 즉답하기 위한 용도.
- 한 장 3블록 구성:
  1. **정당한 이유** — Aggregate Root(예약 트리 일관성 경계) + State Context(State 구현체가 `Reservation` 을 인자로 받음) 겸임은 DDD/GoF 정석. 데이터-행위 강제 분리 = anemic model 안티패턴. → God class 아님.
  2. **이미 한 것** — 영속/조회 책임을 `ReservationRegistry` 로 Extract Class(iter4 완료). `findByPnr` 는 호환 shim.
  3. **향후** — 이중 상태(`status` enum vs `currentState`) 단일화, 중복 생애주기 API/ dead code 정리(저위험, 선택).
- 톤: 방어적이되 과장 없이. "전면 분해" 같은 표현 쓰지 않는다.
