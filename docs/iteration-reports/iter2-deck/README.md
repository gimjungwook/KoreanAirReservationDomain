# Iter 2 Deck — 사용 가이드

> 5/12 OODP iter 2 발표용 슬라이드 (8장).
> 슬라이드는 한국어 본문 + 영어 코드/클래스명/패턴명. Stripe / Linear 톤.

---

## 1. 슬라이드 열기

브라우저에 `index.html` 더블클릭 → 풀스크린 모드.

| 키 | 동작 |
|---|---|
| `→` `Space` `PgDown` | 다음 슬라이드 |
| `←` `PgUp` | 이전 슬라이드 |
| `Home` / `End` | 처음 / 마지막 |
| `1` ~ `8` | 해당 번호로 점프 |
| `P` | 인쇄 (PDF 저장) |

상태는 `localStorage`에 저장 — 새로고침해도 같은 슬라이드 유지.

---

## 2. 다이어그램 PNG 채우기 (사용자 작업 필요)

슬라이드의 placeholder 박스는 8개 PNG가 들어오면 자동 교체된다. 단계:

### 2.1 Eclipse — generator 8개 실행

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

### 2.2 AmaterasUML — 빨강 페인팅 (수동)

각 .cld/.ucd/.acd/.sqd 파일 더블클릭으로 AmaterasUML 에디터 열기 → iter2 신규/변경 노드 선택 → **속성 패널에서 foreground/border 색을 빨강으로**.

**빨강 마크 대상**:

| 파일 | 빨강 처리 대상 |
|---|---|
| `classDiagram-iter2.cld` | RefundPolicy(interface), Full/Partial/NoRefundPolicy, RefundHandler, ReservationLookupService, Refund, RefundRequest, Ticket, AuthService(modified) |
| `reservationSystem-iter2.ucd` | View Booking, Cancel Booking, Issue e-Ticket, Refund Denied, Retrieve Booking by PNR (Guest), Review Refund Request |
| `reservationState-iter2.acd` | 신규 활성 5개 전이 (issueTicket / requestCancellation / confirmCancellation / requestRefund / processRefundDecision) — 화살표 색 빨강 |
| `cancelRefund-iter2.sqd` · `lookupReservation-iter2.sqd` | 전체 시퀀스가 iter2 신규라 별도 마크 불필요 (또는 RefundPolicy 위임 메시지만 빨강) |

### 2.3 Eclipse — PNG export

각 다이어그램 캔버스 우클릭 → **Save as Image → PNG**. 저장 경로:

```
/Users/gimjungwook/Projects/eclipse/KoreanAirReservationDomain/docs/iteration-reports/iter2-deck/assets/diagrams/
```

### 2.4 PNG 파일명 규약 (반드시 일치)

slide HTML이 정확한 이름으로 `<img src="...">` 참조한다. 다음 8개 파일명 그대로:

| Slide | 필요 PNG 파일명 |
|---|---|
| 04 (state diff) | `reservationState-iter1.png` · `reservationState-iter2.png` |
| 05 (class diff) | `classDiagram-iter1.png` · `classDiagram-iter2.png` |
| 06 (cancel + refund) | `cancelRefund-iter2.png` |
| 07 (guest lookup) | `lookupReservation-iter2.png` |

(추가로 UC diff 슬라이드 만들면 `reservationSystem-iter1.png` · `reservationSystem-iter2.png`)

### 2.5 자동 교체 동작

- PNG가 폴더에 있으면 → `<img>` 표시
- PNG가 없으면 → onError 트리거로 placeholder 박스 (점선 테두리 + 파일명 + "drop AmaterasUML PNG export here")

→ 발표 직전까지 PNG 없어도 슬라이드 레이아웃은 깨지지 않음.

---

## 3. 폴더 구조

```
iter2-deck/
├── README.md                      이 문서
├── brand-spec.md                  디자인 토큰·색·폰트 결정 기록
├── speaker-notes.md               한국어 구두 멘트 + Q&A
├── index.html                     deck 진입점 (브라우저에서 더블클릭)
├── slides/
│   ├── 01-cover.html              표지 + changelog
│   ├── 02-roadmap.html            5 패턴 × 4 iteration 매트릭스
│   ├── 03-strategy-pattern.html   Strategy 도입 근거 (Before/After)
│   ├── 04-state-transitions.html  State diagram diff
│   ├── 05-class-diagram.html      Class diagram diff (+9 클래스)
│   ├── 06-cancel-refund-flow.html Sequence cancelRefund — Strategy 위임
│   ├── 07-guest-lookup.html       Sequence lookupReservation — 3-factor verify
│   └── 08-next.html               wrap + iter 3 미리보기
└── assets/
    ├── shared.css                 디자인 토큰 (Stripe/Linear)
    └── diagrams/                  ← 여기에 PNG 8장 떨구기
```

---

## 4. PDF / PPTX export (선택)

### PDF (권장 — 시각 충실도 보존)
```bash
cd /tmp
node /Users/gimjungwook/Vault/.claude/skills/huashu-design/scripts/export_deck_pdf.mjs \
  --slides /Users/gimjungwook/Projects/eclipse/KoreanAirReservationDomain/docs/iteration-reports/iter2-deck/slides \
  --out /Users/gimjungwook/Projects/eclipse/KoreanAirReservationDomain/docs/iteration-reports/iter2-deck/iter2.pdf
```

### PPTX (편집 가능 텍스트박스)
주의: 4가지 hard constraint (텍스트 모두 `<p>/<h*>` 안, CSS gradient 금지, `<p>`에 background/border 금지, div에 background-image 금지)를 만족해야 함. 현재 슬라이드는 시각 자유도 우선이라 PPTX export는 지원 안 함 — 편집 필요하면 PDF에서 작업하거나 슬라이드 HTML 직접 수정 권장.

---

## 5. 발표 직전 체크리스트

- [ ] Eclipse 8 generator 실행 → 16개 산출 파일 생성
- [ ] AmaterasUML에서 빨강 페인팅 (4개 .cld/.ucd/.acd/.sqd)
- [ ] PNG export → `assets/diagrams/` 에 8장
- [ ] 브라우저에서 `index.html` 풀스크린 동작 확인
- [ ] 키보드 ←/→/숫자/P 모두 작동
- [ ] 8장 모두 placeholder 없이 PNG로 교체됐는지 시각 확인
- [ ] HDMI / 어댑터 / USB 백업
- [ ] `speaker-notes.md` 한 번 통독 + 1회 리허설
