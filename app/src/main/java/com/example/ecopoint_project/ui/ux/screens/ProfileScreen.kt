package com.example.ecopoint_project.ui.ux.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
    // 1. AMBIL DATA REAL-TIME DARI DATABASE
    val user by viewModel.currentUser.collectAsState()
    val historyList by viewModel.historyList.collectAsState()

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
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- FOTO PROFIL (ICON) ---
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

            // --- NAMA & USERNAME ASLI ---
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

            // --- KARTU STATISTIK (DATA ASLI) ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EcoGreenPrimary)
            ) {
                Row(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // KOLOM 1: JUMLAH TRANSAKSI
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${historyList.size}", // Hitung jumlah list
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text("Transaksi", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    }

                    // KOLOM 2: TOTAL POIN (Mulai dari 0)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${user?.totalPoints ?: 0}", // Ambil poin dari DB
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text("Total Poin", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    }

                    // KOLOM 3: STATUS LEVEL
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Logika tampilan singkat: Jika poin > 1000 jadi "Pro", jika tidak "New"
                        val status = if ((user?.totalPoints ?: 0) >= 1000) "Pro" else "New"

                        Text(
                            text = status,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text("Status", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // --- TOMBOL LOGOUT ---
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE)), // Merah Muda Lembut
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.ExitToApp, null, tint = Color.Red)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Keluar Akun", color = Color.Red, fontWeight = FontWeight.Bold)
            }
        }
    }
}