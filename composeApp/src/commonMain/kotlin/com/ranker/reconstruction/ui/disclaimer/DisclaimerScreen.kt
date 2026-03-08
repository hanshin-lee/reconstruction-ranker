package com.ranker.reconstruction.ui.disclaimer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ranker.reconstruction.data.model.Complex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisclaimerScreen(
    complex: Complex,
    onAccept: () -> Unit,
    onBack: () -> Unit,
    onNavigateHome: () -> Unit = onBack
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(complex.nameKo, style = MaterialTheme.typography.titleMedium) },
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
                    containerColor = MaterialTheme.colorScheme.surface,
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Button(
                    onClick = onAccept,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text("내용을 확인했습니다 — 시뮬레이션 시작", fontWeight = FontWeight.SemiBold)
                }
            }
        }
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

            // Icon + headline
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier.size(72.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "중요 고지사항",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "시뮬레이션 시작 전 반드시 읽어주세요",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(24.dp))

            // Disclaimer card
            OutlinedCard(
                shape = MaterialTheme.shapes.large,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    DISCLAIMER_ITEMS.forEach { (title, body) ->
                        DisclaimerItem(title = title, body = body)
                        Spacer(Modifier.height(14.dp))
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(Modifier.height(14.dp))

                    Text(
                        "본 서비스는 감정평가사 17년 경력의 현업 전문가가 공시된 자료를 기반으로 제공하는 정보 서비스입니다.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DisclaimerItem(title: String, body: String) {
    Row(verticalAlignment = Alignment.Top) {
        Surface(
            shape = MaterialTheme.shapes.extraSmall,
            color = MaterialTheme.colorScheme.errorContainer
        ) {
            Text(
                title,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
        }
        Spacer(Modifier.width(10.dp))
        Text(
            body,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 20.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

private val DISCLAIMER_ITEMS = listOf(
    "법적 효력 없음" to "본 서비스의 결과는 감정평가법에 따른 공식 감정평가가 아니며, 어떠한 법적 효력도 갖지 않습니다.",
    "추정치" to "모든 수치는 공시된 자료와 전문가 추정에 기반한 시뮬레이션 결과로, 실제 분담금과 다를 수 있습니다.",
    "변동 가능성" to "일반분양가, 조합원분양가, 공사비 등 주요 변수는 사업 진행에 따라 변동될 수 있습니다.",
    "투자 책임" to "본 서비스를 참조한 투자 결정에 대한 책임은 전적으로 사용자에게 있습니다.",
    "단순 참고용" to "실제 분담금 확정은 감정평가사 및 조합의 공식 절차를 통해 이루어집니다.",
)
