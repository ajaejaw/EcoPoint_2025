package com.example.ecopoint_project.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Kita fokus ke Light Theme karena referensi Hulax dominan putih/terang
private val EcoColorScheme = lightColorScheme(
    primary = EcoGreenPrimary,
    secondary = EcoGreenAccent,
    background = EcoWhiteBackground,
    surface = EcoSurfaceWhite,
    onPrimary = EcoSurfaceWhite,
    onBackground = EcoTextBlack,
    onSurface = EcoTextBlack
)

@Composable
fun EcoPoint_ProjectTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EcoColorScheme,
        typography = EcoTypography,
        content = content
    )
}