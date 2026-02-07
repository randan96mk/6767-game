# 🎮 2024 Puzzle Game

A fresh twist on the classic 2048 — merge **6s** and **7s** to reach **6767**!

## 🎯 What Makes 6767 Different from 2048?

| Feature | 2048 | |
|---|---|---|
| Starting tiles | 2 and 4 | **6** and **7** |
| Spawn ratio | 90% → 2, 10% → 4 | 90% → **6**, 10% → **7** |
| Win target | 2048 | **6767** |
| Merge tracks | Single (powers of 2) | **Dual tracks!** |
| Theme | Warm orange/yellow | **Dark cyberpunk neon** |

### 🧠 The Strategic Twist: Dual Merge Tracks

Unlike 2048's single track, 6767 has **two parallel tracks** that NEVER cross:

**6-Track (common, slower):**
```
6 → 12 → 24 → 48 → 96 → 192 → 384 → 768 → 1536 → 3072 → 6144 → 12288 ✓
```
Takes **11 merges** to win (reaches 12288 ≥ 6767)

**7-Track (rare, faster):**
```
7 → 14 → 28 → 56 → 112 → 224 → 448 → 896 → 1792 → 3584 → 7168 ✓
```
Takes **10 merges** to win (reaches 7168 ≥ 6767)

**The dilemma:** 7-tiles are rare (10% spawn) but reach the goal 1 merge faster.
Do you hoard 7s in a corner? Or focus on the abundant 6s? That's the strategy!

---

## 🚀 Quick Start

### Prerequisites
- **Android Studio** Hedgehog (2023.1.1) or newer
- **JDK 17**
- **Android SDK 34**

### Setup
1. Open Android Studio
2. Select **"Open an existing project"**
3. Navigate to the `6767-game/` folder
4. Wait for Gradle sync to complete
5. Click **Run ▶️** (select a device/emulator)

### Build APK
```bash
cd 6767-game
./gradlew assembleRelease
```
APK will be at: `app/build/outputs/apk/release/app-release.apk`

---

## 📁 Project Structure

```
6767-game/
├── app/src/main/java/com/game/puzzle6767/
│   ├── MainActivity.kt              # Entry point
│   ├── engine/
│   │   └── GameEngine.kt            # Pure game logic (testable)
│   ├── viewmodel/
│   │   └── GameViewModel.kt         # State management + undo
│   └── ui/
│       ├── GameScreen.kt            # All composables (screen, board, tiles, overlays)
│       └── theme/
│           └── TileColors.kt        # Color palette + tile styling
├── app/src/main/res/
│   └── values/
│       ├── strings.xml
│       └── styles.xml
├── build.gradle.kts                  # Root build
├── app/build.gradle.kts              # App dependencies
├── settings.gradle.kts
├── gradle.properties
└── README.md
```

---

## 🎨 Features

### MVP (Included)
- ✅ 4×4 game grid with swipe controls
- ✅ Dual-track merge system (6s and 7s)
- ✅ Score tracking (current + all-time best)
- ✅ Best score persistence (SharedPreferences)
- ✅ Win detection (tile ≥ 6767)
- ✅ Game Over detection
- ✅ "Continue playing" after winning
- ✅ Undo last move
- ✅ Tile pop animations
- ✅ Win/Game Over overlays with animations
- ✅ Dark cyberpunk neon theme
- ✅ Move counter
- ✅ Color-coded tiles (different colors per track)

### Future Ideas
- 🔮 Sound effects & haptic feedback
- 🔮 Board size options (3×3, 5×5)
- 🔮 Daily challenges
- 🔮 Google Play leaderboard
- 🔮 Share score as image
- 🔮 AdMob integration
- 🔮 Dark/Light theme toggle

---

## 🛠 Tech Stack

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

## 📄 License

MIT — Free to use, modify, and distribute.
