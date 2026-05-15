# Iter 3 Deck — 사용 가이드 (Vercel-light)

> 11/12주차 OODP iter 3 발표용 슬라이드 (24장). 한국어 본문 + 영어 코드/클래스명/패턴명.
> `iter3-final-hq.pdf`는 이 폴더의 HTML 슬라이드와 diagram assets에서 재생성할 수 있다.
> 2026-05-15 요구사항 반영: 항공권 구매와 연계되는 6개 대도시 우등고속 버스티켓 발매서비스를 iter3 Observer 적용 사례로 추가.

> ⚠️ Demo 슬라이드(19, 20)의 콘솔 출력은 **mockup이 아니라 실제 캡쳐**.
> `java -cp bin com.koreanair.reservation.app.Iter3DemoRunner`를 실행하면 같은 출력이 나온다.
> 출력 원본: `assets/iter3-demo-output.txt`.

---

## 1. 슬라이드 열기

브라우저에 `index.html` 더블클릭 → 풀스크린 모드.

| 키 | 동작 |
|---|---|
| `→` `Space` `PgDown` | 다음 슬라이드 |
| `←` `PgUp` | 이전 슬라이드 |
| `Home` / `End` | 처음 / 마지막 |
| `1` ~ `9` | 해당 번호로 점프 |
| `P` | 인쇄 (PDF 저장) |

상태는 `localStorage`에 저장됨.

---

## 2. 포함된 산출물

| 파일/폴더 | 역할 |
|---|---|
| `index.html` | Vercel-light 슬라이드 플레이어 |
| `slides/*.html` | 24장 슬라이드 (한 파일 = 한 페이지) |
| `assets/shared.css` | Vercel-light 디자인 토큰 (Geist + gradient accents) |
| `assets/diagrams/*.png` | Use Case · Class · Sequence · Demo backup |
| `speaker-script-iter3-ko.md` | 한국어 발표 대본 + 예상 Q&A |
| `tools/generate_diagram_assets.py` | Vercel-light 스타일 PNG 자동 생성 |
| `tools/export_iter3_vector_pdf.mjs` | HTML → 벡터 PDF (Chrome DevTools Protocol) |
| `tools/merge_slide_pdfs.py` | 슬라이드별 PDF → `iter3-final-hq.pdf` 병합 |

---

## 3. 디자인 시스템 (Vercel-light)

- **배경**: `#FFFFFF` (옵션으로 dotted-grid)
- **잉크**: `#000` → `#A3A3A3` 5-단계
- **경계선**: `#EAEAEA` 1px (sharp, no radius excess)
- **그래디언트 accents**:
  - 핑크: `#7928CA → #FF0080` — Observer/iter3 신규 강조
  - 블루: `#0070F3 → #00DFD8` — Itinerary/Mileage 강조
  - 오렌지: `#FF4D4D → #F9CB28` — 보조
- **타이포**: Geist Sans (display) / Geist Mono (technical) / Inter (fallback)
- **Radii**: 4 / 6 / 8 / 12 / 16px (Vercel은 sharp 유지)
- **Shadows**: 거의 평면 (`0 4px 12px rgba(0,0,0,0.05)`)

---

## 4. 다이어그램 PNG 갱신

```bash
cd docs/iteration-reports/iter3-deck
python3 -m pip install pillow
python3 tools/generate_diagram_assets.py
```

**UC 직접 배치 (interactive editor)**

```
open tools/uc-editor.html
# UC oval과 actor stick figure를 드래그
# Export JSON 클릭 → uc-layout.json 다운로드
# 다운받은 파일을 tools/uc-layout.json 으로 저장
python3 tools/generate_diagram_assets.py
# → JSON 우선 사용. 파일 없으면 default 매트릭스 사용
```

생성되는 7개 PNG:

| 파일 | 슬라이드 |
|---|---|
| `reservationSystem-iter3.png` | 06 (Use Case) |
| `classDiagram-iter3.png` | 08 (Class — proper UML notation) |
| `seatHoldExpiry-iter3.png` | 14 (SC-01) |
| `paymentFailureAutoCancel-iter3.png` | 15 (SC-02) |
| `flightStatusPropagation-iter3.png` | 16 (SC-03) |
| `connectingSearch-iter3.png` | 17 (SC-05) |
| `mileagePayment-iter3.png` | 18 (SC-06) |

Demo 슬라이드(19, 20)의 콘솔 출력은 PNG가 아닌 **실제 텍스트 캡쳐** — 갱신은 다음 절차:

```bash
javac -sourcepath src -d bin $(find src -name "*.java" | grep -v "tools/")
java -cp bin com.koreanair.reservation.app.Iter3DemoRunner > docs/iteration-reports/iter3-deck/assets/iter3-demo-output.txt
```

출력 변경 후 `slides/19-demo-observer.html` / `slides/20-demo-extension.html`의 console block을 수동으로 업데이트.

AmaterasUML 원본 export로 교체하려면:

```bash
# Eclipse에서 4개 generator 실행
GenerateClassDiagramIter3.java       → src/classDiagram-iter3.cld
GenerateUseCaseDiagramIter3.java     → src/reservationSystem-iter3.ucd
GenerateStateDiagramsIter3.java      → src/seatState-iter3.acd, flightScheduleState-iter3.acd
GenerateSequenceDiagramsIter3.java   → src/seatHoldExpiry-iter3.sqd 외 4개
```

각 다이어그램 캔버스 우클릭 → Save as Image → PNG → `assets/diagrams/`에 동일 이름으로 저장.

---

## 5. 고화질 PDF 재생성

```bash
cd docs/iteration-reports/iter3-deck
node tools/export_iter3_vector_pdf.mjs
python3 -m pip install pypdf
python3 tools/merge_slide_pdfs.py
```

Chrome 경로가 자동 탐지되지 않으면:

```bash
CHROME_PATH="/Applications/Google Chrome.app/Contents/MacOS/Google Chrome" \
node tools/export_iter3_vector_pdf.mjs
python3 tools/merge_slide_pdfs.py
```

결과:

```
iter3-vector-slide-pdfs/       # 슬라이드별 벡터 PDF (24개)
iter3-final-hq.pdf             # 최종 24페이지 PDF
```

---

## 6. 폴더 구조

```
iter3-deck/
├── README.md                       이 문서
├── speaker-script-iter3-ko.md      24장 기준 발표 대본 + Q&A
├── index.html                      deck 진입점
├── slides/
│   ├── 01-cover.html               Cover
│   ├── 02-feature-list.html        전체 기능 표
│   ├── 03-extension-table.html     iter3 확장 6종 카드
│   ├── 04-rdp-table.html           Iter별 R/DP 매핑
│   ├── 05-roles.html               팀 A 역할 분담
│   ├── 06-usecase.html             Use Case Diagram
│   ├── 07-uc-scenarios.html        UC 시나리오 6종
│   ├── 08-classdiagram.html        Class Diagram (UML notation)
│   ├── 09-class-detail.html        중요 클래스·메서드 인덱스
│   ├── 10-observer-textbook-vs-team.html
│   ├── 11-observer-code.html       코드 전·후 diff
│   ├── 12-state-impact.html        Seat + FlightSchedule 영향
│   ├── 13-sequence-overview.html   Sequence / demo flow index (6개 흐름 안내)
│   ├── 14-seq-hold.html            SC-01 Seat Hold Expiry
│   ├── 15-seq-payfail.html         SC-02 Payment Failure
│   ├── 16-seq-flight.html          SC-03 Flight Propagation
│   ├── 17-seq-connecting.html      SC-05 Connecting Search
│   ├── 18-seq-mileage.html         SC-06 Mileage Payment
│   ├── 19-demo-observer.html       DEMO Observer 4종 (real console)
│   ├── 20-demo-extension.html      DEMO Mileage + Itinerary (real console)
│   ├── 21-pattern-state-code.html  State pattern 코드 맵
│   ├── 22-pattern-strategy-code.html Strategy pattern 코드 맵
│   ├── 23-pattern-observer-code-map.html Observer pattern 코드 맵
│   └── 21-thanks.html              감사합니다 + iter4 preview
├── assets/
│   ├── shared.css                  Vercel-light 디자인 토큰
│   └── diagrams/                   PNG (13개)
└── tools/
    ├── generate_diagram_assets.py  PNG 생성기
    ├── export_iter3_vector_pdf.mjs HTML → PDF
    └── merge_slide_pdfs.py         PDF 병합
```

---

## 7. 발표 직전 체크리스트

- [ ] `assets/diagrams/` PNG 13개 모두 표시되는지 확인
- [ ] 브라우저에서 `index.html` 풀스크린 동작 확인
- [ ] 키보드 ←/→/숫자/P 모두 작동
- [ ] `iter3-final-hq.pdf` 24페이지, 16:9 가로 방향 확인
- [ ] HDMI / 어댑터 / USB 백업
- [ ] `speaker-script-iter3-ko.md` 한 번 통독 + 1회 리허설
- [ ] SwingApp 실행 확인 (`java -cp bin com.koreanair.reservation.app.swing.SwingApp`)
- [ ] 콘솔 데모 백업 시연: `[HOLD-EXPIRY]`, `[AUTO-CANCEL]`, `[FLIGHT-CANCEL]` 로그 모두 확인
