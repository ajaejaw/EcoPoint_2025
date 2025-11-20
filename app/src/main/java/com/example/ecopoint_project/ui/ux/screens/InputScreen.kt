package com.example.ecopoint_project.ui.ux.screens

import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecopoint_project.R
import com.example.ecopoint_project.ui.ux.theme.EcoGreenPrimary
import com.example.ecopoint_project.ui.ux.viewmodel.EcoViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
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

    // State hanya untuk menyimpan hasil foto kamera
    var selectedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // --- SETUP AI (ML Kit) ---
    val labeler = remember { ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS) }

    // --- FUNGSI LOGIKA AI (CERDAS) ---
    fun processImageWithAI(image: InputImage) {
        labeler.process(image)
            .addOnSuccessListener { labels ->
                var detectedCategory: String? = null

                // 1. Kumpulkan semua label yang dilihat AI (lowercase)
                val labelTexts = labels.map { it.text.lowercase() }

                // String untuk debugging (Tampilkan apa yang dilihat AI ke user)
                val debugLabels = labels.sortedByDescending { it.confidence }
                    .take(3)
                    .joinToString { "${it.text} (${(it.confidence * 100).toInt()}%)" }

                // --- TAHAP 1: DETEKSI BAHAN (MATERIAL) - PRIORITAS TINGGI ---
                if (detectedCategory == null) {
                    if (labelTexts.any { it in listOf("glass", "ceramic", "porcelain", "wine bottle", "beer bottle", "jar") }) {
                        detectedCategory = "Kaca"
                    } else if (labelTexts.any { it in listOf("cardboard", "paper", "newspaper", "magazine", "envelope", "tissue", "carton") }) {
                        detectedCategory = "Kertas"
                    } else if (labelTexts.any { it in listOf("metal", "aluminum", "tin", "steel", "iron", "copper", "foil", "can", "beverage can") }) {
                        detectedCategory = "Logam"
                    } else if (labelTexts.any { it in listOf("plastic", "polyethylene", "nylon", "vinyl", "straw", "wrapper", "bag") }) {
                        detectedCategory = "Plastik"
                    } else if (labelTexts.any { it in listOf("vegetable", "fruit", "food", "plant", "leaf", "flower", "meat", "bread") }) {
                        detectedCategory = "Organik"
                    }
                }

                // --- TAHAP 2: DETEKSI BENTUK (SHAPE) - JIKA BAHAN TIDAK KETEMU ---
                if (detectedCategory == null) {
                    if (labelTexts.any { it in listOf("soda", "drink") }) {
                        detectedCategory = "Logam" // Asumsi kaleng minuman
                    } else if (labelTexts.any { it in listOf("box", "book", "notebook", "packaging") }) {
                        detectedCategory = "Kertas"
                    } else if (labelTexts.any { it in listOf("bottle", "water bottle", "container", "cup", "jug", "tub") }) {
                        detectedCategory = "Plastik" // Botol air/wadah defaultnya plastik
                    } else if (labelTexts.any { it in listOf("vase", "tableware") }) {
                        detectedCategory = "Kaca"
                    }
                }

                // --- EKSEKUSI HASIL ---
                if (detectedCategory != null) {
                    selectedJenisSampah = detectedCategory
                    scope.launch {
                        snackbarHostState.showSnackbar("AI: Terdeteksi $detectedCategory (Info: $debugLabels)")
                    }
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar("AI Bingung. Terlihat: $debugLabels")
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Gagal analisis: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Launcher Kamera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            selectedImageBitmap = bitmap
            val image = InputImage.fromBitmap(bitmap, 0)
            processImageWithAI(image)
        }
    }

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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- AREA PREVIEW GAMBAR ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.LightGray.copy(alpha = 0.2f))
                    .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageBitmap != null) {
                    // Tampilkan hasil foto kamera
                    Image(
                        bitmap = selectedImageBitmap!!.asImageBitmap(),
                        contentDescription = "Preview Kamera",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Tampilkan ilustrasi default
                    Image(
                        painter = painterResource(id = R.drawable.img_sorted_trash_png),
                        contentDescription = "Ilustrasi Default",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Text("Deteksi Sampah Otomatis (AI)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.Gray)

            // --- TOMBOL KAMERA (LEBAR PENUH) ---
            Button(
                onClick = { cameraLauncher.launch(null) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ambil Foto Sampah")
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // --- FORM INPUT JENIS (Otomatis Terisi AI) ---
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

            // --- FORM INPUT BERAT ---
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

            Spacer(modifier = Modifier.height(16.dp))

            // --- TOMBOL SETOR ---
            Button(
                onClick = {
                    if (beratSampah.isNotBlank()) {
                        val berat = beratSampah.toDoubleOrNull()
                        if (berat != null && berat > 0) {
                            viewModel.setorSampah(selectedJenisSampah, berat)
                            beratSampah = ""
                            selectedImageBitmap = null // Reset gambar setelah setor
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