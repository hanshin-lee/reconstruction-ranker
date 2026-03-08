package com.ranker.reconstruction.ui.simulation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ranker.reconstruction.data.model.Complex
import com.ranker.reconstruction.data.model.OldUnit
import com.ranker.reconstruction.data.model.SubComplex
import com.ranker.reconstruction.domain.SimulationCalculator

class SimulationState(val complex: Complex) {
    // Slider multipliers
    var generalMult by mutableFloatStateOf(1f)        // 0.5–4.0
    var memberMult by mutableFloatStateOf(1f)          // 0.5–4.0
    var constructionMult by mutableFloatStateOf(1f)    // 0.5–2.0
    var postCompletionMult by mutableFloatStateOf(1f)  // 0.5–2.0 (margin screen)

    // Unit selection
    var selectedSubComplex: SubComplex by mutableStateOf(complex.subComplexes.first())
    var selectedOldUnit: OldUnit? by mutableStateOf(null)

    // Purchase price override (margin screen)
    var purchasePriceMult by mutableFloatStateOf(1f)   // 0.5–2.0, relative to KB median

    val proportionalRate: Double
        get() = SimulationCalculator.proportionalRate(complex, generalMult, memberMult, constructionMult)

    val effectiveMemberPrice: Double
        get() = complex.memberSalePricePerPyeong * memberMult

    val effectivePostPrice: Double
        get() = complex.postCompletionPricePerPyeong * postCompletionMult

    fun effectivePurchasePrice(unit: OldUnit): Double =
        unit.kbMedianPriceM * purchasePriceMult
}
