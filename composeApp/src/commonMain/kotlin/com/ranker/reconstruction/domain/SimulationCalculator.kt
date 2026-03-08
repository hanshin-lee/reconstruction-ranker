package com.ranker.reconstruction.domain

import com.ranker.reconstruction.data.model.Complex
import com.ranker.reconstruction.data.model.NewUnitType
import com.ranker.reconstruction.data.model.OldUnit

object SimulationCalculator {

    /**
     * Proportional rate (비례율).
     * Recomputed whenever any slider changes.
     *
     * @param generalMult  multiplier for general sale price (0.5–4.0)
     * @param memberMult   multiplier for member sale price (0.5–4.0)
     * @param constructionMult multiplier for construction cost (0.5–2.0)
     */
    fun proportionalRate(
        complex: Complex,
        generalMult: Float,
        memberMult: Float,
        constructionMult: Float
    ): Double {
        val adjustedPost = complex.memberSaleTotal * memberMult +
                complex.generalSaleTotal * generalMult +
                complex.fixedPostCompletionTotal
        val adjustedCost = complex.constructionCostTotal * constructionMult +
                complex.fixedProjectCostTotal
        return (adjustedPost - adjustedCost) / complex.totalPreCompletionAssetValue
    }

    /**
     * Rights value (권리가액) for a given old unit.
     * = pre-completion appraisal value × proportional rate
     */
    fun rightsValue(old: OldUnit, proportionalRate: Double): Double =
        old.preCompletionValueM * proportionalRate

    /**
     * Member sale price for a new unit type (조합원분양가).
     * = memberPricePerPyeong × supplyArea_m2 × 0.3025 (m²→pyeong conversion)
     */
    fun newTypePrice(newType: NewUnitType, memberPricePerPyeong: Double): Double =
        memberPricePerPyeong * newType.supplyAreaM2 * 0.3025

    /**
     * Additional contribution (추가분담금) for a (old unit, new unit type) pair.
     * Positive = additional payment required; negative = refund (환급).
     */
    fun contribution(
        old: OldUnit,
        newType: NewUnitType,
        memberPricePerPyeong: Double,
        proportionalRate: Double
    ): Double = newTypePrice(newType, memberPricePerPyeong) - rightsValue(old, proportionalRate)

    /**
     * Investment margin (마진) for a (old unit, new unit type) pair.
     * = projected post-completion value - (purchase price + contribution)
     * If contribution is negative (refund), it reduces the cost basis.
     */
    fun margin(
        newType: NewUnitType,
        postCompletionPricePerPyeong: Double,
        purchasePriceM: Double,
        contribution: Double
    ): Double {
        val projectedValue = postCompletionPricePerPyeong * newType.supplyAreaM2 * 0.3025
        return projectedValue - (purchasePriceM + contribution)
    }
}
