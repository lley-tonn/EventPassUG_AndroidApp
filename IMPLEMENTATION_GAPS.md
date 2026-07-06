# EventPassUG Android — Implementation Gap Report

Audit of the current Android codebase against **EventPassUG_Android_Documentation.md**.

Legend: ✅ implemented · 🟡 partial / stubbed / mock-only · ❌ missing

> Scope note: the app currently runs entirely on **in-memory mock repositories**. There is
> no Room persistence, no network layer, and no real payment/SMS/KYC integrations yet.

---

## 1. Navigation & App Entry (§4A)

| Item | Status | Notes |
|------|--------|-------|
| `EventPassApplication` (@HiltAndroidApp) | ✅ | Present |
| `MainActivity` single-activity + Compose | ✅ | Present |
| Root decision machine (onboarding → auth → main) | 🟡 | `EventPassNavHost` uses `hasCompletedOnboarding` + `authState`, not the documented `hasSeenOnboarding` / `hasChosenAuthMethod` DataStore flags. No `AppRoot`/`AppViewModel` split. |
| Role-aware bottom tabs | ✅ | Home + (Tickets \| Dashboard) + Profile; swaps on role change |
| Guest mode | 🟡 | Implemented via a **sign-in-required dialog** gating protected routes — not the documented `GuestTicketsPlaceholder` / `GuestProfilePlaceholder` screens |
| Deep linking `eventpass://` (pair/event/ticket) | ❌ | No intent-filter in the manifest, no `DeepLinkManager`, no `navDeepLink` |
| Notification channels created on launch | ❌ | Not created in `EventPassApplication.onCreate()` |
| Typed routes (`@Serializable`) | ❌ | Uses stringly-typed routes |
| Notification center / preference-onboarding overlays | ❌ | Not present |

---

## 2. Authentication (§6.1, §8.2)

| Item | Status | Notes |
|------|--------|-------|
| Email/password sign in + sign up | 🟡 | Works but **mock accepts any credentials**; no password hashing/validation |
| Documented test users (john@…, bob@events.com, etc.) | ❌ | Not seeded; any email logs in as attendee |
| Phone OTP | 🟡 | Screens exist; mock accepts **any** 6-digit code (doc specifies `123456`) |
| Social login (Google/Facebook/Apple) | ❌ | `signInWithGoogle` is a mock stub; no Credential Manager / FB SDK |
| `AuthRepository` interface shape | 🟡 | Method names differ from doc (`signInWithEmail` vs `login`, `signUpWithEmail` vs `register`, `signOut` vs `logout`); no `authState: StateFlow` on the repo |

---

## 3. Attendee Features (§6.3)

| Screen | Status | Notes |
|--------|--------|-------|
| `AttendeeHomeScreen` | ✅ | Discovery, categories, inline search |
| `SearchScreen` | 🟡 | UI built; results are placeholder sample data |
| `EventDetailsScreen` | ❌ | Route registered but body is a `TODO` (blank) |
| `TicketPurchaseScreen` | ❌ | Route registered, blank |
| `PaymentConfirmationScreen` / `TicketSuccessScreen` | ❌ | Missing |
| `MyTicketsScreen` | ✅ | Filters, list |
| `TicketDetailScreen` | 🟡 | Built; no PDF export / real QR / refund entry wired |
| `FavoriteEventsScreen` | ❌ | Missing |
| Calendar conflict check on purchase | ❌ | Missing |

---

## 4. Organizer Features (§6.4)

| Screen | Status | Notes |
|--------|--------|-------|
| `OrganizerHomeScreen` | ✅ | Greeting, status filters, event list (sample data) |
| `OrganizerDashboardScreen` | 🟡 | Full UI; **all metrics are `DashboardData.sample`** |
| `CreateEvent` 3-step wizard | 🟡 | UI complete; no draft auto-save, no poster upload, no map/date pickers (stubs), publish is a no-op |
| `EditEventScreen` / `ManageEventTicketsScreen` | ❌ | Missing |
| `EventAnalyticsScreen` (per event) | ❌ | `OrganizerAnalyticsViewModel` exists; no screen |
| Analytics detail screens (Revenue/Ticket/Audience/Marketing/Operations/Predictive) | ❌ | Missing |
| Attendee management + CSV/PDF export | 🟡 | `AttendeeExportService` exists; no UI |
| Charts (Vico) | ❌ | No charting library wired |

---

## 5. Scanner (§6.5)

| Item | Status | Notes |
|------|--------|-------|
| `ScanTicketScreen` (CameraX + ML Kit QR) | ✅ | Working camera + QR detection; result handling is a no-op |
| `ScannerDevicesScreen` (event picker) | 🟡 | UI built, sample events; tapping an event is a no-op |
| `ScannerConnectScreen` / torch / live validation | ❌ | Missing |
| `ScannerPairingScreen` + `eventpass://pair` | ❌ | Missing |
| `PairScannerScreen` / `ManageScannerDevicesScreen` | ❌ | Missing |
| `ScannerSessionService` | 🟡 | File exists; not wired to UI |

---

## 6. Onboarding & Become-Organizer (§6.6)

| Item | Status | Notes |
|------|--------|-------|
| Marketing onboarding (Welcome→Role→Info→Interests→Permissions→Completion) | ✅ | Full 6-step flow |
| Become-Organizer 5 steps (Profile→Identity→Contact→Payout→Terms) | ✅ | Complete + **fully functional on mock data** (email/phone/ID verification mutate the mock user; completing promotes to verified organizer and lands on Dashboard) |

---

## 7. Refunds, Cancellation, Support (§6.7–6.9)

| Screen | Status | Notes |
|--------|--------|-------|
| `RefundRequestScreen` / `OrganizerRefundScreen` | ❌ | `RefundViewModel` + `RefundRepository` exist; **no screens** |
| `EventCancellationFlowScreen` | ❌ | `EventCancellationViewModel` + `CancellationRepository` exist; **no screen** |
| `SupportCenterScreen` / `HelpCenterScreen` / `TroubleshootingScreen` | ❌ | Missing |

---

## 8. Profile & Settings (§6.10)

| Screen | Status | Notes |
|--------|--------|-------|
| `ProfileScreen` (settings hub) | ✅ | Verified/organizer states, switch role, sign in/out |
| `EditProfileScreen` | 🟡 | Built; name edits are not persisted |
| `ChangeEmail` / `AddPhone` / email+phone verification | ✅ | Mock verification mutates user |
| `NationalIDVerificationScreen` | 🟡 | Built; ID capture is a mock toggle (no real camera OCR) |
| `NotificationSettingsScreen` | ❌ | Route defined, no screen |
| `PaymentMethodsScreen` | ❌ | Route defined, no screen |
| Interests / Help / Terms&Privacy screens | ❌ | Missing |

---

## 9. Advanced Features (§6.11)

| Feature | Status | Notes |
|---------|--------|-------|
| Credit-card scanner (`CardScanner`, ML Kit Text) | ❌ | Missing |
| PDF ticket generator (`PdfGenerator`) | ❌ | Missing |
| Calendar conflict detection (`CalendarRepository`) | ❌ | Missing |
| Recommendation engine (`RecommendationUseCase`) | ❌ | Missing |
| Guest browsing | 🟡 | Guest can browse Home; protected actions gated by dialog (see §1) |

---

## 10. Data Models (§7)

| Model | Status |
|-------|--------|
| `User`, `Event`, `Ticket`, `TicketType`, `OrganizerProfile`, `OrganizerAnalytics` | ✅ Present (field parity largely matches; verify per-field if strict parity required) |
| Enums: `UserRole`, `EventCategory` (16), `EventStatus`, `TicketScanStatus`, `PaymentMethod`, `RefundStatus` | ✅ Present |

---

## 11. Repositories & Services (§8)

| Repository | Status |
|------------|--------|
| Auth, Event, Ticket, Refund, Cancellation, UserPreferences | ✅ (mock impls) |
| PaymentRepository | ❌ Missing |
| CalendarRepository | ❌ Missing |
| UserLocationRepository (Fused Location) | ❌ Missing |
| AppNotificationRepository | ❌ Missing |
| EventFilterRepository | ❌ Missing |
| RecommendationRepository | ❌ Missing |
| NotificationAnalyticsRepository | ❌ Missing |
| Persistence | ❌ **No Room** — `data/local/entity` & `dao` are empty; all data is in-memory |

---

## 12. Design Tokens (§5)

| Item | Status | Notes |
|------|--------|-------|
| Brand/semantic/neutral color tokens | ✅ | `EventPassColors` matches (primary `#FF7A00`, etc.) |
| **Organizer accent color** | 🟡 | Code uses `OrganizerAccent = #6366F1` (indigo); doc §6.2 specifies **`#FFA500`** (orange). Mismatch. |
| Typography / Spacing / Radii / Elevation tokens | ✅ | Present |

---

## 13. Config, Testing, Deployment (§11–13)

| Item | Status | Notes |
|------|--------|-------|
| `RoleConfig` (per-role accent/config) | ❌ | Not present as documented; role accent handled inline |
| `PreferenceKeys` / DataStore | 🟡 | DataStore used via `UserPreferencesRepository`; no dedicated `PreferenceKeys` |
| Unit tests (JVM) | ❌ | None found |
| UI tests | ❌ | None found |
| `@Preview`s | 🟡 | Present in `core:design`; sparse in feature screens |
| Deep-link / Play-store / privacy config | ❌ | Not configured |

---

## Highest-impact gaps (suggested priority)

1. **EventDetails → TicketPurchase → Payment → TicketSuccess** — the core attendee purchase funnel is entirely missing (routes are blank stubs).
2. **Real data layer** — Room persistence + wiring repositories to it (everything is mock/in-memory).
3. **Refund + Cancellation screens** — logic (ViewModels/repos) exists but has no UI.
4. **Deep linking + scanner pairing** — `eventpass://` scheme, `ScannerPairing`, device management.
5. **Organizer analytics detail screens** + charting library.
6. **Settings screens** — Notification settings, Payment methods, Support/Help.
7. **Conformance nits** — organizer accent color (`#FFA500`), documented test users, OTP `123456`, `AuthRepository` method naming, notification channels on launch.
