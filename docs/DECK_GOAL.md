# Iteration 4 발표 덱 제작 목표 (DECK_GOAL)

## 0. 목적

- `docs/REPORT_GOAL.md` 와 GWS 보고서(Google Docs ID `1Zd53XHVxSU6cINaN2GWbcmjpsSHpXIDW-PRpDFFVeq8`)에 이미 들어 있는 내용을 발표용 슬라이드 덱으로 옮긴다.
- 덱은 보고서의 핵심을 장표 단위로 재배치한 것이다. 새 사실을 창작하지 않고, 보고서에 있는 내용(기능, 디자인 패턴, 다이어그램, 코드 흐름, 부록 A1-A11)만 사용한다.
- **핵심: 덱의 모든 텍스트와 다이어그램은 보고서(GWS doc)에 이미 있는 것을 그대로 가져다 쓴다. 다이어그램을 새로 그리지 않고 보고서 이미지를 그대로 삽입하고, 설명도 보고서 문장을 장표용으로 짧게 줄여 불릿으로 옮긴다. 새로 만드는 것은 "장표 분할과 불릿 정리"뿐이다.**
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

3. Team Contribution 👥
   - 출처: 보고서 Team Contribution 표
   - 불릿: 팀원별 담당(정욱 도메인/패턴, 재호 JavaFX UI, 경동 control/integration/QA)

4. Feature Overview 🧩
   - 출처: 보고서 Feature 표
   - 불릿: 큰 기능 단위(검색, 예약, 결제, 환불, 좌석, 이벤트 통지 등) + 어느 iteration

5. Iteration Journey (1 → 4) 📈
   - 출처: 보고서 Iteration Progress 표
   - 불릿: Iter1 State 머신, Iter2 DP#1 Strategy, Iter3 DP#2 Observer, Iter4 DP#3-8 + 리팩토링

6. Extension Ideas 🚀
   - 출처: 보고서 Extension 표
   - 불릿: 향후 확장(알림 Email/SMS, 결제 수단 추가, 좌석 추천, 다국어 등) + 재사용/예상 패턴

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

12-19. DP#1 - DP#8 (디자인 패턴당 1장)
    - 각 장표 권장 내용(배치는 자유):
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
- 장표 수는 위 구성 기준 약 22장. 패턴 장표를 묶거나 나눠도 되지만, DP#1-8은 빠짐없이 포함.
- 산출물 형식은 단순 HTML 덱(각 장표 독립 HTML + index.html 집계, 브라우저에서 방향키로 넘김). 각 장표를 PNG로 렌더해 오버플로우, 잘림, 과밀, 가독성을 눈으로 검증한다.

## 4. 완료 기준

- 위 22장(또는 동등) 장표가 명확한 타이틀 + 내용에 맞는 자유 배치(불릿, 짧은 산문, 콜아웃 등) + (해당 시) 보고서 다이어그램 이미지로 채워졌다.
- 슬라이드 텍스트는 한국어다(코드 식별자, 패턴명은 영어).
- 모든 텍스트가 보고서 내용과 일치하고 서로 모순되지 않는다.
- 과한 디자인 카드/그래픽 없이 읽기 쉽게 구성되었고, 장표마다 단조롭지 않게 배치가 다양하다.
- DP#1-8과 State, 행동 다이어그램(UC/Sequence/State), 결론이 모두 포함되었다.
