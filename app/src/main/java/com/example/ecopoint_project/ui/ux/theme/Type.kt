package com.example.ecopoint_project.ui.ux.theme
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val EcoTypography = Typography(
    // Judul Besar (Headline)
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold, // Tebal ala website modern
        fontSize = 32.sp,
        color = EcoTextBlack
    ),
    // Sub-judul
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        color = EcoTextBlack
    ),
    // Teks Biasa (Body)
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = EcoTextGrey
    )
)