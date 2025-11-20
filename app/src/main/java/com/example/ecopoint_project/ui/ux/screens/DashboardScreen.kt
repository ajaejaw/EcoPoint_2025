package com.example.ecopoint_project.ui.ux.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecopoint_project.R // Pastikan ini di-import
import com.example.ecopoint_project.ui.ux.theme.EcoGreenPrimary
import com.example.ecopoint_project.ui.ux.viewmodel.EcoViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: EcoViewModel,
    onNavigateToInput: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val user by viewModel.currentUser.collectAsState()
    val historyList by viewModel.historyList.collectAsState()

    val currentXP = user?.totalPoints ?: 0
    val targetXPForNextLevel = when (user?.currentLevel) {
        "Warga Baru" -> 1000
        "Juragan Loak" -> 5000
        else -> 5000
    }
    val progress = if (targetXPForNextLevel > 0) currentXP.toFloat() / targetXPForNextLevel.toFloat() else 0f

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    // Teks EcoPoint di tengah
                    Text("EcoPoint", fontWeight = FontWeight.Bold, color = EcoGreenPrimary)
                },
                navigationIcon = {
                    // Logo Aplikasi di kiri (tanpa label teks lagi, hanya logo)
                    Image(
                        painter = painterResource(id = R.drawable.ic_ecopoint_logo_png), // Logo utama aplikasi
                        contentDescription = "Logo EcoPoint",
                        modifier = Modifier
                            .size(48.dp) // Ukuran logo
                            .padding(start = 16.dp)
                    )
                },
                actions = {
                    // Tombol Profile di kanan
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Profil", tint = EcoGreenPrimary)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- BAGIAN GREETING & MOTIVASI ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text("Halo, ${user?.name ?: "User"}!", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                Text(
                    text = "Ayo pilah sampahmu hari ini, untuk bumi yang lebih hijau esok hari!",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- KARTU LEVEL SAAT INI ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Level Saat Ini", fontSize = 14.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.width(8.dp))
                        // LOGIKA MENAMPILKAN BADGE SESUAI LEVEL
                        val badgeResource = when (user?.currentLevel) {
                            "Warga Baru" -> R.drawable.ic_badge_warga_baru_png // Menggunakan logo utama sebagai badge Warga Baru
                            "Juragan Loak" -> R.drawable.ic_badge_juragan_loak_png
                            "Sultan Sampah" -> R.drawable.ic_badge_sultan_sampah_png
                            else -> null
                        }
                        badgeResource?.let {
                            Image(
                                painter = painterResource(id = it),
                                contentDescription = "Badge Level ${user?.currentLevel}",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Text(
                        text = user?.currentLevel ?: "Warga Baru",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = EcoGreenPrimary,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = EcoGreenPrimary,
                        trackColor = EcoGreenPrimary.copy(alpha = 0.2f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "$currentXP / $targetXPForNextLevel XP",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- AREA ILUSTRASI BAWAH DASHBOARD (MENGGUNAKAN GAMBAR KERANJANG) ---
            Image(
                painter = painterResource(id = R.drawable.img_kids_recycle_png), // Menggunakan gambar keranjang sampah
                contentDescription = "Ilustrasi keranjang sampah",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Fit // Gunakan Fit agar gambar tidak terpotong
            )

            Spacer(modifier = Modifier.weight(1f))

            // --- AKSI CEPAT ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onNavigateToInput,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .padding(end = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary)
                ) {
                    Text("Setor", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Button(
                    onClick = onNavigateToHistory,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .padding(start = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text("Riwayat", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = EcoGreenPrimary)
                }
            }
        }
    }
}