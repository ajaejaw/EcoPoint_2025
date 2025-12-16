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
    // Diperbarui: Menggunakan Threshold 0.5 (50%) agar AI lebih selektif
    val labeler = remember {
        val options = ImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.5f)
            .build()
        ImageLabeling.getClient(options)
    }

    // --- FUNGSI LOGIKA AI (CERDAS - DENGAN FILTER) ---
    fun processImageWithAI(image: InputImage) {
        labeler.process(image)
            .addOnSuccessListener { labels ->
                var detectedCategory: String? = null

                // --- 1. FILTER BLACKLIST (Mencegah salah deteksi tangan/ruangan) ---
                val noiseWords = listOf(
                    // Bagian Tubuh
                    "hand", "finger", "skin", "nail", "thumb", "person", "human", "arm", "wrist", "leg", "toe", "mouth" , "dude" ,
                    // Lingkungan/Ruangan
                    "room", "indoor", "floor", "wall", "ceiling", "furniture", "table", "chair", "door", "window", "fun" , "jacket" , "jeans" , "Dog" , "screenshot" ,
                    // Abstrak/Umum
                    "design", "pattern", "technology", "electronic", "device", "gadget", "photography", "snapshot", "selfie"
                )

                // Ambil label yang VALID saja (tidak mengandung kata di blacklist)
                val validLabels = labels
                    .filter { label ->
                        val text = label.text.lowercase()
                        noiseWords.none { noise -> text.contains(noise) }
                    }
                    .sortedByDescending { it.confidence }

                // Gunakan labelTexts yang sudah BERSIH untuk logika deteksi
                val labelTexts = validLabels.map { it.text.lowercase() }

                // String untuk debugging (Hanya menampilkan label valid ke user)
                val debugLabels = validLabels.take(3)
                    .joinToString { "${it.text} (${(it.confidence * 100).toInt()}%)" }

                // --- TAHAP 2: DETEKSI BAHAN (MATERIAL) - PRIORITAS TINGGI ---
                if (detectedCategory == null) {
                    if (labelTexts.any { it in listOf("glass", "ceramic", "porcelain", "wine bottle", "beer bottle", "jar", "vase") }) {
                        detectedCategory = "Kaca"
                    } else if (labelTexts.any { it in listOf("cardboard", "paper", "newspaper", "magazine", "envelope", "tissue", "carton", "box", "book", "poster") }) {
                        detectedCategory = "Kertas"
                    } else if (labelTexts.any { it in listOf("metal", "aluminum", "tin", "steel", "iron", "copper", "foil", "can", "beverage can", "soda") }) {
                        detectedCategory = "Logam"
                    } else if (labelTexts.any { it in listOf("plastic", "polyethylene", "nylon", "vinyl", "straw", "wrapper", "bag", "bottle", "water bottle", "cup", "container") }) {
                        detectedCategory = "Plastik"
                    } else if (labelTexts.any { it in listOf("vegetable", "fruit", "food", "plant", "leaf", "flower", "meat", "bread", "banana", "apple", "orange") }) {
                        detectedCategory = "Organik"
                    }
                }

                // --- TAHAP 3: DETEKSI BENTUK (SHAPE) - JIKA BAHAN TIDAK KETEMU ---
                if (detectedCategory == null) {
                    if (labelTexts.any { it in listOf("soda", "drink") }) {
                        detectedCategory = "Logam" // Asumsi kaleng minuman
                    } else if (labelTexts.any { it in listOf("notebook", "packaging") }) {
                        detectedCategory = "Kertas"
                    } else if (labelTexts.any { it in listOf("jug", "tub", "dispenser") }) {
                        detectedCategory = "Plastik"
                    } else if (labelTexts.any { it in listOf("tableware") }) {
                        detectedCategory = "Kaca"
                    }
                }

                // --- EKSEKUSI HASIL ---
                if (detectedCategory != null) {
                    selectedJenisSampah = detectedCategory
                    scope.launch {
                        snackbarHostState.showSnackbar("AI: Terdeteksi $detectedCategory (Sumber: $debugLabels)")
                    }
                } else {
                    // Jika setelah difilter tidak ada yang cocok, beri saran ke user
                    scope.launch {
                        if (validLabels.isEmpty()) {
                            snackbarHostState.showSnackbar("Objek tidak jelas. Coba dekatkan kamera ke sampah.")
                        } else {
                            snackbarHostState.showSnackbar("Jenis tidak dikenali. Terlihat: $debugLabels")
                        }
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