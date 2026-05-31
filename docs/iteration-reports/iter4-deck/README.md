# Iteration 4 Final Deck

이 폴더는 Iteration 4 최종 발표 산출물입니다.

## Files

- `iter4-final-deck.pptx`: editable PowerPoint deck
- `iter4-final-deck.pdf`: 발표/검토용 PDF
- `speaker-script-iter4-ko.md`: 70페이지 수정판 기준 짧은 발표 대본
- `pattern-code-map.md`: DP별 교과서 역할, 우리 코드, 핵심 메서드, 시연 포인트

## Source Alignment

최종 deck은 Google Doc의 Form #1~#3, Section 4~16을 기준으로 구성했습니다. 단, 아래 항목은 실제 코드와 대조해 발표에서 틀리지 않도록 정정했습니다.

- State: Google Doc에는 "interface + 8 concrete states directly"처럼 적힌 부분이 있지만, 실제 코드는 `ReservationState`와 8개 상태 사이에 `AbstractReservationState`가 있어 기본 invalid transition을 한 곳에 모읍니다.
- Factory Method: `PaymentProcessorFactory`는 선택 helper이고, GoF Creator 역할은 `PaymentMethodProcessor`와 `ItineraryFactory`가 맡습니다.
- UI migration: Boundary 중심 JavaFX 전환이 맞지만, Control 계층에 UI 노출을 위한 얇은 glue method가 일부 추가되었습니다.
- Domain layer: 핵심 DP 구조는 유지되지만, 발표에서는 "완전히 한 줄도 안 바뀜"보다 "패턴 구조는 보존됨"으로 말하는 편이 안전합니다.

## Presentation Emphasis

교수님 피드백 기준으로 1, 2번 기능 나열보다 DP별 코드 강화가 핵심입니다. 발표 시간의 대부분은 다음 흐름으로 가져가면 됩니다.

1. 교과서 구조를 먼저 보여준다.
2. 우리 코드의 실제 클래스가 그 역할에 어떻게 대응되는지 말한다.
3. 핵심 메서드와 attribute를 짚는다.
4. JavaFX 앱에서 같은 동작을 시연한다.
5. VS Code에서 같은 클래스명/메서드명을 Ctrl+F로 찾아 코드와 연결한다.

## Red Change Markers

Deck 65페이지의 빨간 변경사항은 iter4 신규/변경 항목을 표시하기 위한 발표 포인트입니다.

- `Composite`: `AirportLocation`, `AirportCity`, city-code search
- `Singleton`: `AppConfig`, global settings listener
- `Factory Method`: `PaymentMethodProcessor`, `ItineraryFactory`
- `Template Method`: `TicketRenderer`
- `Adapter`: `SkypassAdapter`
- `Decorator`: `SeatView` decorator chain
- JavaFX migration: `app.fx`, FXML/CSS/controller split
- QA fixes: double refund, fare scaling, back navigation, connecting booking path
