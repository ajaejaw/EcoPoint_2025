package com.example.ecopoint_project.ui.ux.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
// ... (Import lain sama seperti sebelumnya) ...
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecopoint_project.ui.ux.theme.EcoGreenPrimary
import com.example.ecopoint_project.ui.ux.viewmodel.EcoViewModel

@Composable
fun LoginScreen(
    viewModel: EcoViewModel, // Terima ViewModel
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Pantau status login dari ViewModel
    val loginState by viewModel.loginState.collectAsState()
    val context = LocalContext.current

    // Efek Samping: Jika status berubah jadi "Success", pindah halaman
    LaunchedEffect(loginState) {
        if (loginState == "Success") {
            Toast.makeText(context, "Login Berhasil!", Toast.LENGTH_SHORT).show()
            onLoginSuccess()
            viewModel.resetLoginState() // Reset agar tidak auto-login nanti
        } else if (loginState == "Failed") {
            Toast.makeText(context, "Username atau Password Salah!", Toast.LENGTH_SHORT).show()
            viewModel.resetLoginState()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // ... (Bagian Judul & Text Field SAMA SAJA seperti sebelumnya) ...
        Text("EcoPoint", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = EcoGreenPrimary)
        Spacer(modifier = Modifier.height(48.dp))

        OutlinedTextField(
            value = username, onValueChange = { username = it },
            label = { Text("Username") }, modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))

        // TOMBOL LOGIN YANG ASLI
        Button(
            onClick = {
                if (username.isNotEmpty() && password.isNotEmpty()) {
                    // PANGGIL FUNGSI DI VIEWMODEL
                    viewModel.login(username, password)
                } else {
                    Toast.makeText(context, "Isi semua data!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Masuk", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onNavigateToRegister) {
            Text("Belum punya akun? Daftar", color = Color.Gray)
        }
    }
}