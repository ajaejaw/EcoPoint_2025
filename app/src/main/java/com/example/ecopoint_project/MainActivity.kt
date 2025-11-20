package com.example.ecopoint_project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.ecopoint_project.data.EcoDatabase
import com.example.ecopoint_project.data.repository.EcoRepository
import com.example.ecopoint_project.ui.ux.navigation.EcoNavGraph
import com.example.ecopoint_project.ui.ux.theme.EcoPoint_ProjectTheme
import com.example.ecopoint_project.ui.ux.viewmodel.EcoViewModel
import com.example.ecopoint_project.ui.ux.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inisialisasi Database & Repository
        // Kita buat di sini agar bisa dishare ke semua layar lewat ViewModel
        val database = EcoDatabase.getDatabase(this)
        val repository = EcoRepository(database.ecoDao())

        // 2. Setup ViewModel dengan Factory
        val viewModelFactory = ViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[EcoViewModel::class.java]

        setContent {
            EcoPoint_ProjectTheme {
                // 3. Setup Navigasi (NavGraph)
                // NavGraph adalah pengatur lalu lintas antar layar (Login -> Dashboard -> dll)
                val navController = rememberNavController()

                // Panggil NavGraph utama kita
                EcoNavGraph(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    }
}