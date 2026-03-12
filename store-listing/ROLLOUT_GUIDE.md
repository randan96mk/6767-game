# Play Store Rollout Guide — Step 6: Release Notes → Review → Start Rollout

**App:** 2048 Puzzle Game
**Package:** `com.mkggames.puzzle2048`
**Version:** 1.1.0 (versionCode 3)
**Track:** Production

---

## Prerequisites (Steps 1–5 complete)

Before Step 6, verify:
- [x] AAB built and signed (`app/build/outputs/bundle/release/app-release.aab`)
- [x] AAB uploaded to Play Console (Internal Testing or Production track)
- [x] Store listing filled (title, description, screenshots, icon, feature graphic)
- [x] Content rating completed (IARC — Everyone / PEGI 3)
- [x] Data safety form completed (no data collected)
- [x] Privacy policy live at `https://randan96mk.github.io/6767-game/privacy-policy.html`

---

## Step 6A — Fill Release Notes ("What's New")

### In Play Console

1. Go to **Play Console → 2048 Puzzle Game → Production → Releases**
2. Click **Edit release** (or the current draft release)
3. Scroll to **"What's new in this release"**
4. Select language **English (United States)**
5. Paste the text below (≤ 500 characters):

```
v1.1.0 — What's New

• Sound effects: merge pops, tile spawns, win fanfare, and game over tone
• Haptic feedback on every merge and game event
• Help button with full in-game how-to-play instructions
• Improved tablet layout with centred game board
• Bug fixes and performance improvements
```

> Source file: `store-listing/changelogs/3.txt`

6. Click **Save**

---

## Step 6B — Review Release

### Pre-publish checklist in Play Console

| Section | What to verify |
|---|---|
| **App bundle** | versionCode = 3, versionName = 1.1.0 |
| **Supported devices** | Min SDK 26 (Android 8.0+), broad device coverage |
| **Store listing** | Title, short/full description, screenshots, icon, feature graphic all present |
| **Content rating** | IARC rating shown (Everyone / PEGI 3) |
| **Data safety** | "No data collected or shared" answers saved |
| **Privacy policy** | URL resolves to GitHub Pages page |
| **Release notes** | English text pasted (≤ 500 chars) |
| **Pricing** | Free |
| **Countries** | All countries / regions (or your chosen set) |

### Play Console review warnings

- Fix any **red errors** — release cannot be published until resolved
- Review **yellow warnings** — most can be dismissed for a free puzzle game
- Common warning to ignore: "Your app targets an older API" (only if targetSdk = 35, already compliant)

---

## Step 6C — Start Rollout

### Option A — Staged rollout (recommended for first production push)

1. Click **Start rollout to Production**
2. Set rollout percentage: **10%** (safe first push)
3. Click **Rollout** → **Confirm**
4. Monitor **Android vitals** and **Reviews** for 24–48 h
5. If no crash spikes: increase to **50%** → **100%**

### Option B — Full rollout

1. Click **Start rollout to Production**
2. Leave percentage at **100%**
3. Click **Rollout** → **Confirm**

> **Note:** Google review takes 1–7 days for first-time submissions. Subsequent updates are usually reviewed in a few hours.

---

## Post-Rollout Monitoring

After starting the rollout:

| Signal | Where to check | Threshold to act |
|---|---|---|
| Crash rate | Android vitals → Crashes | > 1.09% of daily active users → halt rollout |
| ANR rate | Android vitals → ANRs | > 0.47% → halt rollout |
| 1-star reviews | Reviews tab | Investigate immediately |
| Rating | Store listing overview | Watch for unexpected dip |
| Install errors | Statistics → Install errors | Any spike |

### Halt rollout if needed

1. Go to **Production → Releases**
2. Click **Halt rollout**
3. Investigate crashes in **Android Vitals → Crash clusters**
4. Fix, bump versionCode, rebuild AAB, re-upload

---

## Version History

| versionCode | versionName | Key changes |
|---|---|---|
| 1 | 1.0.0 | Initial release |
| 2 | 1.0.1 | Bug fixes |
| 3 | 1.1.0 | Sound effects, haptics, help button, tablet layout |

---

## Useful Links

- Play Console: https://play.google.com/console
- Android Vitals: Play Console → Android vitals
- Privacy policy: https://randan96mk.github.io/6767-game/privacy-policy.html
- Release notes file: `store-listing/changelogs/3.txt`
