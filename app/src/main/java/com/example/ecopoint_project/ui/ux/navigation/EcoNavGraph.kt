package com.example.ecopoint_project.ui.ux.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ecopoint_project.ui.ux.screens.*
import com.example.ecopoint_project.ui.ux.viewmodel.EcoViewModel

@Composable
fun EcoNavGraph(
    navController: NavHostController,
    viewModel: EcoViewModel
) {
    NavHost(navController = navController, startDestination = "login") {

        // LOGIN
        composable("login") {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        // REGISTER
        composable("register") {
            RegisterScreen(
                viewModel = viewModel,
                onRegisterSuccess = { navController.popBackStack() },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        // DASHBOARD (Kirim ViewModel di sini)
        composable("dashboard") {
            DashboardScreen(
                viewModel = viewModel, // <--- Penting!
                onNavigateToInput = { navController.navigate("input") },
                onNavigateToHistory = { navController.navigate("history") },
                onNavigateToProfile = { navController.navigate("profile") }
            )
        }

        // INPUT
        composable("input") {
            InputScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // HISTORY (Kirim ViewModel di sini)
        composable("history") {
            HistoryScreen(
                viewModel = viewModel, // <--- Penting!
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // PROFILE
        composable("profile") {
            ProfileScreen(
                viewModel = viewModel, // <--- TAMBAHKAN INI
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    viewModel.resetLoginState()
                    navController.navigate("login") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }
            )
        }
    }
}