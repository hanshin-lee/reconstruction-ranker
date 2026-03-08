package com.ranker.reconstruction.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// ── Brand palette ──────────────────────────────────────────────────────────────
val Brand600   = Color(0xFF1D4ED8)
val Brand500   = Color(0xFF2563EB)
val Brand400   = Color(0xFF60A5FA)
val Brand50    = Color(0xFFEFF6FF)

// ── Semantic ───────────────────────────────────────────────────────────────────
// positive  = Material3 tertiary, negative = Material3 error
// (access via MaterialTheme.colorScheme.tertiary / .error)

// ── Light scheme ───────────────────────────────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary             = Brand500,
    onPrimary           = Color(0xFFFFFFFF),
    primaryContainer    = Brand50,
    onPrimaryContainer  = Color(0xFF1E3A8A),
    secondary           = Color(0xFF64748B),
    onSecondary         = Color(0xFFFFFFFF),
    secondaryContainer  = Color(0xFFF1F5F9),
    onSecondaryContainer= Color(0xFF0F172A),
    tertiary            = Color(0xFF16A34A),   // positive / green
    onTertiary          = Color(0xFFFFFFFF),
    tertiaryContainer   = Color(0xFFDCFCE7),
    onTertiaryContainer = Color(0xFF14532D),
    error               = Color(0xFFDC2626),   // negative / red
    errorContainer      = Color(0xFFFEE2E2),
    onErrorContainer    = Color(0xFF7F1D1D),
    background          = Color(0xFFF8FAFC),
    onBackground        = Color(0xFF0F172A),
    surface             = Color(0xFFFFFFFF),
    onSurface           = Color(0xFF0F172A),
    surfaceVariant      = Color(0xFFF1F5F9),
    onSurfaceVariant    = Color(0xFF64748B),
    outline             = Color(0xFFCBD5E1),
    outlineVariant      = Color(0xFFE2E8F0),
)

// ── Dark scheme ────────────────────────────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary             = Brand400,
    onPrimary           = Color(0xFF0F172A),
    primaryContainer    = Color(0xFF1E3A8A),
    onPrimaryContainer  = Color(0xFFBFDBFE),
    secondary           = Color(0xFF94A3B8),
    onSecondary         = Color(0xFF0F172A),
    secondaryContainer  = Color(0xFF1E293B),
    onSecondaryContainer= Color(0xFFE2E8F0),
    tertiary            = Color(0xFF4ADE80),   // positive / green (lighter)
    onTertiary          = Color(0xFF0F172A),
    tertiaryContainer   = Color(0xFF14532D),
    onTertiaryContainer = Color(0xFFBBF7D0),
    error               = Color(0xFFF87171),   // negative / red (lighter)
    errorContainer      = Color(0xFF7F1D1D),
    onErrorContainer    = Color(0xFFFECACA),
    background          = Color(0xFF0D1117),
    onBackground        = Color(0xFFE2E8F0),
    surface             = Color(0xFF161B22),
    onSurface           = Color(0xFFE2E8F0),
    surfaceVariant      = Color(0xFF1E293B),
    onSurfaceVariant    = Color(0xFF94A3B8),
    outline             = Color(0xFF334155),
    outlineVariant      = Color(0xFF1E293B),
)

// ── Shapes ─────────────────────────────────────────────────────────────────────
private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small      = RoundedCornerShape(8.dp),
    medium     = RoundedCornerShape(12.dp),
    large      = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

// ── Theme entry point ──────────────────────────────────────────────────────────
@Composable
fun RankerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        shapes      = AppShapes,
        content     = content
    )
}
