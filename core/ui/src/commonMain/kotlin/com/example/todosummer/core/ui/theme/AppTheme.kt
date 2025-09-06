package com.example.todosummer.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

// 라이트 모드 색상
private val LightColorScheme = lightColorScheme(
    // Brand: White + Mint
    primary = Color(0xFF2AD4C3),          // Mint
    onPrimary = Color(0xFF00110E),        // Very dark for contrast on mint
    primaryContainer = Color(0xFFCFFFF4), // Light mint container
    onPrimaryContainer = Color(0xFF00201B),
    secondary = Color(0xFF12B8A6),        // Deeper mint for secondary accents
    onSecondary = Color(0xFF00110E),
    secondaryContainer = Color(0xFFB8F3E9),
    onSecondaryContainer = Color(0xFF00201B),
    tertiary = Color(0xFF2563EB),         // Blue tertiary for links/cta accents
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD7E3FF),
    onTertiaryContainer = Color(0xFF001A42),
    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6),
    onError = Color.White,
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFFFFFF),       // Pure white background
    onBackground = Color(0xFF0B0B0F),     // Near black text
    surface = Color(0xFFFFFFFF),          // White surface
    onSurface = Color(0xFF0B0B0F),
    surfaceVariant = Color(0xFFEFF7F5),   // Subtle mint-tinted surface variant
    onSurfaceVariant = Color(0xFF3A4A47),
    outline = Color(0xFF7C8B88),
    outlineVariant = Color(0xFFCBD7D4)
)

// 다크 모드 색상
private val DarkColorScheme = darkColorScheme(
    // Brand: Black + Blue
    primary = Color(0xFF3B82F6),          // Blue accent
    onPrimary = Color.White,
    primaryContainer = Color(0xFF113A7F), // Deep blue container
    onPrimaryContainer = Color(0xFFD6E4FF),
    secondary = Color(0xFF60A5FA),        // Lighter blue secondary
    onSecondary = Color(0xFF061021),
    secondaryContainer = Color(0xFF0F2B54),
    onSecondaryContainer = Color(0xFFD6E4FF),
    tertiary = Color(0xFF22D3EE),         // Cyan/blue tertiary
    onTertiary = Color(0xFF001218),
    tertiaryContainer = Color(0xFF103844),
    onTertiaryContainer = Color(0xFFB9F3FF),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onError = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF0B0B0F),       // Near black background
    onBackground = Color(0xFFE6E8EE),
    surface = Color(0xFF0F1115),          // Slightly lighter than background
    onSurface = Color(0xFFE6E8EE),
    surfaceVariant = Color(0xFF171A22),   // Dark bluish surface variant
    onSurfaceVariant = Color(0xFFA9B2C7),
    outline = Color(0xFF546072),
    outlineVariant = Color(0xFF2A3240)
)

// 현재 테마 모드를 저장하는 CompositionLocal
val LocalThemeMode = compositionLocalOf { ThemeMode.SYSTEM }

/**
 * 앱 테마를 제공하는 Composable
 */
@Composable
fun AppTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val systemInDarkTheme = isSystemInDarkTheme()
    
    val colorScheme = when (themeMode) {
        ThemeMode.LIGHT -> LightColorScheme
        ThemeMode.DARK -> DarkColorScheme
        ThemeMode.SYSTEM -> if (systemInDarkTheme) DarkColorScheme else LightColorScheme
    }
    
    CompositionLocalProvider(LocalThemeMode provides themeMode) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

/**
 * 현재 테마 모드를 반환하는 Composable
 */
@Composable
fun themeMode(): ThemeMode {
    return LocalThemeMode.current
}
