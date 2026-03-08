package com.ranker.reconstruction.ui.quicksim

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ranker.reconstruction.data.model.Complex
import com.ranker.reconstruction.data.model.NewUnitType
import com.ranker.reconstruction.data.model.OldUnit
import com.ranker.reconstruction.data.model.SubComplex
import com.ranker.reconstruction.ui.simulation.SimulationState

// ── Local draft ───────────────────────────────────────────────────────────────
private class QNewUnitDraft {
    var label by mutableStateOf("")
    var exclusiveAreaM2 by mutableStateOf("")
    var supplyAreaM2 by mutableStateOf("")
}

// ── Screen ─────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickSimulationScreen(
    onStartSimulation: (SimulationState) -> Unit,
    onBack: () -> Unit
) {
    // Section 1 — per-pyeong prices
    var postCompletionPrice by remember { mutableStateOf("") }
    var generalSalePrice by remember { mutableStateOf("") }
    var memberSalePrice by remember { mutableStateOf("") }
    var constructionCost by remember { mutableStateOf("") }

    // Section 2 — aggregate totals
    var memberSaleTotal by remember { mutableStateOf("") }
    var generalSaleTotal by remember { mutableStateOf("") }
    var constructionCostTotal by remember { mutableStateOf("") }
    var totalPreCompletionAsset by remember { mutableStateOf("") }

    // Section 3 — my apartment
    var myTypeName by remember { mutableStateOf("") }
    var myExclusiveAreaM2 by remember { mutableStateOf("") }
    var myPreCompletionValueM by remember { mutableStateOf("") }
    var myKbMedianPriceM by remember { mutableStateOf("") }

    // Section 4 — new unit types
    val newUnitDrafts = remember { mutableStateListOf(QNewUnitDraft()) }

    val isValid = isFormValid(
        postCompletionPrice, generalSalePrice, memberSalePrice, constructionCost,
        memberSaleTotal, generalSaleTotal, constructionCostTotal, totalPreCompletionAsset,
        myTypeName, myExclusiveAreaM2, myPreCompletionValueM, myKbMedianPriceM,
        newUnitDrafts
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("직접 입력 시뮬레이션", style = MaterialTheme.typography.titleMedium) },
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
        bottomBar = {
            Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
                Button(
                    onClick = {
                        val complex = buildComplex(
                            postCompletionPrice, generalSalePrice, memberSalePrice, constructionCost,
                            memberSaleTotal, generalSaleTotal, constructionCostTotal, totalPreCompletionAsset,
                            myTypeName, myExclusiveAreaM2, myPreCompletionValueM, myKbMedianPriceM,
                            newUnitDrafts
                        )
                        onStartSimulation(SimulationState(complex))
                    },
                    enabled = isValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text("시뮬레이션 시작", fontWeight = FontWeight.SemiBold)
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // Disclaimer banner
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.errorContainer
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        "본 시뮬레이션은 의사결정 지원 도구로, 공식 감정평가가 아닙니다.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // Section 1
            SectionHeader("기본 가격 (백만원/평)")
            QField("준공후 평단가", postCompletionPrice) { postCompletionPrice = it }
            QField("일반분양가 평단가", generalSalePrice) { generalSalePrice = it }
            QField("조합원분양가 평단가", memberSalePrice) { memberSalePrice = it }
            QField("공사비 평단가", constructionCost) { constructionCost = it }

            Divider()

            // Section 2
            SectionHeader("사업 규모 합계 (백만원)")
            QField("조합원분양 합계", memberSaleTotal) { memberSaleTotal = it }
            QField("일반분양 합계", generalSaleTotal) { generalSaleTotal = it }
            QField("공사비 합계", constructionCostTotal) { constructionCostTotal = it }
            QField("종전자산 합계", totalPreCompletionAsset) { totalPreCompletionAsset = it }

            Divider()

            // Section 3
            SectionHeader("내 아파트")
            QField("평형명 (예: 56㎡)", myTypeName, keyboardType = KeyboardType.Text) { myTypeName = it }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                QField("전용면적(㎡)", myExclusiveAreaM2, modifier = Modifier.weight(1f)) { myExclusiveAreaM2 = it }
                QField("감정가(M)", myPreCompletionValueM, modifier = Modifier.weight(1f)) { myPreCompletionValueM = it }
            }
            QField("KB시세(M)", myKbMedianPriceM) { myKbMedianPriceM = it }

            Divider()

            // Section 4
            SectionHeader("종후 타입")
            newUnitDrafts.forEachIndexed { idx, draft ->
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "타입 ${idx + 1}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (newUnitDrafts.size > 1) {
                                IconButton(
                                    onClick = { newUnitDrafts.removeAt(idx) },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "삭제",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                        QField(
                            "타입명 (예: 84A)",
                            draft.label,
                            keyboardType = KeyboardType.Text
                        ) { draft.label = it }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            QField("전용(㎡)", draft.exclusiveAreaM2, modifier = Modifier.weight(1f)) {
                                draft.exclusiveAreaM2 = it
                            }
                            QField("공급(㎡)", draft.supplyAreaM2, modifier = Modifier.weight(1f)) {
                                draft.supplyAreaM2 = it
                            }
                        }
                    }
                }
            }

            OutlinedButton(
                onClick = { newUnitDrafts.add(QNewUnitDraft()) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("+ 타입 추가")
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────
@Composable
private fun SectionHeader(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun Divider() {
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
private fun QField(
    label: String,
    value: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    keyboardType: KeyboardType = KeyboardType.Decimal,
    onValueChange: (String) -> Unit
) {
    val isError = value.isNotEmpty() &&
        (if (keyboardType == KeyboardType.Text) value.isBlank() else value.toDoubleOrNull() == null)

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        isError = isError,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
    )
}

private fun isFormValid(
    postCompletionPrice: String, generalSalePrice: String,
    memberSalePrice: String, constructionCost: String,
    memberSaleTotal: String, generalSaleTotal: String,
    constructionCostTotal: String, totalPreCompletionAsset: String,
    myTypeName: String, myExclusiveAreaM2: String,
    myPreCompletionValueM: String, myKbMedianPriceM: String,
    newUnitDrafts: List<QNewUnitDraft>
): Boolean {
    val numerics = listOf(
        postCompletionPrice, generalSalePrice, memberSalePrice, constructionCost,
        memberSaleTotal, generalSaleTotal, constructionCostTotal, totalPreCompletionAsset,
        myExclusiveAreaM2, myPreCompletionValueM, myKbMedianPriceM
    )
    val numericOk = numerics.all { it.toDoubleOrNull() != null }
    val myTypeOk = myTypeName.isNotBlank()
    val unitsOk = newUnitDrafts.isNotEmpty() && newUnitDrafts.all {
        it.label.isNotBlank() &&
        it.exclusiveAreaM2.toDoubleOrNull() != null &&
        it.supplyAreaM2.toDoubleOrNull() != null
    }
    return numericOk && myTypeOk && unitsOk
}

private fun buildComplex(
    postCompletionPrice: String, generalSalePrice: String,
    memberSalePrice: String, constructionCost: String,
    memberSaleTotal: String, generalSaleTotal: String,
    constructionCostTotal: String, totalPreCompletionAsset: String,
    myTypeName: String, myExclusiveAreaM2: String,
    myPreCompletionValueM: String, myKbMedianPriceM: String,
    newUnitDrafts: List<QNewUnitDraft>
): Complex = Complex(
    id = "quick_sim_temp",
    nameKo = "내 구역",
    postCompletionPricePerPyeong = postCompletionPrice.toDouble(),
    constructionCostPerPyeong = constructionCost.toDouble(),
    generalSalePricePerPyeong = generalSalePrice.toDouble(),
    memberSalePricePerPyeong = memberSalePrice.toDouble(),
    memberSaleTotal = memberSaleTotal.toDouble(),
    generalSaleTotal = generalSaleTotal.toDouble(),
    fixedPostCompletionTotal = 0.0,
    constructionCostTotal = constructionCostTotal.toDouble(),
    fixedProjectCostTotal = 0.0,
    totalPreCompletionAssetValue = totalPreCompletionAsset.toDouble(),
    subComplexes = listOf(
        SubComplex(
            name = "내 단지",
            oldUnits = listOf(
                OldUnit(
                    typeName = myTypeName.trim(),
                    exclusiveAreaM2 = myExclusiveAreaM2.toDouble(),
                    preCompletionValueM = myPreCompletionValueM.toDouble(),
                    kbMedianPriceM = myKbMedianPriceM.toDouble()
                )
            )
        )
    ),
    newUnitTypes = newUnitDrafts.map {
        NewUnitType(
            label = it.label.trim(),
            exclusiveAreaM2 = it.exclusiveAreaM2.toDouble(),
            supplyAreaM2 = it.supplyAreaM2.toDouble()
        )
    },
    isUserCreated = false
)
