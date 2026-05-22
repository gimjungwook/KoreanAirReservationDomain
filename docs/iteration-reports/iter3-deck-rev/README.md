# Iter 3 Deck — Revision (Observer) · 사용 가이드

> Iteration 3 발표용 **개정판** 슬라이드 (22장). 한국어 구두 + 영어 코드/다이어그램/패턴명.
> 기존 `iter3-deck/`은 그대로 보존하고, 이 폴더(`iter3-deck-rev/`)가 인터랙티브 개정판이다.
> 빌드 모델: P2 라이브 덱(widgets.js 엔진 + presenter 노트 모드) + three.js Observer broadcast.

## 핵심 차별점 (vs `iter3-deck/`)

| 항목 | 기존 iter3-deck | 개정판 iter3-deck-rev |
|---|---|---|
| 형식 | 정적 iframe 24장 | **인터랙티브** 라이브 덱 22장 |
| Observer 설명 | 정적 코드/표 | **three.js 3D broadcast** (publish → N listener, instanceof 선택 처리) |
| 코드/다이어그램 | 분리된 슬라이드 | **다이어그램 ↔ 실제 코드 토글** (디펜스용) |
| 데모 | 콘솔 텍스트 캡처 | **타이프라이터 재생** (시나리오 탭) |
| 클래스 | PNG | **ECB 계층 필터** (Entity/Control/Boundary 강조) |
| 발표자 | 없음 | **presenter.html** 노트 모드 + 타이머 |
| 교수 요구 | — | 정합성 매트릭스 · include/extend · 변경분 빨강 · 예상질문 디펜스 맵 |

## 1. 슬라이드 열기

브라우저에 `index.html` 더블클릭 → 풀스크린. (file:// 호환 — three.js는 `vendor/three.min.js` 로컬 로드)

| 키 | 동작 |
|---|---|
| `→` `Space` `PgDown` | 다음 |
| `←` `PgUp` | 이전 |
| `Home` / `End` | 처음 / 마지막 |
| `V` | 발표자 뷰(presenter.html) 열기 |
| `S` | 노트 오버레이 |
| `R` | 현재 슬라이드 애니메이션 재생 |
| `F` | 전체화면 |
| `?` | 도움말 |

발표자 뷰와 청중 뷰는 `localStorage`로 동기화된다 (한 화면 발표자, 한 화면 청중).

## 2. 인터랙티브 슬라이드

- **05 Observer broadcast (live)** — 이벤트 버튼 선택 → `▶ publish`. 모든 listener에 broadcast, 매칭 1개만 점화(green), 나머지는 instanceof false로 무시.
- **07 Observer 코드** — `구조` ↔ `실제 코드` 토글 (EventPublisher.publish / AutoCancelListener.onEvent).
- **09 Class · ECB** — `전체 / Entity / Control / Boundary` 버튼으로 계층 강조.
- **12, 13 Sequence** — `Sequence(PNG)` ↔ `실제 코드` 토글.
- **14 라이브 데모** — 시나리오 탭 선택 → 실제 콘솔 출력 타이프라이터 재생.

## 3. 구조

```
iter3-deck-rev/
├── index.html               청중 플레이어
├── presenter.html           발표자 뷰 (노트 + 타이머 + next preview)
├── deck-manifest.js         슬라이드 순서 + 한국어 발표 대본(notes)
├── shared/tokens.css        디자인 토큰 (Observer 의미색 · ECB · 코드 테마)
├── assets/
│   ├── widgets.js           OODP.* 위젯 엔진 (observer3D / codeToggle / ecbFilter / demoConsole / cover)
│   ├── diagrams/*.png        AmaterasUML 생성 다이어그램 (iter3-deck에서 복사)
│   └── iter3-demo-output.txt 실제 데모 출력 원본
├── vendor/three.min.js      three.js r128 (file:// 로컬)
├── slides/*.html            22장 (한 파일 = 한 페이지)
├── export-pdf.mjs           HTML → 벡터 PDF 병합 (three.js 렌더 대기 2.6s)
├── renumber.mjs             manifest 순서대로 slide-counter 갱신
└── check-overflow.mjs       1920×1080 오버플로 탐지
```

## 4. PDF 내보내기 / 점검

```bash
cd docs/iteration-reports/iter3-deck-rev
npm install            # playwright + pdf-lib (최초 1회)
node renumber.mjs      # 슬라이드 카운터 동기화
node check-overflow.mjs
node export-pdf.mjs    # → _pdf/OODP-iter3-revision-Observer-deck.pdf
```

## 5. 데모 출력의 진위

14번 콘솔 출력은 mockup이 아니라 `Iter3DemoRunner` 실제 실행 캡처다 (`assets/iter3-demo-output.txt`).
```bash
java -cp bin com.koreanair.reservation.app.Iter3DemoRunner
```
같은 명령을 돌리면 같은 출력이 나온다.

---

A팀 · 김정욱 · 이재호 · 김경동 · ECE312 객체지향 설계패턴 · 2026년 1학기
