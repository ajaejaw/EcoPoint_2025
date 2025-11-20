package com.example.ecopoint_project.ui.ux.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecopoint_project.R
import com.example.ecopoint_project.ui.ux.theme.EcoGreenPrimary
import com.example.ecopoint_project.ui.ux.viewmodel.EcoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: EcoViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Mengambil status login dari ViewModel
    val loginState by viewModel.loginState.collectAsState()
    val context = LocalContext.current

    // Efek Samping: Cek status login
    LaunchedEffect(loginState) {
        if (loginState == "Success") {
            Toast.makeText(context, "Login Berhasil!", Toast.LENGTH_SHORT).show()
            onLoginSuccess()
            viewModel.resetLoginState()
        } else if (loginState == "Failed") {
            Toast.makeText(context, "Username atau Password Salah!", Toast.LENGTH_SHORT).show()
            viewModel.resetLoginState()
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // --- LOGO ---
            Image(
                painter = painterResource(id = R.drawable.ic_ecopoint_logo_png),
                contentDescription = "Logo EcoPoint",
                modifier = Modifier
                    .size(300.dp)
                    .padding(bottom = 1.dp) // <-- DIPERBAIKI: Jarak dikurangi dari 16.dp jadi 4.dp
            )

            // --- TEKS JUDUL ---
            Text(
                text = "EcoPoint",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = EcoGreenPrimary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- INPUT USERNAME ---
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- INPUT PASSWORD ---
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- TOMBOL LOGIN ---
            Button(
                onClick = {
                    if (username.isNotEmpty() && password.isNotEmpty()) {
                        viewModel.login(username, password)
                    } else {
                        Toast.makeText(context, "Isi semua data!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary)
            ) {
                Text("Masuk", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateToRegister) {
                Text("Belum punya akun? Daftar", color = androidx.compose.ui.graphics.Color.Gray)
            }
        }
    }
}