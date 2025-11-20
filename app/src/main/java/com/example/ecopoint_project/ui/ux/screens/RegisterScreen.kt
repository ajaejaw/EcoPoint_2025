package com.example.ecopoint_project.ui.ux.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
fun RegisterScreen(
    viewModel: EcoViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    // State untuk menyimpan ketikan user
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Pantau status dari ViewModel
    val loginState by viewModel.loginState.collectAsState()
    val context = LocalContext.current

    // Efek Samping: Jika berhasil daftar
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Buat Akun Baru",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = EcoGreenPrimary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- INPUT 1: NAMA LENGKAP ---
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nama Lengkap") },
            leadingIcon = { Icon(Icons.Default.AccountCircle, null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- INPUT 2: USERNAME ---
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            leadingIcon = { Icon(Icons.Default.Person, null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- INPUT 3: PASSWORD ---
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- TOMBOL DAFTAR ---
        Button(
            onClick = {
                if (name.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()) {
                    // Panggil ViewModel untuk simpan data ke Database
                    viewModel.register(name, username, password)
                } else {
                    Toast.makeText(context, "Mohon isi semua data!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Daftar Sekarang", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateToLogin) {
            Text("Sudah punya akun? Login", color = Color.Gray)
        }
    }
}