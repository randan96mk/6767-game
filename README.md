# 2048 Puzzle Game

A classic **2048** number puzzle game built with Jetpack Compose and a dark cyberpunk neon theme.

## How to Play

- **Swipe** in any direction (up, down, left, right) to slide all tiles
- When two tiles with the **same number** collide, they **merge into one** (doubled value)
- A new tile spawns after each move: **2** (90% chance) or **4** (10% chance)
- Reach the **2048** tile to win!
- You can keep playing after winning to chase a higher score

### Merge Track

```
2 → 4 → 8 → 16 → 32 → 64 → 128 → 256 → 512 → 1024 → 2048
```

---

## Quick Start

### Prerequisites
- **Android Studio** Hedgehog (2023.1.1) or newer
- **JDK 17**
- **Android SDK 34**

### Setup
1. Open Android Studio
2. Select **"Open an existing project"**
3. Navigate to the project folder
4. Wait for Gradle sync to complete
5. Click **Run** (select a device/emulator)

### Build Debug APK
```bash
./gradlew assembleDebug
```

### Build Release AAB (for Play Store)
```bash
./gradlew bundleRelease
```
AAB output: `app/build/outputs/bundle/release/app-release.aab`

### Build Release APK
```bash
./gradlew assembleRelease
```
APK output: `app/build/outputs/apk/release/app-release.apk`

---

## Project Structure

```
├── app/src/main/
│   ├── java/com/mkggames/puzzle2048/
│   │   ├── MainActivity.kt              # Entry point, sets up Compose UI
│   │   ├── engine/
│   │   │   └── GameEngine.kt            # Pure game logic (grid, merge, win/loss)
│   │   ├── viewmodel/
│   │   │   └── GameViewModel.kt         # MVVM state management, undo, persistence
│   │   └── ui/
│   │       ├── GameScreen.kt            # All composables (header, board, tiles, overlays)
│   │       └── theme/
│   │           └── TileColors.kt        # Dark cyberpunk color palette + tile styling
│   ├── res/
│   │   ├── drawable/                    # Adaptive icon vectors
│   │   ├── mipmap-*/                    # Launcher icons (mdpi through xxxhdpi)
│   │   ├── mipmap-anydpi-v26/           # Adaptive icons for API 26+
│   │   └── values/                      # strings.xml, styles.xml
│   └── AndroidManifest.xml
├── docs/
│   └── privacy-policy.html             # Privacy policy (GitHub Pages)
├── store-listing/
│   ├── icon-512x512.png                # Play Store hi-res icon
│   ├── feature-graphic-1024x500.png    # Play Store feature graphic
│   └── STORE_LISTING.md               # Full store listing text + metadata
├── keystore.properties                 # Signing credentials (git-ignored)
├── build.gradle.kts                    # Root build config
├── app/build.gradle.kts                # App config with signing + bundling
├── settings.gradle.kts
└── gradle.properties
```

---

## Features

- 4x4 game grid with swipe gesture controls
- Classic 2048 merge logic (same tiles double on collision)
- Tile spawn: 2 (90%) or 4 (10%)
- Win detection at 2048, with option to continue playing
- Game Over detection (no valid moves remaining)
- Score tracking (current + all-time best via SharedPreferences)
- Undo last move (single step)
- Tile pop animations on spawn/merge
- Win and Game Over overlay screens with fade animations
- Move counter
- Dark cyberpunk neon theme with color-coded tiles and glow effects
- Adaptive launcher icons for all screen densities
- Portrait and landscape support
- No ads, no in-app purchases, no data collection
- Fully offline — no internet required

### Future Ideas
- Sound effects and haptic feedback
- Board size options (3x3, 5x5)
- Daily challenges
- Google Play leaderboard
- Share score as image

---

## Tech Stack

| Component | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM (ViewModel + StateFlow) |
| Persistence | SharedPreferences |
| Min SDK | API 26 (Android 8.0) |
| Target SDK | API 34 (Android 14) |
| Build | Gradle 8.5 + AGP 8.2 |
| Minification | R8 + ProGuard (release) |

**Zero external dependencies** — only AndroidX/Compose libraries.

---

## Release & Play Store Deployment

### Signing Setup
1. A release keystore is generated at `keystore/release-keystore.jks`
2. Credentials are in `keystore.properties` (git-ignored — do not commit)
3. The `app/build.gradle.kts` automatically loads signing config from `keystore.properties`

### Build Release Bundle
```bash
./gradlew bundleRelease
```
The signed AAB will be at `app/build/outputs/bundle/release/app-release.aab`.

### Play Store Checklist
| Step | Status |
|---|---|
| Google Play Developer account | Required ($25 one-time) |
| App signed with release key | Configured |
| App Bundle (.aab) built | `./gradlew bundleRelease` |
| Hi-res icon (512x512) | `store-listing/icon-512x512.png` |
| Feature graphic (1024x500) | `store-listing/feature-graphic-1024x500.png` |
| Screenshots (min 2) | Capture from emulator |
| Store listing text | `store-listing/STORE_LISTING.md` |
| Privacy policy hosted | `docs/privacy-policy.html` (GitHub Pages) |
| Content rating (IARC) | Fill in Play Console |
| Data safety form | No data collected |
| Target API compliance | SDK 34 (meets requirement) |

### Privacy Policy Hosting (GitHub Pages)
1. Go to repo Settings > Pages
2. Source: **Deploy from a branch**
3. Branch: **main**, folder: **/docs**
4. Save — policy will be live at:
   `https://randan96mk.github.io/6767-game/privacy-policy.html`

### Play Console Upload Steps
1. Go to [play.google.com/console](https://play.google.com/console)
2. Create app > fill in app details
3. Upload AAB to **Internal testing** track first
4. Complete store listing, content rating, data safety
5. Test via internal track link
6. Promote to **Production** when ready
7. Submit for Google review (1-7 days)

---

## Privacy

This app collects **zero personal data**. No analytics, no ads, no tracking, no network access. Your best score is stored locally on your device only and is deleted when you uninstall the app.

[Full Privacy Policy](docs/privacy-policy.html)

---

## License

MIT — Free to use, modify, and distribute.
