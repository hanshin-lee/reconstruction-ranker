# 재건축 랭커 (Reconstruction Ranker)

A data-driven simulation tool for Korean apartment reconstruction (재건축) projects. Built with Kotlin Multiplatform, targeting Android, iOS, and Desktop from a single shared codebase.

> **Disclaimer:** This app is a decision-support tool only. It is not a certified real estate appraisal and carries no legal effect.

---

## What It Does

The app answers two core questions for union members and prospective investors:

1. **How much will I pay (or receive)?** — Simulate the additional contribution (분담금) or refund based on your current unit and the new unit type you want.
2. **Is this investment justified?** — Compute your projected margin given the purchase price of the aging unit, the contribution, and the expected post-completion market price.

### Key Screens

| Screen | Description |
|---|---|
| **Ranking** | Complexes ranked by implied land value per pyeong (지분평당단가) |
| **Proportional Rate** | Interactive 비례율 simulation with 3 sliders |
| **Contribution** | Per-unit-type 분담금 / refund table |
| **Margin** | Investment margin per new apartment type |

---

## Formulas

```
proportional_rate (비례율)
  = (total_post_completion_value − total_project_cost) / total_pre_completion_asset_value

rights_value (권리가액)
  = pre_completion_asset_value × proportional_rate

contribution (분담금)
  = member_sale_price_for_new_type − rights_value
  (negative = refund)

margin (마진)
  = projected_post_completion_value − (purchase_price + contribution)
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| Shared logic | Kotlin Multiplatform (commonMain) |
| UI | Compose Multiplatform |
| Android | Jetpack Compose / Material3 |
| Desktop | Compose for Desktop |
| iOS | Compose Multiplatform |
| Data | JSON files per complex (derived from Excel) |
| Theme | Material3 — light & dark, system-adaptive |

---

## Project Structure

```
composeApp/src/
  commonMain/kotlin/com/ranker/reconstruction/
    App.kt                              # Root composable + navigation
    data/
      model/Complex.kt                  # All data models (@Serializable)
      ComplexRepository.kt              # Loads JSON from composeResources
    domain/
      SimulationCalculator.kt           # Pure calculation functions
    ui/
      theme/Theme.kt                    # Light / dark Material3 themes
      ranking/RankingScreen.kt
      disclaimer/DisclaimerScreen.kt
      simulation/
        SimulationState.kt              # Reactive state holder
        ProportionalRateScreen.kt
        ContributionScreen.kt
      margin/MarginScreen.kt
  commonMain/composeResources/files/
    complex_apgujeong3.json             # 압구정3구역
    complex_sinbanpo2.json              # 신반포2단지
  androidMain/
  desktopMain/
  iosMain/
specs/                                  # Spec-driven development docs
```

---

## Getting Started

### Prerequisites

- JDK 17+
- Android SDK (set `sdk.dir` in `local.properties`)
- Xcode (iOS only)

### Run on Desktop

```bash
./gradlew :composeApp:run
```

### Run on Android

```bash
./gradlew :composeApp:installDebug
```

### Compile Check

```bash
./gradlew :composeApp:compileKotlinDesktop
```

---

## Data

Each complex is stored as a JSON file in `composeResources/files/`. The schema mirrors the Excel source prepared by the domain expert (17-year certified appraiser). All monetary values are in **백만원 (M KRW)**.

Currently included complexes:
- **압구정3구역** (Apgujeong District 3) — 지분평단가 565M KRW/평
- **신반포2단지** (Sinbanpo District 2) — 지분평단가 440M KRW/평

---

## Development Workflow

This project uses **spec-driven development**. All features are defined in `specs/` before implementation begins. See [`specs/reconstruction_ranker_base_spec.md`](specs/reconstruction_ranker_base_spec.md) for the current feature spec.

---

## License

Private — all rights reserved.
