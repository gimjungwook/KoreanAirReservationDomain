# Iter 2 Deck - 사용 가이드

> 5/12 OODP iter 2 발표용 슬라이드 (15장).
> 슬라이드는 한국어 본문 + 영어 코드/클래스명/패턴명. `iter2-final-hq.pdf`는 이 폴더의 HTML 슬라이드와 diagram assets에서 재생성할 수 있다.

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

상태는 `localStorage`에 저장 — 새로고침해도 같은 슬라이드 유지.

---

## 2. 현재 포함된 산출물

| 파일/폴더 | 역할 |
|---|---|
| `index.html` | 발표용 슬라이드 플레이어 |
| `slides/*.html` | 15장 슬라이드 원본. 문구/표/코드는 여기서 수정 |
| `assets/shared.css` | 공통 디자인 토큰 |
| `assets/diagrams/*.png` | Use Case, Class, State, Sequence, demo 백업 이미지 |
| `speaker-script-iter2-ko.md` | 15장 기준 한국어 발표 대본 + 예상 Q&A |
| `iter2-final-hq.pdf` | 제출/발표용 고화질 PDF |
| `tools/generate_diagram_assets.py` | placeholder 없이 발표 가능한 PNG diagram asset 재생성 |
| `tools/export_iter2_vector_pdf.mjs` | 각 HTML slide를 고화질 벡터 PDF로 출력 |
| `tools/merge_slide_pdfs.py` | 슬라이드별 PDF를 `iter2-final-hq.pdf`로 병합 |

---

## 3. 다이어그램 PNG 갱신

현재 repo에는 `assets/diagrams/` 아래 발표용 PNG가 이미 포함되어 있다. 빠르게 갱신하려면:

```bash
cd docs/iteration-reports/iter2-deck
python3 tools/generate_diagram_assets.py
```

필요 패키지:

```bash
python3 -m pip install pillow
```

AmaterasUML 원본 export로 교체하고 싶으면 아래 절차를 사용한다.

슬라이드의 placeholder 박스는 8개 PNG가 들어오면 자동 교체된다. 단계:

### 3.1 Eclipse - generator 8개 실행

`KoreanAirReservationDomain` 프로젝트 → `src/com/koreanair/reservation/tools/` 하위 8개 generator를 각각 우클릭 → **Run As → Java Application**:

```
GenerateClassDiagramIter1.java       → src/classDiagram-iter1.cld
GenerateClassDiagramIter2.java       → src/classDiagram-iter2.cld
GenerateUseCaseDiagramIter1.java     → src/reservationSystem-iter1.ucd
GenerateUseCaseDiagramIter2.java     → src/reservationSystem-iter2.ucd
GenerateStateDiagramsIter1.java      → src/reservationState-iter1.acd (+ flight, seat)
GenerateStateDiagramsIter2.java      → src/reservationState-iter2.acd (+ flight, seat)
GenerateSequenceDiagramsIter1.java   → src/bookFlight-iter1.sqd 외 2개
GenerateSequenceDiagramsIter2.java   → src/bookFlight-iter2.sqd 외 4개 (cancelRefund, lookupReservation 포함)
```

### 3.2 AmaterasUML - 빨강 페인팅 (수동)

각 .cld/.ucd/.acd/.sqd 파일 더블클릭으로 AmaterasUML 에디터 열기 → iter2 신규/변경 노드 선택 → **속성 패널에서 foreground/border 색을 빨강으로**.

**빨강 마크 대상**:

| 파일 | 빨강 처리 대상 |
|---|---|
| `classDiagram-iter2.cld` | RefundPolicy(interface), Full/Partial/NoRefundPolicy, RefundHandler, ReservationLookupService, Refund, RefundRequest, Ticket, AuthService(modified) |
| `reservationSystem-iter2.ucd` | View Booking, Cancel Booking, Issue e-Ticket, Refund Denied, Retrieve Booking by PNR (Guest), Review Refund Request |
| `reservationState-iter2.acd` | 신규 활성 5개 전이 (issueTicket / requestCancellation / confirmCancellation / requestRefund / processRefundDecision) — 화살표 색 빨강 |
| `cancelRefund-iter2.sqd` · `lookupReservation-iter2.sqd` | 전체 시퀀스가 iter2 신규라 별도 마크 불필요 (또는 RefundPolicy 위임 메시지만 빨강) |

### 3.3 Eclipse - PNG export

각 다이어그램 캔버스 우클릭 → **Save as Image → PNG**. 저장 경로:

```
docs/iteration-reports/iter2-deck/assets/diagrams/
```

### 3.4 PNG 파일명 규약 (반드시 일치)

slide HTML이 정확한 이름으로 `<img src="...">` 참조한다. 다음 8개 파일명 그대로:

| Slide | 필요 PNG 파일명 |
|---|---|
| 06 (Use Case diff) | `reservationSystem-iter1.png`, `reservationSystem-iter2.png` |
| 08 (Class diff) | `classDiagram-iter1.png`, `classDiagram-iter2.png` |
| 12 (State diff) | `reservationState-iter1.png`, `reservationState-iter2.png` |
| 13 (Sequence) | `cancelRefund-iter2.png`, `lookupReservation-iter2.png` |
| 14 (Demo backup) | `demo-01-lookup.png` ... `demo-06-strategy.png` |

### 3.5 자동 교체 동작

- PNG가 폴더에 있으면 → `<img>` 표시
- PNG가 없으면 → onError 트리거로 placeholder 박스 (점선 테두리 + 파일명 + "drop AmaterasUML PNG export here")

→ 발표 직전까지 PNG 없어도 슬라이드 레이아웃은 깨지지 않음.

---

## 4. 폴더 구조

```
iter2-deck/
├── README.md                      이 문서
├── brand-spec.md                  디자인 토큰·색·폰트 결정 기록
├── speaker-notes.md               한국어 구두 멘트 + Q&A
├── speaker-script-iter2-ko.md      15장 기준 발표 대본
├── index.html                     deck 진입점 (브라우저에서 더블클릭)
├── iter2-final-hq.pdf             최종 고화질 PDF
├── slides/
│   ├── 01-cover.html              표지 + changelog
│   └── 15-thanks.html             마무리
└── assets/
    ├── shared.css                 디자인 토큰
    └── diagrams/                  PNG diagram/demo assets
```

---

## 5. 고화질 PDF 재생성

macOS 기준:

```bash
cd docs/iteration-reports/iter2-deck
node tools/export_iter2_vector_pdf.mjs
python3 -m pip install pypdf
python3 tools/merge_slide_pdfs.py
```

Chrome 경로가 자동 탐지되지 않으면:

```bash
CHROME_PATH="/Applications/Google Chrome.app/Contents/MacOS/Google Chrome" \
node tools/export_iter2_vector_pdf.mjs
python3 tools/merge_slide_pdfs.py
```

결과:

```text
iter2-vector-slide-pdfs/       # 슬라이드별 벡터 PDF
iter2-final-hq.pdf             # 최종 15페이지 PDF
```

---

## 6. 수정 워크플로우

1. 슬라이드 문구나 표 수정: `slides/*.html`
2. 공통 색/글꼴/간격 수정: `assets/shared.css`
3. 다이어그램 수정: `assets/diagrams/*.png` 교체 또는 `tools/generate_diagram_assets.py` 수정 후 실행
4. 발표 대본 수정: `speaker-script-iter2-ko.md`
5. 최종 PDF 재생성: `tools/export_iter2_vector_pdf.mjs` → `tools/merge_slide_pdfs.py`

---

## 7. 발표 직전 체크리스트

- [ ] `assets/diagrams/` PNG가 모두 표시되는지 확인
- [ ] 브라우저에서 `index.html` 풀스크린 동작 확인
- [ ] 키보드 ←/→/숫자/P 모두 작동
- [ ] `iter2-final-hq.pdf` 15페이지, 16:9 가로 방향 확인
- [ ] HDMI / 어댑터 / USB 백업
- [ ] `speaker-script-iter2-ko.md` 한 번 통독 + 1회 리허설
