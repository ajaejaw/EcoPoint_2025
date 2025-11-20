package com.example.ecopoint_project.ui.ux.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecopoint_project.R
import com.example.ecopoint_project.ui.ux.theme.EcoGreenPrimary
import com.example.ecopoint_project.ui.ux.viewmodel.EcoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: EcoViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Mengambil status dari ViewModel
    val loginState by viewModel.loginState.collectAsState()
    val context = LocalContext.current

    // Efek Samping: Cek hasil register
    LaunchedEffect(loginState) {
        if (loginState == "RegisterSuccess") {
            Toast.makeText(context, "Akun berhasil dibuat! Silakan Login.", Toast.LENGTH_LONG).show()
            onRegisterSuccess()
            viewModel.resetLoginState()
        } else if (loginState == "UsernameTaken") {
            Toast.makeText(context, "Username sudah dipakai!", Toast.LENGTH_SHORT).show()
            viewModel.resetLoginState()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Daftar Akun", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateToLogin) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // --- LOGO ---
            Image(
                painter = painterResource(id = R.drawable.ic_ecopoint_logo_png),
                contentDescription = "Logo EcoPoint",
                modifier = Modifier
                    .size(300.dp) // Ukuran sedikit lebih kecil dibanding Login agar muat
                    .padding(bottom = 4.dp) // <-- DIPERBAIKI: Jarak dirapatkan jadi 4.dp
            )

            // --- TEKS JUDUL ---
            Text(
                text = "Buat Akun Baru",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = EcoGreenPrimary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- INPUT NAMA LENGKAP ---
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Lengkap") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- INPUT USERNAME ---
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- INPUT PASSWORD ---
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- TOMBOL DAFTAR ---
            Button(
                onClick = {
                    if (name.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()) {
                        viewModel.register(name, username, password)
                    } else {
                        Toast.makeText(context, "Mohon isi semua data!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Daftar Sekarang", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}