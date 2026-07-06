# EventPassUG — Android

Native **Android** event discovery & management app for Uganda — attendees find and buy
tickets; organizers create events, sell tickets, and scan attendees at the door. Built with
**Kotlin** and **Jetpack Compose**.

![Platform](https://img.shields.io/badge/platform-Android%207%2B-green)
![Language](https://img.shields.io/badge/Kotlin-2.x-blueviolet)
![UI](https://img.shields.io/badge/UI-Jetpack%20Compose-blue)
![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%2B%20Clean%20%2B%20Multi--module-orange)

---

## Quick start

```bash
cd android
# JDK 17 is required (the toolchain does not support JDK 21+/25)
JAVA_HOME=$(/usr/libexec/java_home -v 17) ./gradlew :app:assembleDebug
```

Open the `android/` directory in **Android Studio** (Giraffe+), let Gradle sync, then Run
`app` on an emulator or device (min SDK **24 / Android 7.0**).

## Tech stack

- **UI:** Jetpack Compose, Material 3, Material Icons Extended
- **Architecture:** MVVM + Clean Architecture, unidirectional data flow (`StateFlow`)
- **DI:** Hilt
- **Navigation:** Navigation Compose (single-Activity)
- **Async:** Kotlin Coroutines + Flow
- **Local:** DataStore (preferences); Room (planned)
- **Media/Camera:** Coil, CameraX + ML Kit Barcode (QR scanning)
- **Maps:** Maps Compose / Play Services Maps

## Module layout

```text
android/
├── app/                      # single Activity, navigation host, DI, mock repos, VMs
├── core/  (common, design, ui, domain, data)
└── feature/ (onboarding, auth, attendee, organizer, profile, become-organizer, notifications)
```

## Documentation

- **[EventPassUG_Android_Documentation.md](EventPassUG_Android_Documentation.md)** — the
  complete technical specification (architecture, navigation, design tokens, features, data
  models, repositories).
- **[IMPLEMENTATION_GAPS.md](IMPLEMENTATION_GAPS.md)** — current implementation status vs. the
  spec (what's done, partial, or missing).

## Status

Actively under construction. The UI for the core flows (onboarding, auth, attendee home/
tickets, profile, the full Become-an-Organizer flow, organizer home/dashboard, create-event
wizard, QR scanner) is in place, running on **mock in-memory data**. See
[IMPLEMENTATION_GAPS.md](IMPLEMENTATION_GAPS.md) for what remains.
