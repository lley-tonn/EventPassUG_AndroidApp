# EventPassUG Android Application
## Complete Technical Documentation (Kotlin / Jetpack Compose)

---

**Version:** 1.0 (Android port)
**Generated:** July 2026
**Platform:** Android 8.0+ (API 26+)
**Architecture:** Jetpack Compose + MVVM + Clean Architecture
**Companion doc:** [EventPassUG iOS Documentation](./EventPassUG_Complete_Documentation.md)

> This document is the Android build specification for EventPassUG. It mirrors the iOS
> app's features, data models, screens, routing, and design system, translating every
> iOS/SwiftUI concept into its idiomatic Kotlin/Android equivalent. Where the iOS doc is
> the source of truth for *what* the app does, this doc is the source of truth for *how*
> to build it on Android. A full iOS↔Android mapping table is in §14.5.

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [Project Overview](#2-project-overview)
3. [System Requirements](#3-system-requirements)
4. [Architecture](#4-architecture)
4A. [Navigation, Routing & Screen Flow](#4a-navigation-routing--screen-flow)
5. [Design System & Tokens](#5-design-system--tokens)
6. [Features](#6-features)
7. [Data Models](#7-data-models)
8. [Repositories & Services](#8-repositories--services)
9. [UI Components](#9-ui-components)
10. [Utilities](#10-utilities)
11. [Configuration](#11-configuration)
12. [Testing Strategy](#12-testing-strategy)
13. [Deployment](#13-deployment)
14. [Appendix](#14-appendix)

---

# 1. Executive Summary

EventPassUG is a mobile event-management platform for the Ugandan market. Users discover
events, purchase tickets, and manage their event experiences as **attendees**, while
**organizers** create events, manage ticket sales, scan entries, and analyze performance.
This document specifies the Android implementation.

## Key Highlights

- **Dual-Role Support**: A single account operates as both Attendee and Organizer
- **100% Jetpack Compose**: Declarative UI, no XML layouts
- **55+ Features**: Feature parity with the iOS app
- **Design System**: Centralized Compose theme + design tokens for consistency
- **Privacy-First**: On-device processing (OCR, card scan), minimal data collection
- **Offline Support**: Core functionality works without network (Room cache)
- **Adaptive Layouts**: Phone + tablet (foldables) via Window Size Classes

## Technology Stack

| Concern | iOS | Android (this doc) |
|---------|-----|--------------------|
| UI Framework | SwiftUI | **Jetpack Compose** (Material 3) |
| Architecture | MVVM + Clean | **MVVM + Clean** (unchanged) |
| Async | async/await, Combine | **Kotlin Coroutines + Flow** |
| DI | Manual `ServiceContainer` | **Hilt (Dagger)** |
| Local DB | Core Data | **Room** |
| Key-Value Prefs | UserDefaults / `@AppStorage` | **Jetpack DataStore (Preferences)** |
| Crypto | CryptoKit (SHA256) | **`java.security.MessageDigest` / Tink** |
| Maps | MapKit | **Maps Compose (Google Maps SDK)** |
| Camera | AVFoundation | **CameraX** |
| QR scan | AVCaptureMetadataOutput | **ML Kit Barcode Scanning** |
| OCR (card scan) | Vision framework | **ML Kit Text Recognition** |
| Calendar | EventKit | **CalendarContract (Calendar Provider)** |
| Navigation | `NavigationStack` / `TabView` | **Navigation Compose + Bottom Navigation** |
| Deep links | `onOpenURL` + URL scheme | **Intent filters / App Links** |
| Image loading | `AsyncImage` | **Coil (`AsyncImage`)** |
| PDF generation | PDFKit / UIGraphics | **`android.graphics.pdf.PdfDocument`** |
| Charts | Swift Charts | **Vico** or **Compose canvas charts** |

---

# 2. Project Overview

## 2.1 Purpose

EventPassUG solves key challenges in the Ugandan event ecosystem:

1. **For Attendees**: Discover local events, purchase tickets securely, manage experiences
2. **For Organizers**: Create events, sell tickets, validate entries, analyze business metrics
3. **For the Market**: A trusted, localized platform with Uganda-specific payment methods

## 2.2 Target Market

- **Primary**: Uganda (Kampala focus)
- **Secondary**: East African region
- **Payment Methods**: MTN Mobile Money, Airtel Money, Card payments (Visa/Mastercard)
- **Currency**: UGX (Ugandan Shilling)

## 2.3 User Personas

### Attendee
- Discovers and browses events
- Purchases tickets via mobile money or card
- Views QR codes for event entry
- Rates and reviews attended events

### Organizer
- Creates and publishes events
- Configures ticket types and pricing
- Scans tickets at venue entry
- Views analytics and earnings
- Manages attendee lists

---

# 3. System Requirements

## 3.1 Development Requirements

| Requirement | Version |
|-------------|---------|
| Android Studio | Ladybug (2024.2)+ |
| Kotlin | 2.0+ |
| Gradle (AGP) | 8.5+ |
| JDK | 17 |
| Compose BOM | 2024.09+ |
| Compose Compiler | Kotlin 2.0 Compose plugin |

## 3.2 Runtime Requirements

| Requirement | Minimum | Target |
|-------------|---------|--------|
| Android (`minSdk`) | 26 (Android 8.0) | — |
| `targetSdk` / `compileSdk` | — | 35 (Android 15) |
| Device | Any phone/tablet on API 26+ | — |
| Storage | ~60 MB | — |
| Network | Required for initial sync, optional for core features |

> **minSdk rationale:** API 26 gives adaptive icons, notification channels, and modern
> `java.time`. Drop to API 24 only if analytics show meaningful low-end device share
> (add core-library desugaring for `java.time` if you do).

## 3.3 Permissions

| Permission | Manifest entry | Purpose | Required |
|------------|----------------|---------|----------|
| Camera | `android.permission.CAMERA` | QR scanning, card scanning | Yes (Organizers) |
| Location | `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION` | Nearby events, directions | Optional |
| Notifications | `POST_NOTIFICATIONS` (API 33+) | Reminders, updates | Optional |
| Calendar | `READ_CALENDAR` | Conflict detection (read-only) | Optional |
| Photos/Media | Photo Picker (no permission) or `READ_MEDIA_IMAGES` | Event poster upload | Yes (Organizers) |
| Internet | `INTERNET`, `ACCESS_NETWORK_STATE` | API/sync | Yes |

> Prefer the **Android Photo Picker** (`ActivityResultContracts.PickVisualMedia`) for poster
> upload — it needs no storage permission on any API level.

---

# 4. Architecture

## 4.1 Architecture Pattern

EventPassUG Android follows **Clean Architecture + MVVM**, organized feature-first, the same
conceptual layering as iOS:

```
┌─────────────────────────────────────────────────────┐
│                      App Layer                       │
│  (Application, MainActivity, NavHost, Hilt modules)  │
├─────────────────────────────────────────────────────┤
│                   Features Layer                     │
│   (Auth, Attendee, Organizer, Common, Scanner) —     │
│        Composable screens + ViewModels               │
├──────────────────┬──────────────────────────────────┤
│   Domain Layer   │           Data Layer             │
│ (Models, UseCases│  (Repositories, Room, DataStore, │
│  Repo interfaces)│   Retrofit services)             │
├──────────────────┴──────────────────────────────────┤
│                      UI Layer                        │
│    (Compose components, Theme, Design System)        │
├─────────────────────────────────────────────────────┤
│                     Core Layer                       │
│      (DI, Utilities, Extensions, Config)             │
└─────────────────────────────────────────────────────┘
```

## 4.2 Project Structure

Two viable module strategies. Pick one:

- **(A) Single-module, package-by-feature** — fastest to start, mirrors the iOS folder tree 1:1.
- **(B) Multi-module Gradle** (`:app`, `:core`, `:domain`, `:data`, `:designsystem`, `:feature:*`) — enforces layer boundaries at compile time, better for a team. Recommended for parity with the iOS "layer can import" rules.

Package tree (strategy A shown; strategy B splits the same packages into modules):

```
com.eventpassug/
├── app/                         # Application Layer
│   ├── EventPassApplication.kt      (@HiltAndroidApp)
│   ├── MainActivity.kt              (single Activity, setContent { })
│   └── navigation/
│       ├── AppNavHost.kt            (NavHost, routes)
│       ├── Routes.kt                (typed route/sealed nav destinations)
│       └── MainScaffold.kt          (bottom nav / role-based tabs)
│
├── feature/                     # Feature Modules
│   ├── auth/                    # Authentication
│   ├── attendee/                # Attendee features
│   ├── organizer/               # Organizer features
│   ├── scanner/                 # Scanner features
│   ├── common/                  # Shared feature screens (profile, settings, support)
│   ├── onboarding/              # Onboarding
│   ├── refunds/                 # Refund system
│   └── cancellation/            # Event cancellation
│
├── domain/                      # Business logic
│   ├── model/                       (data classes, enums)
│   ├── repository/                  (repository interfaces)
│   └── usecase/                     (optional: RecommendationUseCase, etc.)
│
├── data/                        # Data access
│   ├── repository/                  (repository implementations)
│   ├── local/
│   │   ├── room/                    (RoomDatabase, DAOs, entities)
│   │   └── datastore/               (Preferences DataStore)
│   ├── remote/                      (Retrofit APIs, DTOs) — future backend
│   └── mock/                        (Mock*Repository — current default)
│
├── ui/                          # Reusable UI
│   ├── designsystem/                (Theme, Color, Type, Spacing tokens)
│   └── components/                  (EventCard, CategoryTile, charts, etc.)
│
└── core/                        # Infrastructure
    ├── di/                          (Hilt @Module objects)
    ├── util/                        (QrGenerator, PdfGenerator, Haptics, etc.)
    ├── extension/
    └── config/                      (RoleConfig, storage keys)
```

## 4.3 Layer Responsibilities

| Layer | Purpose | Can Depend On |
|-------|---------|---------------|
| **app** | Entry point, navigation host, DI wiring | All layers |
| **feature** | Composable screens + ViewModels | domain, ui, core |
| **domain** | Pure Kotlin models, repo interfaces, use cases | Kotlin stdlib only (no Android) |
| **data** | Repo impls, Room, DataStore, Retrofit | domain, core |
| **ui** | Reusable Composables, theme | core |
| **core** | DI, utilities, extensions | Android framework only |

> Keep `domain` a pure Kotlin/JVM module (no Android imports) so business logic and models
> are unit-testable without Robolectric — the equivalent of iOS "Domain imports Foundation only."

## 4.4 Data Flow (Unidirectional / UDF)

```
User event (Composable)
        │  onClick / onValueChange
        ▼
   ViewModel (exposes StateFlow<UiState>)
        │  calls
        ▼
   UseCase / Repository (interface)
        │
   ┌────┴─────┬──────────┐
   ▼          ▼          ▼
 Retrofit   Room       DataStore
 (network)  (cache)    (prefs)
   │          │          │
   └──────────┴──────────┘
              │ Flow<DomainModel>
              ▼
   ViewModel updates _uiState.value
              │  StateFlow emits
              ▼
   Composable recomposes (collectAsStateWithLifecycle)
```

Each screen exposes a single immutable `UiState` (data class) via
`StateFlow`, collected with `collectAsStateWithLifecycle()`. One-off events (navigation,
snackbars) use a `Channel`/`SharedFlow` of `UiEvent`.

## 4.5 Dependency Injection (Hilt)

The iOS `ServiceContainer` is replaced by **Hilt modules**. Repositories are bound to their
interfaces and injected into ViewModels via `@HiltViewModel` + constructor injection.

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton
    abstract fun bindAuthRepository(impl: MockAuthRepository): AuthRepository

    @Binds @Singleton
    abstract fun bindEventRepository(impl: MockEventRepository): EventRepository

    @Binds @Singleton
    abstract fun bindTicketRepository(impl: MockTicketRepository): TicketRepository

    @Binds @Singleton
    abstract fun bindPaymentRepository(impl: MockPaymentRepository): PaymentRepository
    // ... refund, notification, calendar, location, preferences, filter, recommendation, etc.
}

@HiltViewModel
class AttendeeHomeViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val recommendationRepository: RecommendationRepository,
    private val favoriteManager: FavoriteManager
) : ViewModel() { /* ... */ }
```

Swap `@Binds` targets from `Mock*` to real implementations when the backend is ready — the
same "TODO: replace mock" seam as iOS, done in one module.

---

# 4A. Navigation, Routing & Screen Flow

This mirrors iOS §4A. Where iOS uses `ContentView`'s state machine + `TabView` +
`DeepLinkManager`, Android uses **Navigation Compose** with a single-Activity architecture.

## 4A.1 App Entry Point

**`EventPassApplication.kt`** — annotated `@HiltAndroidApp`; creates notification channels
and initializes any SDKs (Maps, analytics) in `onCreate()`.

**`MainActivity.kt`** — the **single Activity**. In `setContent { }` it applies the app theme
and hosts the root navigation:

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EventPassTheme {
                val navController = rememberNavController()
                AppRoot(navController = navController)
            }
        }
        // Deep links arrive via intent filters → handled by NavController automatically,
        // plus a manual hook for custom eventpass:// parsing (see 4A.4).
    }
}
```

## 4A.2 Root Navigation Flow (AppRoot)

The iOS `ContentView` decision machine becomes a small root Composable that observes
DataStore flags + auth state and picks a start destination / top-level graph.

State sources:
- `hasSeenOnboarding: Flow<Boolean>` (DataStore) — mirrors iOS `@AppStorage hasSeenOnboarding`
- `hasChosenAuthMethod: Flow<Boolean>` (DataStore)
- `authState: StateFlow<AuthState>` (from `AuthRepository`, exposed by a shared `AppViewModel`)

**Decision priority (top to bottom) — identical to iOS:**

| # | Condition | Destination |
|---|-----------|-------------|
| 1 | `!hasSeenOnboarding` (first launch) | `onboarding` graph (`OnboardingScreen`) |
| 2 | Seen, no auth method, not authenticated | `AuthChoiceScreen` |
| 3 | Authenticated (not mid organizer-signup) | `main` graph with `currentActiveRole` |
| 4 | Guest / returning-not-logged-in | `main` graph in **guest mode** (`userRole = null`) |

```kotlin
@Composable
fun AppRoot(navController: NavHostController, vm: AppViewModel = hiltViewModel()) {
    val ui by vm.uiState.collectAsStateWithLifecycle()

    val startGraph = when {
        !ui.hasSeenOnboarding            -> Graph.Onboarding
        !ui.hasChosenAuthMethod
            && !ui.isAuthenticated       -> Graph.AuthChoice
        else                             -> Graph.Main   // authed OR guest
    }

    NavHost(navController, startDestination = startGraph.route) {
        onboardingGraph(navController, onComplete = vm::markOnboardingSeen)
        authGraph(navController, onGuest = vm::continueAsGuest,
                                 onAuthed = vm::markAuthMethodChosen)
        mainGraph(navController, userRole = ui.currentActiveRole) // null = guest
    }

    // Post-login preferences onboarding, shown over the tab scaffold
    if (ui.isAuthenticated && ui.needsPreferenceOnboarding) {
        // navigate to preference onboarding graph (fullscreen)
    }
}
```

**Key transitions (parity with iOS):**
- Completing onboarding → `markOnboardingSeen()` persists `hasSeenOnboarding = true`. It never shows again.
- `AuthChoiceScreen` branches three ways:
  - **Sign in / register** → `ModernAuthScreen` (modal bottom sheet or full screen)
  - **Become an organizer** → `OrganizerSignupGraph` (Auth → KYC steps); an `organizerSignupInProgress` flag prevents premature switch to the main app
  - **Continue as guest** → `continueAsGuest()` sets `hasChosenAuthMethod = true`, enters guest `main`
- After auth, if `user.hasCompletedOnboarding == false`, show **preferences onboarding** full-screen over the scaffold.
- Cross-fade between top-level states with `AnimatedContent` / `Crossfade` (0.3s) to match iOS.

```
App launch (MainActivity → AppRoot)
  │
  ├─ hasSeenOnboarding == false ─────────► OnboardingScreen ──(done)──► persist flag
  │
  ├─ seen, no auth, not authed ──────────► AuthChoiceScreen
  │                                           ├─ Sign in/Register ─► ModernAuthScreen
  │                                           ├─ Become Organizer ─► OrganizerSignupGraph (Auth→KYC)
  │                                           └─ Continue as Guest ─► set hasChosenAuthMethod
  │
  ├─ authenticated ──────────────────────► MainScaffold(role = currentActiveRole)
  │                                           └─ if !hasCompletedOnboarding ─► PreferenceOnboarding (fullscreen)
  │
  └─ guest / returning ──────────────────► MainScaffold(role = null)   // guest mode
```

## 4A.3 Tab Navigation (MainScaffold)

The iOS `MainTabView` becomes a `Scaffold` with a `NavigationBar` (Material 3 bottom nav)
and a nested `NavHost`. Three tabs, role-based, guest gets `effectiveRole = attendee`. Tab
accent color comes from `RoleConfig.primaryColor(role)`; selecting a tab fires
`HapticFeedback` (`view.performHapticFeedback`).

**Attendee / Guest tabs:**

| Tab | Icon | Authenticated | Guest |
|-----|------|---------------|-------|
| Home | `Icons.Filled.Home` | `AttendeeHomeScreen` | `AttendeeHomeScreen` |
| Tickets | `Icons.Filled.ConfirmationNumber` | `TicketsScreen` | `GuestTicketsPlaceholder` |
| Profile | `Icons.Filled.Person` | `ProfileScreen` | `GuestProfilePlaceholder` |

**Organizer tabs:**

| Tab | Icon | Screen |
|-----|------|--------|
| Home | `Icons.Filled.Home` | `OrganizerHomeScreen` |
| Dashboard | `Icons.Filled.BarChart` | `OrganizerDashboardScreen` |
| Profile | `Icons.Filled.Person` | `ProfileScreen` |

Guests browse Home freely; Tickets/Profile show placeholder Composables with contextual
sign-in CTAs. Each tab keeps its own back stack — use a nested `NavHost` per tab **or**
Navigation Compose's multiple-back-stack support (`saveState`/`restoreState`) so switching
tabs preserves scroll position and navigation depth (equivalent to iOS per-tab
`NavigationView`s).

```kotlin
@Composable
fun MainScaffold(role: UserRole?, rootNav: NavHostController) {
    val effective = role ?: UserRole.ATTENDEE
    val tabNav = rememberNavController()
    Scaffold(
        bottomBar = { RoleBottomBar(effective, tabNav) }
    ) { padding ->
        NavHost(tabNav, startDestination = homeRoute(effective),
                modifier = Modifier.padding(padding)) {
            if (effective == UserRole.ATTENDEE) attendeeTabs(rootNav)
            else organizerTabs(rootNav)
        }
    }
    // Organizer-signup full-screen flow hosted above the scaffold (survives auth changes)
}
```

## 4A.4 Deep Linking & URL Schemes

iOS registers `eventpass://` and routes via `DeepLinkManager`. On Android, declare the scheme
with an **intent filter** in the manifest and let **Navigation Compose** dispatch, plus a
manager for the scanner-pairing flow.

**Manifest:**

```xml
<activity android:name=".app.MainActivity" android:exported="true">
    <intent-filter android:autoVerify="true">
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data android:scheme="eventpass" />
    </intent-filter>
</activity>
```

**Supported deep links (identical semantics to iOS):**

| URL pattern | Route | Behavior |
|-------------|-------|----------|
| `eventpass://pair?session={UUID}&event={UUID}` | Scanner pairing | Parse → set `scannerPairingData`, show `ScannerPairingScreen` over root |
| `eventpass://event?id={UUID}` | Event details | Navigate to `event/{id}` |
| `eventpass://ticket?id={UUID}` | Ticket details | Navigate to `ticket/{id}` |
| anything else | Unknown | Ignored/logged |

Parse defensively (validate scheme, query keys, and `UUID.fromString` in a `runCatching`);
malformed links resolve to `Unknown`. For destinations with a stable route, attach
`deepLinks = listOf(navDeepLink { uriPattern = "eventpass://event?id={id}" })` directly on the
`composable(...)` so `NavController` handles them. Route the pairing case through a
`DeepLinkManager` (a `@Singleton` exposing `StateFlow<ScannerPairingData?>`) presented as a
full-screen destination over any screen — matching the iOS app-root `fullScreenCover`.

> As in iOS, `event`/`ticket` deep links are fully specified here; wire them to their
> `composable` routes when those detail screens accept an ID argument.

## 4A.5 In-App Navigation Patterns

- **`NavController.navigate("route/{id}")`** — push navigation within a tab's `NavHost` (iOS `NavigationLink`). Use **typed routes** (Kotlin serialization / `@Serializable` route objects on Nav Compose 2.8+) instead of stringly-typed paths.
- **`ModalBottomSheet` / full-screen `composable`** — modal flows (auth, verification, notifications, create-event, export options) — the iOS `.sheet`.
- **Dedicated full-screen graphs** — immersive multi-step flows (onboarding, organizer signup/KYC, scanner pairing) — the iOS `.fullScreenCover`.

**Profile as the settings hub** (`ProfileScreen`): the central hub, navigating to:

| Row | Destination |
|-----|-------------|
| Edit Profile | `EditProfileScreen` |
| Interests | `FavoriteEventCategoriesScreen` |
| Notifications | `NotificationSettingsScreen` |
| Payment Methods | `PaymentMethodsScreen` |
| Help Center | `HelpCenterScreen` |
| Contact Support | `SupportCenterScreen` |
| Terms & Privacy | `TermsAndPrivacyScreen` |

Plus modal/full-screen: email/phone verification, add email/phone, account linking, National
ID verification, and **Become an Organizer** (`BecomeOrganizerGraph`). Role switching updates
`currentActiveRole` in DataStore/auth state; `AppRoot`/`MainScaffold` observe it and swap the
entire tab set.

## 4A.6 Screen Connection Map (Navigation Graph)

```
MainActivity → AppRoot (NavHost, root)
     ├── OnboardingScreen ─────────► (first launch only)
     ├── AuthChoiceScreen
     │     ├── ModernAuthScreen (sheet / fullscreen)
     │     └── OrganizerSignupGraph (fullscreen → KYC steps)
     │
     └── MainScaffold (bottom nav + nested NavHost) ──────────────┐
          │                                                        │
          ├── [Attendee/Guest]                                     │
          │    ├── Tab 0: AttendeeHomeScreen                       │
          │    │      ├── SearchScreen                             │
          │    │      ├── EventDetailsScreen ─► TicketPurchase ───►│
          │    │      │        │                PaymentConfirmation
          │    │      │        │                └► TicketSuccess    │
          │    │      │        ├── CalendarConflictSheet           │
          │    │      │        └── (follow organizer, share, report)
          │    │      └── FavoriteEventsScreen                     │
          │    ├── Tab 1: TicketsScreen ─► TicketDetailScreen      │
          │    │              └── RefundRequestScreen, PDF share    │
          │    │         (guest: GuestTicketsPlaceholder)          │
          │    └── Tab 2: ProfileScreen (settings hub — see 4A.5)  │
          │              (guest: GuestProfilePlaceholder)          │
          │                                                        │
          └── [Organizer]                                          │
               ├── Tab 0: OrganizerHomeScreen                      │
               │      ├── CreateEventWizard (sheet, 3 steps)       │
               │      ├── QrScannerScreen                          │
               │      ├── OrganizerNotificationCenterScreen (sheet)│
               │      └── EventAnalyticsScreen (per event)         │
               │            ├── ManageEventTicketsScreen           │
               │            ├── EventCancellationFlowScreen        │
               │            └── SelectEventForScannersScreen       │
               ├── Tab 1: OrganizerDashboardScreen                 │
               │      ├── SelectEventForScannersScreen             │
               │      └── OrganizerAnalyticsDashboardScreen        │
               │            ├── RevenueAnalyticsDetail             │
               │            ├── TicketAnalyticsDetail              │
               │            ├── AudienceAnalyticsDetail            │
               │            ├── MarketingInsightsDetail            │
               │            ├── OperationsDetail                   │
               │            └── PredictiveAnalysisDetail           │
               └── Tab 2: ProfileScreen                            │
                                                                   │
     Root overlays (present over any screen):                     │
       • ScannerPairingScreen  ◄── eventpass://pair deep link ─────┘
       • PreferenceOnboarding (post-login)

Scanner sub-app (entered from Dashboard or EventAnalyticsScreen):
  SelectEventForScannersScreen ─► PairScannerScreen ─► ManageScannerDevicesScreen
  ScannerConnectScreen (device-side pairing + live validation)
```

---

# 5. Design System & Tokens

The iOS `AppDesignSystem.swift` is ported to a Compose design system: a `MaterialTheme`
wrapper (`EventPassTheme`) plus token objects. Prefer exposing tokens via **`CompositionLocal`**
(e.g. `LocalSpacing`, `LocalAppColors`) so they're accessed like `MaterialTheme` values.

**Location**: `ui/designsystem/` → `Theme.kt`, `Color.kt`, `Type.kt`, `Spacing.kt`, `Shape.kt`, `Elevation.kt`.

## 5.1 Color Tokens

### Brand Colors

| Token | Hex | Usage |
|-------|-----|-------|
| `primary` | #FF7A00 | Main brand color, CTAs |
| `primaryDark` | #E66D00 | Pressed/hover states |
| `primaryLight` | #FFA040 | Backgrounds, accents |

### Semantic Colors

| Token | Value | Usage |
|-------|-------|-------|
| `success` | Green | Success states |
| `warning` | Orange | Warnings |
| `error` | Red | Errors, destructive |
| `info` | Blue | Info, links |

### Background / Surface (map to Material 3 roles)

| iOS Token | Compose / Material 3 |
|-----------|----------------------|
| `backgroundPrimary` | `colorScheme.background` |
| `backgroundSecondary` | `colorScheme.surface` |
| `backgroundTertiary` | `colorScheme.surfaceVariant` |
| `backgroundGrouped` | `colorScheme.surfaceContainerLow` |

### Text Colors

| iOS Token | Compose |
|-----------|---------|
| `textPrimary` | `colorScheme.onBackground` / `onSurface` |
| `textSecondary` | `onSurfaceVariant` |
| `textTertiary` | `onSurfaceVariant.copy(alpha = 0.6f)` |
| `textInverse` | `colorScheme.onPrimary` (white) |

### Special & Role Colors

| Token | Hex | Usage |
|-------|-----|-------|
| `happeningNow` | #7CFC66 | Live event indicator |
| `premium` | #FFD700 | Premium/VIP badges |
| `attendeePrimary` | #FF7A00 | Attendee role accent |
| `organizerPrimary` | #FFA500 | Organizer role accent |

```kotlin
object AppColors {
    val Primary = Color(0xFFFF7A00)
    val PrimaryDark = Color(0xFFE66D00)
    val PrimaryLight = Color(0xFFFFA040)
    val HappeningNow = Color(0xFF7CFC66)
    val Premium = Color(0xFFFFD700)
    val AttendeePrimary = Color(0xFFFF7A00)
    val OrganizerPrimary = Color(0xFFFFA500)
}
```

Provide **light and dark** `ColorScheme`s (iOS supports dark mode). Define `LightColorScheme`
and `DarkColorScheme` in `Theme.kt`; do **not** blindly enable Material You dynamic color if
you need the orange brand identity to stay fixed.

## 5.2 Typography Tokens

Port the semantic styles into a Compose `Typography` plus named `TextStyle`s. iOS uses SF
rounded for several tokens; the Android analog is a rounded family (bundle **Nunito** or
**Quicksand**, or use the platform default). Map iOS Dynamic Type to Compose `sp` (which
already scales with system font size).

| iOS Token | Compose style | Font / weight |
|-----------|---------------|---------------|
| `hero` / `largeTitle` | `displaySmall` | Bold |
| `title` | `headlineSmall` | SemiBold |
| `title2` | `titleLarge` | SemiBold |
| `section` (title3) | `titleMedium` | SemiBold |
| `cardTitle` (headline) | `titleMedium` (17sp) | SemiBold |
| `body` | `bodyLarge` | Regular |
| `bodyEmphasized` | `bodyLarge` | SemiBold |
| `secondary` (subheadline) | `bodyMedium` (15sp) | Regular |
| `callout` | `bodyMedium` | Regular |
| `caption` | `bodySmall` | Regular |
| `footnote` | `labelSmall` | Regular |
| `buttonPrimary` | `labelLarge` | SemiBold |

## 5.3 Spacing Tokens

Provide via a `Spacing` data class exposed through `LocalSpacing`.

| Token | dp | Usage |
|-------|----|-------|
| `xxs` | 2 | Micro |
| `xs` | 4 | Tight |
| `sm` | 8 | Small gaps |
| `md` | 16 | Standard |
| `lg` | 24 | Section |
| `xl` | 32 | Large |
| `xxl` | 48 | Major |
| `xxxl` | 64 | Screen margins |
| `compact` | 6 | Compact layouts |
| `item` | 12 | List item spacing |
| `edge` | 16 | Screen edge padding |

```kotlin
data class Spacing(
    val xxs: Dp = 2.dp, val xs: Dp = 4.dp, val sm: Dp = 8.dp,
    val md: Dp = 16.dp, val lg: Dp = 24.dp, val xl: Dp = 32.dp,
    val xxl: Dp = 48.dp, val xxxl: Dp = 64.dp,
    val compact: Dp = 6.dp, val item: Dp = 12.dp, val edge: Dp = 16.dp
)
val LocalSpacing = staticCompositionLocalOf { Spacing() }
```

## 5.4 Corner Radius / Shape Tokens

| Token | dp | Usage | Material 3 shape |
|-------|----|-------|------------------|
| `xs` | 4 | Badges, tags | `extraSmall` |
| `sm` | 8 | Small elements | `small` |
| `md` / `card` / `button` | 12 | Standard, cards, buttons | `medium` |
| `input` | 10 | Text fields | custom |
| `lg` | 16 | Cards | `large` |
| `xl` | 24 | Large cards | `extraLarge` |
| `pill` | 100 (`CircleShape`) | Pills, toggles | — |
| `badge` | 6 | Status badges | custom |

## 5.5 Elevation / Shadow Tokens

iOS shadow radii map to Compose `Modifier.shadow(elevation, shape)` (and Material 3 tonal
elevation on surfaces).

| Token | iOS shadow | Compose elevation (approx) |
|-------|-----------|----------------------------|
| `subtle` | black 5%, r4 | 1–2 dp |
| `card` | black 8%, r8 | 3 dp |
| `button` | black 10%, r6 | 2 dp |
| `elevated` | black 12%, r16 | 6 dp |
| `floating` | black 15%, r20 | 8–12 dp |

Provide `Modifier.cardShadow()`, `elevatedShadow()`, etc. as extension functions to match iOS
view modifiers.

## 5.6 Button / Input Dimensions

| Token | dp |
|-------|----|
| Button `heightLarge` | 56 |
| Button `heightMedium` | 48 |
| Button `heightSmall` | 36 |
| Button `heightCompact` | 32 |
| `minimumTouchTarget` | 48 (Material min; iOS uses 44) |
| Input `height` | 52 |
| Input `heightCompact` | 44 |
| Input icon size | 20 |

> Android's minimum touch target is **48 dp** (Material accessibility) vs iOS 44 pt — use 48.

## 5.7 Animation Tokens

| Token | Compose spec | Duration |
|-------|--------------|----------|
| `quick` | `tween(150)` | 0.15s |
| `standard` | `tween(200)` | 0.2s |
| `slow` | `tween(400)` | 0.4s |
| `spring` | `spring(dampingRatio = 0.7f)` | ~0.3s |
| `springBouncy` | `spring(dampingRatio = 0.6f)` | ~0.4s |

## 5.8 Border Tokens

| Token | dp |
|-------|----|
| `width` | 1 |
| `selectedWidth` | 2 |
| `thickWidth` | 3 |

---

# 6. Features

Feature parity with iOS. Each subsection lists the Android screen (`*Screen` Composable +
`*ViewModel`) and any Android-specific integration notes.

## 6.1 Authentication System

**Location**: `feature/auth/`

### 6.1.1 Email/Password
- Full registration with validation
- Password hashing (SHA-256 + salt via `MessageDigest`; for production prefer **Argon2/BCrypt** server-side)
- Email format + password strength validation
- Flow: `Register → Enter details → Validate → Hash → Create user → Login`

### 6.1.2 Phone OTP
- 6-digit verification, E.164 phone validation
- Mock OTP in development (`123456`)
- Production: integrate SMS gateway / Firebase Phone Auth
- Flow: `Enter phone → Send OTP → Enter code → Verify → Login/Register`

### 6.1.3 Social Login
- **Google** → Credential Manager API (`androidx.credentials`) + Sign in with Google
- **Facebook** → Facebook Login SDK
- **Apple** → optional on Android via web OAuth (Apple JS) if required
- Status: mock implementations, ready for SDK swap

### 6.1.4 Test Users

| Email | Password | Role |
|-------|----------|------|
| john@example.com | password123 | Attendee |
| jane@example.com | password123 | Attendee |
| alice@example.com | password123 | Attendee |
| bob@events.com | organizer123 | Organizer |
| sarah@events.com | organizer123 | Organizer |

## 6.2 Dual Role Support

Single account, both roles. Role switching in Profile updates `currentActiveRole` (DataStore
+ auth state). On switch, the bottom-nav tab set, accent color (Attendee #FF7A00 / Organizer
#FFA500), and home surface all change — `MainScaffold` recomposes with the new role.

## 6.3 Attendee Features

| Screen | Composable | Notes |
|--------|-----------|-------|
| Event Discovery | `AttendeeHomeScreen` | Grid/list, 16 category filters, time filters (Today/Week/Month), "happening now", availability, sales countdown, recommendations |
| Search | `SearchScreen` | By name/location/category, recent searches, suggestions |
| Event Details | `EventDetailsScreen` | Hero poster (Coil), organizer + follow, Maps Compose map, description, age restriction, ticket types, ratings/reviews; actions: like, share (`Intent.ACTION_SEND`), report, add to calendar, buy |
| Ticket Purchase | `TicketPurchaseScreen` | Type + quantity, payment method, price calc, calendar conflict check, order summary, T&Cs |
| Tickets | `TicketsScreen` | All tickets, filter Upcoming/Past/All, grid/list toggle, quick QR |
| Ticket Detail | `TicketDetailScreen` | Full scannable QR, event info, map, PDF generate/share, refund request |
| Favorites | `FavoriteEventsScreen` | Like/unlike, persisted, update notifications |

**Purchase flow:**
```
Select ticket type → Choose quantity → Select payment method →
Review order → Confirm payment → Success → View QR
```
Payment methods: MTN Mobile Money, Airtel Money, Card (Visa/Mastercard).

## 6.4 Organizer Features

| Screen | Composable | Notes |
|--------|-----------|-------|
| Dashboard | `OrganizerDashboardScreen` | Cards: total revenue, tickets sold, active/upcoming events; quick actions |
| Event Creation | `CreateEventWizard` | 3-step wizard (see below), draft auto-save (30s), validation, preview, edit mode |
| Event Management | via card/menu | Edit (pre-filled, warns if tickets sold), delete (confirm + attendee-count warning, blocked for ongoing) |
| QR Scanning | `QrScannerScreen` | **CameraX + ML Kit Barcode**; instant validation, ticket info, already-scanned detection, invalid/not-started alerts |
| Event Analytics | `EventAnalyticsScreen` | Revenue, tickets by type, attendance, velocity, timeline, peak periods; line/pie/bar charts (Vico) |
| Analytics Dashboard | `OrganizerAnalyticsDashboardScreen` | Detail screens: Revenue, Ticket, Audience, Marketing, Operations, Predictive |
| Attendee Management | list + export | View attendees, export CSV/PDF, check-in status, VIP flags |
| Scanner Devices | `feature/organizer/scanner/` | Pair via deep link, manage multiple devices, per-event assignment, status |

**Create Event Wizard steps:**
1. **Basic Info** — title, description, category (16), age restriction
2. **Date & Venue** — start/end date-time, venue name, address, map location picker (Maps Compose + Places)
3. **Tickets & Media** — ticket types (name, price, qty, sale dates), poster upload (min 900×1125), image validation

**Scan validation results:** ✅ Valid (green) · ⚠️ Already scanned (yellow) · ❌ Invalid (red) · 🔒 Not started (gray).

## 6.5 Scanner Features

| Screen | Composable | Notes |
|--------|-----------|-------|
| Scanner Connect | `ScannerConnectScreen` | CameraX preview, torch toggle, responsive frame, live validation |
| Deep Link Pairing | `ScannerPairingScreen` | Scan pairing QR (`eventpass://pair`), auto device registration, event assignment |

## 6.6 Onboarding

- **Marketing onboarding** (`OnboardingScreen`): Welcome → Role select → Basic info → Personalization (interests) → Permissions → Completion.
- **Organizer onboarding** (`BecomeOrganizerGraph`, 5 steps): Profile completion → Identity verification (National ID) → Contact info → Payout setup (Mobile Money/Bank) → Terms agreement.

## 6.7 Refund System

- `RefundRequestScreen` — reason selection, amount, policy, tracking
- `OrganizerRefundScreen` — pending requests, approve/reject with notes, batch ops

## 6.8 Event Cancellation

- `EventCancellationFlowScreen` — reason, impact preview (attendees/revenue), compensation plan, attendee notification, automatic refunds

## 6.9 Support System

- `SupportCenterScreen` — FAQ, help articles, submit ticket, contact options
- `HelpCenterScreen` — searchable articles, category nav, related topics
- `TroubleshootingScreen` — common issues, step-by-step, device diagnostics

## 6.10 Profile & Settings

- `EditProfileScreen` — name, photo, contact methods, DOB, location
- `NotificationSettingsScreen` — reminders, purchase confirmations, updates, recommendations, marketing (opt-in), quiet hours; backed by notification channels
- `PaymentMethodsScreen` — add/remove, default, card scanning
- `NationalIDVerificationScreen` — ID capture (front/back), validation, status

## 6.11 Advanced Features

### 6.11.1 Credit Card Scanner
**`core/util/CardScanner.kt`** — CameraX + **ML Kit Text Recognition** on-device OCR; extracts
card number, expiry, name; Luhn validation; brand detection (Visa/Mastercard/Amex/Discover);
**no images stored**.

### 6.11.2 PDF Ticket Generator
**`core/util/PdfGenerator.kt`** — `android.graphics.pdf.PdfDocument`; poster color extraction
(Palette API), gradient header, embedded QR, print-optimized, share via `FileProvider` +
`Intent.ACTION_SEND`.

### 6.11.3 Calendar Conflict Detection
**`data/repository/CalendarRepository.kt`** — query `CalendarContract.Events` (READ_CALENDAR);
conflict types exact/partial/adjacent; warning UI + override; read-only.

### 6.11.4 Recommendation Engine
**`domain/usecase/RecommendationUseCase.kt`** — same scoring model as iOS:

| Factor | Points | | Factor | Points |
|--------|--------|-|--------|--------|
| Category Match | 40 | | Nearby Event | 15 |
| Purchase History | 35 | | Upcoming Soon | 15 |
| Followed Organizer | 30 | | Popular Event | 10 |
| Like History | 25 | | This Weekend | 10 |
| Happening Now | 25 | | Price Match | 8 |
| Same City | 20 | | High Rating / Free / Recently Added | 5 each |

### 6.11.5 Guest Browsing
Browse events + view details without an account; auth prompts gate protected actions
(purchase, favorite, profile) via placeholder Composables with sign-in CTAs.

---

# 7. Data Models

Kotlin `data class`es in `domain/model/`. Use `kotlinx.serialization` for JSON and `UUID`
(or `String`) for IDs. Room entities live separately in `data/local/room/` and map to these.

## 7.1 User

```kotlin
data class User(
    val id: UUID,
    var firstName: String,
    var lastName: String,
    var email: String? = null,
    var phoneNumber: String? = null,
    var profileImageUrl: String? = null,
    var role: UserRole,
    val dateJoined: Long,                 // epoch millis
    var isEmailVerified: Boolean = false,
    var isPhoneVerified: Boolean = false,
    var isVerified: Boolean = false,
    var nationalIdNumber: String? = null,
    var isAttendeeRole: Boolean = true,
    var isOrganizerRole: Boolean = false,
    var currentActiveRole: UserRole,
    var favoriteEventIds: List<UUID> = emptyList(),
    var followedOrganizerIds: List<UUID> = emptyList(),
    var favoriteEventTypes: List<String> = emptyList(),
    var hasCompletedOnboarding: Boolean = false
)
```

## 7.2 Event

```kotlin
data class Event(
    val id: UUID,
    var title: String,
    var description: String,
    var organizerId: UUID,
    var organizerName: String,
    var posterUrl: String? = null,
    var category: EventCategory,
    var startDate: Long,
    var endDate: Long,
    var venue: Venue,
    var ticketTypes: List<TicketType>,
    var status: EventStatus,
    var rating: Double = 0.0,
    var totalRatings: Int = 0,
    var likeCount: Int = 0,
    var ageRestriction: AgeRestriction
)

data class Venue(
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
)
```

## 7.3 Ticket

```kotlin
data class Ticket(
    val id: UUID,
    val ticketNumber: String,
    val orderNumber: String,
    val eventId: UUID,
    val eventTitle: String,
    val eventDate: Long,
    val eventEndDate: Long,
    val eventVenue: String,
    val ticketType: TicketType,
    val userId: UUID,
    val purchaseDate: Long,
    var scanStatus: TicketScanStatus = TicketScanStatus.UNUSED,
    var scanDate: Long? = null,
    val qrCodeData: String,
    var userRating: Double? = null
)
```

## 7.4 TicketType

```kotlin
data class TicketType(
    val id: UUID,
    var name: String,
    var price: Double,
    var quantity: Int,
    var sold: Int = 0,
    var description: String? = null,
    var perks: List<String> = emptyList(),
    var saleStartDate: Long,
    var saleEndDate: Long,
    var isUnlimitedQuantity: Boolean = false
)
```

## 7.5 OrganizerProfile

```kotlin
data class OrganizerProfile(
    var publicEmail: String,
    var publicPhone: String,
    var brandName: String? = null,
    var website: String? = null,
    var instagramHandle: String? = null,
    var twitterHandle: String? = null,
    var facebookPage: String? = null,
    var followerCount: Int = 0,
    var completedOnboardingSteps: List<OrganizerOnboardingStep> = emptyList(),
    var payoutMethod: PayoutMethod? = null,
    var agreedToTermsDate: Long? = null
)
```

## 7.6 OrganizerAnalytics

```kotlin
data class OrganizerAnalytics(
    val eventId: UUID,
    val eventTitle: String,
    val lastUpdated: Long,
    val revenue: Double,
    val ticketsSold: Int,
    val totalCapacity: Int,
    val attendanceRate: Double,
    val ticketVelocity: Double,
    val totalAttendees: Int,
    val repeatAttendees: Int,
    val eventViews: Int,
    val uniqueViews: Int,
    val conversionRate: Double,
    val checkinRate: Double,
    val grossRevenue: Double,
    val netRevenue: Double,
    val platformFees: Double,
    val healthScore: Int
)
```

## 7.7 Enumerations

```kotlin
enum class UserRole { ATTENDEE, ORGANIZER }

enum class EventCategory {                 // 16 categories
    MUSIC, ARTS_CULTURE, CONCERTS, SPORTS_WELLNESS,
    TECHNOLOGY, FUNDRAISING, COMEDY, POETRY,
    DRAMA, EXHIBITIONS, NETWORKING, EDUCATION,
    FOOD, NIGHTLIFE, FESTIVALS, OTHER
}

enum class EventStatus { DRAFT, PUBLISHED, ONGOING, COMPLETED, CANCELLED }

enum class TicketScanStatus { UNUSED, SCANNED, EXPIRED }

enum class PaymentMethod { MTN_MOMO, AIRTEL_MONEY, CARD }

enum class RefundStatus { PENDING, APPROVED, REJECTED, PROCESSING, COMPLETED, FAILED }
```

---

# 8. Repositories & Services

## 8.1 Pattern

All repositories are **interfaces in `domain/repository/`** with implementations in
`data/repository/` (`Mock*` now, real later), bound via Hilt (§4.5). Suspend functions return
domain models; observable streams return `Flow`.

## 8.2 Core Repositories

```kotlin
interface AuthRepository {
    val authState: StateFlow<AuthState>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(user: User, password: String): Result<User>
    suspend fun sendOtp(phoneNumber: String): Result<Unit>
    suspend fun verifyOtp(phoneNumber: String, code: String): Result<User>
    suspend fun logout()
    fun currentUser(): User?
}

interface EventRepository {
    fun observeEvents(): Flow<List<Event>>
    suspend fun getEvent(id: UUID): Result<Event>
    suspend fun createEvent(event: Event): Result<Event>
    suspend fun updateEvent(event: Event): Result<Unit>
    suspend fun deleteEvent(id: UUID): Result<Unit>
    suspend fun searchEvents(query: String): Result<List<Event>>
    suspend fun rateEvent(id: UUID, rating: Double, review: String?): Result<Unit>
}

interface TicketRepository {
    suspend fun purchaseTickets(event: Event, type: TicketType, quantity: Int): Result<List<Ticket>>
    fun observeUserTickets(): Flow<List<Ticket>>
    suspend fun scanTicket(qrCode: String): Result<Ticket>
    suspend fun validateTicket(code: String): Result<TicketValidationResult>
    suspend fun rateTicket(ticketId: UUID, rating: Double): Result<Unit>
}

interface PaymentRepository {
    suspend fun processPayment(amount: Double, method: PaymentMethod): Result<Payment>
    suspend fun verifyPayment(reference: String): Result<PaymentStatus>
    fun observePaymentHistory(): Flow<List<Payment>>
}
```

> Use `Result<T>` (or a sealed `Resource<T>`) instead of thrown exceptions across boundaries;
> it plays better with Compose state than try/catch.

## 8.3 Additional Repositories

| Repository | Purpose |
|------------|---------|
| RefundRepository | Refund request + processing |
| AppNotificationRepository | In-app notifications |
| CalendarRepository | Calendar conflict queries (CalendarContract) |
| UserLocationRepository | Location services (Fused Location Provider) |
| UserPreferencesRepository | Settings (DataStore) |
| EventFilterRepository | Event filtering |
| RecommendationRepository | Recommendation scoring inputs |
| NotificationAnalyticsRepository | Notification tracking |

## 8.4 Services

| Service | Android implementation |
|---------|------------------------|
| ScannerSessionService | Pairing session lifecycle |
| AttendeeExportService | CSV/PDF export (`PdfDocument`, CSV writer, `FileProvider` share) |
| EventReportExportService | Report export |
| InAppNotificationManager | Snackbar/in-app banner dispatch |
| FavoriteManager / FollowManager | Local favorite/follow state (DataStore + repo) |

---

# 9. UI Components

Reusable Composables in `ui/components/`. Each has a `@Preview`.

## 9.1 Event Components
- **`EventCard`** — Coil poster + gradient overlay, title, category badge, date/location/price, like button, status indicators
- **`CategoryTile`** — icon + label, selection state, `LazyRow` horizontal scroll

## 9.2 Form Components
- **`AppTextField`** (validated), `PhoneField`, `EmailField`, `PasswordField` (visibility toggle), `OtpInput` (6 digits)

## 9.3 Analytics Components
- **`LineChart` / `BarChart` / `PieChart`** — via **Vico** (or custom `Canvas`)
- **`MetricCard`, `TrendIndicator`, `ComparisonView`** — dashboard tiles

## 9.4 Common Components

| Component | Purpose |
|-----------|---------|
| `LoadingView` | Loading states (`CircularProgressIndicator`) |
| `AppTopBar` | `TopAppBar` navigation headers |
| `NotificationBadge` | `BadgedBox` unread count |
| `VerificationRequiredOverlay` | ID-verification gate |
| `PulsingDot` | Live indicator (`rememberInfiniteTransition`) |
| `SalesCountdownTimer` | Ticket-sale countdown |
| `AnimatedLikeButton` | Like toggle animation |

---

# 10. Utilities

`core/util/`.

## 10.1 Date Utilities (`DateUtils.kt`)
Use **`java.time`** (`LocalDateTime`, `ZonedDateTime`, `Duration`):
- `formatEventDateTime()`, `formatEventFullDateTime()`, `formatRelativeTime()` ("2 hours ago")
- `isToday()`, `isThisWeek()`, `isThisMonth()`

## 10.2 QR Code Generator (`QrGenerator.kt`)
- Generate with **ZXing** (`QRCodeWriter` → `Bitmap`) or ML Kit; render in Compose via `Image(bitmap)`.

## 10.3 Haptic Feedback (`Haptics.kt`)
- `LocalHapticFeedback` / `view.performHapticFeedback(...)`: `light()`, `medium()`, `success()`, `error()`, `warning()`, `selection()`

## 10.4 Image Utilities

| Utility | Android equivalent |
|---------|--------------------|
| ImageCompressor | `Bitmap.compress(JPEG, quality)` |
| ImageValidator | Size/format checks |
| ImageStorage | Coil disk cache / internal storage |
| ImageColorExtractor | **Palette** API (dominant/vibrant color) |

## 10.5 Responsive / Adaptive
- **`WindowSizeClass`** (Material 3 adaptive) for phone vs tablet/foldable layouts — the analog of iOS `ResponsiveSize` + iPad `.stack` handling. Use `Modifier.widthIn(max = …)` to cap content width on large screens (QR views, scanner frames).

---

# 11. Configuration

## 11.1 Role Configuration (`core/config/RoleConfig.kt`)

```kotlin
object RoleConfig {
    val AttendeePrimary = Color(0xFFFF7A00)
    val OrganizerPrimary = Color(0xFFFFA500)
    val LightBackground = Color(0xFFFBFBF7)
    val DarkBackground = Color(0xFF000000)
    val HappeningNow = Color(0xFF7CFC66)

    fun primaryColor(role: UserRole): Color = when (role) {
        UserRole.ATTENDEE -> AttendeePrimary
        UserRole.ORGANIZER -> OrganizerPrimary
    }
    fun accentColor(role: UserRole): Color = primaryColor(role)
}
```

## 11.2 Preference Keys (DataStore) (`core/config/PreferenceKeys.kt`)

DataStore replaces `@AppStorage`/UserDefaults.

```kotlin
object PreferenceKeys {
    val HAS_SEEN_ONBOARDING = booleanPreferencesKey("has_seen_onboarding")
    val HAS_CHOSEN_AUTH_METHOD = booleanPreferencesKey("has_chosen_auth_method")
    val CURRENT_USER_ID = stringPreferencesKey("current_user_id")
    val USER_ROLE = stringPreferencesKey("user_role")
    val FAVORITE_EVENT_IDS = stringSetPreferencesKey("favorite_event_ids")
    // notification prefs, etc.
}
```

## 11.3 Build Config
- `build.gradle.kts` product flavors or `BuildConfig` fields for `debug` (mock data) vs `release` (real APIs).
- Store API keys (Maps) in `local.properties` → `manifestPlaceholders`, never in VCS.

---

# 12. Testing Strategy

## 12.1 Unit Tests (JVM)
Target ViewModels, use cases, domain logic, repositories. Tools: **JUnit5**, **MockK**,
**Turbine** (Flow testing), **kotlinx-coroutines-test** (`runTest`, `StandardTestDispatcher`).

| Target | Goal |
|--------|------|
| ViewModels | 80%+ |
| Domain / use cases | 90%+ |
| Repositories | 70%+ |

## 12.2 Mock Implementations
`MockAuthRepository`, `MockEventRepository`, `MockTicketRepository`, `MockPaymentRepository`
(and the rest) — the default DI bindings, also reused as test doubles.

## 12.3 UI Tests
**Compose UI test** (`createAndroidComposeRule`, `composeTestRule.onNodeWith…`) for critical
flows: authentication (login/register/OTP), ticket purchase, event creation, QR scanning.
Use **Hilt test** (`@HiltAndroidTest`, `@BindValue`) to inject fakes.

## 12.4 Previews
Every screen and component ships `@Preview` (light + dark) with mock data — the analog of
SwiftUI `#Preview`.

---

# 13. Deployment

## 13.1 Build Types

| Build type | Purpose |
|------------|---------|
| `debug` | Development, mock data, no minify |
| `release` | Production, real APIs, R8 minify + shrink |

Ship an **Android App Bundle (`.aab`)** to Play; enable R8 (`isMinifyEnabled = true`,
`isShrinkResources = true`) with a tuned `proguard-rules.pro` (keep rules for
kotlinx-serialization, Room, Retrofit models).

## 13.2 Play Store Requirements
- `minSdk 26`, `targetSdk 35` (meet Play's current target-API policy)
- Phone + tablet support; dynamic text scaling (`sp`); dark theme; edge-to-edge
- Accessibility: 48 dp touch targets, content descriptions, TalkBack pass
- Data safety form, privacy policy URL, signed release (Play App Signing)

## 13.3 Privacy

**Data collected:** profile info, location (with permission), purchase history, event interactions.

**Privacy features:** on-device card scanning (no card images stored), calendar read-only,
location opt-in, Photo Picker (no broad storage permission), notification opt-in (API 33+
runtime `POST_NOTIFICATIONS`).

---

# 14. Appendix

## 14.1 Suggested Package Count (target parity)

| Layer | Approx. files |
|-------|---------------|
| app (Application, MainActivity, navigation) | ~6 |
| feature (screens + viewmodels) | ~80 |
| domain (models, repo interfaces, use cases) | ~25 |
| data (repos, room, datastore, mock) | ~25 |
| ui (designsystem + components) | ~25 |
| core (di, util, config) | ~25 |

## 14.2 Feature Count Summary

| Category | Count |
|----------|-------|
| Authentication | 4 |
| Attendee | 12 |
| Organizer | 15 |
| Scanner | 4 |
| Common | 14 |
| Support | 6 |
| **Total** | **55+** |

## 14.3 Design Token Count

| Category | Count |
|----------|-------|
| Colors | 18 | 
| Typography | 18 |
| Spacing | 12 |
| Corner Radius | 10 |
| Elevation/Shadow | 5 |
| Button Dimensions | 10 |
| Animations | 5 |
| **Total** | **78** |

## 14.4 Recommended Libraries (versions indicative — pin latest stable)

| Purpose | Library |
|---------|---------|
| DI | `com.google.dagger:hilt-android` |
| Navigation | `androidx.navigation:navigation-compose` |
| Async | `org.jetbrains.kotlinx:kotlinx-coroutines-android` |
| Local DB | `androidx.room:room-runtime` + `room-ktx` |
| Prefs | `androidx.datastore:datastore-preferences` |
| Networking | `com.squareup.retrofit2:retrofit` + `kotlinx-serialization-converter` |
| Images | `io.coil-kt:coil-compose` |
| Camera | `androidx.camera:camera-camera2` / `camera-lifecycle` / `camera-view` |
| Barcode/QR scan | `com.google.mlkit:barcode-scanning` |
| OCR (card) | `com.google.mlkit:text-recognition` |
| QR generate | `com.google.zxing:core` |
| Maps | `com.google.maps.android:maps-compose` |
| Location | `com.google.android.gms:play-services-location` |
| Charts | `com.patrykandpatrick.vico:compose-m3` |
| Palette | `androidx.palette:palette-ktx` |
| Auth (Google) | `androidx.credentials` + `googleid` |
| Testing | JUnit5, MockK, Turbine, `kotlinx-coroutines-test`, Compose UI test, Hilt test |

## 14.5 iOS ↔ Android Concept Mapping

| iOS / SwiftUI | Android / Kotlin |
|---------------|------------------|
| `View` (struct) | `@Composable fun` |
| `@State` / `@Binding` | `remember { mutableStateOf() }` / hoisted state |
| `@StateObject` / `@ObservedObject` | `hiltViewModel()` + `StateFlow` |
| `@EnvironmentObject` | Hilt injection / `CompositionLocal` |
| `@AppStorage` | DataStore (Preferences) |
| `ObservableObject` / `@Published` | `ViewModel` + `MutableStateFlow` |
| `async/await` | `suspend` + coroutines |
| `Combine` publishers | `Flow` / `StateFlow` |
| `NavigationStack` / `NavigationLink` | `NavHost` / `navController.navigate` |
| `TabView` | `Scaffold` + `NavigationBar` + nested `NavHost` |
| `.sheet` | `ModalBottomSheet` / dialog destination |
| `.fullScreenCover` | full-screen `composable` destination / graph |
| `onOpenURL` + URL scheme | intent filter + `navDeepLink` |
| `AsyncImage` | Coil `AsyncImage` |
| Core Data | Room |
| UserDefaults | DataStore |
| CryptoKit | `MessageDigest` / Tink |
| MapKit | Maps Compose |
| AVFoundation (capture) | CameraX |
| Vision (OCR/QR) | ML Kit |
| EventKit | CalendarContract |
| PDFKit | `PdfDocument` |
| Swift Charts | Vico |
| `ServiceContainer` (manual DI) | Hilt modules |
| `#Preview` | `@Preview` |
| Dynamic Type | `sp` units (auto-scales) |
| Size classes / iPad `.stack` | `WindowSizeClass` |

## 14.6 References
- [Android app architecture guide](https://developer.android.com/topic/architecture)
- [Jetpack Compose docs](https://developer.android.com/jetpack/compose)
- [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- [Hilt DI](https://developer.android.com/training/dependency-injection/hilt-android)
- [Material 3 for Compose](https://developer.android.com/jetpack/compose/designsystems/material3)
- [EventPassUG iOS Documentation](./EventPassUG_Complete_Documentation.md)

---

**Document Version**: 1.0 (Android port)
**Last Updated**: July 2026
**Maintained By**: EventPassUG Development Team

---

*This document mirrors the iOS spec (`EventPassUG_Complete_Documentation.md`). When the iOS
feature set changes, update both docs. For any behavioral question, the iOS doc is the
source of truth for **what**; this doc is the source of truth for **how** on Android.*
