package com.ranker.reconstruction.ui.simulation

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

// ── Screen ─────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContributionScreen(
    state: SimulationState,
    onNavigateToMargin: () -> Unit,
    onBack: () -> Unit
) {
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

                // ── Section title + rate badge ─────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "분담금 시뮬레이션",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    val rate = state.proportionalRate
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            "비례율 ${"%.1f".format(rate * 100)}%",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // ── Sub-complex selector ───────────────────────────────────────
                SectionLabel("단지 선택")
                Spacer(Modifier.height(8.dp))
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    state.complex.subComplexes.forEachIndexed { index, sub ->
                        SegmentedButton(
                            selected = state.selectedSubComplex == sub,
                            onClick = {
                                state.selectedSubComplex = sub
                                state.selectedOldUnit = null
                            },
                            shape = SegmentedButtonDefaults.itemShape(
                                index,
                                state.complex.subComplexes.size
                            ),
                            label = { Text(sub.name, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // ── Unit type selection ────────────────────────────────────────
                SectionLabel("내 아파트 평형")
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    state.selectedSubComplex.oldUnits.forEach { unit ->
                        val selected = state.selectedOldUnit == unit
                        FilterChip(
                            selected = selected,
                            onClick = { state.selectedOldUnit = if (selected) null else unit },
                            label = {
                                Text(
                                    "${unit.typeName}  ${"%.0f".format(unit.exclusiveAreaM2)}㎡",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(Modifier.height(20.dp))

                // ── Sliders ────────────────────────────────────────────────────
                SectionLabel("변수 조정")
                Spacer(Modifier.height(12.dp))
                SimulationSlider(
                    label = "일반분양가",
                    baselineValue = state.complex.generalSalePricePerPyeong,
                    multiplier = state.generalMult,
                    minMult = 0.5f, maxMult = 4.0f, unit = "백만원/평",
                    onMultiplierChange = { state.generalMult = it }
                )
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
                Spacer(Modifier.height(24.dp))
            }

            // ── Contribution results ───────────────────────────────────────────
            val selectedUnit = state.selectedOldUnit
            if (selectedUnit != null) {
                ContributionResults(
                    oldUnit = selectedUnit,
                    newTypes = state.complex.newUnitTypes,
                    memberPricePerPyeong = state.effectiveMemberPrice,
                    proportionalRate = state.proportionalRate
                )
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = onNavigateToMargin,
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text("마진 계산 보기", fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.height(8.dp))
                    DisclaimerFooter()
                    Spacer(Modifier.height(20.dp))
                }
            } else {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Surface(
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "위에서 내 아파트 평형을 선택하면\n종후 타입별 분담금이 표시됩니다.",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                }
            }
        }
    }
}

// ── Contribution results ───────────────────────────────────────────────────────
@Composable
fun ContributionResults(
    oldUnit: OldUnit,
    newTypes: List<NewUnitType>,
    memberPricePerPyeong: Double,
    proportionalRate: Double
) {
    val rightsValue = SimulationCalculator.rightsValue(oldUnit, proportionalRate)

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        // Rights value badge
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Text(
                "권리가액 ${"%.0f".format(rightsValue)}백만원  ·  ${oldUnit.typeName}  ·  비례율 ${"%.1f".format(proportionalRate * 100)}%",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        Spacer(Modifier.height(12.dp))

        // Cards for each new unit type
        newTypes.forEach { newType ->
            val contribution = SimulationCalculator.contribution(
                oldUnit, newType, memberPricePerPyeong, proportionalRate
            )
            val newTypePrice = SimulationCalculator.newTypePrice(newType, memberPricePerPyeong)
            val isRefund = contribution < 0

            ContributionRow(
                typeName = newType.label,
                exclusiveAreaM2 = newType.exclusiveAreaM2,
                memberPrice = newTypePrice,
                contribution = contribution,
                isRefund = isRefund
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ContributionRow(
    typeName: String,
    exclusiveAreaM2: Double,
    memberPrice: Double,
    contribution: Double,
    isRefund: Boolean
) {
    val contribColor = if (isRefund) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error

    OutlinedCard(
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Type label
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(width = 52.dp, height = 36.dp)
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

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    "${"%.1f".format(exclusiveAreaM2)}㎡",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "분양가 ${"%.0f".format(memberPrice)}백만원",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    if (isRefund) "환급" else "분담금",
                    style = MaterialTheme.typography.labelSmall,
                    color = contribColor
                )
                Text(
                    if (isRefund)
                        "${"%.0f".format(-contribution)}백만원"
                    else
                        "+${"%.0f".format(contribution)}백만원",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = contribColor
                )
            }
        }
    }
}
