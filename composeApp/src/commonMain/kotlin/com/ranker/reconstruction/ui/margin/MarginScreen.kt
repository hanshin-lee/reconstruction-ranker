package com.ranker.reconstruction.ui.margin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ranker.reconstruction.data.model.NewUnitType
import com.ranker.reconstruction.data.model.OldUnit
import com.ranker.reconstruction.domain.SimulationCalculator
import com.ranker.reconstruction.ui.simulation.DisclaimerFooter
import com.ranker.reconstruction.ui.simulation.SectionLabel
import com.ranker.reconstruction.ui.simulation.SimulationSlider
import com.ranker.reconstruction.ui.simulation.SimulationState

// ── Screen ─────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarginScreen(
    state: SimulationState,
    onBack: () -> Unit
) {
    val selectedUnit = state.selectedOldUnit ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.complex.nameKo, style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Spacer(Modifier.height(20.dp))

                // ── Page header ────────────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "마진 계산",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            "${selectedUnit.typeName}  ·  비례율 ${"%.1f".format(state.proportionalRate * 100)}%",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(Modifier.height(20.dp))

                // ── Sliders ────────────────────────────────────────────────────
                SectionLabel("변수 조정")
                Spacer(Modifier.height(12.dp))

                SimulationSlider(
                    label = "조합원분양가",
                    baselineValue = state.complex.memberSalePricePerPyeong,
                    multiplier = state.memberMult,
                    minMult = 0.5f, maxMult = 4.0f, unit = "백만원/평",
                    onMultiplierChange = { state.memberMult = it }
                )
                Spacer(Modifier.height(12.dp))
                SimulationSlider(
                    label = "공사비",
                    baselineValue = state.complex.constructionCostPerPyeong,
                    multiplier = state.constructionMult,
                    minMult = 0.5f, maxMult = 2.0f, unit = "백만원/평",
                    onMultiplierChange = { state.constructionMult = it }
                )
                Spacer(Modifier.height(12.dp))
                SimulationSlider(
                    label = "준공후 평단가 (시세)",
                    baselineValue = state.complex.postCompletionPricePerPyeong,
                    multiplier = state.postCompletionMult,
                    minMult = 0.5f, maxMult = 2.0f, unit = "백만원/평",
                    onMultiplierChange = { state.postCompletionMult = it }
                )
                Spacer(Modifier.height(12.dp))
                SimulationSlider(
                    label = "매입가 (현재 아파트)",
                    baselineValue = selectedUnit.kbMedianPriceM,
                    multiplier = state.purchasePriceMult,
                    minMult = 0.5f, maxMult = 2.0f, unit = "백만원",
                    onMultiplierChange = { state.purchasePriceMult = it }
                )
                Spacer(Modifier.height(24.dp))
            }

            // ── Margin result cards ────────────────────────────────────────────
            MarginResults(
                oldUnit = selectedUnit,
                newTypes = state.complex.newUnitTypes,
                memberPricePerPyeong = state.effectiveMemberPrice,
                postCompletionPricePerPyeong = state.effectivePostPrice,
                proportionalRate = state.proportionalRate,
                purchasePriceM = state.effectivePurchasePrice(selectedUnit)
            )

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Spacer(Modifier.height(16.dp))
                DisclaimerFooter()
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

// ── Margin results ─────────────────────────────────────────────────────────────
@Composable
private fun MarginResults(
    oldUnit: OldUnit,
    newTypes: List<NewUnitType>,
    memberPricePerPyeong: Double,
    postCompletionPricePerPyeong: Double,
    proportionalRate: Double,
    purchasePriceM: Double
) {
    // Pre-compute to show summary
    val margins = newTypes.map { newType ->
        val contribution = SimulationCalculator.contribution(
            oldUnit, newType, memberPricePerPyeong, proportionalRate
        )
        val margin = SimulationCalculator.margin(
            newType, postCompletionPricePerPyeong, purchasePriceM, contribution
        )
        Triple(newType, contribution, margin)
    }

    val bestMargin  = margins.maxOfOrNull { it.third }
    val worstMargin = margins.minOfOrNull { it.third }

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        SectionLabel("결과 요약")
        Spacer(Modifier.height(10.dp))

        // Best / worst summary bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SummaryStatCard(
                label = "최대 마진",
                value = bestMargin,
                modifier = Modifier.weight(1f)
            )
            SummaryStatCard(
                label = "최소 마진",
                value = worstMargin,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))
        SectionLabel("타입별 상세")
        Spacer(Modifier.height(10.dp))

        margins.forEach { (newType, contribution, margin) ->
            MarginRow(
                typeName = newType.label,
                exclusiveAreaM2 = newType.exclusiveAreaM2,
                projectedValue = postCompletionPricePerPyeong * newType.supplyAreaM2 * 0.3025,
                contribution = contribution,
                margin = margin
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SummaryStatCard(
    label: String,
    value: Double?,
    modifier: Modifier = Modifier
) {
    val isPositive = (value ?: 0.0) >= 0
    val valueColor = if (isPositive) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
    val bgColor    = if (isPositive) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.errorContainer

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = bgColor,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = valueColor.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                if (value != null) "${"%.0f".format(value)}M" else "-",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        }
    }
}

@Composable
private fun MarginRow(
    typeName: String,
    exclusiveAreaM2: Double,
    projectedValue: Double,
    contribution: Double,
    margin: Double
) {
    val isPositive   = margin >= 0
    val marginColor  = if (isPositive) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
    val contribColor = if (contribution < 0) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
    val isRefund     = contribution < 0

    OutlinedCard(
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(
            width = if (isPositive) 1.dp else 1.dp,
            color = if (isPositive) marginColor.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outlineVariant
        ),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Type label + area
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(width = 52.dp, height = 32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                "${typeName}타입",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "${"%.1f".format(exclusiveAreaM2)}㎡",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Margin (big)
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "마진",
                        style = MaterialTheme.typography.labelSmall,
                        color = marginColor
                    )
                    Text(
                        "${"%.0f".format(margin)}백만원",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = marginColor
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(10.dp))

            // Detail row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MarginDetailStat(
                    label = if (isRefund) "환급" else "분담금",
                    value = if (isRefund)
                        "${"%.0f".format(-contribution)}M"
                    else
                        "+${"%.0f".format(contribution)}M",
                    valueColor = contribColor
                )
                MarginDetailStat(
                    label = "준공후 가치",
                    value = "${"%.0f".format(projectedValue)}M",
                    valueColor = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun MarginDetailStat(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color
) {
    Column {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = valueColor
        )
    }
}
