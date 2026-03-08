package com.ranker.reconstruction.ui.ranking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ranker.reconstruction.data.model.Complex

// ── Medal colours ──────────────────────────────────────────────────────────────
private val Gold   = Color(0xFFD97706)
private val Silver = Color(0xFF64748B)
private val Bronze = Color(0xFFB45309)

private fun medalColor(rank: Int): Color = when (rank) {
    1 -> Gold
    2 -> Silver
    3 -> Bronze
    else -> Color(0xFF94A3B8)
}

// ── Screen ─────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingScreen(
    complexes: List<Complex>,
    onComplexClick: (Complex) -> Unit,
    onAddComplexClick: () -> Unit = {},
    onQuickSimulationClick: () -> Unit = {},
    onDeleteComplex: (Complex) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "재건축 랭커",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "지분평당단가 기준",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    TextButton(onClick = onQuickSimulationClick) {
                        Text(
                            "직접 입력",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddComplexClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "구역 추가")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(complexes) { index, complex ->
                ComplexCard(
                    rank = index + 1,
                    complex = complex,
                    onClick = { onComplexClick(complex) },
                    onDelete = if (complex.isUserCreated) ({ onDeleteComplex(complex) }) else null
                )
            }
        }
    }
}

// ── Card ───────────────────────────────────────────────────────────────────────
@Composable
private fun ComplexCard(
    rank: Int,
    complex: Complex,
    onClick: () -> Unit,
    onDelete: (() -> Unit)?
) {
    val mc = medalColor(rank)
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(
            width = if (rank == 1) 1.5.dp else 1.dp,
            color = if (rank == 1) mc.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant
        ),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank badge
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = mc.copy(alpha = 0.12f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = when (rank) { 1 -> "🥇"; 2 -> "🥈"; 3 -> "🥉"; else -> "$rank" },
                        fontSize = if (rank <= 3) 22.sp else 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = mc
                    )
                }
            }

            Spacer(Modifier.width(14.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    complex.nameKo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricChip(
                        label = "지분평단가",
                        value = "${complex.landValuePerPyeong.toInt()}M/평",
                        highlight = true
                    )
                    MetricChip(
                        label = "준공후",
                        value = "${complex.postCompletionPricePerPyeong.toInt()}M/평"
                    )
                    MetricChip(
                        label = "공사비",
                        value = "${complex.constructionCostPerPyeong}M/평"
                    )
                }
            }

            // Delete button (user-created only) or chevron
            if (onDelete != null) {
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "삭제",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            } else {
                Text(
                    "›",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ── Metric chip ────────────────────────────────────────────────────────────────
@Composable
private fun MetricChip(label: String, value: String, highlight: Boolean = false) {
    Column {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Medium,
            color = if (highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}
