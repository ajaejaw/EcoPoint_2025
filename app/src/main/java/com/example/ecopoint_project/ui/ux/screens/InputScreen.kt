package com.example.ecopoint_project.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreen() {
    // Variabel sementara untuk menyimpan ketikan (State)
    var beratSampah by remember { mutableStateOf("") }
    var jenisSampah by remember { mutableStateOf("Plastik") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Form Setor Sampah", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(24.dp))

        // Input Berat
        OutlinedTextField(
            value = beratSampah,
            onValueChange = { beratSampah = it },
            label = { Text("Berat Sampah (Kg)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Simulasi Dropdown Jenis Sampah (Sederhana pakai Radio Button dulu biar cepat)
        Text("Jenis Sampah:")
        Row {
            RadioButton(selected = jenisSampah == "Plastik", onClick = { jenisSampah = "Plastik" })
            Text("Plastik", modifier = Modifier.padding(top = 14.dp))

            Spacer(modifier = Modifier.width(16.dp))

            RadioButton(selected = jenisSampah == "Kertas", onClick = { jenisSampah = "Kertas" })
            Text("Kertas", modifier = Modifier.padding(top = 14.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tombol Simpan
        Button(
            onClick = { /* Nanti diisi logika simpan */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Simpan Transaksi")
        }
    }
}