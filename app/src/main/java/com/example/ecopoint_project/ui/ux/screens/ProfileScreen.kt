package com.example.ecopoint_project.ui.ux.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState // <--- Tambahkan ini untuk Scroll
import androidx.compose.foundation.verticalScroll      // <--- Tambahkan ini untuk Scroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecopoint_project.ui.ux.theme.EcoGreenPrimary
import com.example.ecopoint_project.ui.ux.viewmodel.EcoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: EcoViewModel,
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {
    val user by viewModel.currentUser.collectAsState()
    val historyList by viewModel.historyList.collectAsState()
    // 1. AMBIL DATA GRAFIK
    val weeklyStats by viewModel.weeklyStats.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profil Saya", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Agar bisa di-scroll jika layar kecil
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- FOTO PROFIL ---
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(EcoGreenPrimary.copy(alpha = 0.1f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = EcoGreenPrimary,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = user?.name ?: "Memuat...",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "@${user?.username ?: "-"}",
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- KARTU STATISTIK ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EcoGreenPrimary)
            ) {
                Row(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${historyList.size}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("Transaksi", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${user?.totalPoints ?: 0}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("Total Poin", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val status = if ((user?.totalPoints ?: 0) >= 1000) "Pro" else "New"
                        Text(status, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("Status", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 2. TAMPILKAN GRAFIK MINGGUAN ---
            if (weeklyStats.isNotEmpty()) {
                WeeklyBarChart(weeklyData = weeklyStats)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- TOMBOL LOGOUT ---
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.ExitToApp, null, tint = Color.Red)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Keluar Akun", color = Color.Red, fontWeight = FontWeight.Bold)
            }
        }
    }
}