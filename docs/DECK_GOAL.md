# Iteration 4 발표 덱 제작 목표 (DECK_GOAL)

## 0. 목적

- `docs/REPORT_GOAL.md` 와 GWS 보고서(Google Docs ID `1Zd53XHVxSU6cINaN2GWbcmjpsSHpXIDW-PRpDFFVeq8`)에 이미 들어 있는 내용을 발표용 슬라이드 덱으로 옮긴다.
- 덱은 보고서의 핵심을 장표 단위로 재배치한 것이다. 새 사실을 창작하지 않고, 보고서에 있는 내용(기능, 디자인 패턴, 다이어그램, 코드 흐름, 부록 A1-A11)만 사용한다.
- **핵심: 이 덱은 보고서를 그대로 발표용 장표로 옮긴 것이다 — "보고서를 장표화해서 그냥 발표한다"고 생각한다. 보고서에 있는 표(핵심 본문 표 3개 + Team Contribution 표), 코드 블록, 다이어그램, 설명을 빠짐없이 장표화한다. 표/다이어그램/코드는 새로 만들지 않고 보고서와 실제 소스의 것을 그대로 쓰고, 설명만 장표용으로 짧게 줄인다.**
- **표 3개 필수: Feature 표, Iteration Progress 표, Extension Feature 표는 보고서처럼 반드시 장표로 포함한다(표 형태 그대로). Team Contribution 표도 포함하며 Iteration 4 작업은 빨간색으로 표시한다.**
- **코드 필수: 각 디자인 패턴은 실제 소스 코드를 보여주는 코드 장표를 포함한다(아래 2번 12-19 참고).**
- 보고서와 덱은 서로 모순되지 않아야 한다(DP 번호, pull 모델, 클래스/메서드 이름, State 미번호 규칙 등 모두 보고서와 동일).

## 1. 스타일 규칙 (최소, 디자인 없음)

- 정교한 디자인, 디자인 카드, 그래픽 레이아웃, 배경 이미지, 아이콘 세트 같은 것은 만들지 않는다.
- 각 장표에는 명확한 타이틀을 둔다. 타이틀 아래 본문은 내용에 맞게 자유롭게 배치한다. 장표마다 배치가 달라도 된다.
- 무조건 다 불릿일 필요는 없다. 내용에 따라 형태를 고른다: 불릿, 짧은 산문 한두 문장, 핵심 문장 한 줄, 2단 비교, 강조 숫자/콜아웃, 이미지 + 캡션, 표 등. 단조로운 불릿 도배는 피한다.
- 서브타이틀은 필요할 때만 넣는다(필수 아님).
- 이모지는 절제해서 쓴다. 장표당 하나 정도 포인트 강조용. 이모지로 도배하지 않는다.
- 한 장표에 내용을 과밀하게 넣지 않는다. 핵심만, 가독성 우선.
- 슬라이드 텍스트는 한국어로 작성한다. 단, 클래스명, 메서드명, 코드 식별자, 디자인 패턴명(예: DP#1 Strategy, EventPublisher, getInstance)은 영어 원어를 그대로 유지한다.
- 보고서에 있는 다이어그램 이미지(Use Case, Sequence, State, DP#1-8 구현 다이어그램, 교과서 사진)는 필요한 장표에 그대로 삽입할 수 있다. 다이어그램을 새로 그리지 않고 보고서 것을 재사용한다.
- 색, 폰트, 애니메이션 같은 정교한 서식은 신경 쓰지 않는다. 읽히기만 하면 된다.

## 2. 장표 구성 (보고서 섹션 → 장표 매핑)

각 줄은 한 장표다. 아래는 "어떤 보고서 내용을 담을지" 매핑이며, 영어 라벨과 "불릿" 표기는 참고용이다. 실제 장표의 제목과 본문은 한국어로 작성하고, 배치는 1번 스타일 규칙대로 내용에 맞게 자유롭게 한다(불릿이 아니어도 됨).

1. Title — Korean Air Reservation System, OODP Iteration 4
   - 서브타이틀: Team A, design-pattern report
   - 불릿: 팀원 이름, 발표 날짜(placeholder), iteration 4

2. Agenda / Overview ✈️
   - 서브타이틀: what this deck covers
   - 불릿: 프로젝트 한 줄 소개, 다룰 내용(기능, 행동 다이어그램, 디자인 패턴 DP#1-8, 결론)

3. Team Contribution 표 👥
   - 보고서 Team Contribution 표를 표 형태로 보여준다(Member / Responsibility / Main Contribution / Related Iteration). Iteration 4에서 한 작업은 빨간색.

4. Feature 표 🧩
   - 보고서 Feature 표를 표 형태로 그대로(Feature / Sub-feature / Implementation Iteration). Iteration 4 항목 빨강.

5. Iteration Progress 표 📈
   - 보고서 Iteration Progress 표를 표 형태로(Iteration / Sub-iteration / Refactoring·Design Pattern / Applied Location / Summary). Iteration 4 행 빨강. 열이 많아 길면 글씨를 줄이거나 핵심 열만 남겨 한 장에 들어오게 한다.

6. Extension Feature 표 🚀
   - 보고서 Extension Feature 표를 표 형태로(Base Feature / Extension Feature / Notes).

7. Behavioral — Use Case 🗺️
   - 출처: 보고서 Use Case 다이어그램 + 설명
   - 불릿: 액터(Customer/Member/Guest/Admin/Payment Gateway/Skypass System), iter4 빨강 신규 유스케이스
   - 다이어그램: usecase-cumulative 이미지 삽입

8. Behavioral — Sequence (Reservation / Payment / Ticketing) 🔁
   - 출처: 보고서 reservation-main Sequence + 설명
   - 불릿: Factory Method 무인자 createPayment, Observer pull(notify/getState)
   - 다이어그램: sequence-reservation-main 이미지

9. Behavioral — Sequence (Adapter / Decorator) 🔌
   - 출처: 보고서 mileage-adapter, seat-decorator Sequence
   - 불릿: Adapter postDeduct 맵 변환, Decorator super 위임 누적
   - 다이어그램: 두 sequence 이미지

10. Behavioral — Reservation State Machine 🔄
    - 출처: 보고서 State 다이어그램 + 설명
    - 불릿: 8개 상태, 허용 전이만 처리, 나머지는 InvalidStateTransitionException
    - 다이어그램: state-reservation-lifecycle 이미지

11. State Implementation (not a numbered DP) 🧱
    - 출처: 보고서 Section 7 + 부록 A3
    - 불릿: Context=Reservation, ReservationState 인터페이스 → 8 ConcreteState, 교과서 규칙상 State는 DP 번호 미부여(초기 DP#1 → 추후 수정 이력)
    - 다이어그램: state-impl 이미지(교과서 사진 좌, 구현 우)

12-19. DP#1 - DP#8 (디자인 패턴당 3장: 설명 장표 + 비교 전용 장표 + 코드 장표)
    - 각 DP는 세 장으로 구성한다.
      - (a) 설명 장표: 아래 권장 내용
      - (b) 비교 전용 장표: 교과서 사진과 구현 다이어그램만 좌우로 크게 1대1로 보여준다. 타이틀, 설명 텍스트, 캡션, 푸터 없이 두 이미지만(직접 시각 비교용). 설명 장표 바로 다음에 배치한다.
      - (c) 코드 장표: 그 패턴의 핵심을 보여주는 실제 소스 코드를 보여준다. dark 배경(IDE 다크 모드) 코드 블록, monospace, 가능하면 syntax highlight. 보고서의 코드 블록을 재사용하거나 실제 소스에서 발췌하며, 코드를 창작하지 않고 실제 소스 그대로 쓴다(클래스명, 메서드명, 시그니처 일치). 핵심 메서드 위주로 한 장에 들어갈 만큼 발췌하고, 역할을 설명하는 짧은 주석(한국어)을 달 수 있다. 패턴당 1장(필요하면 한 장에 두 개 짧은 발췌).
    - (a) 설명 장표 권장 내용(배치는 자유):
      - 타이틀: DP#n PatternName (패턴명은 영어 유지)
      - "무엇을 위해 적용했나" 한 줄 + 역할 대응(교과서 role → 구현 클래스), 필요성, 구현 방식, 적용 이득(SOLID) — 불릿이든 짧은 설명이든 내용에 맞게
      - 다이어그램: 교과서 사진 + 구현 다이어그램 이미지. 구현 다이어그램이 가로로 길면 풀폭으로 크게 배치해 가독성 확보(작게 욱여넣지 않기)
    - 매핑: DP#1 Strategy(RefundHandler/RefundPolicy, RefundPolicyResolver) / DP#2 Observer(pull) / DP#3 Composite(Airport/AirportCity) / DP#4 Singleton(AppConfig) / DP#5 Factory Method(PaymentMethodProcessor, ItineraryFactory) / DP#6 Template Method(TicketRenderer) / DP#7 Adapter(SkypassAdapter) / DP#8 Decorator(SeatView)

20. DP Application Strength (Conclusion) 🏁
    - 출처: 보고서 부록 A1
    - 불릿: 기능 완결도, 재사용성, 테스트 적합성 한 줄씩

21. Status & Next Steps 📋
    - 출처: 보고서 부록 A2(구현 완료/보완 필요), A11(SRP 분해)
    - 불릿: 구현 완료 핵심, 보완 필요(빨강 1-2개), 다음 iteration(Extract Class 완전 분리)

22. Closing / Q&A 🙋
    - 서브타이틀: thank you
    - 불릿: 핵심 한 줄 요약, 질문

## 3. 글로벌 규칙

- DP 번호는 Scheme B 고정: DP#1 Strategy, DP#2 Observer, DP#3 Composite, DP#4 Singleton, DP#5 Factory Method, DP#6 Template Method, DP#7 Adapter, DP#8 Decorator. State는 DP 번호 미부여.
- iteration 4 신규/변경 항목은 보고서처럼 빨강으로 표시해도 되지만, 디자인 최소 원칙상 빨강 텍스트 정도로만(테두리/박스 불필요).
- 보고서에 있는 사실만 사용. 새 클래스/메서드/숫자 창작 금지. 불확실하면 보고서 본문/부록(A1-A11)을 출처로 확인.
- 행동 다이어그램(Use Case, Sequence, State)과 표(핵심 본문 표 3개 + Team Contribution)는 각각 "그것만" 크게 집중해서 보여주는 독립 장표를 둔다(DP 비교 전용 장표와 같은 원리). 그 집중 장표에서는 다이어그램이나 표 자체를 화면 가득 크게 배치하고, 군더더기 설명은 빼거나 최소화한다. 설명이 필요하면 앞이나 뒤에 별도 설명 장표를 둔다(설명 + 집중 2단 구성도 좋다).
- 장표 수는 DP당 3장(설명 + 비교 + 코드) + 표 3개 + Team Contribution 표 + 행동 다이어그램 + State + 결론 기준 약 38-40장. DP#1-8과 표 3개는 빠짐없이 포함.
- 산출물 형식은 단순 HTML 덱(각 장표 독립 HTML + index.html 집계, 브라우저에서 방향키로 넘김). 각 장표를 PNG로 렌더해 오버플로우, 잘림, 과밀, 가독성을 눈으로 검증한다.

## 4. 완료 기준

- 보고서가 그대로 장표로 옮겨졌다(보고서 장표화 발표본). 명확한 타이틀 + 내용에 맞는 자유 배치(불릿, 짧은 산문, 콜아웃, 표 등) + 보고서 다이어그램 이미지.
- 핵심 본문 표 3개(Feature, Iteration Progress, Extension Feature)와 Team Contribution 표가 표 형태로 포함되었고, Iteration 4 작업/항목은 빨간색이다.
- 각 디자인 패턴이 설명 장표 + 비교 전용 장표 + 실제 소스 코드 장표를 갖는다. 코드는 실제 소스 그대로이고 다크 코드 블록으로 보인다.
- 슬라이드 텍스트는 한국어다(코드, 클래스/메서드명, 패턴명은 영어).
- 교과서 사진은 모두 올바른 방향(회전/뒤집힘 없음)으로 들어갔다.
- 모든 텍스트/표/코드가 보고서 및 실제 소스와 일치하고 서로 모순되지 않는다.
- 과한 디자인 카드/그래픽 없이 읽기 쉽게 구성되었고, 장표마다 단조롭지 않게 배치가 다양하다.
- DP#1-8과 State, 행동 다이어그램(UC/Sequence/State), 표 3개, 결론이 모두 포함되었다.
- 각 장표를 PNG로 렌더해 오버플로우/잘림/과밀/사진 회전/가독성을 눈으로 검증해 통과했다.
