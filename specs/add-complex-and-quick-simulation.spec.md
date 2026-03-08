# Feature: Add District & Quick Simulation

## Status
in-progress

## Overview

Extends the Reconstruction Ranker with two user-facing entry points from the Ranking screen:

1. **Add District (구역 추가)** — A 3-step wizard that lets the user enter full data for a new reconstruction complex. The complex is persisted to a local JSON file on the device and appears in the ranked list across app restarts.

2. **Quick Simulation (직접 입력)** — A single-page form for an ephemeral simulation session. The user enters key values for an unnamed or custom district, runs the full simulation flow (proportional rate → contribution → margin), and the data is discarded when they return to the home screen. Nothing is saved.

Also adds **Home navigation** so users can return to the Ranking screen from any simulation screen.

---

## Acceptance Criteria

### Feature A — Home Navigation
- [x] Every simulation screen (Disclaimer, ProportionalRate, Contribution, Margin) has a Home icon button in the TopAppBar `actions` slot
- [x] Tapping the Home icon navigates immediately to the Ranking screen
- [x] Back navigation (arrow button) continues to work as before

### Feature B — Add District Wizard
- [ ] A FAB with "+" icon appears on the Ranking screen
- [ ] Tapping the FAB opens a 3-step wizard: 기본 정보 → 사업 규모 & 기존 세대 → 종후 타입
- [ ] **Step 1 — 기본 정보**: collects 구역 이름, 준공후 평단가, 공사비 평단가, 일반분양가 평단가, 조합원분양가 평단가; shows live 지분평단가 preview
- [ ] **Step 2 — 사업 규모 & 기존 세대**: collects 조합원분양 합계, 일반분양 합계, 공사비 합계, 종전자산 합계; allows dynamic addition of sub-complexes (단지) and old unit types (세대) with their 4 fields each
- [ ] **Step 3 — 종후 타입**: allows dynamic addition of new unit types (타입명, 전용면적, 공급면적)
- [ ] Each step validates required fields; "다음" / "저장" button is disabled until all required fields are valid
- [ ] Step progress indicator (1/3, 2/3, 3/3) is shown at the top of the wizard
- [ ] "저장" in Step 3 constructs a Complex, persists it, and returns to Ranking with the list refreshed
- [ ] The new complex appears in the ranking list sorted by landValuePerPyeong
- [ ] User-created complexes display a delete (🗑) icon on their ranking card
- [ ] Tapping delete removes the complex from the list and from persistent storage
- [ ] User-created complexes persist across app restarts (Android: filesDir, Desktop: ~/.reconstruction-ranker/, iOS: NSDocumentDirectory)
- [ ] Cancel / back in Step 1 returns to Ranking without saving
- [ ] Back in Steps 2 and 3 returns to the previous wizard step

### Feature C — Quick Simulation
- [ ] A "직접 입력" text button appears in the Ranking screen TopAppBar actions
- [ ] Tapping it opens the Quick Simulation screen
- [ ] The screen has a compact disclaimer banner at the top
- [ ] Four sections: 기본 가격, 사업 규모, 내 아파트, 종후 타입
- [ ] "시뮬레이션 시작" bottom bar button is disabled until all required fields are valid
- [ ] On submit, constructs an in-memory Complex and SimulationState; navigates to ProportionalRate screen
- [ ] Data is NOT saved to persistent storage (ephemeral session only)
- [ ] The full simulation flow (ProportionalRate → Contribution → Margin) works correctly with the entered data
- [ ] Home icon returns to Ranking and discards the ephemeral state

---

## Non-Goals
- No cloud sync or multi-device support
- No edit functionality for saved complexes (delete and re-add)
- No form auto-save / draft persistence

---

## File Inventory

### New Files
- `composeApp/src/commonMain/kotlin/.../data/UserComplexStore.kt` — expect declaration
- `composeApp/src/androidMain/kotlin/.../data/UserComplexStore.android.kt` — Android actual
- `composeApp/src/desktopMain/kotlin/.../data/UserComplexStore.desktop.kt` — Desktop actual
- `composeApp/src/iosMain/kotlin/.../data/UserComplexStore.ios.kt` — iOS actual
- `composeApp/src/commonMain/kotlin/.../ui/addcomplex/AddComplexScreen.kt` — wizard UI
- `composeApp/src/commonMain/kotlin/.../ui/quicksim/QuickSimulationScreen.kt` — quick sim UI

### Modified Files
- `data/model/Complex.kt` — `val isUserCreated: Boolean = false`
- `data/ComplexRepository.kt` — merge bundled + user-created, add/delete operations
- `App.kt` — new Screen variants, refreshKey, home nav wiring
- `ui/ranking/RankingScreen.kt` — FAB, "직접 입력" action, delete for user complexes
- `ui/disclaimer/DisclaimerScreen.kt` — Home icon
- `ui/simulation/ProportionalRateScreen.kt` — Home icon
- `ui/simulation/ContributionScreen.kt` — Home icon
- `ui/margin/MarginScreen.kt` — Home icon
- `androidMain/.../MainActivity.kt` — UserComplexStore.init(applicationContext)
