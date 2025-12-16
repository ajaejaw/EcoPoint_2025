package com.example.ecopoint_project.ui.ux.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit // <-- IMPORT BARU
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType // <-- IMPORT BARU
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog // <-- IMPORT BARU
import com.example.ecopoint_project.data.entity.TransaksiEntity
import com.example.ecopoint_project.ui.ux.theme.EcoGreenPrimary
import com.example.ecopoint_project.ui.ux.viewmodel.EcoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: EcoViewModel,
    onNavigateBack: () -> Unit
) {
    val historyList by viewModel.historyList.collectAsState()
    // State untuk mengontrol dialog edit
    var showEditDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<TransaksiEntity?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Riwayat Setoran", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        if (historyList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Belum ada riwayat sampah.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(historyList) { item ->
                    HistoryItem(
                        item = item,
                        onDeleteClick = {
                            viewModel.deleteSampah(item)
                        },
                        onEditClick = {
                            itemToEdit = item
                            showEditDialog = true
                        }
                    )
                }
            }
        }
    }

    // Tampilkan dialog jika showEditDialog adalah true
    if (showEditDialog && itemToEdit != null) {
        EditHistoryDialog(
            item = itemToEdit!!,
            onDismiss = {
                showEditDialog = false
                itemToEdit = null
            },
            onSave = { updatedItem ->
                viewModel.updateSampah(updatedItem) // Panggil fungsi update di ViewModel
                showEditDialog = false
                itemToEdit = null
            }
        )
    }
}

@Composable
fun HistoryItem(
    item: TransaksiEntity,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit // Parameter baru untuk aksi edit
) {
    val dateString = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(item.date))

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(EcoGreenPrimary.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.DateRange, null, tint = EcoGreenPrimary)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(text = item.trashType, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = dateString, color = Color.Gray, fontSize = 12.sp)
                    Text(text = "${item.weightInKg} Kg", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(text = "+${item.earnedPoints} XP", color = EcoGreenPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                Spacer(modifier = Modifier.height(8.dp))

                Row { // <-- Bungkus Tombol dalam Row
                    // TOMBOL EDIT
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f) // Warna biru
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp)) // <-- Jarak antar tombol

                    // TOMBOL HAPUS
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint = Color.Red.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun EditHistoryDialog(
    item: TransaksiEntity,
    onDismiss: () -> Unit,
    onSave: (TransaksiEntity) -> Unit
) {
    var newWeight by remember { mutableStateOf(item.weightInKg.toString()) }
    var weightError by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Edit Kilo Sampah", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = newWeight,
                    onValueChange = {
                        newWeight = it
                        weightError = null // Hapus error saat user mengetik
                    },
                    label = { Text("Berat Sampah (Kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = weightError != null,
                    supportingText = { if (weightError != null) Text(weightError!!) }
                )
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Batal")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        val weightValue = newWeight.toDoubleOrNull()
                        if (weightValue == null || weightValue <= 0) {
                            weightError = "Berat tidak valid"
                        } else {
                            // Buat objek baru dengan berat yang diperbarui
                            // Poin akan dihitung ulang di ViewModel
                            val updatedItem = item.copy(weightInKg = weightValue)
                            onSave(updatedItem)
                        }
                    }) {
                        Text("Simpan")
                    }
                }
            }
        }
    }
}
