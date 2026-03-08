package com.ranker.reconstruction.ui.addcomplex

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import kotlin.random.Random

// ── Local draft models (not @Serializable — form state only) ──────────────────
private class OldUnitDraft {
    var typeName by mutableStateOf("")
    var exclusiveAreaM2 by mutableStateOf("")
    var preCompletionValueM by mutableStateOf("")
    var kbMedianPriceM by mutableStateOf("")
}

private class SubComplexDraft {
    var name by mutableStateOf("")
    val oldUnits: SnapshotStateList<OldUnitDraft> = mutableStateListOf(OldUnitDraft())
}

private class NewUnitTypeDraft {
    var label by mutableStateOf("")
    var exclusiveAreaM2 by mutableStateOf("")
    var supplyAreaM2 by mutableStateOf("")
}

// ── Screen ─────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddComplexScreen(
    onSaved: (Complex) -> Unit,
    onCancel: () -> Unit
) {
    var step by remember { mutableStateOf(1) }

    // Step 1 fields
    var nameKo by remember { mutableStateOf("") }
    var postCompletionPrice by remember { mutableStateOf("") }
    var constructionCost by remember { mutableStateOf("") }
    var generalSalePrice by remember { mutableStateOf("") }
    var memberSalePrice by remember { mutableStateOf("") }

    // Step 2 fields
    var memberSaleTotal by remember { mutableStateOf("") }
    var generalSaleTotal by remember { mutableStateOf("") }
    var constructionCostTotal by remember { mutableStateOf("") }
    var totalPreCompletionAsset by remember { mutableStateOf("") }
    val subComplexDrafts = remember { mutableStateListOf(SubComplexDraft()) }

    // Step 3 fields
    val newUnitDrafts = remember { mutableStateListOf(NewUnitTypeDraft()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (step) {
                            1 -> "기본 정보"
                            2 -> "사업 규모 & 기존 세대"
                            else -> "종후 타입"
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { if (step == 1) onCancel() else step-- }) {
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
                val isNextEnabled = when (step) {
                    1 -> step1Valid(nameKo, postCompletionPrice, constructionCost, generalSalePrice, memberSalePrice)
                    2 -> step2Valid(memberSaleTotal, generalSaleTotal, constructionCostTotal, totalPreCompletionAsset, subComplexDrafts)
                    else -> step3Valid(newUnitDrafts)
                }
                Button(
                    onClick = {
                        if (step < 3) {
                            step++
                        } else {
                            val complex = buildComplex(
                                nameKo, postCompletionPrice, constructionCost,
                                generalSalePrice, memberSalePrice,
                                memberSaleTotal, generalSaleTotal, constructionCostTotal,
                                totalPreCompletionAsset, subComplexDrafts, newUnitDrafts
                            )
                            onSaved(complex)
                        }
                    },
                    enabled = isNextEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text(
                        if (step < 3) "다음" else "저장",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Step progress indicator
            StepIndicator(current = step, total = 3)

            when (step) {
                1 -> Step1Content(
                    nameKo = nameKo, onNameKoChange = { nameKo = it },
                    postCompletionPrice = postCompletionPrice, onPostChange = { postCompletionPrice = it },
                    constructionCost = constructionCost, onConstrChange = { constructionCost = it },
                    generalSalePrice = generalSalePrice, onGeneralChange = { generalSalePrice = it },
                    memberSalePrice = memberSalePrice, onMemberChange = { memberSalePrice = it }
                )
                2 -> Step2Content(
                    memberSaleTotal = memberSaleTotal, onMemberTotalChange = { memberSaleTotal = it },
                    generalSaleTotal = generalSaleTotal, onGeneralTotalChange = { generalSaleTotal = it },
                    constructionCostTotal = constructionCostTotal, onConstrTotalChange = { constructionCostTotal = it },
                    totalPreCompletionAsset = totalPreCompletionAsset, onTotalAssetChange = { totalPreCompletionAsset = it },
                    subComplexDrafts = subComplexDrafts
                )
                3 -> Step3Content(newUnitDrafts = newUnitDrafts)
            }
        }
    }
}

// ── Step indicator ─────────────────────────────────────────────────────────────
@Composable
private fun StepIndicator(current: Int, total: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (i in 1..total) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = if (i <= current) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
            ) {}
        }
    }
}

// ── Step 1: Basic info ─────────────────────────────────────────────────────────
@Composable
private fun Step1Content(
    nameKo: String, onNameKoChange: (String) -> Unit,
    postCompletionPrice: String, onPostChange: (String) -> Unit,
    constructionCost: String, onConstrChange: (String) -> Unit,
    generalSalePrice: String, onGeneralChange: (String) -> Unit,
    memberSalePrice: String, onMemberChange: (String) -> Unit
) {
    val postVal = postCompletionPrice.toDoubleOrNull()
    val constrVal = constructionCost.toDoubleOrNull()
    val landValue = if (postVal != null && constrVal != null) postVal - constrVal else null

    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Spacer(Modifier.height(4.dp))

        FormField("구역 이름", nameKo, onNameKoChange, keyboardType = KeyboardType.Text)
        FormField("준공후 평단가 (백만원/평)", postCompletionPrice, onPostChange)
        FormField("공사비 평단가 (백만원/평)", constructionCost, onConstrChange)

        // Live preview
        if (landValue != null) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    "지분평단가 예상: ${"%.1f".format(landValue)} 백만원/평",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        FormField("일반분양가 평단가 (백만원/평)", generalSalePrice, onGeneralChange)
        FormField("조합원분양가 평단가 (백만원/평)", memberSalePrice, onMemberChange)
        Spacer(Modifier.height(8.dp))
    }
}

// ── Step 2: Aggregates + Sub-complexes ────────────────────────────────────────
@Composable
private fun Step2Content(
    memberSaleTotal: String, onMemberTotalChange: (String) -> Unit,
    generalSaleTotal: String, onGeneralTotalChange: (String) -> Unit,
    constructionCostTotal: String, onConstrTotalChange: (String) -> Unit,
    totalPreCompletionAsset: String, onTotalAssetChange: (String) -> Unit,
    subComplexDrafts: SnapshotStateList<SubComplexDraft>
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Spacer(Modifier.height(4.dp))
        SectionHeader("사업 규모 합계 (백만원)")
        FormField("조합원분양 합계", memberSaleTotal, onMemberTotalChange)
        FormField("일반분양 합계", generalSaleTotal, onGeneralTotalChange)
        FormField("공사비 합계", constructionCostTotal, onConstrTotalChange)
        FormField("종전자산 합계", totalPreCompletionAsset, onTotalAssetChange)

        Spacer(Modifier.height(4.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(Modifier.height(4.dp))
        SectionHeader("기존 세대 (단지 / 평형)")

        subComplexDrafts.forEachIndexed { subIdx, sub ->
            SubComplexEditor(
                draft = sub,
                onRemove = if (subComplexDrafts.size > 1) ({ subComplexDrafts.removeAt(subIdx) }) else null
            )
        }

        OutlinedButton(
            onClick = { subComplexDrafts.add(SubComplexDraft()) },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("+ 단지 추가")
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun SubComplexEditor(draft: SubComplexDraft, onRemove: (() -> Unit)?) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("단지", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (onRemove != null) {
                    IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "단지 삭제",
                            tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                    }
                }
            }
            FormField("단지 이름", draft.name, { draft.name = it }, keyboardType = KeyboardType.Text)

            draft.oldUnits.forEachIndexed { unitIdx, unit ->
                OldUnitEditor(
                    draft = unit,
                    onRemove = if (draft.oldUnits.size > 1) ({ draft.oldUnits.removeAt(unitIdx) }) else null
                )
            }

            TextButton(onClick = { draft.oldUnits.add(OldUnitDraft()) }) {
                Text("+ 세대 추가", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun OldUnitEditor(draft: OldUnitDraft, onRemove: (() -> Unit)?) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("세대", style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (onRemove != null) {
                    IconButton(onClick = onRemove, modifier = Modifier.size(20.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "세대 삭제",
                            tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FormField("평형명", draft.typeName, { draft.typeName = it },
                    modifier = Modifier.weight(1f), keyboardType = KeyboardType.Text)
                FormField("전용(㎡)", draft.exclusiveAreaM2, { draft.exclusiveAreaM2 = it },
                    modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FormField("감정가(M)", draft.preCompletionValueM, { draft.preCompletionValueM = it },
                    modifier = Modifier.weight(1f))
                FormField("KB시세(M)", draft.kbMedianPriceM, { draft.kbMedianPriceM = it },
                    modifier = Modifier.weight(1f))
            }
        }
    }
}

// ── Step 3: New unit types ─────────────────────────────────────────────────────
@Composable
private fun Step3Content(newUnitDrafts: SnapshotStateList<NewUnitTypeDraft>) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Spacer(Modifier.height(4.dp))
        SectionHeader("종후 타입 (신축 아파트)")

        newUnitDrafts.forEachIndexed { idx, draft ->
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("타입 ${idx + 1}", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (newUnitDrafts.size > 1) {
                            IconButton(onClick = { newUnitDrafts.removeAt(idx) }, modifier = Modifier.size(20.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "삭제",
                                    tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                    FormField("타입명 (예: 84A)", draft.label, { draft.label = it }, keyboardType = KeyboardType.Text)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FormField("전용(㎡)", draft.exclusiveAreaM2, { draft.exclusiveAreaM2 = it },
                            modifier = Modifier.weight(1f))
                        FormField("공급(㎡)", draft.supplyAreaM2, { draft.supplyAreaM2 = it },
                            modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        OutlinedButton(
            onClick = { newUnitDrafts.add(NewUnitTypeDraft()) },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("+ 타입 추가")
        }
        Spacer(Modifier.height(8.dp))
    }
}

// ── Shared UI helpers ──────────────────────────────────────────────────────────
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
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    keyboardType: KeyboardType = KeyboardType.Decimal
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

// ── Validation helpers ─────────────────────────────────────────────────────────
private fun step1Valid(
    nameKo: String,
    postCompletion: String, constructionCost: String,
    generalSale: String, memberSale: String
): Boolean = nameKo.isNotBlank() &&
    listOf(postCompletion, constructionCost, generalSale, memberSale).all { it.toDoubleOrNull() != null }

private fun step2Valid(
    memberSaleTotal: String, generalSaleTotal: String,
    constructionCostTotal: String, totalPreCompletionAsset: String,
    subComplexDrafts: List<SubComplexDraft>
): Boolean {
    val totalsOk = listOf(memberSaleTotal, generalSaleTotal, constructionCostTotal, totalPreCompletionAsset)
        .all { it.toDoubleOrNull() != null }
    val subOk = subComplexDrafts.isNotEmpty() && subComplexDrafts.all { sub ->
        sub.name.isNotBlank() && sub.oldUnits.isNotEmpty() && sub.oldUnits.all { unit ->
            unit.typeName.isNotBlank() &&
            unit.exclusiveAreaM2.toDoubleOrNull() != null &&
            unit.preCompletionValueM.toDoubleOrNull() != null &&
            unit.kbMedianPriceM.toDoubleOrNull() != null
        }
    }
    return totalsOk && subOk
}

private fun step3Valid(newUnitDrafts: List<NewUnitTypeDraft>): Boolean =
    newUnitDrafts.isNotEmpty() && newUnitDrafts.all { draft ->
        draft.label.isNotBlank() &&
        draft.exclusiveAreaM2.toDoubleOrNull() != null &&
        draft.supplyAreaM2.toDoubleOrNull() != null
    }

// ── Build domain model ─────────────────────────────────────────────────────────
private fun buildComplex(
    nameKo: String,
    postCompletionPrice: String, constructionCost: String,
    generalSalePrice: String, memberSalePrice: String,
    memberSaleTotal: String, generalSaleTotal: String,
    constructionCostTotal: String, totalPreCompletionAsset: String,
    subComplexDrafts: List<SubComplexDraft>,
    newUnitDrafts: List<NewUnitTypeDraft>
): Complex {
    val id = "user_${Random.nextLong().and(0xFFFFFFFFL)}"
    return Complex(
        id = id,
        nameKo = nameKo.trim(),
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
        subComplexes = subComplexDrafts.map { sub ->
            SubComplex(
                name = sub.name.trim(),
                oldUnits = sub.oldUnits.map { unit ->
                    OldUnit(
                        typeName = unit.typeName.trim(),
                        exclusiveAreaM2 = unit.exclusiveAreaM2.toDouble(),
                        preCompletionValueM = unit.preCompletionValueM.toDouble(),
                        kbMedianPriceM = unit.kbMedianPriceM.toDouble()
                    )
                }
            )
        },
        newUnitTypes = newUnitDrafts.map { draft ->
            NewUnitType(
                label = draft.label.trim(),
                exclusiveAreaM2 = draft.exclusiveAreaM2.toDouble(),
                supplyAreaM2 = draft.supplyAreaM2.toDouble()
            )
        },
        isUserCreated = true
    )
}
