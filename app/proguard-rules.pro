# ═══════════════════════════════════════════════════════════
# ProGuard / R8 Rules — 2048 Puzzle Game
# ═══════════════════════════════════════════════════════════

# ─── Keep annotations ────────────────────────────────────
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses

# ─── AndroidX Lifecycle / ViewModel ──────────────────────
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep class * extends androidx.lifecycle.AndroidViewModel { *; }

# ─── Jetpack Compose ─────────────────────────────────────
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }
-keepclassmembers class androidx.compose.** { *; }

# Keep Compose runtime stability
-keep class androidx.compose.runtime.** { *; }

# ─── Kotlin Coroutines ───────────────────────────────────
-dontwarn kotlinx.coroutines.**
-keep class kotlinx.coroutines.** { *; }

# ─── Kotlin Serialization (future-proof) ─────────────────
-keepattributes RuntimeVisibleAnnotations

# ─── App-specific: keep GameEngine and data classes ──────
-keep class com.mkggames.puzzle2048.engine.GameEngine { *; }
-keep class com.mkggames.puzzle2048.engine.GameEngine$* { *; }
-keep class com.mkggames.puzzle2048.viewmodel.GameState { *; }
-keep class com.mkggames.puzzle2048.viewmodel.GameStatus { *; }

# ─── Remove logging in release ───────────────────────────
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
}
