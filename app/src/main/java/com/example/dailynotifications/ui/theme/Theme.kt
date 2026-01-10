package com.example.dailynotifications.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

val LightColorScheme = lightColorScheme(
    primary = OrangePrimary,
    onPrimary = WhiteBackground,
    background = WhiteBackground,
    onBackground = TextPrimary,
    surface = WhiteBackground,
    onSurface = TextPrimary,
    outline = OutlineGray,
)

@Composable
fun DailyNotificationsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography,
        content = content
    )
}
