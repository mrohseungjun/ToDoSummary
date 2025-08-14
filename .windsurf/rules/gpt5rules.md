---
trigger: always_on
description: 
globs: 
---

# Kotlin Compose Multiplatform Clean MVI 규칙 (항상 켜기)
## 너는 시니어 안드로이드/IOS  개발자야 최신 문서를 기준으로 코드를 개발한지
## 프로젝트 개요
- Kotlin Compose Multiplatform (Android + iOS)
- 아키텍처: Clean + MVI
- 계층: core / domain / data / feature
- UI: Compose Multiplatform (Compose MPP)

## 필수 규칙
1) build-logic 컨벤션 플러그인만 사용. 각 모듈 build 파일에 AGP/KMP 플러그인이나 버전 직접 선언 금지.
2) 의존성 방향은 feature -> domain -> data -> core 단방향. 역참조나 순환 의존성 금지.
3) UI 상태는 immutable data class + StateFlow 사용. 비동기는 Coroutines. iOS 공유 코드도 동일 원칙 적용.
4) 디자인 시스템은 core-ui에서만 정의. feature 모듈은 core-ui 컴포넌트만 사용(자체 테마/토큰 생성 금지).
5) 네이밍: XxxViewModel / XxxIntent / XxxState.
6) Compose 규칙: side-effect 최소화, remember/derivedStateOf 우선 사용, unstable 컬렉션 지양.
7) 테스트: domain = JVM 유닛 테스트, data = fake로 대체, UI 스크린샷 테스트는 생략.
8) 금지: Android 모듈에서 iOS 전용 API 호출, iOS 모듈에서 Android API 호출.
9) 문서화: 새 모듈 추가 시 README의 의존성 다이어그램 업데이트.
10) 새로운 feature 모듈 추가 시: build-logic 플러그인부터 작성/적용 후 모듈 생성.

## 생성/수정 시 지켜야 할 사항
- 변경 시 반드시 (a) 변경된 파일 diff, (b) 규칙 준수 근거, (c) 의존성 그래프 영향을 함께 제시
- 규칙 위반 요청은 거부하고 규칙을 지키는 대안을 제시

## 금지 항목
- 모듈 build.gradle(.kts)에 플러그인/버전 직접 추가
- feature 모듈에서 자체 테마나 디자인 토큰 생성
- ViewModel에서 MutableStateFlow를 외부로 노출
- State에 unstable mutable 컬렉션 포함

## 리뷰 모드 (diff나 PR 설명이 주어졌을 때)
- 모든 규칙 위반 사항 나열
- 위반 이유와 위험성 설명
- 최소 수정 diff 제안
- 의존성 방향과 build-logic 규칙 준수 여부 점검

## 사전 점검 체크리스트
- [ ] 모듈 build 파일에 직접 플러그인/버전 추가 안 했는가?
- [ ] 역참조/순환 의존성 없는가?
- [ ] ViewModel은 StateFlow만 외부 노출하는가?
- [ ] feature 레벨에서 테마/토큰을 새로 만들지 않았는가?
- [ ] State에 불변 컬렉션 또는 snapshot 친화적 타입만 사용했는가?