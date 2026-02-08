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

### Build APK
```bash
./gradlew assembleRelease
```
APK output: `app/build/outputs/apk/release/app-release.apk`

---

## Project Structure

```
app/src/main/
├── java/com/game/puzzle6767/
│   ├── MainActivity.kt              # Entry point, sets up Compose UI
│   ├── engine/
│   │   └── GameEngine.kt            # Pure game logic (grid, merge, win/loss detection)
│   ├── viewmodel/
│   │   └── GameViewModel.kt         # MVVM state management, undo, score persistence
│   └── ui/
│       ├── GameScreen.kt            # All composables (header, board, tiles, overlays)
│       └── theme/
│           └── TileColors.kt        # Dark cyberpunk color palette + per-tile styling
├── res/
│   ├── drawable/
│   │   ├── ic_launcher_background.xml   # Adaptive icon background vector
│   │   └── ic_launcher_foreground.xml   # Adaptive icon foreground vector
│   ├── mipmap-anydpi-v26/
│   │   ├── ic_launcher.xml              # Adaptive icon (API 26+)
│   │   └── ic_launcher_round.xml        # Round adaptive icon (API 26+)
│   ├── mipmap-mdpi/                     # 48×48 launcher icons
│   ├── mipmap-hdpi/                     # 72×72 launcher icons
│   ├── mipmap-xhdpi/                    # 96×96 launcher icons
│   ├── mipmap-xxhdpi/                   # 144×144 launcher icons
│   ├── mipmap-xxxhdpi/                  # 192×192 launcher icons
│   └── values/
│       ├── strings.xml
│       └── styles.xml
├── AndroidManifest.xml
build.gradle.kts                     # Root build config
app/build.gradle.kts                 # App module dependencies
settings.gradle.kts                  # Gradle settings
gradle.properties                    # Gradle JVM args
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

**Zero external dependencies** — only AndroidX/Compose libraries.

---

## License

MIT — Free to use, modify, and distribute.
