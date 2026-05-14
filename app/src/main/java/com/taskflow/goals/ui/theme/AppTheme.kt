package com.taskflow.goals.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Background = Color(0xFF080808)
private val Surface = Color(0xFF121212)
private val SurfaceVariant = Color(0xFF1C1C1C)
private val PrimaryRed = Color(0xFFE53935)
private val OnPrimary = Color.White
private val TextMain = Color(0xFFF2F2F2)
private val TextMuted = Color(0xFF9A9A9A)
private val Outline = Color(0xFF2E2E2E)

private val AppDarkColors = darkColorScheme(
    primary = PrimaryRed,
    onPrimary = OnPrimary,
    primaryContainer = Color(0xFF5C1010),
    onPrimaryContainer = TextMain,
    secondary = Color(0xFF7A7A7A),
    onSecondary = TextMain,
    tertiary = Color(0xFFFFB300),
    onTertiary = Color.Black,
    background = Background,
    onBackground = TextMain,
    surface = Surface,
    onSurface = TextMain,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextMuted,
    outline = Outline,
    error = PrimaryRed,
    onError = OnPrimary,
)

private val AppShapes = Shapes(
    extraLarge = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(20.dp),
    medium = RoundedCornerShape(16.dp),
    small = RoundedCornerShape(12.dp),
)

private val AppTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Black,
        fontSize = 34.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.6).sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        letterSpacing = 0.6.sp,
    ),
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppDarkColors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content,
    )
}
