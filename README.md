<div align="center">

# ✈️ 대한항공 Skypass 티켓 예약 시스템

**한동대학교 · ECE312 객체지향 설계패턴 · 2026년 1학기 · 설계프로젝트 #2 · A팀**

[![Java](https://img.shields.io/badge/Java-17%2B-007396?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![IDE](https://img.shields.io/badge/IDE-Eclipse-2C2255?style=flat-square&logo=eclipseide&logoColor=white)](https://www.eclipse.org/)
[![UML](https://img.shields.io/badge/UML-AmaterasUML-1E90FF?style=flat-square)](http://amateras.sourceforge.jp/)
[![Patterns](https://img.shields.io/badge/GoF-State%20%C2%B7%20Strategy%20%C2%B7%20Observer%20%C2%B7%20Singleton-6A5ACD?style=flat-square)](https://en.wikipedia.org/wiki/Design_Patterns)
[![Iteration](https://img.shields.io/badge/Iteration-3%20%2F%204-FF8C00?style=flat-square)](#-진행-상태)
[![License](https://img.shields.io/badge/License-Academic%20Reference-708090?style=flat-square)](#%EF%B8%8F-라이선스-및-학술-무결성)

</div>

---

설계프로젝트 #1에서 만든 UML 모델을 자바 데스크톱 애플리케이션으로 구현하고, 4번의 iteration을 거치며 점진적으로 정제해 나가는 프로젝트. 각 iteration은 하나의 주축 디자인 패턴을 중심에 둔다.

> [!NOTE]
> 본 저장소는 진행 중인 산출물입니다. iteration 3 **Observer 패턴 구현**까지 끝까지 동작하며, iteration 4부터는 계획 단계입니다 (각 iteration 보고서는 [`docs/iteration-reports/`](docs/iteration-reports/) 참조).

---

## 📌 진행 상태

| Iteration | 패턴 | 상태 | 다루는 기능 |
| :---: | :--- | :---: | :--- |
| **1** | **State** — 8개 구상 상태 클래스 | ✅ 동작 | 회원 가입 · 로그인 · 검색 · 직항 · 승객 · 결제 |
| **2** | **Strategy** — `RefundPolicy` family | ✅ 동작 | 취소 · 환불 · e-Ticket · 예약 조회 · 좌석 선택 · salted-hash auth |
| **3** | **Observer** — 비동기 이벤트 전파 | ✅ 동작 | 환승 · multi-city · 마일리지 · 자동 취소 |
| **4** | **Singleton** + 옵션 **Factory Method** | 📋 계획 | 관리자 환불 · 전역 설정 · PDF · 실시간 추적 |

### 패턴 로드맵

```mermaid
gantt
    title OODP 4-iteration 패턴 로드맵
    dateFormat  YYYY-MM-DD
    axisFormat  %m/%d
    section Iter 1
    State pattern (active)         :done,    s1, 2026-04-17, 11d
    section Iter 2
    Strategy refund family         :         s2, 2026-04-28, 14d
    section Iter 3
    Observer event propagation     :         s3, 2026-05-12, 13d
    section Iter 4
    Singleton + Factory Method     :         s4, 2026-05-26, 13d
```

---

## 🏛 아키텍처 (ECB)

```mermaid
flowchart LR
    User((사용자))

    subgraph Boundary
        UI[ReservationUI]
        PG[PaymentGatewayInterface]
        SK[SkypassInterface]
    end

    subgraph Control
        BC[BookingController]
        AS[AuthService]
        FS[FlightSearchService]
        PP[PaymentProcessor]
        RH[RefundHandler]
    end

    subgraph Domain
        R[Reservation<br/>Context]
        S[ReservationState<br/>8 concrete states]
        FR[FareRule]
        P[Payment]
        F[Flight / FlightSchedule]
    end

    User --> UI
    UI <--> BC
    BC --> AS
    BC --> FS
    BC --> PP
    BC --> R
    PP --> PG
    FS --> F
    R --> S
    R --> FR
    R --> P
```

<details>
<summary>📁 <b>패키지 구조 펼쳐보기</b></summary>

```
src/com/koreanair/reservation/
├── app/                    # 진입점(App 콘솔, FxApp), 목 인프라, 샘플 데이터
│   └── fx/                 # JavaFX UI — Navigator, ShellController, *.fxml, app.css
│       └── screen/         # 화면별 Controller (Login, Search, Passenger, Seat, Payment, ...)
├── boundary/               # ReservationUI, PaymentGatewayInterface, SkypassInterface
├── control/                # BookingController, AuthService, FlightSearchService,
│                           # PaymentProcessor, RefundHandler
├── domain/
│   ├── reservation/        # Reservation 애그리거트 (State 패턴의 Context)
│   │   └── state/          # 8개 *State + AbstractReservationState
│   │                       # + InvalidStateTransitionException
│   ├── flight/             # Flight, FlightSchedule, FareRule, Seat, SeatInventory, ...
│   ├── passenger/          # Passenger, MileageAccount (iter3), PassengerType
│   ├── payment/            # Payment, PaymentMethod, Refund (iter2), RefundRequest (iter2)
│   └── user/               # User, Member, Admin, GuestBookingRequester
└── tools/                  # AmaterasUML 에미터 (Generate*Diagram.java)
```

총 **114개 자바 파일**, **18개 패키지**.

</details>

---

## 🚶 Walking Skeleton (iteration 1)

iteration 1의 happy path는 `App.main(...)`에서 끝까지 동작합니다.

```mermaid
sequenceDiagram
    actor 사용자
    participant UI as ReservationUI
    participant BC as BookingController
    participant R as Reservation
    participant PG as PaymentGateway

    사용자->>UI: 검색 / 선택 / 승객 입력
    UI->>BC: setPassengerInfo(...)
    BC->>R: enterPassengerInfo(p)
    Note right of R: Initiated → PendingPayment
    사용자->>UI: 결제 확인
    UI->>BC: confirmPayment(...)
    BC->>PG: authorize(payment)
    PG-->>BC: ✅ true
    BC->>R: processPayment()
    Note right of R: PendingPayment → Confirmed
    BC-->>UI: PNR 확정
```

콘솔에서는 두 줄이 출력되어 State 전이를 직접 확인할 수 있습니다.

```
[STATE] Initiated -> PendingPayment
[STATE] PendingPayment -> Confirmed
```

UI 는 **JavaFX(`app.fx.FxApp`)** 로 구현되어 있으며, Control 과 Domain 코드를 그대로 사용하면서 동일한 시나리오를 구동합니다. 화면 레이아웃은 FXML, 스타일은 CSS, 로직은 Controller 로 분리됩니다. 처음에는 Swing 으로 시작했으나 선언형 FXML + CSS 구조로 전환했습니다 — Control/Domain 을 한 줄도 건드리지 않고 Boundary 만 교체했다는 점이 **ECB 아키텍처의 비파괴적 경계 교체**를 그대로 증명합니다.

### State 패턴 전이도

```mermaid
stateDiagram-v2
    [*] --> Initiated
    Initiated --> PendingPayment : enterPassengerInfo
    PendingPayment --> Confirmed : processPayment ✅
    PendingPayment --> Cancelled : handlePaymentFailure ❌
    Confirmed --> Ticketed : issueTicket
    Confirmed --> CancellationRequested : requestCancellation
    Ticketed --> CancellationRequested : requestCancellation
    CancellationRequested --> Cancelled : confirmCancellation
    CancellationRequested --> RefundRequested : requestRefund
    RefundRequested --> Refunded : processRefundDecision
    Refunded --> [*]
    Cancelled --> [*]
```

> [!NOTE]
> Iteration 1에서는 **3개 전이만 실제로 동작**합니다 (`enterPassengerInfo`, `processPayment` 성공, `handlePaymentFailure`). 나머지 전이는 8개 `*State` 클래스에 선언만 되어 있으며, iteration 2부터 본문이 채워집니다.

---

## 🛠 빌드 및 실행

UI 는 **JavaFX(FXML + CSS)** 로 구현되어 있고, 의존성 관리는 **Maven** 으로 합니다. Maven Wrapper(`mvnw`)가 포함되어 있어 별도 설치 없이 바로 실행됩니다.

### A) JavaFX UI (권장)

```bash
./mvnw javafx:run          # macOS / Linux
mvnw.cmd javafx:run        # Windows
```

> JavaFX 23 의존성은 첫 실행 시 Maven 이 자동으로 내려받습니다. `tools/`(AmaterasUML 에미터)는 Eclipse 플러그인 jar 에 의존하므로 Maven 빌드에서 제외됩니다.

### B) 콘솔 드라이버

```bash
./mvnw -q compile
java -cp target/classes com.koreanair.reservation.app.App
```

### C) Eclipse

```
File → Import → Existing Maven Projects → clone한 디렉토리 선택
```

진입점:

| 모드 | 클래스 |
| --- | --- |
| JavaFX UI | `com.koreanair.reservation.app.fx.FxApp` |
| 콘솔 | `com.koreanair.reservation.app.App` |

---

## 🎨 다이어그램 자동 생성

UML 다이어그램 4종(use case · class · sequence · state)은 `com.koreanair.reservation.tools` 패키지의 에미터 클래스가 소스 트리에서 자동 생성합니다.

| 에미터 | 출력 파일 |
| --- | --- |
| `GenerateUseCaseDiagram` | `reservationSystem.ucd` |
| `GenerateClassDiagram` | `classDiagram.cld` |
| `GenerateSequenceDiagrams` | `bookFlight.sqd` · `adminOperations.sqd` · `memberBookingTicket.sqd` |
| `GenerateStateDiagrams` | `reservationState.acd` · `seatState.acd` · `flightScheduleState.acd` |

각 에미터는 워크스페이스에 AmaterasUML XML 파일을 쓰며, Eclipse에서 AmaterasUML 플러그인으로 열어 PNG로 export하면 됩니다.

> [!TIP]
> 다이어그램을 손으로 그리지 않고 소스에서 자동 생성하는 이유: iteration이 진행되며 설계가 바뀌어도 한 번의 rebuild로 모든 다이어그램이 자동 동기화됩니다 — **"그림 그리기"보다 "문서 컴파일"에 가깝습니다.**

---

## 📄 제출물

iteration별 보고서는 [`docs/iteration-reports/`](docs/iteration-reports/) 아래에 보관합니다.

| Iteration | 패턴 | 한국어 | 영문 |
| :---: | :--- | :---: | :---: |
| **1** | State (Walking Skeleton) | [📄 KO](docs/iteration-reports/iteration-1-ko.md) | [📄 EN](docs/iteration-reports/iteration-1-en.md) |
| **2** | Strategy (`RefundPolicy` family) | [📄 KO](docs/iteration-reports/iteration-2-ko.md) | — |

---

## 👥 A팀

<table>
<tr>
<td align="center" width="33%">
<b>김정욱</b><br>
<sub>Jungwook Kim</sub><br><br>
🧱 <b>도메인 & 패턴</b>
</td>
<td align="center" width="33%">
<b>이재호</b><br>
<sub>Jaeho Lee</sub><br><br>
🖼 <b>Boundary</b>
</td>
<td align="center" width="33%">
<b>김경동</b><br>
<sub>Gyungdong Kim</sub><br><br>
⚙️ <b>Control & 어댑터</b>
</td>
</tr>
<tr>
<td valign="top">
<sub>
• <code>Reservation</code> 애그리거트<br>
• State · Strategy · Observer · Singleton 패턴<br>
• AmaterasUML 에미터<br>
• 통합
</sub>
</td>
<td valign="top">
<sub>
• Swing UI 패널 7종<br>
• 콘솔 프런트엔드<br>
• <code>ReservationUI</code> 구현
</sub>
</td>
<td valign="top">
<sub>
• <code>PaymentProcessor</code><br>
• <code>RefundHandler</code><br>
• <code>PaymentGatewayInterface</code> 목 구현<br>
• <code>AuthService</code><br>
• JUnit 스위트
</sub>
</td>
</tr>
</table>

---

## ⚖️ 라이선스 및 학술 무결성

> [!IMPORTANT]
> 본 저장소는 **학술·참고 목적**으로 공개됩니다. 다른 학기·다른 기관의 OODP 제출에 그대로 재사용하는 것은 학술적 부정행위에 해당하며 허용되지 않습니다.

---

<div align="center">
<sub>Made with ☕ and the Gang-of-Four book · Spring 2026</sub>
</div>
