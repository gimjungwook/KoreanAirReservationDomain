# Iteration 4 작업 요구사항 정리

## 0. 작업 대상, 자료 위치, 전역 서식 규칙

- 작업 대상 Google Workspace Docs 문서 ID: `1Zd53XHVxSU6cINaN2GWbcmjpsSHpXIDW-PRpDFFVeq8`
- 목표는 위 GWS 문서를 완성하는 것이다.
- 본 보고서는 특정 iteration 하나만 다루는 문서가 아니라, 모든 iteration의 정보가 누적되는 단일 누적 보고서(cumulative report)이다.
- iteration이 진행될 때마다 새 문서를 만들지 않고, 기존 보고서에 정보를 추가하거나 수정하여 항상 전체(Full) 정보를 담는다. 즉 해당 iteration만 문서화하는 것이 아니라 기존 iteration 내용 위에 누적한다.
- 따라서 보고서에는 Iteration 1부터 현재 단계(Iteration 4)까지의 모든 기능, 디자인 패턴, 리팩토링 내용이 빠짐없이 포함되어야 하며, 이번 iteration에서 새로 추가되거나 변경된 부분만 빨간색으로 구분 표시한다(이전 iteration 누적분은 검은색 유지).
- 단, 문서 본문에 "누적 보고서"나 "cumulative report" 같은 표현을 명시적으로 적지 않는다. 누적은 내용 구성으로 자연스럽게 드러나도록 하고, 그것을 설명하는 라벨이나 문장은 넣지 않는다.
- 보고서는 기본적으로 영어로 기술하고, 필요에 따라 한국어로 보충한다(아래 0번 언어 규칙과 3개 탭 규칙을 따른다).
- 실제 GWS 문서는 Google Docs의 tab 기능을 사용하여 동일한 보고서의 세 가지 버전을 별도의 tab으로 구분해 만든다.
  - Tab 1 (전체 한국어): 제목, section heading, 표 제목/컬럼명/셀, 본문, 캡션, 코드 설명 등 모든 텍스트를 한국어로 작성한다. (다이어그램 이미지 내부 텍스트와 코드 식별자 자체는 제외)
  - Tab 2 (전체 영어): 위 모든 텍스트를 영어로 작성한다.
  - Tab 3 (혼합): 표/다이어그램 내부/코드 블록은 영어, 일반 설명 문단과 캡션은 한국어로 작성한다(아래 0번 언어 규칙).
- 세 tab은 모두 동일한 문서 구조(순서), 동일한 표/다이어그램/코드 블록, 동일한 Iteration 4 빨강 글자와 빨강 사각 테두리, 동일한 서식 규칙(Times New Roman 12pt, title/heading만 bold, 본문 justify, double line spacing, 본문 첫 줄 들여쓰기)을 따른다.
- 코드 블록 주석 언어는 각 tab의 언어를 따른다. 전체 한국어 tab은 한국어 주석, 전체 영어 tab은 영어 주석, 혼합 tab은 한국어 주석을 사용한다.
- 다이어그램 이미지는 세 tab에서 동일한 이미지를 사용한다(다이어그램 내부는 코드 식별자 기준 영어 유지).
- 작업 자료는 로컬 기준 `~/Downloads/OODP` 폴더에 있다.
- `~/Downloads/OODP` 안의 코드, 보고서 자료, 교과서 사진, PlantUML 파일, 렌더링된 다이어그램, 비교 이미지 등을 기준 자료로 사용한다.
- 최종 산출물은 단순히 기능이 동작하는 수준이 아니라, 교수님 교과서의 패턴 구조, 다이어그램 배치, 보고서 형식을 최대한 정확히 따라야 한다.
- 실제 작업 대상인 GWS 보고서는 언어 사용 범위를 구분해서 작성한다.
- 실제 GWS 보고서의 표와 다이어그램 내부 텍스트는 반드시 영어로 작성한다.
- 영어로 작성해야 하는 항목은 표 제목, 표 컬럼명, 표 내부 셀 텍스트, 다이어그램 내부 클래스명, 메서드명, 어트리뷰트명, 관계명, 코드 블록 제목, 코드 내부 내용이다.
- 실제 GWS 보고서의 일반 설명 문단은 한국어로 최대한 자세하게 작성한다.
- 한국어로 작성해야 하는 항목은 디자인 패턴 설명, 필요성 설명, 구현 방식 설명, 적용 이득 설명, 리팩토링 설명, SOLID 관점 설명, 교과서 사진과 구현 다이어그램 비교 설명, Iteration 4 변경사항 설명이다.
- 수정 불가능한 교과서 원본 사진이나 이미지 안에 포함된 언어는 예외로 둔다.
- 실제 GWS 보고서의 모든 폰트는 Times New Roman, 12 pt로 통일한다.
- 실제 GWS 보고서에서 bold 처리는 title과 section heading에만 사용한다.
- 실제 GWS 보고서의 각 일반 문단은 시작 부분에 indentation을 적용한다.
- paragraph indentation은 본문 paragraph에 적용하며, title, section heading, table text, code block, image caption에는 적용하지 않는다.
- 각 문단의 첫 줄 indentation은 문서 전체에서 일관되게 유지한다.
- Team Contribution Table은 Overview 또는 Introduction 직후, 본문 시작 부분에 배치한다.
- Team Contribution Table은 기능/iteration/design pattern 내용을 설명하는 세 개의 필수 본문 표와 구분되는 front-matter 성격의 행정 표로 처리한다.
- 즉, 교수님이 요구한 본문 핵심 표 3개는 그대로 유지하되, 팀원 분담표는 개요 다음에 별도로 배치한다.
- 모든 표는 페이지 밖으로 overflow가 발생하면 안 된다.
- 표의 전체 너비는 문서 본문 영역 안에 들어와야 한다.
- 표는 불필요하게 페이지 전체 폭을 강제로 차지하지 말고, content에 맞춰 적절한 width로 조정한다.
- 각 column width는 해당 column의 실제 내용 길이에 맞춰 조정한다.
- 긴 문장은 셀 안에서 줄바꿈되도록 처리하고, 표가 좌우로 밀려 페이지를 벗어나지 않게 한다.
- 본문, 표 내부 텍스트, 캡션, 다이어그램 설명, 코드 설명에는 bold를 사용하지 않는다.
- 일반 본문 paragraph는 첫 줄 indentation을 적용한다.
- 표 내부 텍스트, 코드 블록, 제목, 섹션 heading, 이미지 캡션에는 paragraph indentation을 적용하지 않는다.
- 코드 예시는 일반 본문처럼 작성하지 않고, 2 row × 1 column 구조의 코드 블록 형태로 작성한다.
- 코드 블록의 첫 번째 row는 코드 제목 header로 사용한다.
- 코드 블록의 두 번째 row는 실제 code area로 사용한다.
- 코드 area는 실제 IDE dark mode처럼 어두운 배경을 적용한다.
- 코드 area에는 가능하면 syntax highlighting을 적용한다.
- 코드 블록도 페이지 밖으로 overflow되면 안 되며, 긴 코드는 줄바꿈 또는 필요한 부분 중심의 excerpt로 정리한다.
- 코드 블록 내부에는 각 줄 또는 주요 블록이 어떤 역할을 하는지 가능한 한 자세한 설명 주석을 단다.
- 코드의 식별자(클래스명, 메서드명, 변수명)와 코드 자체는 영어로 작성하되, 코드 주석은 한국어로 작성하는 것을 허용한다.
- 주석은 단순 반복 설명이 아니라, 해당 코드가 디자인 패턴에서 수행하는 역할(예: factoryMethod 호출, Context의 전략 위임, Observer pull 등)과 의도를 설명한다.
- 한국어 주석은 코드 가독성 규칙(monospace, dark mode, syntax highlighting)을 그대로 따르며, 주석은 주석 색상으로 표시한다.
- Iteration 4 변경사항은 기존 문서에 이미 적용된 방식처럼 빨간색 글자색으로 표시한다.
- Iteration 4의 빨간색 텍스트도 Times New Roman, 12 pt를 유지한다.
- 빨간색은 Iteration 4 변경사항 표시 목적으로만 사용한다.

---

## 1. 교과서 패턴 매칭 기준

- 구현은 교과서 패턴과 이름 기준이 아니라 역할 기준으로 대응되어야 한다.
- 전체 구조는 교과서 패턴과 1:1로 매칭되어야 한다.
- 배치도 교과서 패턴과 1:1로 매칭되어야 한다.
- 클래스 구조는 교과서 패턴의 participant role과 대응되어야 한다.
- 클래스명, 메서드명, 어트리뷰트명이 교과서와 반드시 완전히 동일할 필요는 없다.
- 다만 교과서 패턴에서 요구하는 클래스 역할, 메서드 역할, 어트리뷰트 역할은 반드시 구현에 존재해야 한다.
- 예를 들어 교과서의 `ConcreteStrategy`와 이름이 다르더라도, 실제로 구체 전략 객체의 책임을 수행하고 같은 호출 구조에 참여한다면 대응되는 것으로 볼 수 있다.
- 교과서 패턴에 필요한 핵심 메서드와 핵심 어트리뷰트가 누락되면 안 된다.
- 교과서 패턴의 필수 역할이 충족된 뒤에는 프로젝트 구현에 필요한 추가 메서드나 추가 어트리뷰트가 존재해도 된다.
- 추가 요소는 패턴 의도를 흐리거나, 책임을 과도하게 만들거나, SOLID를 위반하지 않는 경우에만 허용한다.
- 최적화보다 교과서와의 구조적 일치를 우선한다.
- 코드나 설계를 더 효율적으로 만들 수 있더라도, 교수님은 교과서와 정확히 대응되는 구조를 선호한다.
- 성능 최적화, 구조 단순화, 코드 압축보다 패턴 재현성을 우선한다.
- 디자인 패턴 구조를 임의로 변형하지 않는다.
- 교수님 교과서에 제시된 패턴의 의도와 구조를 유지한다.
- 현재 코드 구조가 교과서 패턴을 제대로 표현하기 어렵다면 리팩토링한다.
- 리팩토링의 목적은 임의 개선이 아니라 교과서 패턴과의 구조적 일치이다.
- 책임 분리가 부족하거나 역할이 과도한 클래스는 분리한다.
- 리팩토링 후에도 프로그램은 정상 작동해야 한다.

---

## 2. 교과서 사진 및 다이어그램 요구사항

- 교과서 사진은 문서에서 똑바로 보여야 한다.
- 사진이 회전되어 있거나 기울어져 있거나 뒤집혀 있으면 올바른 방향으로 회전한 뒤 삽입한다.
- 사진 방향은 실제 교과서 원본과 일치해야 한다.
- 보고서에 삽입된 다이어그램 및 캡처 이미지도 방향을 확인한다.
- 다이어그램은 PlantUML로 작성하되, 최종 렌더링 결과는 Amateras UML 스타일을 엄격하게 따라야 한다.
- 다이어그램 소스 형식은 PlantUML을 사용한다.
- PlantUML 기본 스타일을 그대로 사용하면 안 된다.
- PlantUML을 개조하여 Amateras Modeler / Amateras UML에서 생성된 것처럼 보이게 만들어야 한다.
- 스타일 기준 참고 저장소: `https://github.com/takezoe/amateras-modeler.git`
- 최종 GWS 보고서의 모든 UML 다이어그램은 Amateras UML 출력처럼 보여야 한다.
- 클래스 박스, compartment 구분, 선 스타일, 화살표 모양, 관계선 표현, 폰트 크기, 여백, 정렬, 배치가 Amateras UML 스타일과 다르면 안 된다.
- PlantUML을 사용하더라도 최종 이미지에서 PlantUML 기본 스타일 특유의 둥근 박스, 기본 색상, 그림자, 기본 여백, 기본 화살표, 기본 폰트 느낌이 남아 있으면 수정 대상이다.
- draw.io, Mermaid, StarUML, Visual Paradigm 등 다른 UML 도구 특유의 스타일이 남아 있으면 안 된다.
- 다이어그램 내용이 맞더라도 시각 스타일이 Amateras UML과 다르면 완료로 보지 않는다.
- 최종 보고서에는 PlantUML 소스를 Amateras UML 스타일로 커스터마이징하여 렌더링한 이미지 또는 그와 동일하게 보이는 다이어그램만 삽입한다.
- 각 구현 다이어그램은 실제 교과서 사진과 기본적으로 좌우 병렬로 juxtapose하여 비교한다.
- 다만 교과서 사진과 구현 다이어그램이 가로로 길어서 페이지 overflow가 발생하거나 가독성이 떨어지는 경우에는 위아래 배치를 허용한다.
- 위아래 배치를 사용할 때는 위쪽에 교과서 사진, 아래쪽에 구현 다이어그램을 배치한다.
- 배치 방식은 좌우든 상하든 교과서 사진과 구현 다이어그램의 1:1 대응 관계를 명확히 보여주어야 한다.
- 교과서 사진과 구현 다이어그램 자료는 `~/Downloads/OODP`에서 가져온다.
- 각 디자인 패턴 설명 섹션에서 왼쪽에는 실제 교과서 사진을 배치한다.
- 오른쪽에는 해당 교과서 사진을 기준으로 작성한 구현 다이어그램을 배치한다.
- 오른쪽 구현 다이어그램은 PlantUML 기반이며 Amateras UML 스타일로 렌더링되어야 한다.
- 좌우 비교 또는 상하 비교는 구조, 역할, 관계, 배치가 1:1로 대응되는지 바로 확인할 수 있게 구성한다.
- 교과서 사진과 구현 다이어그램은 같은 크기 또는 비교 가능한 비율로 배치한다.
- 좌우 배치에서는 위아래 기준선과 주요 클래스 위치를 최대한 맞춘다.
- 상하 배치에서는 왼쪽 기준선, 주요 클래스 순서, 관계 방향을 최대한 비교 가능하게 맞춘다.
- 어떤 배치를 사용하든 이미지가 페이지 밖으로 overflow되면 안 된다.
- 오른쪽 구현 다이어그램이 Amateras UML처럼 보이지 않으면 PlantUML 스타일을 수정하고 다시 렌더링한다.
- 좌우 비교 영역이 Iteration 4에서 새로 추가되었거나 변경되었다면 해당 비교 영역 바깥을 빨간색 사각 테두리 또는 클라우드 마킹으로 감싼다.
- 교과서 사진 자체의 border나 UML 다이어그램 자체의 border 색을 바꾸면 안 된다.
- 보고서의 다이어그램, 클래스 구조, 패턴 설명은 제공된 교과서 사진과 대응되어야 한다.
- 사진 속 역할, 관계, 화살표 방향, 클래스 배치가 코드와 보고서에서 맞아야 한다.
- 클래스명, 메서드명, 필드명이 사진과 완전히 동일할 필요는 없지만, 대응되는 역할의 요소가 코드와 보고서에 반드시 존재해야 한다.
- 관계선, 상속, 구현, 의존 관계는 사진과 맞아야 한다.
- 사진에 없는 추가 메서드나 어트리뷰트는 패턴을 왜곡하거나 책임을 과도하게 만들지 않는 경우에만 허용한다.
- 구현 다이어그램의 각 클래스 박스에는 그 클래스의 실제 어트리뷰트와 메서드를 모두 표시한다.
- 교과서 그림이 최소한의 멤버만 보여 주더라도, 오른쪽(또는 아래쪽) 구현 다이어그램은 실제 코드에 존재하는 필드와 메서드를 빠짐없이 드러내어 독자가 실제 구현을 확인할 수 있게 한다.
- 식별용 메서드(예: name())만 표시하고 구체 클래스가 실제로 구현(override)한 핵심 연산을 숨기지 않는다.
- 어트리뷰트와 메서드 표기는 영어로 하고 가시성 표시(+/-/#)와 타입을 포함하며 Amateras UML 스타일을 유지한다.
- 교과서 사진과 구현 다이어그램은 항상 좌우 배치(왼쪽 사진, 오른쪽 다이어그램)로만 배치하며, 상하 배치는 사용하지 않는다.
- 멤버가 많아 다이어그램이 커지더라도 좌우 배치를 유지하고, 멤버를 임의로 생략하지 않으며, 다이어그램은 충분한 해상도로 렌더링하여 좌우 비교가 가능하게 한다.

---

## 3. Iteration 4 변경사항 표시 규칙

- 현재 작업 단계는 Iteration 4이다.
- 문서와 코드에서 Iteration 4에 새로 적용된 변경사항을 명확히 구분해야 한다.
- 이전 iteration에서 이미 구현된 내용과 Iteration 4에서 추가·수정된 내용을 혼동하면 안 된다.
- Iteration 4 변경사항은 모두 빨간색으로 표시한다.
- 실제 GWS 보고서에서 Iteration 4 관련 섹션은 빨간색 글자색을 사용한다.
- Iteration 4에서 새로 추가된 설명, 표 항목, 다이어그램 설명, 코드 설명은 빨간색 글자색을 사용한다.
- Iteration 4에서 설계 요소가 변경되었다면 해당 변경 설명도 빨간색으로 표시한다.
- Iteration 4 변경사항은 장표나 다이어그램 영역뿐만 아니라 문서 본문 텍스트에서도 빨간색으로 표시해야 한다.
- Iteration 4 관련 제목, 본문 문단, 표 내부 텍스트, 캡션, 코드 설명, 다이어그램 설명 텍스트는 모두 빨간색 글자색을 적용한다.
- 빨간색 박스만 그리고 내부 텍스트를 검은색으로 두면 안 된다.
- 빨간색 사각 테두리 또는 클라우드 마킹은 변경 영역 강조용이며, 빨간색 글자색 표시도 별도로 반드시 적용한다.
- 변경 영역은 빨간색 사각 테두리로 감싼다.
- 클라우드 마킹 또는 레드라이닝 방식도 사용할 수 있다.
- 변경 영역은 한눈에 식별 가능해야 한다.
- 빨간색 글자색만으로 끝내지 말고, 변경 공간 자체도 시각적으로 강조한다.
- UML 다이어그램 자체의 border 색은 바꾸면 안 된다.
- 이 요구사항은 다이어그램 내부 border를 빨간색으로 바꾸라는 뜻이 아니다.
- 클래스 다이어그램, 시퀀스 다이어그램, 패턴 다이어그램의 원래 border 색상은 유지한다.
- 특히 Amateras UML 스타일의 클래스 박스 border, compartment line, association line, inheritance arrow, dependency arrow 색상은 임의로 바꾸지 않는다.
- 대신 다이어그램 바깥쪽 또는 변경 영역 주변에 별도의 빨간색 사각 테두리나 클라우드 마킹을 추가한다.
- 다이어그램 내부 스타일은 Amateras UML 기준으로 유지하고, 변경 영역 표시만 별도로 한다.

---

## 4. 팀원 분담표와 필수 본문 표 3개 배치 순서

- 실제 GWS 보고서에는 Team Contribution Table도 작성해야 한다.
- Team Contribution Table은 Overview 또는 Introduction 직후, 본문 시작 전에 배치한다.
- 권장 문서 순서는 다음과 같다.
  - Title
  - Overview / Introduction
  - Team Contribution Table
  - Feature Table
  - Iteration Progress Table
  - Extension Feature Table
  - `DP#1` detailed explanation
  - `DP#2` detailed explanation
  - `DP#3` detailed explanation
  - 이후 적용한 디자인 패턴 수만큼 `DP#n` 섹션 추가

- Team Contribution Table은 프로젝트 행정 정보에 가까운 표로 보고, 교수님이 요구한 기능/iteration/extension 관련 핵심 표 3개와 구분한다.
- 교수님이 “표 3개”라고 강조한 부분은 Feature Table, Iteration Progress Table, Extension Feature Table 세 개를 의미하는 것으로 관리한다.
- 따라서 본문 핵심 표는 정확히 세 개로 유지한다.
- Team Contribution Table을 넣더라도 Feature Table, Iteration Progress Table, Extension Feature Table의 존재와 순서는 유지한다.
- 본문 핵심 표 3개는 다음 순서로 배치한다.
  - Feature Table
  - Iteration Progress Table
  - Extension Feature Table

- 세 본문 핵심 표 다음에 디자인 패턴별 상세 설명 섹션을 작성한다.
- 각 표 안에서 이번 Iteration 4에서 무엇이 추가·변경·리팩토링되었는지 설명한다.
- 리팩토링이 수행된 항목은 표에서 반드시 refactoring이 있었다고 명시한다.
- 기존 기능을 수정하거나 구조를 바꾼 경우, 단순 변경인지 refactoring인지 구분해서 표기한다.
- 초안 단계에서는 실제 장표 번호와 보고서 페이지 번호를 placeholder로 둘 수 있다(내용·요약·설명은 먼저 완성).
- **단, 최종 단계에서는 비워둔 페이지/섹션 참조를 빠짐없이 실제 값으로 채운다. placeholder(`Report p.__` 등)를 그대로 제출하지 않는다.**
- **이후 내용을 추가·삭제·이동해 페이지가 밀리면 표의 페이지/섹션 참조도 같이 갱신한다. 표의 페이지 번호가 실제 위치와 어긋난 채로 두지 않는다(편집 시 페이지 참조 동기화 필수).**
- Iteration 4 관련 표 텍스트는 빨간색 글자색으로 표시한다.
- Iteration 4 관련 표 영역은 빨간색 사각 테두리 또는 클라우드 마킹으로 감싼다.
- 본문 핵심 표는 정확히 세 종류만 포함한다.
- 본문 핵심 표로 허용되는 표는 Feature Table, Iteration Progress Table, Extension Feature Table 세 개뿐이다.
- Team Contribution Table은 예외적으로 Overview / Introduction 직후에 배치하는 front-matter 표로 처리한다.
- Team Contribution Table을 본문 핵심 표 3개 중 하나로 세지 않는다.
- Team Contribution Table 외에 네 번째 본문 표, 보조 표, 요약 표, 비교 표, 체크리스트 표 등은 넣지 않는다.
- 다이어그램 비교는 표로 만들지 않는다.
- 다이어그램 비교는 기본적으로 왼쪽 교과서 사진, 오른쪽 구현 다이어그램의 좌우 이미지 배치로 처리한다.
- 단, 두 이미지가 가로로 길어 페이지 overflow가 생기거나 너무 작아져 가독성이 떨어지는 경우에는 위쪽 교과서 사진, 아래쪽 구현 다이어그램의 상하 배치로 처리한다.
- 교수님이 요구한 세 개의 본문 핵심 표가 누락되면 안 된다.
- 모든 표는 페이지 경계 밖으로 넘어가면 안 된다.
- 표 너비는 content에 맞춰 조정하되, 최종적으로 페이지 본문 영역 안에 완전히 들어와야 한다.
- column width는 내용 길이에 맞춰 배분한다.
- 긴 셀 내용은 표를 넓히지 말고 셀 내부 줄바꿈으로 처리한다.
- 표가 다음 페이지로 넘어갈 정도로 길어지는 경우에는 행 단위로 자연스럽게 page break가 일어나야 하며, 한 행이나 표 자체가 페이지 밖으로 잘리면 안 된다.
- 표 제목과 표 본문이 분리되어 의미가 끊기지 않도록 배치한다.

### 4-1. Team Contribution Table

- Team Contribution Table은 Overview 또는 Introduction 직후에 배치한다.
- 이 표는 팀원별 역할 분담을 보여주기 위한 표이다.
- 권장 컬럼은 다음과 같다.
  - Member
  - Responsibility
  - Main Contribution
  - Related Iteration
  - Related Section / Page

- `Related Section / Page`는 초안에서만 `Report p.__ / Slide __` placeholder를 허용하고, **최종본에서는 각 행이 가리키는 내용이 기술된 실제 보고서 섹션/페이지로 채운다.** Iteration Progress Table의 "Described Page" 컬럼도 동일하게 채운다.
- 페이지나 장표 번호만 잠시 비워두더라도, 어떤 섹션과 어떤 기여인지 내용은 반드시 작성한다. 편집으로 페이지가 밀리면 이 참조도 동기화한다.
- 실제 GWS 보고서에서는 컬럼명을 영어로 작성한다.
- 각 팀원이 담당한 기능, 코드, 보고서, 다이어그램, 테스트, 리팩토링, 디자인 패턴 적용 내용을 구체적으로 적는다.
- Iteration 4에서 새로 수행한 담당 내용은 빨간색 글자색으로 표시한다.
- Iteration 4 관련 팀원 분담 내용이 있는 행 또는 셀은 빨간색 사각 테두리나 클라우드 마킹으로 강조할 수 있다.
- Team Contribution Table의 텍스트도 Times New Roman, 12 pt를 적용한다.
- Team Contribution Table의 일반 셀 텍스트는 bold 처리하지 않는다.
- 표 제목만 bold 처리한다.
- Team Contribution Table도 페이지 밖으로 overflow가 발생하면 안 된다.
- Team Contribution Table의 column width는 Member, Responsibility, Main Contribution, Related Iteration, Related Section / Page의 실제 content 길이에 맞춰 조정한다.
- Main Contribution처럼 내용이 길어질 수 있는 column은 셀 내부 줄바꿈을 사용한다.
- 이 표는 본문 핵심 표 3개와 별도로 관리한다.

### 4-2. Feature Table

- Feature Table은 필수이다.
- Feature Table 컬럼은 다음과 같다.
  - Feature
  - Sub-feature
  - Implementation Iteration

- 프로젝트의 전체 기능을 큰 기능 단위로 정리한다.
- 각 Sub-feature는 구체적으로 작성한다.
- 각 Sub-feature가 어느 iteration에서 구현되었는지 명확히 표시한다.
- Iteration 4에서 구현된 항목은 빨간색으로 표시한다.
- 기존 기능이 Iteration 4에서 리팩토링되었거나 수정되었다면 그 사실도 반영한다.
- 리팩토링된 기능은 `Refactored` 또는 `Refactoring applied`처럼 표 안에서 명시한다.
- 단순 기능 추가, 기능 변경, 리팩토링을 구분해서 작성한다.
- 기능 이름은 코드와 보고서에서 사용하는 명칭과 일관되어야 한다.
- Sub-feature는 추상적으로 쓰지 말고 실제 구현 단위와 연결되도록 작성한다.
- Implementation Iteration은 `Iteration 1`, `Iteration 2`, `Iteration 3`, `Iteration 4`처럼 명확히 적는다.
- Iteration 4에서 변경된 기능은 해당 행 또는 관련 셀에서 변경 내용을 설명한다.
- 기능 구현에 디자인 패턴이 직접 연결되면 해당 `DP#n` 표기를 함께 넣는다.
- 예: `Iteration 4 / DP#2`, `Iteration 3 / DP#1`, `Iteration 4 / Refactoring + DP#3`
- 해당 기능이 리팩토링되었다면 `Implementation Iteration` 또는 관련 설명 셀에 반드시 `Refactored`를 포함한다.
- Feature Table은 페이지 본문 영역 밖으로 넘치지 않도록 작성한다.
- Feature, Sub-feature, Implementation Iteration column의 너비는 content에 맞춰 조정한다.
- Sub-feature 내용이 길면 column을 무리하게 넓히지 말고 셀 내부에서 줄바꿈한다.

### 4-3. Iteration Progress Table

- Iteration Progress Table은 필수이다.
- 교수님이 이 표를 두 번 강조했으므로 특히 정확하게 작성한다.
- Iteration Progress Table 컬럼은 다음과 같다.
  - Iteration
  - Sub-iteration
  - Refactoring / Design Pattern
  - Applied Location
  - Summary
  - Described Page

- `Iteration` 컬럼에는 `Iteration 1`, `Iteration 2`, `Iteration 3`, `Iteration 4`처럼 전체 iteration 번호를 적는다.
- `Sub-iteration` 컬럼에는 `4-1`, `4-2`, `4-3`처럼 세부 단계를 적는다.
- `Refactoring / Design Pattern` 컬럼에는 해당 단계에서 적용한 리팩토링 또는 디자인 패턴을 적는다.
- 디자인 패턴은 반드시 `DP#n` 형식으로 번호를 붙인다.
- 예: `DP#1 Strategy Pattern`, `DP#2 Factory Method Pattern`, `DP#3 Observer Pattern`, `DP#4 Adapter Pattern`
- 리팩토링은 디자인 패턴과 구분해서 적는다.
- 예: `Extract Class + DP#1 Strategy Pattern`, `Move Method + DP#2 Factory Method Pattern`
- State Pattern은 교수님이 이 보고서에서 디자인 패턴으로 인정하지 않는다고 했으므로 `DP#n` 번호를 부여하지 않는다.
- 상태 전환 로직이 있으면 numbered design pattern이 아니라 refactoring 또는 state-management implementation으로 설명한다.
- `Applied Location` 컬럼에는 실제로 적용된 클래스, 모듈, 기능 영역을 적는다.
- 예: `Reservation`, `Payment`, `SeatSelection`, `Notification`
- `Summary` 컬럼에는 무엇을 왜 적용했는지 설명한다.
- 단순히 “refactored”처럼 모호하게 쓰면 안 된다.
- 책임 분리, 의존성 역전, 중복 제거, 확장성 개선 등 적용 목적을 명시한다.
- `Described Page` 컬럼에는 해당 내용이 설명된 보고서 페이지 번호를 적는다.
- 정확한 페이지 번호가 아직 확정되지 않았다면 `Report p.__` 형식으로 placeholder를 넣는다.
- 실제 장표 번호가 필요한 경우 `Slide __` 형식으로 placeholder를 넣는다.
- 페이지/장표 번호는 빈칸 또는 placeholder로 두더라도, Summary와 Applied Location 내용은 완성해야 한다.
- Iteration 4 행은 빨간색으로 표시한다.
- Iteration 4의 Sub-iteration, 적용 패턴, 적용처, 요약, 기술된 페이지는 모두 빨간색으로 표시한다.
- Iteration 4에서 적용된 디자인 패턴은 `DP#n` 번호와 함께 명시한다.
- 같은 행 안에서 리팩토링과 디자인 패턴 적용을 구분해 읽을 수 있어야 한다.
- 리팩토링이 수행된 행에는 반드시 `Refactoring` 또는 구체적 리팩토링명, 예를 들어 `Extract Class`, `Move Method`, `Extract Interface`, `Replace Conditional with Polymorphism` 등을 표기한다.
- 기존 기능을 변경했는데 구조 개선이 포함되었다면 단순 `Changed`로만 쓰지 말고 `Refactored`라고 명시한다.
- Iteration Progress Table은 column 수가 많으므로 overflow 방지가 특히 중요하다.
- 표 전체가 페이지 본문 영역을 벗어나지 않도록 column width를 content 기준으로 조정한다.
- Summary column은 길어질 수 있으므로 셀 내부 줄바꿈을 사용한다.
- Described Page column은 짧게 유지하여 불필요한 폭을 차지하지 않게 한다.
- 필요하면 Applied Location과 Summary의 문장을 간결하게 줄여 표가 페이지 밖으로 밀리지 않게 한다.

### 4-4. Extension Feature Table

- Extension Feature Table은 필수이다.
- Extension Feature Table 컬럼은 다음과 같다.
  - Base Feature
  - Extension Feature
  - Notes

- `Extension Feature` 컬럼에는 추가사항과 변경사항을 포함한다.
- `Notes` 컬럼에는 expected refactoring / design pattern과 expected implementation timing을 포함한다.
- 실제 구현하지 않은 기능도 포함할 수 있다.
- 있으면 좋은 향후 기능은 가능한 한 많이 포함한다.
- 표는 기능 확장 가능성을 충분히 보여주어야 한다.
- 각 확장 기능은 기존 Base Feature와 연결해서 작성한다.
- 기존 기능을 변경했다면 리팩토링 여부를 표시한다.
- 리팩토링이 필요한 확장 기능이나 이미 리팩토링된 기능은 Notes에 `Refactoring required`, `Refactoring applied`, 또는 구체적인 리팩토링명을 적는다.
- **iteration 1 원래 계획에 없다가 이후 추가·적용된 기능은 "이미 적용된 확장"으로 이 표에 포함하고 Notes에 `Refactoring applied`와 추가 iteration을 적는다.** 판정은 iter1 보고서의 Feature Inventory + Pattern Roadmap(계획 패턴 = State / Strategy / Observer / Singleton / Factory Method)과 실제 산출물 대조로 한다. 계획 외 추가분:
  - DP#3 Composite(공항 계층 검색), DP#6 Template Method(e-Ticket 렌더러), DP#7 Adapter(Skypass 마일리지), DP#8 Decorator(좌석 부가옵션) — Iteration 4.
  - 연계 버스 티켓 자동 발권(DP#2 Observer 활용) — 교수님 지시로 추가, Iteration 3.
  - 환불 정책 자동 선택(RefundPolicyResolver, DP#1 Strategy) — Iteration 4.
- 이미 적용된 확장 중 **Iteration 4 추가분만 빨간색**, 이전 iteration(버스티켓 iter3 등) 추가분과 미구현 향후 확장은 검정으로 둔다.
- 단순 확장과 구조 변경을 구분한다.
- 확장에 리팩토링이 필요하면 예상 리팩토링 또는 예상 디자인 패턴을 Notes에 적는다.
- 예상 디자인 패턴도 가능하면 `DP#n candidate` 또는 `Expected DP#n` 형식으로 표기한다.
- 이미 본문에서 설명한 디자인 패턴을 확장 기능에서 재사용한다면 같은 DP 번호를 재사용한다.
- 새 패턴을 도입할 수 있다면 기존 번호와 충돌하지 않는 다음 번호를 사용한다.
- State Pattern은 향후 확장 후보로도 `DP#n` 번호를 부여하지 않는다.
- 상태 전환이 필요하다면 state-management refactoring, enum-based state separation, state-transition responsibility separation 등으로 표현한다.
- 포함하면 좋은 확장 기능 예시는 reservation cancellation/modification, reservation notification, payment method expansion, seat recommendation, user-tier discount, administrator statistics, reservation history search, automatic refund policy, external payment API integration, email/SMS notification, multilingual UI support, logging/audit trail, stronger exception handling 등이다.
- Extension Feature Table은 내용이 많아질 수 있으므로 overflow가 발생하지 않도록 작성한다.
- Base Feature column은 짧게 유지하고, Extension Feature와 Notes column은 content에 맞춰 폭을 조정한다.
- Notes가 길어지면 셀 내부 줄바꿈을 사용하고, 표 너비를 페이지 밖으로 늘리지 않는다.

---

## 5. 디자인 패턴 상세 설명 섹션

- Team Contribution Table과 세 개의 필수 본문 표 다음에는 각 디자인 패턴별 상세 설명을 작성한다.
- 실제 GWS 보고서 순서는 다음 흐름을 따른다.
  - Overview / Introduction
  - Team Contribution Table
  - Feature Table
  - Iteration Progress Table
  - Extension Feature Table
  - `DP#1` detailed explanation
  - `DP#2` detailed explanation
  - `DP#3` detailed explanation
  - 이후 적용한 디자인 패턴 수만큼 `DP#n` 섹션 추가

- 각 디자인 패턴 제목은 반드시 `DP#n` 형식을 사용한다.
- 예: `DP#1 Strategy Pattern`, `DP#2 Factory Method Pattern`, `DP#3 Observer Pattern`
- 표에서 사용한 `DP#n` 번호와 본문 디자인 패턴 설명 번호는 반드시 일치해야 한다.
- 같은 패턴을 여러 기능에서 재사용하는 경우 같은 `DP#n`을 사용한다.
- 서로 다른 설계 목적의 패턴 적용은 별도의 `DP#n`으로 분리한다.
- State Pattern은 디자인 패턴 번호를 부여하지 않는다.
- 교수님이 State Pattern을 이 보고서의 인정 디자인 패턴으로 보지 않기 때문에 `DP#n State Pattern`이라고 쓰면 안 된다.
- 상태 관련 구현이 필요하면 디자인 패턴 설명 섹션이 아니라 refactoring 또는 implementation details로 설명한다.
- 상태 전환 구조는 인정 디자인 패턴 개수에 포함하지 않는다.
- 각 `DP#n` 섹션은 교과서 사진과 구현 다이어그램 비교를 포함해야 한다.
- 왼쪽에는 `~/Downloads/OODP`의 실제 교과서 사진을 넣는다.
- 오른쪽에는 PlantUML 기반 Amateras UML 스타일 구현 다이어그램을 넣는다.
- 두 이미지는 기본적으로 좌우 병렬로 배치한다.
- 단, 이미지가 가로로 길어 문서 폭을 초과하거나 가독성이 떨어지면 상하 배치를 사용한다.
- 상하 배치 시 위쪽에는 교과서 사진, 아래쪽에는 구현 다이어그램을 배치한다.
- 좌우 배치와 상하 배치 모두 1:1 비교가 가능하도록 크기, 정렬, 순서를 맞춘다.
- 각 `DP#n` 섹션에는 교과서 패턴과 실제 구현의 역할 대응 설명을 포함한다.
- 이름이 동일할 필요는 없지만, 교과서 패턴에서 요구하는 역할이 실제 코드 요소에 어떻게 매핑되는지 설명한다.
- 각 `DP#n` 섹션에는 왜 이 디자인 패턴이 필요했는지 설명한다.
- 기존 코드의 문제점, 예를 들어 책임 과다, 중복, 확장성 부족, 의존성 문제 등을 근거로 작성한다.
- 각 `DP#n` 섹션에는 어떻게 구현했는지 설명한다.
- 생성 또는 변경된 클래스, 인터페이스, 메서드, 어트리뷰트를 식별해서 설명한다.
- 어떤 객체가 어떤 객체를 생성, 호출, 위임, 의존하는지 설명한다.
- 코드 예시가 필요한 경우 2 row × 1 column 구조의 코드 블록을 사용한다.
- 코드 블록의 첫 번째 row에는 코드 제목을 넣는다.
- 코드 블록의 두 번째 row에는 실제 코드를 넣는다.
- 실제 코드 row는 IDE dark mode처럼 어두운 배경을 적용하고 syntax highlighting을 적용한다.
- 코드 블록은 Times New Roman 본문 규칙과 별도로, 코드 가독성을 위해 monospace 계열 표현을 사용할 수 있다.
- 코드 블록 안에는 각 줄/블록의 역할을 설명하는 자세한 주석을 단다. 코드와 식별자는 영어, 주석은 한국어로 작성하는 것을 허용한다.
- 주석은 해당 코드가 디자인 패턴에서 맡는 역할과 의도를 설명하고, 주석 색상(syntax highlighting 기준)으로 표시한다.
- 단, 코드 블록 제목, 설명 문장, 캡션은 실제 GWS 보고서 서식 규칙을 따른다.
- 각 `DP#n` 섹션에는 적용 후 얻은 이득을 설명한다.
- 이득은 SOLID, 확장성, 유지보수성, 테스트 용이성, 중복 제거, 결합도 감소, 책임 분리 관점에서 작성한다.
- 각 `DP#n` 섹션에는 해당 패턴이 처음 적용된 iteration을 명시한다.
- 해당 디자인 패턴 적용 과정에서 리팩토링이 수행되었다면, 어떤 리팩토링이 적용되었는지 본문과 관련 표에 모두 명시한다.
- 실제 보고서 페이지 번호와 장표 번호는 정확한 번호가 확정되기 전까지 `Report p.__`, `Slide __`처럼 placeholder로 둔다.
- 페이지/장표 번호 placeholder를 사용하더라도 설명 내용, 필요성, 구현 방식, 이득은 먼저 완성한다.
- Iteration 4에서 새로 적용되었거나 변경된 패턴이라면 관련 설명 전체를 빨간색 글자색으로 표시하고, 빨간색 사각 테두리 또는 클라우드 마킹으로 감싼다.

### 5-1. 행동 다이어그램 (Use Case / Sequence / State)

- 본 보고서는 누적 보고서이므로, 클래스/패턴 다이어그램뿐 아니라 행동 다이어그램(Use Case, Sequence, State machine)도 모두 포함한다.
- 행동 다이어그램은 세 개의 필수 본문 표 다음, 그리고 DP#1~DP#n 상세 설명 섹션 앞에 별도 섹션으로 배치한다(예: `행동 다이어그램 - Use Case / Sequence / State`). 즉 디자인 패턴 상세보다 먼저 시스템 전체 동작을 보여준다.
- Use Case Diagram: 시스템 전체 유스케이스를 Iteration 1부터 현재(Iteration 4)까지 누적해서 보여준다. 이번 iteration에서 새로 추가된 유스케이스(예: 좌석 옵션 선택, 외부 마일리지 결제, e-Ticket 렌더링, 연계 버스 발권 등)는 빨간색으로 구분한다.
- Sequence Diagram: 주요 시나리오(예약 생성, 검색, 결제, 발권, 환불, 이벤트 알림 등)를 시퀀스로 보여준다. iteration이 진행되며 추가·변경된 흐름(예: 결제 Factory Method 생성, 마일리지 Adapter 호출, 좌석 Decorator 누적, Observer pull 통지 등)은 빨간색으로 구분한다.
- State Diagram: 예약 생애주기 상태 전이(8개 상태)를 행동 관점의 상태 기계로 보여준다. 이는 6장 State 클래스 구조 다이어그램과 별개이며, 상태 사이의 전이(이벤트)를 표현한다. iteration에서 추가·변경된 상태나 전이는 빨간색으로 구분한다.
- 모든 행동 다이어그램도 PlantUML로 작성하고 Amateras UML 스타일로 렌더링하며, 내부 텍스트(액터명, 메시지, 상태명, 전이 라벨)는 영어로 작성한다.
- 행동 다이어그램의 캡션과 설명 문단은 본문 언어 규칙(3개 탭)을 따르며, Iteration 4 신규/변경 흐름의 설명은 빨간색 글자색으로 표시하고 변경 영역을 빨간색 사각 테두리로 감싼다.
- 세 개 탭(전체 한국어 / 전체 영어 / 혼합) 모두 동일한 행동 다이어그램을 포함한다.

---

## 6. GoF 및 SOLID 요구사항

- 디자인 패턴을 적용한 모든 코드는 GoF 디자인 패턴 구조를 엄격하게 따라야 한다.
- 적용한 패턴 구조는 교과서의 패턴 구조와 맞아야 한다.
- 패턴 참여 클래스는 교과서의 역할과 대응되어야 한다.
- 패턴 이름만 붙이고 실제 구조가 맞지 않으면 안 된다.
- 실제 클래스 구조, 메서드 호출 관계, 객체 생성 책임, 의존 방향이 패턴 의도에 맞아야 한다.
- 인터페이스나 추상 클래스를 구현/상속하는 모든 구체 클래스는, 그 패턴이 요구하는 핵심 연산을 실제로 구현(override)해야 한다.
- abstract로 선언된 메서드는 모든 구체 클래스가 반드시 구현해야 하며, 미구현 상태로 두면 안 된다.
- default 또는 상위의 기본 구현에 의존하는 경우라도, 해당 구체 클래스가 책임지는 핵심 연산은 직접 override하여 채운다. 빈 구현(`{}`)이나 의미 없는 stub만 남기지 않는다.
- 예: State 패턴의 각 ConcreteState는 자신이 허용하는 전이를 실제로 override해야 하고, Strategy의 각 ConcreteStrategy는 알고리즘 메서드를 실제로 구현해야 하며, Factory Method의 각 ConcreteCreator는 팩토리 메서드를 실제로 override해 자기 ConcreteProduct를 생성해야 한다.
- 컴파일이 된다는 사실만으로 충족으로 보지 않는다. 각 구체 참여자가 자신이 맡은 연산을 실제로 채우고 있는지를 패턴별로 일일이 확인한다.
- 구현 다이어그램에는 각 구체 클래스가 어떤 연산을 실제로 구현(override)하는지 드러나야 한다. 구체 클래스 박스에 식별용 메서드(예: name())만 표시하고 실제 구현한 핵심 연산을 숨기지 않는다.
- 보고서의 모든 설명(패턴 설명, 역할 대응, 구현 방식, 코드 블록, 다이어그램, 시퀀스)은 실제 소스 코드와 정확히 일치해야 한다. 보고서가 코드와 다르면 완료로 보지 않는다.
- 보고서에 기술한 클래스명, 메서드명, 시그니처, 호출 구조, 패턴의 동작 방식(예: Observer가 push인지 pull인지, Factory Method가 무인자인지 인자 있는지)이 실제 코드와 달라서는 안 된다.
- 예: Observer를 "pull 모델(notifyObservers() 무인자 / update() 무인자 / 옵서버가 subject.getState()로 상태를 당김)"로 기술하려면 실제 EventPublisher와 EventListener 코드도 그 구조여야 한다. 만약 실제 코드가 "push 모델(publish(DomainEvent event)로 이벤트를 넘기고 리스너가 onEvent(event)로 받음)"이라면, 보고서 설명을 실제 push 구조에 맞게 고치거나 코드를 pull 구조로 리팩토링하여 둘을 일치시킨다. 어느 쪽이 현재 소스의 실제 모습인지 먼저 확인한 뒤 정렬한다.
- 작업 전 항상 실제 소스(EventPublisher.java, EventListener.java 등)를 열어 현재 구조를 확인하고, 보고서가 참조하는 코드 버전과 실제 제출/공유되는 코드 버전(원격/병합본)이 동일한지 점검한다. 로컬에만 있는 리팩토링이 보고서에 반영되고 공유 코드에는 반영되지 않는 불일치를 만들지 않는다.
- 보고서에서 인정되는 디자인 패턴은 `DP#n`으로 번호를 부여한다.
- State Pattern은 `DP#n` 번호를 부여하지 않는다.
- 모든 코드는 SOLID 원칙을 엄격하게 준수해야 한다.
- SRP: 하나의 클래스는 하나의 책임만 가져야 한다.
- `Reservation`이 예약 생성, 검증, 결제, 알림, 할인 계산을 모두 담당하면 책임이 과도하다.
- 책임이 큰 클래스는 적절히 분리한다.
- OCP: 시스템은 확장에는 열려 있고 수정에는 닫혀 있어야 한다.
- 새로운 결제 방식, 할인 정책, 알림 방식이 추가될 때 핵심 클래스 수정이 최소화되어야 한다.
- LSP: 하위 타입은 상위 타입을 대체해도 동작이 깨지면 안 된다.
- 계약을 위반하는 상속 구조를 피한다.
- ISP: 인터페이스는 역할별로 작게 나누어야 하며, 클래스가 사용하지 않는 메서드에 의존하면 안 된다.
- 하나의 큰 범용 인터페이스보다 역할 기반의 작은 인터페이스를 선호한다.
- DIP: 고수준 모듈은 저수준 구현체에 직접 의존하면 안 된다.
- 추상화를 사용하여 고수준 정책이 구체 클래스가 아니라 인터페이스에 의존하도록 한다.
- `Reservation` 클래스는 책임 분리 관점에서 특히 검토한다.
- `Reservation`은 예약 데이터 또는 예약 상태 표현에 집중해야 한다.
- 예약 생성 로직은 service 또는 factory로 분리할 수 있다.
- 결제 로직은 payment 관련 클래스 또는 strategy-like policy로 분리할 수 있다.
- 알림 로직은 notification 관련 클래스로 분리할 수 있다.
- 할인 계산 로직은 discount policy 또는 pricing strategy로 분리할 수 있다.
- 상태 전환 로직은 별도의 state-management responsibility로 분리할 수 있다.
- 상태 전환 로직을 분리하더라도 `DP#n State Pattern`으로 표기하지 않는다.

---

## 7. 실제 동작 보장

- 설계와 보고서만 맞추는 것이 아니라 시스템이 실제로 동작해야 한다.
- 리팩토링 후 기존 기능이 깨지면 안 된다.
- Iteration 4에서 추가·수정한 기능도 정상 동작해야 한다.
- 컴파일 오류가 없어야 한다.
- 런타임 오류가 없어야 한다.
- 주요 기능 흐름을 테스트해야 한다.
- 예약 생성이 정상 동작해야 한다.
- 예약 조회가 정상 동작해야 한다.
- 예약 수정 또는 취소가 구현되어 있다면 정상 동작해야 한다.
- 결제 관련 기능이 구현되어 있다면 정상 동작해야 한다.
- 알림 관련 기능이 구현되어 있다면 정상 동작해야 한다.
- Iteration 4 변경사항이 기존 기능과 충돌하면 안 된다.
- 리팩토링 후 회귀 테스트가 필요하다.
- 가능하면 리팩토링 전후의 입력과 출력 동작을 비교한다.
- 클래스 책임을 분리하더라도 사용자 관점의 기능 결과는 유지되어야 한다.

---

## 8. 작업 Workflow 요구사항

- 작업은 한 번에 끝내지 말고 반복 루프로 진행한다.
- 필수 workflow는 다음과 같다.
  - Search
  - Plan
  - Edit
  - Audit

- 모든 요구사항이 충족될 때까지 위 루프를 반복한다.

### 8-1. Search

- `~/Downloads/OODP` 폴더에서 교과서 사진, 코드, 보고서, 다이어그램 소스를 확인한다.
- 교과서 디자인 패턴 구조를 확인한다.
- 제공된 사진 속 클래스 구조와 관계를 확인한다.
- 현재 코드 구조를 확인한다.
- 현재 보고서 구조를 확인한다.
- Iteration 4에서 도입된 변경사항을 식별한다.
- 교과서 패턴의 역할을 확인한다.
- 필수 메서드 역할과 어트리뷰트 역할을 확인한다.
- 실제 코드에서 해당 역할을 수행하는 요소를 식별한다.
- 현재 코드와 교과서 패턴을 비교한다.
- 현재 보고서와 요구사항을 비교한다.
- 사진 방향과 배치를 확인한다.
- 다이어그램이 Amateras UML처럼 보이는지 확인한다.
- PlantUML 기본 스타일이 남아 있는지 확인한다.
- 커스터마이징된 PlantUML 결과가 Amateras UML처럼 보이는지 확인한다.
- Amateras UML이 아닌 다른 시각 스타일이 남아 있는지 확인한다.

### 8-2. Plan

- 교과서 패턴과 현재 코드의 차이를 목록화한다.
- 추가, 삭제, 이동, 분리할 클래스를 결정한다.
- 이동 또는 분리할 메서드를 결정한다.
- 어느 어트리뷰트를 어느 클래스로 이동할지 결정한다.
- Iteration 4 변경사항을 어디에 빨간색으로 표시할지 결정한다.
- Iteration 4 관련 본문, 표 텍스트, 캡션, 다이어그램 설명, 코드 설명의 빨간색 적용 범위를 결정한다.
- Team Contribution Table을 Overview 또는 Introduction 직후에 배치할지 확인한다.
- 필수 본문 표 3개를 어디에 배치할지 결정한다.
- 각 교과서 사진과 구현 다이어그램을 어느 위치에서 좌우 병렬로 비교할지 결정한다.
- 이미지가 가로로 길어 overflow가 예상되면 상하 배치로 전환할지 결정한다.
- 좌우 비교 또는 상하 비교 레이아웃이 표로 오해되지 않도록 한다.
- 코드 예시가 필요한 위치에서는 2 row × 1 column 코드 블록을 사용할지 결정한다.
- 교과서 1:1 대응, Amateras UML 스타일, GoF 패턴 정확성, SOLID 준수, 기능 안정성, Iteration 4 표시, 표/다이어그램/코드 내부 텍스트 영어 작성, 일반 설명 문단 한국어 상세 작성, Times New Roman 12 pt 통일, title/heading만 bold 처리 기준을 함께 고려한다.
- 리팩토링된 부분을 어느 표와 어느 본문 위치에서 명시할지 계획한다.
- 보고서 페이지와 장표 번호는 `Report p.__`, `Slide __` placeholder로 먼저 넣고, 정확한 번호는 최종 pagination 이후 채우도록 계획한다.

### 8-3. Edit

- 코드를 교과서 패턴 역할 구조에 맞게 리팩토링한다.
- 클래스, 메서드, 어트리뷰트를 교과서의 역할 구조와 맞춘다.
- 이름이 달라도 필수 교과서 역할을 수행하는지 확인한다.
- 추가 메서드나 어트리뷰트는 책임 과다 또는 패턴 왜곡을 만들지 않을 때만 유지한다.
- 책임이 큰 클래스를 분리한다.
- 필요한 디자인 패턴을 적용한다.
- 보고서 섹션을 수정한다.
- `~/Downloads/OODP`의 교과서 사진과 다이어그램을 사용한다.
- 기본적으로 왼쪽에는 교과서 사진, 오른쪽에는 PlantUML 기반 Amateras UML 스타일 구현 다이어그램을 배치한다.
- 두 이미지가 가로로 길어 overflow되거나 가독성이 떨어지면 위쪽에는 교과서 사진, 아래쪽에는 구현 다이어그램을 배치한다.
- 코드 예시는 2 row × 1 column 코드 블록으로 작성한다.
- 첫 번째 row에는 코드 제목 header를 넣고, 두 번째 row에는 IDE dark mode 배경과 syntax highlighting이 적용된 실제 코드를 넣는다.
- Iteration 4 변경사항을 빨간색으로 표시한다.
- Iteration 4 관련 제목, 본문 문단, 표 내부 텍스트, 캡션, 코드 설명, 다이어그램 설명에 빨간색 글자색을 적용한다.
- Iteration 4 관련 텍스트가 검은색으로 남아 있지 않도록 한다.
- 변경 영역에 빨간색 사각 테두리 또는 클라우드 마킹을 추가한다.
- Team Contribution Table을 Overview 또는 Introduction 직후에 작성한다.
- 모든 표의 너비를 content에 맞게 조정하고, 페이지 본문 영역 밖으로 overflow가 발생하지 않게 한다.
- 표에서 리팩토링된 부분은 반드시 `Refactoring`, `Refactored`, 또는 구체적 리팩토링명으로 표시한다.
- 정확한 보고서 페이지와 장표 번호가 아직 확정되지 않은 경우 `Report p.__`, `Slide __` placeholder를 사용한다.
- placeholder를 사용하더라도 표의 나머지 내용은 빈칸으로 두지 말고 완성한다.
- 긴 셀 내용은 줄바꿈 처리하고, 표를 좌우로 밀어내지 않는다.
- 본문 핵심 표는 Feature Table, Iteration Progress Table, Extension Feature Table 세 개만 작성한다.
- Team Contribution Table 외의 추가 본문 표는 만들지 않는다.
- 실제 GWS 보고서의 표, 다이어그램, 코드 블록 내부 텍스트는 영어로 작성한다.
- 실제 GWS 보고서의 일반 설명 문단은 한국어로 최대한 자세하게 작성한다.
- 실제 GWS 보고서의 모든 텍스트는 Times New Roman, 12 pt를 적용한다.
- 실제 GWS 보고서에서 bold는 title과 section heading에만 사용한다.
- 일반 본문 paragraph의 첫 줄에는 indentation을 적용한다.
- title, section heading, table cell text, code block, image caption에는 paragraph indentation을 적용하지 않는다.
- 본문, 표 내부 텍스트, 캡션, 다이어그램 설명, 코드 설명에는 bold를 사용하지 않는다.
- 다이어그램 자체 border 색은 바꾸지 않는다.
- PlantUML의 skinparam, 선 스타일, 폰트, 박스 형태, 여백, 화살표 스타일을 조정하여 Amateras UML 스타일로 렌더링한다.
- 다른 UML 도구 스타일이 보이면 다이어그램을 수정하거나 렌더링 결과를 교체한다.
- 교과서와 다른 임의 구조를 사용하지 않는다.
- 실제 코드가 동작하지 않는 상태에서 설계 보고서만 작성하지 않는다.

### 8-4. Audit

- 교과서 패턴과 코드가 역할 및 구조 기준으로 맞는지 확인한다.
- 제공된 교과서 사진과 보고서 다이어그램이 1:1로 대응되는지 확인한다.
- `~/Downloads/OODP`의 교과서 사진과 구현 다이어그램이 문서에서 좌우 병렬 또는 상하 배치로 비교 가능하게 들어갔는지 확인한다.
- 좌우 배치인 경우 왼쪽에는 교과서 사진, 오른쪽에는 구현 다이어그램이 있는지 확인한다.
- 상하 배치인 경우 위쪽에는 교과서 사진, 아래쪽에는 구현 다이어그램이 있는지 확인한다.
- 상하 배치는 가로 길이 때문에 overflow 또는 가독성 문제가 있는 경우에만 사용했는지 확인한다.
- 사진 방향이 올바른지 확인한다.
- Iteration 4 변경사항이 모두 빨간색 글자색인지 확인한다.
- Iteration 4 관련 제목, 본문, 표 텍스트, 캡션, 코드 설명, 다이어그램 설명이 검은색으로 남아 있지 않은지 확인한다.
- 변경 영역이 빨간색 사각 테두리 또는 클라우드 마킹으로 감싸졌는지 확인한다.
- UML 다이어그램 자체 border 색이 변경되지 않았는지 확인한다.
- 모든 UML 다이어그램이 PlantUML로 작성되었는지 확인한다.
- 커스터마이징된 PlantUML 결과가 Amateras UML 스타일을 엄격하게 따르는지 확인한다.
- 클래스 박스, compartment, 관계선, 화살표, 폰트, 여백, 정렬, 배치가 Amateras UML처럼 보이는지 확인한다.
- 실제 GWS 보고서의 표, 다이어그램, 코드 블록 내부 텍스트가 영어로 작성되었는지 확인한다.
- 실제 GWS 보고서의 일반 설명 문단이 한국어로 자세하게 작성되었는지 확인한다.
- 실제 GWS 보고서의 모든 텍스트가 Times New Roman, 12 pt인지 확인한다.
- 일반 본문 paragraph의 첫 줄 indentation이 적용되었는지 확인한다.
- title, section heading, table text, code block, image caption에 불필요한 indentation이 들어가지 않았는지 확인한다.
- bold가 title과 section heading에만 적용되었는지 확인한다.
- 본문, 표 내부 텍스트, 캡션, 다이어그램 설명, 코드 설명이 bold가 아닌지 확인한다.
- 코드 예시가 2 row × 1 column 구조인지 확인한다.
- 코드 블록 첫 번째 row가 코드 제목 header인지 확인한다.
- 코드 블록 두 번째 row가 IDE dark mode 배경과 syntax highlighting이 적용된 code area인지 확인한다.
- 코드 블록이 페이지 밖으로 overflow되지 않는지 확인한다.
- Team Contribution Table이 Overview 또는 Introduction 직후에 포함되었는지 확인한다.
- 모든 표가 페이지 밖으로 overflow되지 않는지 확인한다.
- 모든 표의 width가 content에 맞춰져 있고, 본문 영역 안에 들어오는지 확인한다.
- 긴 셀 내용이 셀 내부에서 줄바꿈되는지 확인한다.
- Feature Table이 포함되었는지 확인한다.
- Iteration Progress Table이 포함되었는지 확인한다.
- Extension Feature Table이 포함되었는지 확인한다.
- 본문 핵심 표가 정확히 세 개만 있는지 확인한다.
- Team Contribution Table 외에 추가 본문 표가 없는지 확인한다.
- 다이어그램 비교 레이아웃이 표로 추가되지 않았는지 확인한다.
- 디자인 패턴이 `DP#n`으로 번호화되었는지 확인한다.
- 리팩토링된 항목이 모든 관련 표에서 명시되어 있는지 확인한다.
- 보고서 페이지와 장표 번호가 필요한 위치에는 `Report p.__`, `Slide __` placeholder가 들어가 있는지 확인한다.
- 페이지/장표 번호 placeholder 외의 설명 내용이 빈칸으로 남아 있지 않은지 확인한다.
- State Pattern이 `DP#n`으로 번호화되지 않았는지 확인한다.
- GoF 패턴 구조가 실제 코드에 구현되었는지 확인한다.
- SOLID 위반이 남아 있지 않은지 확인한다.
- 시스템이 실제로 실행되는지 확인한다.
- Audit에서 문제가 발견되면 Search 단계로 돌아가 Search → Plan → Edit → Audit 루프를 반복한다.

---

## 9. 최종 완료 기준

- 최종 산출물은 아래 조건을 모두 만족해야 한다.
- 교과서 디자인 패턴과 구현 구조가 역할 기준으로 1:1 대응된다.
- 제공된 사진, 코드, 보고서가 역할, 관계, 배치 기준으로 1:1 대응된다.
- 교과서 사진과 구현 다이어그램이 문서에서 좌우 병렬 또는 상하 배치로 juxtapose되어 있다.
- 좌우 배치인 경우 왼쪽에는 교과서 사진이 있다.
- 좌우 배치인 경우 오른쪽에는 PlantUML 기반 Amateras UML 스타일 구현 다이어그램이 있다.
- 상하 배치인 경우 위쪽에는 교과서 사진, 아래쪽에는 구현 다이어그램이 있다.
- 상하 배치는 가로 길이 때문에 좌우 배치가 부적절한 경우에만 사용되었다.
- 사진과 다이어그램 방향이 올바르다.
- Iteration 4 변경사항은 모두 빨간색으로 표시되어 있다.
- Iteration 4 관련 제목, 본문, 표 텍스트, 캡션, 코드 설명, 다이어그램 설명은 빨간색 글자색이다.
- Iteration 4 관련 텍스트가 검은색으로 남아 있지 않다.
- Iteration 4 변경 영역은 빨간색 사각 테두리 또는 클라우드 마킹으로 감싸져 있다.
- UML 다이어그램 자체 border 색은 변경되지 않았다.
- 모든 UML 다이어그램은 PlantUML로 작성되었다.
- 모든 UML 다이어그램은 Amateras UML 스타일로 커스터마이징되어 렌더링되었다.
- PlantUML 기본 스타일 또는 다른 UML 도구 스타일이 남아 있지 않다.
- Team Contribution Table이 Overview 또는 Introduction 직후에 포함되어 있다.
- 모든 표는 페이지 밖으로 overflow되지 않는다.
- 모든 표의 width는 content에 맞춰져 있으며, 문서 본문 영역 안에 들어온다.
- 긴 표 내용은 셀 내부 줄바꿈으로 처리되어 있다.
- Feature Table이 포함되어 있다.
- Iteration Progress Table이 포함되어 있다.
- Extension Feature Table이 포함되어 있다.
- 본문 핵심 표는 정확히 세 개만 있다.
- Team Contribution Table 외에 추가 본문 표가 없다.
- 다이어그램 비교 영역은 표가 아니라 좌우 이미지 배치 또는 필요 시 상하 이미지 배치로 처리되어 있다.
- 코드 예시는 2 row × 1 column 구조이며, header row와 dark-mode syntax-highlighted code row로 구성되어 있다.
- 실제 GWS 보고서의 표, 다이어그램, 코드 블록 내부 텍스트는 영어로 작성되어 있다.
- 실제 GWS 보고서의 일반 설명 문단은 한국어로 자세하게 작성되어 있다.
- 실제 GWS 보고서의 모든 텍스트는 Times New Roman, 12 pt이다.
- 일반 본문 paragraph에는 첫 줄 indentation이 적용되어 있다.
- title, section heading, table text, code block, image caption에는 불필요한 indentation이 없다.
- bold는 title과 section heading에만 적용되어 있다.
- 본문, 표 내부 텍스트, 캡션, 다이어그램 설명, 코드 설명은 bold가 아니다.
- 인정 디자인 패턴은 모두 `DP#n`으로 번호화되어 있다.
- 리팩토링된 모든 항목은 관련 표에서 `Refactoring`, `Refactored`, 또는 구체적 리팩토링명으로 명시되어 있다.
- 정확한 보고서 페이지와 장표 번호가 확정되지 않은 항목은 `Report p.__`, `Slide __` placeholder를 사용한다.
- placeholder는 페이지/장표 번호에만 사용하고, 내용 설명은 완성되어 있다.
- State Pattern은 `DP#n`으로 번호화되어 있지 않다.
- 모든 코드는 관련 GoF 패턴 구조를 따른다.
- 모든 코드는 SOLID 원칙을 따른다.
- `Reservation`처럼 책임이 과도한 클래스는 적절히 분리되어 있다.
- 리팩토링 후에도 프로그램이 정상 작동한다.
- Search → Plan → Edit → Audit 루프를 반복하여 누락된 요구사항이 없다.

---

## 10. 금지 또는 고위험 실수

- 표, 다이어그램, 코드 블록 내부 텍스트를 한국어로 작성하지 않는다.
- 일반 설명 문단을 영어로만 작성하거나 지나치게 짧고 피상적인 한국어로 작성하지 않는다.
- 실제 GWS 보고서에서 Times New Roman 12 pt 이외의 폰트를 사용하지 않는다.
- 일반 본문 paragraph에 첫 줄 indentation을 누락하지 않는다.
- title, section heading, table text, code block, image caption에 불필요한 indentation을 넣지 않는다.
- 일반 본문, 표 텍스트, 캡션, 다이어그램 설명, 코드 설명을 bold 처리하지 않는다.
- Iteration 4 변경사항을 검은색 텍스트로 남기지 않는다.
- Iteration 4 영역에 빨간색 테두리만 적용하고 본문 글자색을 검은색으로 두지 않는다.
- PlantUML로 작성했지만 Amateras UML처럼 보이게 개조하지 않은 다이어그램을 사용하지 않는다.
- Amateras UML이 아닌 다른 다이어그램 스타일을 사용하지 않는다.
- UML 다이어그램 자체 border를 빨간색으로 바꾸지 않는다.
- Team Contribution Table을 누락하지 않는다.
- 표가 페이지 밖으로 overflow되는 상태로 두지 않는다.
- 표 width를 불필요하게 전체 페이지 폭으로 고정하거나 content와 맞지 않게 설정하지 않는다.
- 긴 셀 내용 때문에 표가 좌우로 밀려나는 상태를 방치하지 않는다.
- 필수 본문 표 3개 중 하나라도 누락하지 않는다.
- Team Contribution Table 외에 네 번째 본문 표나 보조 표를 추가하지 않는다.
- 교과서 사진과 구현 다이어그램 비교를 표 형태로 만들지 않는다.
- 가로로 긴 다이어그램을 억지로 좌우 배치해서 overflow를 만들지 않는다.
- 코드 예시를 일반 본문 텍스트처럼 삽입하지 않는다.
- 코드 블록에 IDE dark mode 배경과 syntax highlighting 없이 평문 코드만 넣지 않는다.
- `Reservation` 같은 클래스에 과도한 책임을 유지하지 않는다.
- 디자인 패턴 이름만 쓰고 실제 구조를 맞추지 않는 것을 금지한다.
- 인정 디자인 패턴에 `DP#n` 번호를 붙이지 않는 것을 금지한다.
- 리팩토링한 부분을 표에서 누락하거나 단순 변경처럼만 표기하지 않는다.
- 정확한 페이지/장표 번호를 모른다는 이유로 관련 내용 전체를 비워두지 않는다.
- 페이지/장표 번호는 placeholder로 두되, 내용 설명은 반드시 작성한다.
- State Pattern을 `DP#n`으로 표기하지 않는다.
- 각 디자인 패턴의 필요성 설명을 누락하지 않는다.
- 각 디자인 패턴의 구현 방식 설명을 누락하지 않는다.
- 각 디자인 패턴의 적용 이득 설명을 누락하지 않는다.
- SOLID를 설명만 하고 코드에 반영하지 않는 것을 금지한다.
- 작동하지 않는 코드를 제출하지 않는다.
- 방향이 틀어진 사진을 삽입하지 않는다.

---

## 11. 팀원 피드백 반영 (2026-06-04)

### 11-1. 문서, 코드 정합성

- 현재 보고서의 큰 항목(기능 표, 행동 다이어그램, DP별 정합성) 구조는 유지한다.
- 각 항목마다 "코드에서 해당 책임을 수행하는 실체 클래스 + 호출 흐름"을 1:1로 링크한다(예: 환불 알고리즘 -> RefundHandler/RefundPolicy, 이벤트 통지 -> EventPublisher/TicketPurchasePublisher 등).
- JavaFX 언급은 현재 구현 UI와 용어를 통일한다(Swing 등 과거 용어가 남아 있으면 제거).
- 실제 화면 라벨(메뉴, 버튼, 상태명)이 문서의 Use Case 및 시나리오 흐름과 일치하는지 검증한다.
- Observer 흐름 세 건(버스 티켓 발급, 결제 실패 자동 취소, 항공편 상태 전파)을 "이벤트 발생지 -> 이벤트 객체 -> pull 호출 순서"까지 동일한 템플릿으로 병렬 정리한다.
- Iteration 4 확장(다도시 연동, 상태 관리, 데모 편의성 요구)과 기존 Iteration 3 핵심 흐름이 중복되지 않고 연속적으로 이어지는지 "재사용 / 재정의 구분"으로 각 항목당 한 줄 요약한다.

### 11-2. 디자인 패턴 강화 포인트

- DP#1 Strategy: `resolvePolicy`는 if/else 분기보다 정책 Resolver(팩토리)로 분리하고, RefundHandler는 `setStrategy` + delegate만 수행하도록 유지하여 교과서 Context/Strategy 역할 정렬을 다듬는다.
- DP#2 Observer: push -> pull 전환이 실제 메서드에서 확인되므로, 통지(notify)와 조회(pull)의 구분을 코드 주석 또는 짧은 다이어그램으로 동시에 표기한다.
- DP#3 부터 DP#8까지는 구현 다이어그램(클래스)과 시퀀스 다이어그램 사이의 대응 표를 추가한다.

### 11-3. 구조적 품질

- 한 클래스에 책임이 과도하게 몰린 구간(Controller, Registry, Adapter 등)은 1차 분해 기준으로 Extract Class / Extract Method를 남기고, 다음 단계에서 책임을 완전히 분리한다.
- 예약 / 결제 / 취소 / 환불 흐름에서 에러 및 예외 경로를 동일한 화면에서 안내하도록 로그와 상태 메시지를 정리한다.
- 문서 마지막에 "현재 구현 완료 / 보완 필요(빨간색)" 상태표를 넣어, 변경 이력(Iteration 4 보강점)과 실제 제출본을 명확히 분리한다.

### 11-4. 최종 제출 권장

- 최종본은 "DP 적용 강도(기능 완결도, 재사용성, 테스트 적합성)"를 2-3문장으로 정리해 한 페이지에 넣어, 발표에서 요구되는 결론을 전달한다.
- 사용자 시나리오(예약 조회, 취소, 환불, 좌석 변경, 마일리지)는 "문서에 적힌 순서"와 "데모 실행 순서"를 같은 순번으로 맞춘다.

### 11-5. State 패턴 번호 정책 변경 (앞 섹션 우선)

- 앞 섹션(특히 1번, 3번, 5번)에서 State 패턴은 인정 디자인 패턴이 아니므로 `DP#n` 번호를 부여하지 않기로 했으나, 팀원 피드백에 따라 이 항목이 우선한다.
- State 패턴을 일단 `DP#1`로 정해 둔다(번호 부여).
- 이후 `DP#1`을 수정했다는 변경 이력을 함께 표기하여, 초기에 State를 디자인 패턴으로 보지 않아 제외했다가 다시 포함한 과정이 문서에 드러나도록 한다.
- 즉 "초기 제외 -> DP#1로 재지정 -> 추후 DP#1 수정" 흐름을 변경 이력으로 남긴다.
