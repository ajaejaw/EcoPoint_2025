package com.example.ecopoint_project

import DashboardScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ecopoint_project.ui.theme.EcoPoint_ProjectTheme

// Sesuaikan import ini dengan nama folder tema Anda (bisa ui.theme atau ui.ux.theme)


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            EcoPoint_ProjectTheme {
                // Container utama aplikasi
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Panggil layar Dashboard yang sudah kita buat
                    DashboardScreen(
                        onNavigateToInput = {},
                        onNavigateToHistory = {}
                    )
                }
            }
        }
    }
}

