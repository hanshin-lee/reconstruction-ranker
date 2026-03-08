package com.ranker.reconstruction.ui.simulation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Screen ─────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProportionalRateScreen(
    state: SimulationState,
    onNavigateToContribution: () -> Unit,
    onBack: () -> Unit,
    onNavigateHome: () -> Unit = onBack
) {
    val rate = state.proportionalRate
    val ratePercent = rate * 100

    val rateColor = when {
        rate < 0.0 -> MaterialTheme.colorScheme.error
        rate > 1.3 -> MaterialTheme.colorScheme.tertiary
        else       -> MaterialTheme.colorScheme.primary
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.complex.nameKo, style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateHome) {
                        Icon(Icons.Default.Home, contentDescription = "홈")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Button(
                    onClick = onNavigateToContribution,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text("분담금 시뮬레이션 보기", fontWeight = FontWeight.SemiBold)
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            // ── Big rate display ───────────────────────────────────────────────
            Text(
                "비례율",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(8.dp))

            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = rateColor.copy(alpha = 0.08f),
                border = BorderStroke(1.5.dp, rateColor.copy(alpha = 0.3f)),
                modifier = Modifier.size(180.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${"%.1f".format(ratePercent)}%",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = rateColor
                        )
                        if (rate >= 0) {
                            Text(
                                text = when {
                                    rate > 1.2 -> "우량"
                                    rate > 0.9 -> "양호"
                                    rate > 0.7 -> "보통"
                                    else       -> "주의"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = rateColor.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            // Warning banner
            if (rate < 0) {
                Spacer(Modifier.height(12.dp))
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        "비례율이 0% 미만 — 사업성 없음",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Reset button
            TextButton(
                onClick = {
                    state.generalMult = 1f
                    state.memberMult = 1f
                    state.constructionMult = 1f
                }
            ) {
                Text(
                    "기준값으로 초기화",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(24.dp))

            // ── Sliders ────────────────────────────────────────────────────────
            SectionLabel("변수 조정")
            Spacer(Modifier.height(16.dp))

            SimulationSlider(
                label = "일반분양가",
                baselineValue = state.complex.generalSalePricePerPyeong,
                multiplier = state.generalMult,
                minMult = 0.5f, maxMult = 4.0f, unit = "백만원/평",
                onMultiplierChange = { state.generalMult = it }
            )
            Spacer(Modifier.height(20.dp))
            SimulationSlider(
                label = "조합원분양가",
                baselineValue = state.complex.memberSalePricePerPyeong,
                multiplier = state.memberMult,
                minMult = 0.5f, maxMult = 4.0f, unit = "백만원/평",
                onMultiplierChange = { state.memberMult = it }
            )
            Spacer(Modifier.height(20.dp))
            SimulationSlider(
                label = "공사비",
                baselineValue = state.complex.constructionCostPerPyeong,
                multiplier = state.constructionMult,
                minMult = 0.5f, maxMult = 2.0f, unit = "백만원/평",
                onMultiplierChange = { state.constructionMult = it }
            )

            Spacer(Modifier.height(24.dp))
            DisclaimerFooter()
            Spacer(Modifier.height(16.dp))
        }
    }
}

// ── Shared composables (used across screens) ───────────────────────────────────

@Composable
fun SectionLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun SimulationSlider(
    label: String,
    baselineValue: Double,
    multiplier: Float,
    minMult: Float,
    maxMult: Float,
    unit: String,
    onMultiplierChange: (Float) -> Unit
) {
    val effectiveValue = baselineValue * multiplier
    val pct = (multiplier * 100).toInt()

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "${"%.1f".format(effectiveValue)} $unit",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "기준 ${"%.1f".format(baselineValue)} × $pct%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(4.dp))
            Slider(
                value = multiplier,
                onValueChange = onMultiplierChange,
                valueRange = minMult..maxMult,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "${"%.0f".format(baselineValue * minMult)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "${"%.0f".format(baselineValue * maxMult)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun DisclaimerFooter() {
    Text(
        "※ 본 시뮬레이션은 의사결정 지원 도구로, 공식 감정평가가 아닙니다.",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )
}
