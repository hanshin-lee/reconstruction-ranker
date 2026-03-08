# Feature: Reconstruction Ranker — Base Product

## Status
<!-- draft | ready | in-progress | complete -->
in-progress

## Overview

Reconstruction Ranker is a data-driven simulation tool for Korean apartment reconstruction (재건축) projects. It targets two audiences: current union members who already own aging apartments in a reconstruction zone, and prospective investors considering purchasing one. The tool answers two questions: (1) how much additional contribution (분담금) must I pay to receive a new apartment, and (2) is the current purchase price of the aging apartment justified given the projected outcome? The product is positioned explicitly as a decision-support tool, not a formal appraisal — legal disclaimers are mandatory throughout.

The core data source is a per-complex Excel file prepared by domain expert Lee Jaemin (17-year certified appraiser). As more complexes are added, the ranking and simulation expand accordingly.

---

## Tech Stack

This project is built with **Kotlin Multiplatform (KMP)** to support multiple target platforms from a shared codebase.

| Layer | Technology |
|---|---|
| Shared business logic | Kotlin Multiplatform (common module) |
| Android | Compose Multiplatform |
| iOS | Compose Multiplatform / SwiftUI interop |
| Web | Compose for Web or Kotlin/JS (TBD) |
| Desktop | Compose Multiplatform (TBD) |
| Data | Per-complex structured data files (derived from Excel source) |

All simulation logic (proportional rate, contribution, margin) lives in the shared `common` module and is platform-agnostic. UI is built with Compose Multiplatform where possible; platform-specific code is limited to file I/O, networking, and OS integrations.

---

## Goals

- Rank reconstruction complexes by their implied land value per pyeong, computed from post-completion market value and construction cost
- Simulate the additional contribution (or refund) for each unit type within a selected complex, dynamically driven by three sliders: general sale price, member sale price, and construction cost
- Simulate the investment margin: difference between projected post-completion apartment value and total cost basis (KB market price of aging unit + contribution or − refund)
- Make the proportional rate (비례율) transparent and interactive so users understand how it drives contribution amounts
- Support feedback collection (initially Google Forms; later community board)
- Include mandatory legal disclaimer on every simulation screen

---

## Non-Goals

- This is NOT a certified real estate appraisal or legally binding valuation
- Does not cover redevelopment (재개발) projects in this phase — only reconstruction (재건축)
- Does not automatically ingest KB market price data — currently manual entry per complex; automation is a future milestone
- Does not handle the full construction permit / legal filing workflow
- No authentication or user accounts in the MVP

---

## Acceptance Criteria

### Feature 1 — Complex Ranking List
- [x] Display a ranked list of reconstruction complexes sorted by implied land value per pyeong (지분평당단가), descending
- [x] For each complex, show: complex name, land value per pyeong, post-completion price per pyeong, estimated construction cost per pyeong
- [x] Ranking formula: `land_value_per_pyeong = post_completion_price_per_pyeong − construction_cost_per_pyeong`; the resulting per-pyeong figure represents the current implied value of the land share
- [x] Tapping/clicking a complex navigates to its Contribution Simulation screen

### Feature 2 — Proportional Rate (비례율) Simulation
- [x] Show legal disclaimer before entering the simulation
- [x] Display the current proportional rate (비례율) for the selected complex
- [x] Proportional rate formula: `(total_post_completion_value − total_project_cost) / total_pre_completion_asset_value`
- [x] Three interactive sliders update the rate in real time:
  - General sale price (일반분양가): range 50 % – 400 % of baseline value
  - Member sale price (조합원분양가): range 50 % – 400 % of baseline value
  - Construction cost (공사비): range 50 % – 200 % of baseline value
- [x] Sliders are visually distinct and easy to drag
- [x] A "Go to Contribution Simulation" button advances to Feature 3

### Feature 3 — Contribution (분담금) Simulation
- [x] User selects their sub-complex (단지, e.g. Hyundai 1·2 vs Hyundai 10) and then their unit type (평형) with exclusive area (전용면적) shown alongside
- [x] After selection, only the chosen unit type row is displayed; all others collapse
- [x] A table shows every available new apartment type (59㎡, 84㎡, … up to penthouse) and the corresponding contribution amount or refund for the selected old unit
- [ ] Contribution formula per new type:
  ```
  additional_contribution = member_sale_price_for_new_type − rights_value_of_old_unit
  rights_value = pre_completion_asset_value × proportional_rate
  member_sale_price_for_new_type = member_price_per_pyeong × supply_area_m2 × 0.3025
  ```
  - Negative result = refund to the member; positive result = additional payment required
- [x] The same three sliders (general sale price, member sale price, construction cost) from Feature 2 are visible and update the contribution table in real time
- [x] Sliders and contribution table are spatially close on screen (avoid scroll distance between them)

### Feature 4 — Margin (투자수익) Calculation
- [x] Accessible as a next step from the Contribution Simulation screen
- [ ] Margin formula:
  ```
  margin = projected_post_completion_value − (current_kb_price + additional_contribution)
  projected_post_completion_value = post_completion_price_per_pyeong × supply_area_m2 × 0.3025
  ```
  (if additional_contribution is negative, i.e. a refund, it reduces the cost basis)
- [x] Two additional sliders:
  - Post-completion price per pyeong (준공후 평단가): adjustable around baseline
  - Purchase price of aging unit: adjustable around current KB median price
- [x] Margin is shown per new apartment type alongside the contribution table

### Feature 5 — Data & Feedback
- [x] Each complex's data is loaded from a structured data file derived from the per-complex Excel source
- [ ] Google Forms link is embedded for: (a) incorrect information reports, (b) "Add our complex" requests
- [x] Legal disclaimer text (provided by domain expert) is shown before any simulation and on all output screens

---

## Design Notes

### Data

Each complex is represented by a data record containing:

| Field | Description | Example (Apgujeong District 3) |
|---|---|---|
| `complex_name` | Complex name | Apgujeong District 3 |
| `post_completion_price_per_pyeong` | Projected new apartment market price per pyeong (M KRW) | 250 |
| `construction_cost_per_pyeong` | Estimated construction cost per pyeong (M KRW) | 10.6 |
| `land_value_per_pyeong` | Derived ranking score (M KRW/pyeong) | 565 (computed from excel sheet F32) |
| `general_sale_price_per_pyeong` | Baseline general sale price per pyeong | 85 |
| `member_sale_price_per_pyeong` | Baseline member sale price per pyeong | 83 |
| `total_post_completion_value` | Sum of all post-completion asset values | from excel I51 |
| `total_project_cost` | Sum of all project/business costs | from excel I56 |
| `total_pre_completion_asset_value` | Sum of all old unit appraisal values | from excel I61 |
| `sub_complexes[]` | List of sub-complexes within the zone | Hyundai 1·2, Hyundai 10, Daelim Villarte |
| `old_unit_types[]` | Per sub-complex: unit type, exclusive area, supply area, pre-completion asset value, KB median price | see excel rows 75–113 |
| `new_unit_types[]` | New apartment types: exclusive area, supply area | see excel columns AC–AO row 69–73 |

Raw source: per-complex `.xlsx` files (currently 2 complexes: Apgujeong District 3, Sinbanpo District 2).

### Behavior

**Ranking screen**: Iterate all complexes, compute `land_value_per_pyeong = post_completion_price_per_pyeong − construction_cost_per_pyeong`, sort descending, render list.

**Proportional rate**: Recomputed reactively whenever any slider moves.
```
proportional_rate = (total_post_completion_value(sliders) − total_project_cost(sliders)) / total_pre_completion_asset_value
```
Both `total_post_completion_value` and `total_project_cost` are functions of the three slider values (general price, member price, construction cost).

**Contribution per (old type, new type) pair**:
```
rights_value = pre_completion_asset_value[old_type] × proportional_rate
new_type_price = member_sale_price_per_pyeong × supply_area_m2[new_type] × 0.3025
contribution = new_type_price − rights_value
```

**Margin per (old type, new type) pair**:
```
post_value = post_completion_price_per_pyeong × supply_area_m2[new_type] × 0.3025
cost_basis = purchase_price[old_type] + contribution   // contribution negative = refund
margin = post_value − cost_basis
```

### Edge Cases

- Slider at minimum (50%) for general/member sale price may push proportional rate below 0 — display a clear warning rather than a negative rate silently
- Construction cost at maximum (200%) may make the project economically unviable — show a warning
- Refund scenarios (negative contribution): label clearly as "refund" not "contribution" to avoid user confusion
- Units with very large pre-completion asset values relative to small new unit types will always produce refunds — this is expected and correct

---

## Open Questions

- [ ] Should ranking be by `land_value_per_pyeong` (current approach) or by investment return rate (ROI)? Both perspectives exist internally — decide before v1 launch
- [ ] KB market price data: is use of KB data legally permissible? If not, substitute with Korea Real Estate Board (한국부동산원) price or actual transaction price
- [ ] Data storage format for complex Excel → backend: flat JSON files per complex, or a database? Decision drives backend architecture
- [ ] Authentication / access control: does any screen require login before v1, or is everything public?
- [ ] Legal disclaimer exact wording: needs sign-off from domain expert (Lee Jaemin) before launch
- [ ] Notification opt-in (for complex updates, KB price refreshes): implement in MVP or defer?

---

## References

- `~/Documents/Projects/(26_0307)_파트너공유_설명포함/★ 1_ 재건축랭커_프로젝트브리핑_to이한신님_(0307).docx` — project briefing document
- `~/Documents/Projects/(26_0307)_파트너공유_설명포함/★ 2_ 재건축랭커_DRAFT_프롬프트_클로드_(0222).txt` — original Claude prompt log with detailed business logic
- `~/Documents/Projects/(26_0307)_파트너공유_설명포함/2-1_ 압구정3구역_(0131).xlsx` — Apgujeong District 3 source data
- `~/Documents/Projects/(26_0307)_파트너공유_설명포함/2-2_ 신반포2단지_(0201).xlsx` — Sinbanpo District 2 source data
- `~/Documents/Projects/(26_0307)_파트너공유_설명포함/3_ 20260109_단지_면적정보_이재민작업중_(0222).xlsx` — raw data processing history
