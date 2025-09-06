## 프로젝트 개요

이 프로젝트는 Android / iOS(및 Web 준비 가능)를 대상으로 하는 Kotlin Multiplatform(Compose MPP) 앱입니다. 공용 UI는 Compose Multiplatform로 작성되며, 클린 아키텍처(Clean) + MVI 패턴을 따릅니다.

- 공용 앱 코드: [`/composeApp`](./composeApp/src)
  - [`commonMain`](./composeApp/src/commonMain/kotlin): 모든 타깃(Android/iOS 등)에 공통으로 적용되는 코드
  - 타깃별 폴더: [`androidMain`](./composeApp/src/androidMain/kotlin), [`iosMain`](./composeApp/src/iosMain/kotlin) 등 플랫폼 전용 코드
- iOS 엔트리: [`/iosApp`](./iosApp/iosApp)

참고 문서
- [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
- [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform)
- [Kotlin/Wasm](https://kotl.in/wasm/) (선택)

웹 앱 실행(옵션): `:composeApp:wasmJsBrowserDevelopmentRun` Gradle 태스크로 실행할 수 있습니다.

## 모듈과 의존성

```
composeApp
  ├─ 의존: core:ui, core:data, core:domain, core:common
  ├─ 의존: feature:main  ← 루트 피처(탭/바텀바/내비게이션 조합)
  ├─ (임시) 의존: feature:todo, feature:ai, feature:settings

feature:main → feature:todo, feature:ai, feature:settings, core:ui, core:common

feature:todo → core:ui, core:domain
feature:ai   → core:ui, core:domain
feature:settings → core:ui, core:common

core:domain → core:common
core:data   → core:domain, core:common
core:ui     → core:common
```

의존성 방향 원칙: `feature → domain → data → core` (역참조/순환 금지)

비고
- `feature:main`이 앱 전역 UI 셸(탭, 바텀바, 내비게이션)을 소유하고 각 피처 라우트를 조합합니다.
- `composeApp`은 애플리케이션 엔트리로 DI를 초기화하고, 화면 구성은 `feature:main`에 위임합니다.
- `composeApp` → `feature:todo/ai/settings` 직접 의존은 점진적으로 제거 예정입니다(루트 라우팅 완성 후).

## 아키텍처 원칙 (Clean + MVI)
- 상태는 불변 데이터 클래스 + `StateFlow`로 관리하고, 비동기는 Coroutines를 사용합니다.
- ViewModel은 상태만 외부로 노출하며, 인텐트(`XxxIntent`)로 액션을 처리합니다.
- 디자인 시스템은 `core:ui`에만 존재합니다(피처에서 테마/토큰 생성 금지).
- 의존성 방향은 단방향: `feature → domain → data → core`.

## 데이터 레이어 (Room Multiplatform)
- 데이터베이스: `core/data/local/database/TodoDatabase.*` (Android/iOS 각각 빌더 제공), `BundledSQLiteDriver` 사용
- DAO/Entity: `TodoDao`, `TodoEntity` (Room), 공용 변환 함수 제공
- DataSource: `RoomTodoDataSource`가 `TodoDao`를 래핑
- Repository: `TodoRepositoryImpl`이 DataSource를 감싸 도메인 모델로 변환

## 도메인 레이어 (UseCases)
- `DomainDI.provideTodoUseCases(repository)`를 통해 `TodoUseCases`를 구성
- 피처의 ViewModel은 해당 UseCases를 주입받아 사용

## 의존성 주입 (Koin)
- Android: `composeApp/src/androidMain/.../TodosummerApplication.kt`에서 `startKoin { androidContext + modules(appModules()) }`
- iOS: `initKoinIfNeeded()`에서 `startKoin { modules(appModules()) }`
- `appModules()`는 `dataModule`(DB/DAO/Repository/DataSource) + 도메인 UseCases 바인딩을 포함

## 테마(브랜드 컬러)
- `core/ui/theme/AppTheme.kt`에서 라이트/다크 팔레트 정의
  - 라이트: 화이트 + 민트
  - 다크: 블랙 + 블루
- 공통 컴포넌트: `AppTopBar`, `AppFab`, `AppIcons`, `Dimens` 등

## 빌드/실행
- Android 디버그 빌드: `./gradlew :composeApp:assembleDebug`
- iOS 실행: `iosApp/iosApp.xcodeproj`를 Xcode에서 열어 실행
- Web(선택): `:composeApp:wasmJsBrowserDevelopmentRun`

## 개발 규칙 체크리스트
- [ ] 모듈 build 파일에 직접 플러그인/버전 추가 금지(build-logic 컨벤션만 사용)
- [ ] 역참조/순환 의존성 금지(단방향 유지)
- [ ] ViewModel은 `StateFlow`로 상태만 외부 노출(불변)
- [ ] 피처 레벨에서 테마/토큰 생성 금지(디자인 시스템은 core-ui에서만)
- [ ] 상태에 mutable 컬렉션 금지(스냅샷 친화 타입 사용)