package com.example.ecopoint_project.ui.ux.screens

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecopoint_project.R // Pastikan import R ini ada
import com.example.ecopoint_project.ui.ux.theme.EcoGreenPrimary
import com.example.ecopoint_project.ui.ux.viewmodel.EcoViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreen(
    viewModel: EcoViewModel,
    onNavigateBack: () -> Unit
) {
    val jenisSampahOptions = listOf("Plastik", "Kertas", "Logam", "Kaca", "Organik")
    var selectedJenisSampah by remember { mutableStateOf(jenisSampahOptions[0]) }
    var expanded by remember { mutableStateOf(false) }
    var beratSampah by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Setor Sampah", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- 1. GAMBAR ILUSTRASI (BARU) ---
            // Gaya disamakan dengan Dashboard: Lebar penuh, sudut membulat, tinggi proporsional
            Image(
                painter = painterResource(id = R.drawable.img_sorted_trash_png),
                contentDescription = "Ilustrasi Pilah Sampah",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp) // Tinggi disesuaikan agar proporsional di form
                    .clip(RoundedCornerShape(16.dp)), // Sudut membulat 16dp
                contentScale = ContentScale.Crop // Potong rapi agar mengisi frame
            )

            Spacer(modifier = Modifier.height(8.dp))

            // --- 2. FORM INPUT (TETAP SAMA) ---
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedJenisSampah,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Jenis Sampah") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    jenisSampahOptions.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                selectedJenisSampah = item
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = beratSampah,
                onValueChange = { newValue ->
                    val filteredValue = newValue.filter { it.isDigit() || it == '.' }
                    if (filteredValue.count { it == '.' } <= 1) {
                        beratSampah = filteredValue
                    }
                },
                label = { Text("Berat Sampah (Kg)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (beratSampah.isNotBlank()) {
                        val berat = beratSampah.toDoubleOrNull()
                        if (berat != null && berat > 0) {
                            viewModel.setorSampah(selectedJenisSampah, berat)
                            beratSampah = ""
                            onNavigateBack()
                        } else {
                            scope.launch { snackbarHostState.showSnackbar("Berat tidak valid.") }
                        }
                    } else {
                        scope.launch { snackbarHostState.showSnackbar("Isi berat sampah.") }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary)
            ) {
                Text("Setor Sekarang", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}