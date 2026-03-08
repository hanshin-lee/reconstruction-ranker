package com.ranker.reconstruction.data.model

import kotlinx.serialization.Serializable

/**
 * A reconstruction zone (재건축 구역) with all data needed for ranking and simulation.
 * All monetary values are in M KRW (백만원).
 */
@Serializable
data class Complex(
    val id: String,
    val nameKo: String,

    // Ranking fields
    val postCompletionPricePerPyeong: Double,  // H17: projected new apt market price (M KRW/pyeong)
    val constructionCostPerPyeong: Double,      // H24: estimated construction cost (M KRW/pyeong)

    // Proportional rate simulation baselines
    val generalSalePricePerPyeong: Double,      // F38: baseline general sale price (M KRW/pyeong)
    val memberSalePricePerPyeong: Double,       // F39: baseline member sale price (M KRW/pyeong)

    // I51 components — post-completion total breakdown
    val memberSaleTotal: Double,               // variable: scales with member price slider
    val generalSaleTotal: Double,              // variable: scales with general price slider
    val fixedPostCompletionTotal: Double,      // fixed: rental housing + commercial

    // I56 components — project cost breakdown
    val constructionCostTotal: Double,         // variable: scales with construction cost slider
    val fixedProjectCostTotal: Double,         // fixed: other project costs

    val totalPreCompletionAssetValue: Double,  // I61: total appraised value of old units (M KRW)

    val subComplexes: List<SubComplex>,
    val newUnitTypes: List<NewUnitType>,
    val isUserCreated: Boolean = false
) {
    val landValuePerPyeong: Double
        get() = postCompletionPricePerPyeong - constructionCostPerPyeong
}

@Serializable
data class SubComplex(
    val name: String,
    val oldUnits: List<OldUnit>
)

/**
 * An old (pre-reconstruction) apartment unit type within a sub-complex.
 * preCompletionValueM: appraised value used in rights calculation (M KRW)
 * kbMedianPriceM: KB median market price — starting point for margin slider (M KRW)
 */
@Serializable
data class OldUnit(
    val typeName: String,
    val exclusiveAreaM2: Double,
    val preCompletionValueM: Double,
    val kbMedianPriceM: Double
)

/**
 * A new apartment unit type to be built after reconstruction.
 * supplyAreaM2 is used in price calculations (× 0.3025 to convert m² to pyeong).
 */
@Serializable
data class NewUnitType(
    val label: String,
    val exclusiveAreaM2: Double,
    val supplyAreaM2: Double
)
