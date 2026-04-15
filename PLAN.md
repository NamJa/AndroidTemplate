# Android Template Project - Implementation Plan

> **작성일**: 2026-04-15
> **목표**: Modern Clean Architecture 기반 Android 멀티 모듈 템플릿 프로젝트 구축

---

## 1. Tech Stack

| Category | Library | Version | 비고 |
|----------|---------|---------|------|
| Language | Kotlin | 2.3.20 | |
| Build | AGP (Android Gradle Plugin) | 9.1.1 | |
| Build | KSP | 2.3.6 | 2.3.0부터 버전 체계 단순화 |
| Build | Gradle Version Catalog | - | `libs.versions.toml` |
| Build | Convention Plugins | - | `build-logic` 모듈 |
| Compose | Compose BOM | 2026.03.01 | Material3 포함 |
| Compose | Activity Compose | 1.13.0 | |
| Navigation | Navigation 3 | 1.1.0 | NavDisplay + 개발자 소유 backstack |
| Navigation | Lifecycle ViewModel Nav3 | 2.10.0 | NavEntry ViewModel 스코핑 |
| DI | Hilt | 2.59.2 | |
| Network | Retrofit | 3.0.0 | |
| Network | OkHttp | 5.3.2 | 5.0.0 정식 릴리즈 이후 안정화 |
| Serialization | Kotlin Serialization | 1.11.0 | Retrofit converter 포함 |
| Image | Coil 3 | 3.4.0 | `coil3-compose` |
| Async | Coroutines | 1.10.2 | |
| Jetpack | Lifecycle | 2.10.0 | runtime-compose, viewmodel-compose |
| Jetpack | Room | 2.8.4 | |
| Jetpack | DataStore | 1.2.1 | Preferences |
| Jetpack | Core KTX | 1.18.0 | |
| Logging | Timber | 5.0.1 | |
| Test | JUnit | 4.13.2 | `MainDispatcherRule`(TestWatcher) 호환을 위해 JUnit4 채택 |
| Test | Truth | 1.4.5 | |
| Test | MockK | 1.14.9 | |
| Test | Turbine | 1.2.1 | Flow 테스트 |
| Test | Compose UI Test | BOM 관리 | |

---

## 2. Module Structure

```
AndroidTemplate/
├── app/                              # Application 모듈 (진입점)
├── build-logic/                      # Convention Plugins
│   └── convention/
├── core/
│   ├── common/                       # 공통 유틸, 확장 함수, Result wrapper
│   ├── data/                         # Repository 구현체, Mapper
│   ├── database/                     # Room DB, Entity, Dao
│   ├── datastore/                    # DataStore Preferences
│   ├── designsystem/                 # Theme, Color, Typography, 공용 컴포넌트
│   ├── domain/                       # UseCase, Repository 인터페이스, Domain Model
│   ├── model/                        # 전체 모듈 공유 모델 (UI/Domain 경계)
│   ├── network/                      # Retrofit, OkHttp, DTO, ApiService
│   ├── ui/                           # 재사용 가능한 Compose UI 컴포넌트
│   └── testing/                      # 테스트 유틸, Fake 구현체
├── feature/
│   ├── home/                         # Home 기능 모듈 (예시)
│   └── settings/                     # Settings 기능 모듈 (예시)
├── gradle/
│   └── libs.versions.toml            # Version Catalog
├── build.gradle.kts                  # Root build script
├── settings.gradle.kts
└── gradle.properties
```

### 2.1 Module Dependency Graph

```
feature:home ──┬── core:domain
               ├── core:model
               ├── core:ui
               └── core:designsystem

feature:settings ──┬── core:domain
                   ├── core:model
                   └── core:designsystem        (UI 컴포넌트 미사용 → core:ui 미의존)

core:data ──┬── core:domain
            ├── core:model
            ├── core:network
            ├── core:database
            ├── core:datastore
            └── core:common

core:domain ──── core:model

core:network ──── core:model
core:database ──── core:model
core:datastore ──── (Android only)

core:ui ──┬── core:designsystem
          └── core:model

app ──┬── feature:home, feature:settings              (Presentation 진입점)
      ├── core:data                                   (Hilt DI 바인딩 제공)
      ├── core:network, core:database, core:datastore (DI 모듈 등록을 위해 의존)
      ├── core:domain, core:model, core:common
      ├── core:ui, core:designsystem
      └── (Compose, Nav3, Lifecycle, Serialization 등 직접 사용)
```

### 2.2 핵심 원칙

- **feature 모듈은 core:data, core:network, core:database에 직접 의존하지 않는다**
  - feature -> core:domain (인터페이스) -> core:data (구현체, app에서 Hilt로 바인딩)
- **core:model은 순수 Kotlin 모듈** (Android 의존성 없음)
- **core:domain은 순수 Kotlin 모듈** (Android 의존성 없음, Coroutines만 허용)
- **단방향 의존성**: 상위 레이어가 하위 레이어를 알지 못함

---

## 3. Architecture Overview

```
┌─────────────────────────────────────────────────────┐
│                    Presentation Layer                │
│  ┌─────────────┐  ┌─────────────┐                   │
│  │ feature:home│  │feature:settings│                 │
│  │  Screen     │  │  Screen     │                   │
│  │  ViewModel  │  │  ViewModel  │                   │
│  │  UiState    │  │  UiState    │                   │
│  └──────┬──────┘  └──────┬──────┘                   │
│         │                │                          │
│  ┌──────┴────────────────┴──────┐                   │
│  │   core:ui / core:designsystem│                   │
│  └──────────────────────────────┘                   │
├─────────────────────────────────────────────────────┤
│                     Domain Layer                    │
│  ┌──────────────────────────────┐                   │
│  │         core:domain          │                   │
│  │  UseCase                     │                   │
│  │  Repository Interface        │                   │
│  └──────────────┬───────────────┘                   │
│  ┌──────────────┴───────────────┐                   │
│  │         core:model           │                   │
│  │  Domain Model (순수 Kotlin)   │                   │
│  └──────────────────────────────┘                   │
├─────────────────────────────────────────────────────┤
│                      Data Layer                     │
│  ┌──────────────────────────────┐                   │
│  │          core:data           │                   │
│  │  RepositoryImpl              │                   │
│  │  Mapper (DTO <-> Model)      │                   │
│  └───┬──────────┬───────────┬───┘                   │
│  ┌───┴───┐  ┌───┴────┐  ┌──┴──────┐                │
│  │network│  │database│  │datastore│                 │
│  │Retrofit│  │ Room  │  │DataStore│                 │
│  │  DTO  │  │Entity  │  │         │                 │
│  └───────┘  └────────┘  └─────────┘                 │
└─────────────────────────────────────────────────────┘
```

### 3.1 Presentation Layer 패턴

```kotlin
// UiState - Sealed interface
sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val items: List<Item>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

// ViewModel - StateFlow 기반
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getItemsUseCase: GetItemsUseCase,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = getItemsUseCase()
        .map(HomeUiState::Success)
        .catch { emit(HomeUiState.Error(it.message.orEmpty())) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState.Loading,
        )
}

// Screen - Stateless Composable (ViewModel은 Nav3 entry에서 주입)
@Composable
fun HomeScreen(
    onItemClick: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeContent(uiState = uiState, onItemClick = onItemClick)
}
```

### 3.2 Domain Layer 패턴

```kotlin
// UseCase - operator fun invoke
class GetItemsUseCase @Inject constructor(
    private val repository: ItemRepository,
) {
    operator fun invoke(): Flow<List<Item>> = repository.getItems()
}

// Repository Interface
interface ItemRepository {
    fun getItems(): Flow<List<Item>>
    suspend fun getItemById(id: Long): Item
    suspend fun refresh()
}
```

### 3.3 Data Layer 패턴

```kotlin
// Repository 구현체 - Offline-first
class ItemRepositoryImpl @Inject constructor(
    private val itemDao: ItemDao,
    private val networkDataSource: ItemNetworkDataSource,
) : ItemRepository {

    override fun getItems(): Flow<List<Item>> =
        itemDao.observeAll().map { entities ->
            entities.map { it.toDomainModel() }
        }

    override suspend fun refresh() {
        val response = networkDataSource.getItems()
        itemDao.upsertAll(response.map { it.toEntity() })
    }
}
```

---

## 4. Build System

### 4.1 Version Catalog (`gradle/libs.versions.toml`)

```toml
[versions]
kotlin = "2.3.20"
agp = "9.1.1"
ksp = "2.3.6"  # 2.3.0부터 버전 체계 단순화 (Kotlin 2.3.x 호환)

# Compose
compose-bom = "2026.03.01"
activity-compose = "1.13.0"

# Navigation 3
nav3 = "1.1.0"

# Jetpack
lifecycle = "2.10.0"
room = "2.8.4"
datastore = "1.2.1"
core-ktx = "1.18.0"

# DI
hilt = "2.59.2"
hilt-navigation-compose = "1.3.0"

# Network
retrofit = "3.0.0"
okhttp = "5.3.2"
kotlinx-serialization = "1.11.0"

# Image
coil = "3.4.0"

# Async
coroutines = "1.10.2"

# Logging
timber = "5.0.1"

# Test
junit = "4.13.2"
truth = "1.4.5"
mockk = "1.14.9"
turbine = "1.2.1"

[libraries]
# Compose BOM
compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "compose-bom" }
compose-ui = { group = "androidx.compose.ui", name = "ui" }
compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
compose-material3 = { group = "androidx.compose.material3", name = "material3" }
compose-material-icons-extended = { group = "androidx.compose.material", name = "material-icons-extended" }
compose-runtime = { group = "androidx.compose.runtime", name = "runtime" }

# Activity
activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activity-compose" }

# Navigation 3
nav3-runtime = { group = "androidx.navigation3", name = "navigation3-runtime", version.ref = "nav3" }
nav3-ui = { group = "androidx.navigation3", name = "navigation3-ui", version.ref = "nav3" }
lifecycle-viewmodel-nav3 = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-navigation3", version.ref = "lifecycle" }

# Lifecycle
lifecycle-runtime-compose = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version.ref = "lifecycle" }
lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycle" }

# Room
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }

# DataStore
datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastore" }

# Core
core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "core-ktx" }

# Hilt
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-compiler", version.ref = "hilt" }
hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version.ref = "hilt-navigation-compose" }

# Network
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
retrofit-kotlinx-serialization = { group = "com.squareup.retrofit2", name = "converter-kotlinx-serialization", version.ref = "retrofit" }
okhttp = { group = "com.squareup.okhttp3", name = "okhttp", version.ref = "okhttp" }
okhttp-logging = { group = "com.squareup.okhttp3", name = "logging-interceptor", version.ref = "okhttp" }
kotlinx-serialization-json = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version.ref = "kotlinx-serialization" }

# Image
coil-compose = { group = "io.coil-kt.coil3", name = "coil-compose", version.ref = "coil" }
coil-network-okhttp = { group = "io.coil-kt.coil3", name = "coil-network-okhttp", version.ref = "coil" }

# Async
coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }
coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "coroutines" }

# Logging
timber = { group = "com.jakewharton.timber", name = "timber", version.ref = "timber" }

# Test
junit = { group = "junit", name = "junit", version.ref = "junit" }
truth = { group = "com.google.truth", name = "truth", version.ref = "truth" }
mockk = { group = "io.mockk", name = "mockk", version.ref = "mockk" }
turbine = { group = "app.cash.turbine", name = "turbine", version.ref = "turbine" }
compose-ui-test-manifest = { group = "androidx.compose.ui", name = "ui-test-manifest" }
compose-ui-test-junit4 = { group = "androidx.compose.ui", name = "ui-test-junit4" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
android-library = { id = "com.android.library", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-jvm = { id = "org.jetbrains.kotlin.jvm", version.ref = "kotlin" }
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
compose-compiler = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
room = { id = "androidx.room", version.ref = "room" }
```

### 4.2 Convention Plugins (`build-logic/convention/`)

빌드 설정 중복을 제거하고, 모듈별 일관된 구성을 위해 Convention Plugin을 사용한다.

| Plugin ID | 적용 대상 | 역할 |
|-----------|----------|------|
| `template.android.application` | `:app` | Application 모듈 기본 설정 |
| `template.android.application.compose` | `:app` | Application + Compose 설정 |
| `template.android.library` | `core:*` | Library 모듈 기본 설정 |
| `template.android.library.compose` | Compose 사용 모듈 | Library + Compose 설정 |
| `template.android.feature` | `feature:*` | Feature 모듈 (Compose + Hilt + Nav3) |
| `template.android.hilt` | Hilt 사용 모듈 | Hilt + KSP 설정 |
| `template.android.room` | `core:database` | Room + KSP 설정 |
| `template.jvm.library` | `core:model`, `core:domain` | 순수 JVM 모듈 |

---

## 5. 각 모듈 상세 설명

### 5.1 `:app`

- **역할**: 앱의 진입점. `Application` 클래스, `MainActivity`, NavDisplay, Hilt DI 그래프의 루트
- **적용 플러그인**: `template.android.application`, `template.android.application.compose`, `template.android.hilt`, `kotlin-serialization`
- **주요 파일**:
  - `TemplateApplication.kt` - `@HiltAndroidApp`, `Timber.plant`, `SingletonImageLoader.setSafe`로 Coil ImageLoader 설정
  - `MainActivity.kt` - `@AndroidEntryPoint`, `enableEdgeToEdge()` + `setContent { TemplateApp() }`
  - `ui/TemplateApp.kt` - 앱 진입 Composable. `AppViewModel`로부터 다크 테마 상태 수신 후 `TemplateTheme`으로 래핑
  - `ui/AppViewModel.kt` - `@HiltViewModel`. `GetUserDataUseCase`로 다크 테마 상태(`StateFlow<Boolean>`) 노출
  - `navigation/TemplateNavDisplay.kt` - Top-level `NavDisplay` + `rememberNavBackStack` + `entryProvider`로 feature entry 조합
- **참고**: Route 정의(`HomeRoute`, `SettingsRoute`)는 각 feature 모듈에 co-locate되어 있으므로 app 모듈에는 별도 `route/` 패키지를 두지 않는다.

### 5.2 `:core:common`

- **역할**: 프로젝트 전반에서 사용되는 유틸리티
- **적용 플러그인**: `template.android.library`, `template.android.hilt`
- **주요 내용**:
  - `Result` wrapper (`Result<T>` - Loading, Success, Error)
  - Kotlin extension functions
  - `Dispatcher` qualifier (`@IoDispatcher`, `@DefaultDispatcher`)
  - `di/DispatcherModule.kt` - Coroutine Dispatcher 제공

### 5.3 `:core:model`

- **역할**: 순수 Kotlin 도메인 모델 (Android 의존성 없음)
- **적용 플러그인**: `template.jvm.library`
- **주요 내용**:
  - 도메인 모델 data class 정의 (`Item`, `UserData` 등)
  - Presentation, Domain, Data 레이어 경계에서 공유

### 5.4 `:core:domain`

- **역할**: 비즈니스 로직 UseCase와 Repository 인터페이스 (순수 Kotlin)
- **적용 플러그인**: `template.jvm.library`
- **의존성**: `:core:model`
- **주요 내용**:
  - `repository/` - `ItemRepository`, `UserDataRepository` 인터페이스
  - `usecase/` - `GetItemsUseCase`, `RefreshItemsUseCase`, `GetUserDataUseCase`, `SetDarkThemeEnabledUseCase` (모두 `operator fun invoke`)

### 5.5 `:core:data`

- **역할**: Repository 구현체, 데이터 소스 조합, DTO-Model 매핑
- **적용 플러그인**: `template.android.library`, `template.android.hilt`
- **의존성**: `:core:domain`, `:core:model`, `:core:network`, `:core:database`, `:core:datastore`, `:core:common`
- **주요 내용**:
  - `repository/` - `ItemRepositoryImpl` (offline-first), `UserDataRepositoryImpl` (DataStore 기반)
  - `mapper/` - Entity/DTO <-> Domain Model 변환
  - `di/DataModule.kt` - Repository 바인딩 (`@Binds`)

### 5.6 `:core:network`

- **역할**: Retrofit, OkHttp 설정, API 서비스 정의, DTO, Coil ImageLoader 제공
- **적용 플러그인**: `template.android.library`, `template.android.hilt`, `kotlin-serialization`
- **의존성**: `:core:model`
- **주요 내용**:
  - `di/NetworkModule.kt` - OkHttp, Retrofit `@Provides`
  - `di/CoilModule.kt` - `OkHttpClient` 기반 Coil3 `ImageLoader` `@Provides` (app에 직접 OkHttp 노출 없이 ImageLoader만 주입)
  - `datasource/` - Network DataSource (`ItemNetworkDataSource`)
  - `api/` - Retrofit API 인터페이스
  - `model/` - Network DTO (`@Serializable`)
  - `interceptor/` - 커스텀 Interceptor (Auth, Logging 등)

### 5.7 `:core:database`

- **역할**: Room DB 설정, Entity, Dao
- **적용 플러그인**: `template.android.library`, `template.android.hilt`, `template.android.room`
- **의존성**: `:core:model`
- **주요 내용**:
  - `TemplateDatabase.kt` - `@Database` 정의
  - `dao/` - DAO 인터페이스
  - `entity/` - Room Entity
  - `di/DatabaseModule.kt` - DB 인스턴스 `@Provides`

### 5.8 `:core:datastore`

- **역할**: DataStore Preferences로 간단한 Key-Value 저장
- **적용 플러그인**: `template.android.library`, `template.android.hilt`
- **주요 내용**:
  - `UserPreferencesDataSource.kt` - DataStore 읽기/쓰기
  - `di/DataStoreModule.kt` - DataStore 인스턴스 제공

### 5.9 `:core:designsystem`

- **역할**: 앱 전체 디자인 시스템 (Theme, Color, Typography, 공용 컴포넌트)
- **적용 플러그인**: `template.android.library.compose`
- **주요 내용**:
  - `theme/` - `Theme.kt`, `Color.kt`, `Typography.kt`
  - `component/` - `TemplateButton`, `TemplateTopBar`, `TemplateCard` 등
  - `icon/` - 앱 아이콘 리소스

### 5.10 `:core:ui`

- **역할**: 비즈니스 로직에 가까운 재사용 UI 컴포넌트
- **적용 플러그인**: `template.android.library.compose`
- **의존성**: `:core:designsystem`, `:core:model`
- **주요 내용**:
  - 모델 기반 리스트 아이템 컴포넌트
  - 에러/로딩 상태 표시 컴포넌트

### 5.11 `:core:testing`

- **역할**: 테스트 유틸리티, Fake 구현체
- **적용 플러그인**: `template.android.library`
- **의존성**: `:core:domain`, `:core:model`, JUnit4, Coroutines Test, Turbine, Truth
- **주요 내용**:
  - `MainDispatcherRule` - JUnit4 `TestWatcher` 기반 `Dispatchers.Main` 교체 룰
  - `fake/FakeItemRepository`, `fake/FakeUserDataRepository` - 도메인 Repository Fake 구현
  - Compose 테스트 헬퍼

### 5.12 `:feature:home` / `:feature:settings`

- **역할**: 각 Feature의 화면(Screen), ViewModel, Navigation Route/Entry 정의
- **적용 플러그인**: `template.android.feature` (Compose + Material Icons Extended + Hilt + Nav3 + Serialization 통합)
- **의존성**: `:core:domain`, `:core:model`, `:core:ui` *(home만)*, `:core:designsystem`
- **주요 내용**:
  - `HomeScreen.kt` / `SettingsScreen.kt` - Composable 화면 (Material `IconButton` + 표준 아이콘 사용)
  - `HomeViewModel.kt` / `SettingsViewModel.kt` - `@HiltViewModel`
  - `HomeUiState.kt` / `SettingsUiState.kt` - UI 상태 sealed interface
  - `navigation/HomeRoute.kt` / `navigation/SettingsRoute.kt` - `@Serializable data object Route : NavKey` (feature 모듈 내부에 co-locate)
  - `navigation/HomeNavEntry.kt` / `navigation/SettingsNavEntry.kt` - `EntryProviderScope<NavKey>` 확장 함수로 entry 등록

---

## 6. Navigation 전략 (Jetpack Navigation 3)

Navigation 2.x의 `NavController`/`NavHost` 대신 **Navigation 3**의 **개발자 소유 backstack** + **`NavDisplay`** 패턴을 사용한다.

### 6.1 Nav2 vs Nav3 핵심 차이

| 항목 | Navigation 2.x | Navigation 3 |
|------|---------------|-------------|
| 핵심 모델 | Graph 기반 `NavController` | State 기반 개발자 소유 `List` |
| Backstack | 라이브러리 소유 (블랙박스) | 개발자 소유 `SnapshotStateList` |
| UI Composable | `NavHost` | `NavDisplay` |
| Route 정의 | `composable<Route>` | `entry<Route>` in `entryProvider` |
| Route 타입 | `@Serializable` | `@Serializable` + `NavKey` 인터페이스 |
| 화면 전환 | `navController.navigate()` | `backStack.add()` |
| 뒤로 가기 | `navController.popBackStack()` | `backStack.removeLastOrNull()` |
| 멀티 페인 | 미지원 | `SceneStrategy`로 기본 지원 |
| ViewModel 스코핑 | `NavBackStackEntry` | `ViewModelStoreNavEntryDecorator` |

### 6.2 Route 정의 (`NavKey` + `@Serializable`)

Route는 **각 feature 모듈** 내부 `navigation/` 패키지에 co-locate한다. Feature를 다른 앱에 옮기거나 분리할 때 Route 정의가 함께 따라가도록 하기 위함이다.

```kotlin
// feature:home/.../navigation/HomeRoute.kt
@Serializable
data object HomeRoute : NavKey

// feature:settings/.../navigation/SettingsRoute.kt
@Serializable
data object SettingsRoute : NavKey

// 인자가 있는 Route 예시 (feature 내부에 위치)
@Serializable
data class DetailRoute(val itemId: Long) : NavKey
```

### 6.3 Feature 모듈 - Entry 등록

Nav3 1.1.0의 entryProvider DSL 수신자 타입은 `EntryProviderScope<NavKey>`이다. 화면 간 이동 콜백은 인자로 받아 entry 생성 시점에 주입한다.

```kotlin
// feature:home/.../navigation/HomeNavEntry.kt
fun EntryProviderScope<NavKey>.homeEntry(
    onItemClick: (Long) -> Unit,
    onSettingsClick: () -> Unit,
) {
    entry<HomeRoute> {
        HomeScreen(
            onItemClick = onItemClick,
            onSettingsClick = onSettingsClick,
        )
    }
}

// feature:settings/.../navigation/SettingsNavEntry.kt
fun EntryProviderScope<NavKey>.settingsEntry(
    onBackClick: () -> Unit,
) {
    entry<SettingsRoute> {
        SettingsScreen(onBackClick = onBackClick)
    }
}
```

### 6.4 App 모듈 - NavDisplay 조합

```kotlin
// app/.../navigation/TemplateNavDisplay.kt
@Composable
fun TemplateNavDisplay() {
    val backStack = rememberNavBackStack(HomeRoute)

    NavDisplay(
        backStack = backStack,
        // Nav3 1.1.0의 onBack 콜백은 인자 없는 `() -> Unit`이다.
        onBack = { backStack.removeLastOrNull() },
        // entryDecorators 인자는 기본값(NavDisplay.DEFAULT_ENTRY_DECORATORS)을 사용한다.
        // 기본값에 SaveableStateHolderNavEntryDecorator와 ViewModelStoreNavEntryDecorator가 포함되어 있어
        // ViewModel 스코핑/상태 보존이 자동으로 이뤄진다.
        entryProvider = entryProvider {
            homeEntry(
                onItemClick = { /* TODO: navigate to detail */ },
                onSettingsClick = { backStack.add(SettingsRoute) },
            )
            settingsEntry(
                onBackClick = { backStack.removeLastOrNull() },
            )
        },
    )
}
```

### 6.5 ViewModel 스코핑

`ViewModelStoreNavEntryDecorator`가 각 `NavEntry`에 `ViewModelStoreOwner`를 제공한다.
Entry 안에서 `hiltViewModel()`을 호출하면 해당 entry의 생명주기에 스코핑된다.
Entry가 backstack에서 제거되면 ViewModel도 자동 정리된다.

```kotlin
entry<HomeRoute> {
    // hiltViewModel()은 이 NavEntry에 스코핑됨
    val viewModel: HomeViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeContent(uiState = uiState)
}
```

---

## 7. DI 전략 (Hilt)

### Module 구성

| Hilt Module | 위치 | 제공 내용 |
|-------------|------|----------|
| `DispatcherModule` | `:core:common` | `@IoDispatcher`, `@DefaultDispatcher` |
| `NetworkModule` | `:core:network` | `OkHttpClient`, `Retrofit`, `ApiService` |
| `CoilModule` | `:core:network` | Coil3 `ImageLoader` (`OkHttpClient` 재사용). app은 `Provider<ImageLoader>`만 주입 |
| `DatabaseModule` | `:core:database` | `TemplateDatabase`, `Dao` |
| `DataStoreModule` | `:core:datastore` | `DataStore<Preferences>` |
| `DataModule` | `:core:data` | Repository `@Binds` (`ItemRepository`, `UserDataRepository`) |

### Hilt + Clean Architecture 바인딩 흐름

```
@HiltAndroidApp TemplateApplication
    └── @AndroidEntryPoint MainActivity
        └── @HiltViewModel HomeViewModel
            └── @Inject GetItemsUseCase
                └── @Inject ItemRepository (interface)
                    └── @Binds ItemRepositoryImpl (core:data에서 바인딩)
                        ├── @Inject ItemDao (core:database에서 @Provides)
                        └── @Inject ItemNetworkDataSource (core:network에서 @Provides)
```

---

## 8. Testing 전략

### 8.1 테스트 계층

| 테스트 유형 | 위치 | 도구 | 대상 |
|------------|------|------|------|
| Unit Test | `test/` | JUnit4 + MockK + Truth + Turbine + `MainDispatcherRule` | ViewModel, UseCase, Repository |
| UI Test | `androidTest/` | Compose UI Test | Screen Composable |
| Integration | `androidTest/` | Hilt Test + Room in-memory | Data Layer |

> JUnit4를 사용하는 이유: `MainDispatcherRule`은 JUnit4의 `TestWatcher`를 상속해 `Dispatchers.Main`을 테스트 디스패처로 교체한다. JUnit5의 `Extension` 기반 동등 구현도 가능하나, JUnit4가 안드로이드 instrumentation 테스트와 일관되게 사용 가능해 채택했다.

### 8.2 테스트 원칙

- **ViewModel 테스트**: Fake Repository 사용, StateFlow 결과 검증 (Turbine)
- **UseCase 테스트**: Fake Repository 사용, 비즈니스 로직 검증
- **Repository 테스트**: Fake DAO/NetworkDataSource 사용, 매핑 및 조합 로직 검증
- **Screen 테스트**: Compose UI Test로 UI 상태별 렌더링 검증

---

## 9. Implementation Steps

총 7단계로 나누어 구현한다.

### Step 1: 프로젝트 초기 설정

1. Root `build.gradle.kts`, `settings.gradle.kts` 생성
2. `gradle.properties` 설정 (JVM args, AndroidX, non-transitive R class 등)
3. `gradle/libs.versions.toml` 작성 (전체 Version Catalog)
4. Gradle Wrapper 설정

### Step 2: Build Logic (Convention Plugins)

1. `build-logic/` 모듈 구조 생성
2. `build-logic/convention/build.gradle.kts` 설정
3. Convention Plugin 구현:
   - `AndroidApplicationConventionPlugin`
   - `AndroidLibraryConventionPlugin`
   - `AndroidComposeConventionPlugin`
   - `AndroidHiltConventionPlugin`
   - `AndroidFeatureConventionPlugin`
   - `AndroidRoomConventionPlugin`
   - `JvmLibraryConventionPlugin`
4. 공통 Kotlin/Android 설정 헬퍼 함수 작성

### Step 3: Core 모듈 - 기반 계층

1. `:core:model` - 도메인 모델 정의
2. `:core:common` - Result wrapper, Dispatcher, Extensions
3. `:core:designsystem` - Theme, Color, Typography, 기본 컴포넌트

### Step 4: Core 모듈 - 데이터 계층

1. `:core:network` - Retrofit, OkHttp, NetworkModule, 예시 ApiService/DTO
2. `:core:database` - Room Database, 예시 Entity/Dao, DatabaseModule
3. `:core:datastore` - DataStore Preferences, DataStoreModule
4. `:core:domain` - 예시 UseCase, Repository 인터페이스
5. `:core:data` - Repository 구현체, Mapper, DataModule

### Step 5: Core 모듈 - UI 계층

1. `:core:ui` - 공용 UI 컴포넌트 (에러/로딩 상태 등)
2. `:core:testing` - 테스트 유틸, `MainDispatcherRule`, Fake Repository 구현체

### Step 6: Feature 모듈

1. `:feature:home` - HomeScreen, HomeViewModel, HomeUiState, HomeNavEntry
2. `:feature:settings` - SettingsScreen, SettingsViewModel, SettingsNavEntry

### Step 7: App 모듈 조합 및 마무리

1. `:app` - Application, MainActivity, NavDisplay, Route 정의, 앱 레벨 UI
2. 전체 모듈 연결 및 빌드 확인
3. `.gitignore`, `CLAUDE.md`, 프로젝트 문서 정리

---

## 10. 프로젝트 설정 상세

### `gradle.properties`

```properties
org.gradle.jvmargs=-Xmx4g -XX:+UseParallelGC
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configuration-cache=true

android.useAndroidX=true
android.nonTransitiveRClass=true

kotlin.code.style=official
```

### Android 공통 설정

| 항목 | 값 |
|------|-----|
| compileSdk | 36 |
| minSdk | 26 |
| targetSdk | 36 |
| Java 호환성 | 17 |
| applicationId (app) | `com.template.app` |

---

## 11. 디렉토리 전체 구조 (파일 레벨)

```
AndroidTemplate/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── kotlin/com/template/app/
│       │   ├── TemplateApplication.kt        # @HiltAndroidApp + Coil ImageLoader 설정
│       │   ├── MainActivity.kt                # @AndroidEntryPoint, enableEdgeToEdge
│       │   ├── navigation/
│       │   │   └── TemplateNavDisplay.kt      # NavDisplay + entryProvider 조합 (Route는 feature에 위치)
│       │   └── ui/
│       │       ├── TemplateApp.kt             # AppViewModel 다크 테마 → TemplateTheme 래핑
│       │       └── AppViewModel.kt            # @HiltViewModel, GetUserDataUseCase 사용
│       └── res/
│           ├── drawable/ic_launcher_foreground.xml
│           ├── mipmap-anydpi-v26/ic_launcher{,_round}.xml
│           ├── values/{colors,strings,themes}.xml
│           └── xml/{backup,data_extraction}_rules.xml
│
├── build-logic/
│   ├── settings.gradle.kts
│   ├── gradle/libs.versions.toml             # 루트 catalog 참조 (Convention Plugin에서 사용)
│   └── convention/
│       ├── build.gradle.kts
│       └── src/main/kotlin/
│           ├── AndroidApplicationConventionPlugin.kt
│           ├── AndroidApplicationComposeConventionPlugin.kt
│           ├── AndroidLibraryConventionPlugin.kt
│           ├── AndroidLibraryComposeConventionPlugin.kt
│           ├── AndroidFeatureConventionPlugin.kt
│           ├── AndroidHiltConventionPlugin.kt
│           ├── AndroidRoomConventionPlugin.kt
│           ├── JvmLibraryConventionPlugin.kt
│           └── com/template/convention/
│               ├── KotlinAndroid.kt
│               ├── AndroidCompose.kt
│               └── ProjectExtensions.kt       # libs catalog accessor 등 헬퍼
│
├── core/
│   ├── common/
│   │   ├── build.gradle.kts
│   │   └── src/main/
│   │       ├── AndroidManifest.xml
│   │       └── kotlin/com/template/core/common/
│   │           ├── di/
│   │           │   ├── DispatcherModule.kt
│   │           │   └── Dispatchers.kt          # @IoDispatcher, @DefaultDispatcher qualifier
│   │           └── result/
│   │               └── Result.kt
│   │
│   ├── model/                                  # template.jvm.library
│   │   ├── build.gradle.kts
│   │   └── src/main/kotlin/com/template/core/model/
│   │       ├── Item.kt
│   │       └── UserData.kt
│   │
│   ├── domain/                                 # template.jvm.library
│   │   ├── build.gradle.kts
│   │   └── src/main/kotlin/com/template/core/domain/
│   │       ├── repository/
│   │       │   ├── ItemRepository.kt
│   │       │   └── UserDataRepository.kt
│   │       └── usecase/
│   │           ├── GetItemsUseCase.kt
│   │           ├── RefreshItemsUseCase.kt
│   │           ├── GetUserDataUseCase.kt
│   │           └── SetDarkThemeEnabledUseCase.kt
│   │
│   ├── data/
│   │   ├── build.gradle.kts
│   │   └── src/main/
│   │       ├── AndroidManifest.xml
│   │       └── kotlin/com/template/core/data/
│   │           ├── repository/
│   │           │   ├── ItemRepositoryImpl.kt
│   │           │   └── UserDataRepositoryImpl.kt
│   │           ├── mapper/
│   │           │   └── ItemMapper.kt
│   │           └── di/
│   │               └── DataModule.kt           # @Binds Repository
│   │
│   ├── network/
│   │   ├── build.gradle.kts
│   │   └── src/main/
│   │       ├── AndroidManifest.xml
│   │       └── kotlin/com/template/core/network/
│   │           ├── api/ItemApi.kt
│   │           ├── datasource/ItemNetworkDataSource.kt
│   │           ├── interceptor/AuthInterceptor.kt
│   │           ├── model/ItemResponse.kt
│   │           └── di/
│   │               ├── NetworkModule.kt
│   │               └── CoilModule.kt           # OkHttpClient 재사용 ImageLoader
│   │
│   ├── database/
│   │   ├── build.gradle.kts
│   │   └── src/main/
│   │       ├── AndroidManifest.xml
│   │       └── kotlin/com/template/core/database/
│   │           ├── TemplateDatabase.kt
│   │           ├── dao/ItemDao.kt
│   │           ├── entity/ItemEntity.kt
│   │           └── di/DatabaseModule.kt
│   │
│   ├── datastore/
│   │   ├── build.gradle.kts
│   │   └── src/main/
│   │       ├── AndroidManifest.xml
│   │       └── kotlin/com/template/core/datastore/
│   │           ├── UserPreferencesDataSource.kt
│   │           └── di/DataStoreModule.kt
│   │
│   ├── designsystem/
│   │   ├── build.gradle.kts
│   │   └── src/main/
│   │       ├── AndroidManifest.xml
│   │       └── kotlin/com/template/core/designsystem/
│   │           ├── theme/{Color,Theme,Typography}.kt
│   │           └── component/{TemplateTopBar,TemplateButton}.kt
│   │
│   ├── ui/
│   │   ├── build.gradle.kts
│   │   └── src/main/
│   │       ├── AndroidManifest.xml
│   │       └── kotlin/com/template/core/ui/
│   │           ├── ItemRow.kt
│   │           ├── LoadingState.kt
│   │           └── ErrorState.kt
│   │
│   └── testing/
│       ├── build.gradle.kts
│       └── src/main/
│           ├── AndroidManifest.xml
│           └── kotlin/com/template/core/testing/
│               ├── rule/MainDispatcherRule.kt   # JUnit4 TestWatcher
│               └── fake/
│                   ├── FakeItemRepository.kt
│                   └── FakeUserDataRepository.kt
│
├── feature/
│   ├── home/                                   # template.android.feature
│   │   ├── build.gradle.kts
│   │   └── src/
│   │       ├── main/
│   │       │   ├── AndroidManifest.xml
│   │       │   └── kotlin/com/template/feature/home/
│   │       │       ├── HomeScreen.kt           # IconButton(Icons.Default.Settings)
│   │       │       ├── HomeViewModel.kt
│   │       │       ├── HomeUiState.kt
│   │       │       └── navigation/
│   │       │           ├── HomeRoute.kt        # @Serializable data object : NavKey
│   │       │           └── HomeNavEntry.kt     # EntryProviderScope 확장
│   │       └── test/kotlin/com/template/feature/home/
│   │           └── HomeViewModelTest.kt
│   │
│   └── settings/                               # template.android.feature
│       ├── build.gradle.kts
│       └── src/
│           ├── main/
│           │   ├── AndroidManifest.xml
│           │   └── kotlin/com/template/feature/settings/
│           │       ├── SettingsScreen.kt       # IconButton(Icons.AutoMirrored.Filled.ArrowBack)
│           │       ├── SettingsViewModel.kt
│           │       ├── SettingsUiState.kt
│           │       └── navigation/
│           │           ├── SettingsRoute.kt
│           │           └── SettingsNavEntry.kt
│           └── test/kotlin/com/template/feature/settings/
│               └── SettingsViewModelTest.kt
│
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/gradle-wrapper.properties
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── .gitignore
```

---

## 12. 주의 사항 및 확인 필요 항목

| # | 항목 | 상태 | 상세 |
|---|------|------|------|
| 1 | **KSP 버전 체계 변경** | 채택 | KSP 2.3.0부터 버전 체계가 `KotlinVer-KSPVer` → 단순 `2.3.x`로 변경. KSP 2.3.6 + Kotlin 2.3.20 조합 사용 |
| 2 | **Retrofit 3.0.0 + OkHttp 5.3.2** | 채택 | Retrofit 3.x는 OkHttp 5.x 전이 의존성. 명시 버전(`okhttp = 5.3.2`)으로 고정 |
| 3 | **Compose BOM 호환성** | 채택 | BOM `2026.03.01` + Material3 + Material Icons Extended를 BOM 버전으로 통합 관리 |
| 4 | **compileSdk** | 채택 | AGP 9.1.1 기준 `compileSdk = 36`, `minSdk = 26`, `targetSdk = 36` |
| 5 | **Hilt 2.59.2 + KSP** | 채택 | Hilt 2.59.2가 KSP를 완전 지원. `kapt` 없이 `ksp`만 사용 |
| 6 | **JUnit4 채택** | 채택 | `MainDispatcherRule`이 `TestWatcher`를 상속하므로 JUnit4 사용. 향후 JUnit5 마이그레이션 시 `@RegisterExtension` 기반 동등 구현 필요 |
| 7 | **Coil ImageLoader 의존성 차단** | 채택 | `core:network`가 `OkHttpClient`를 `implementation`으로만 노출하므로 `:app`에서 직접 보이지 않음. 대신 `core:network/di/CoilModule.kt`에서 `ImageLoader`를 제공하고 `:app`은 `Provider<ImageLoader>`만 주입 |
| 8 | **Route 위치 (Feature co-location)** | 채택 | `HomeRoute`/`SettingsRoute`는 각 feature 모듈의 `navigation/` 패키지에 두어 모듈 분리/이식이 쉽게 유지됨. `:app`에는 별도 route 패키지 없음 |
| 9 | **Nav3 entry DSL 명칭** | 채택 | Nav3 1.1.0의 entryProvider DSL 수신자는 `EntryProviderScope<NavKey>`이며 `entry<T> { ... }` 블록으로 등록. 모든 NavEntry 확장 함수에 적용 |
| 10 | **NavDisplay onBack 시그니처** | 채택 | Nav3 1.1.0에서 `onBack: () -> Unit`으로 단순화. 콜백 내부에서 `backStack.removeLastOrNull()` 호출 |
| 11 | **TYPESAFE_PROJECT_ACCESSORS** | 채택 | `settings.gradle.kts`의 `enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")`로 `projects.core.domain` 등 타입 안전 접근 사용 |
| 12 | **Material Icons Extended 사이즈** | 미정 | 전체 Icon set을 포함해 빌드 사이즈가 증가. Release 빌드는 R8 minify로 미사용 아이콘이 제거되도록 `isMinifyEnabled = true` 유지 |
| 13 | **Application 모듈 namespace** | 채택 | `applicationId = "com.template.app"`, `namespace = "com.template.app"`. 패키지/디렉토리(`com/template/app/`)와 일치 |
