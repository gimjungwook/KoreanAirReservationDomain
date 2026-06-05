# Iteration 4 발표 덱 제작 목표 (DECK_GOAL)

## 0. 목적

- `docs/REPORT_GOAL.md` 와 GWS 보고서(Google Docs ID `1Zd53XHVxSU6cINaN2GWbcmjpsSHpXIDW-PRpDFFVeq8`)에 이미 들어 있는 내용을 발표용 슬라이드 덱으로 옮긴다.
- 덱은 보고서의 핵심을 장표 단위로 재배치한 것이다. 새 사실을 창작하지 않고, 보고서에 있는 내용(기능, 디자인 패턴, 다이어그램, 코드 흐름, 부록 A1-A11)만 사용한다.
- 보고서와 덱은 서로 모순되지 않아야 한다(DP 번호, pull 모델, 클래스/메서드 이름, State 미번호 규칙 등 모두 보고서와 동일).

## 1. 스타일 규칙 (최소, 디자인 없음)

- 정교한 디자인, 디자인 카드, 그래픽 레이아웃, 배경 이미지, 아이콘 세트 같은 것은 만들지 않는다.
- 각 장표는 다음 세 가지로만 구성한다.
  - 타이틀 (한 줄)
  - 서브타이틀 (한 줄, 그 장표가 무엇을 말하는지 요약)
  - 설명 텍스트 (불릿 목록)
- 이모지는 적당히만 쓴다. 타이틀이나 불릿 머리에 포인트 강조용으로 하나 정도. 장표를 이모지로 도배하지 않는다.
- 불릿은 장표당 5-7개 이내, 한 불릿은 한 줄(길어도 두 줄)로 간결하게.
- 슬라이드 텍스트는 영어로 작성한다(한동대 수업 규칙: 슬라이드 영어, 구두 발표 한국어). 클래스명, 메서드명, 코드 식별자는 그대로 영어.
- 보고서에 있는 다이어그램 이미지(Use Case, Sequence, State, DP#1-8 구현 다이어그램, 교과서 사진)는 필요한 장표에 그대로 삽입할 수 있다. 다이어그램을 새로 그리지 않고 보고서 것을 재사용한다.
- 색, 폰트, 애니메이션 같은 정교한 서식은 신경 쓰지 않는다. 읽히기만 하면 된다.

## 2. 장표 구성 (보고서 섹션 → 장표 매핑)

각 줄은 한 장표다. 타이틀 / 서브타이틀 / 불릿 내용 출처를 적는다.

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
    - 각 장표 공통 구조:
      - 타이틀: DP#n PatternName
      - 서브타이틀: 한 줄로 "무엇을 위해 적용했나"
      - 불릿: 역할 대응(교과서 role → 구현 클래스), 왜 필요했나, 어떻게 구현했나, 적용 이득(SOLID) 1줄씩
      - 다이어그램: 교과서 사진(좌) + 구현 다이어그램(우) 이미지
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
- 산출물 형식은 추후 결정(Google Slides 또는 단순 HTML/마크다운 덱). 우선 내용과 장표 순서를 위 구성대로 채우는 것이 목표.

## 4. 완료 기준

- 위 22장(또는 동등) 장표가 타이틀 + 서브타이틀 + 불릿 + (해당 시) 다이어그램 이미지로 채워졌다.
- 모든 텍스트가 보고서 내용과 일치하고 서로 모순되지 않는다.
- 디자인 카드/그래픽 없이 텍스트와 적당한 이모지로만 구성되었다.
- DP#1-8과 State, 행동 다이어그램(UC/Sequence/State), 결론이 모두 포함되었다.
