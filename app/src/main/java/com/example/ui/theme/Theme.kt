package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class NetOpsThemeMode(val title: String) {
    CYBER_CYAN("Cyber Cyan"),
    NEON_PURPLE("Neon Purple"),
    MATRIX_EMERALD("Matrix Emerald"),
    SOLAR_GOLD("Solar Gold"),
    OCEAN_SAPPHIRE("Ocean Sapphire")
}

@Immutable
data class NetOpsThemeColors(
    val primary: Color,
    val secondary: Color,
    val accent: Color,
    val background: Color,
    val surface: Color,
    val glassSurface: Color,
    val glassBorder: Color,
    val textPrimary: Color = TextPrimaryDark,
    val textSecondary: Color = TextSecondaryDark,
    val textMuted: Color = TextMutedDark,
    val success: Color = StatusSuccess,
    val warning: Color = StatusWarning,
    val error: Color = StatusError,
    val info: Color = StatusInfo,
    val glow: Color,
    val gradientStart: Color,
    val gradientEnd: Color
)

fun getThemeTokens(mode: NetOpsThemeMode, glassOpacity: Float = 0.65f): NetOpsThemeColors {
    val alphaInt = (glassOpacity.coerceIn(0.1f, 0.95f) * 255).toInt()
    return when (mode) {
        NetOpsThemeMode.CYBER_CYAN -> NetOpsThemeColors(
            primary = CyanPrimary,
            secondary = CyanSecondary,
            accent = CyanAccent,
            background = CyanBg,
            surface = CyanSurface,
            glassSurface = CyanSurface.copy(alpha = glassOpacity),
            glassBorder = CyanGlassBorder,
            glow = CyanGlow,
            gradientStart = CyanBg,
            gradientEnd = CyanSurface
        )
        NetOpsThemeMode.NEON_PURPLE -> NetOpsThemeColors(
            primary = PurplePrimary,
            secondary = PurpleSecondary,
            accent = PurpleAccent,
            background = PurpleBg,
            surface = PurpleSurface,
            glassSurface = PurpleSurface.copy(alpha = glassOpacity),
            glassBorder = PurpleGlassBorder,
            glow = PurpleGlow,
            gradientStart = PurpleBg,
            gradientEnd = PurpleSurface
        )
        NetOpsThemeMode.MATRIX_EMERALD -> NetOpsThemeColors(
            primary = EmeraldPrimary,
            secondary = EmeraldSecondary,
            accent = EmeraldAccent,
            background = EmeraldBg,
            surface = EmeraldSurface,
            glassSurface = EmeraldSurface.copy(alpha = glassOpacity),
            glassBorder = EmeraldGlassBorder,
            glow = EmeraldGlow,
            gradientStart = EmeraldBg,
            gradientEnd = EmeraldSurface
        )
        NetOpsThemeMode.SOLAR_GOLD -> NetOpsThemeColors(
            primary = GoldPrimary,
            secondary = GoldSecondary,
            accent = GoldAccent,
            background = GoldBg,
            surface = GoldSurface,
            glassSurface = GoldSurface.copy(alpha = glassOpacity),
            glassBorder = GoldGlassBorder,
            glow = GoldGlow,
            gradientStart = GoldBg,
            gradientEnd = GoldSurface
        )
        NetOpsThemeMode.OCEAN_SAPPHIRE -> NetOpsThemeColors(
            primary = SapphirePrimary,
            secondary = SapphireSecondary,
            accent = SapphireAccent,
            background = SapphireBg,
            surface = SapphireSurface,
            glassSurface = SapphireSurface.copy(alpha = glassOpacity),
            glassBorder = SapphireGlassBorder,
            glow = SapphireGlow,
            gradientStart = SapphireBg,
            gradientEnd = SapphireSurface
        )
    }
}

val LocalNetOpsColors = staticCompositionLocalOf {
    getThemeTokens(NetOpsThemeMode.CYBER_CYAN)
}

@Composable
fun NetOpsTheme(
    themeMode: NetOpsThemeMode = NetOpsThemeMode.CYBER_CYAN,
    glassOpacity: Float = 0.65f,
    content: @Composable () -> Unit
) {
    val tokens = getThemeTokens(themeMode, glassOpacity)
    val colorScheme = darkColorScheme(
        primary = tokens.primary,
        onPrimary = Color.Black,
        primaryContainer = tokens.surface,
        onPrimaryContainer = tokens.primary,
        secondary = tokens.secondary,
        onSecondary = Color.Black,
        background = tokens.background,
        onBackground = tokens.textPrimary,
        surface = tokens.surface,
        onSurface = tokens.textPrimary,
        surfaceVariant = tokens.surface.copy(alpha = 0.8f),
        onSurfaceVariant = tokens.textSecondary,
        outline = tokens.glassBorder,
        error = tokens.error,
        onError = Color.Black
    )

    CompositionLocalProvider(LocalNetOpsColors provides tokens) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
