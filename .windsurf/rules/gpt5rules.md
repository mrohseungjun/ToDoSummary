---
trigger: always_on
description: 
globs: 
---

1) 이 저장소는 Kotlin Compose Multiplatform 기반. Android와 iOS를 동시 타깃.
2) 아키텍처: Clean + MVI. 계층은 core/domain/data/feature. UI는 Compose MPP.
3) build-logic의 컨벤션 플러그인을 반드시 사용. 모듈에 직접 AGP/KMP 플러그인/버전 선언 금지.
4) UI 상태는 immutable data class + StateFlow. 비동기는 Coroutines. iOS 공유 코드도 동일 원칙.
5) 의존성 방향: feature -> domain -> data -> core. 역참조/순환 의존성 금지.
6) 디자인 시스템은 core-ui에서만 정의. 각 feature는 core-ui 컴포넌트만 사용(자체 테마 생성 금지).
7) 공용 네이밍: ViewModel은 XxxViewModel, Intent는 XxxIntent, State는 XxxState.
8) Compose 규칙: @Composable는 side-effect 최소화, remember/derivedStateOf 우선, unstable 컬렉션 지양.
9) 테스트: domain은 JVM 유닛, data는 fake로 대체, UI는 screenshot 테스트 생략.
10) 생성 금지: 안드로이드 모듈에서 iOS 전용 API 호출, iOS에서 Android API 호출.
11) 문서: 새 모듈 추가 시 README에 의존성 다이어그램 업데이트.
12) 변경 시나리오: 새로운 feature 모듈이 필요하면 build-logic 플러그인부터 추가하고 적용.
