// File: ui/screens/DashboardScreen.kt

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecopoint_project.ui.theme.* // Import warna kita tadi

@Composable
fun DashboardScreen(
    onNavigateToInput: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp), // Padding besar agar lega (Whitespace)
        horizontalAlignment = Alignment.Start // Rata kiri ala website modern
    ) {
        // HEADER
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.headlineLarge // Font Besar Tebal
        )
        Text(
            text = "Manage your eco-impact today.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
        )

        // KARTU LEVEL (Hulax Style: Putih Bersih, Rounded, Soft Shadow)
        Card(
            shape = RoundedCornerShape(24.dp), // Sudut sangat bulat
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp), spotColor = Color(0x1A000000)) // Bayangan halus transparan
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Your Status",
                    style = MaterialTheme.typography.titleMedium,
                    color = EcoGreenPrimary // Aksen Hijau
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Level 5",
                        style = MaterialTheme.typography.headlineLarge,
                        fontSize = 48.sp // Angka Besar
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    // Badge sederhana
                    Surface(
                        color = EcoGreenAccent.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(
                            text = "Eco Warrior",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = EcoGreenPrimary,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Progress Bar Modern (Tebal & Hijau)
                LinearProgressIndicator(
                    progress = 0.7f,
                    color = EcoGreenPrimary,
                    trackColor = EcoGreenPrimary.copy(alpha = 0.1f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp) // Lebih tebal
                        .shadow(0.dp) // Flat
                )
                Text(
                    text = "700 / 1000 XP",
                    modifier = Modifier.padding(top = 8.dp).align(Alignment.End),
                    style = MaterialTheme.typography.labelMedium,
                    color = EcoTextGrey
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // TOMBOL MENU (Grid 2 Kolom)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Tombol Setor (Action Button)
            Button(
                onClick = onNavigateToInput,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EcoTextBlack), // Hitam elegan
                modifier = Modifier.weight(1f).height(56.dp)
            ) {
                Text("Setor Sampah")
            }

            // Tombol Riwayat (Secondary Button)
            OutlinedButton(
                onClick = onNavigateToHistory,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, EcoTextGrey.copy(alpha = 0.3f)),
                modifier = Modifier.weight(1f).height(56.dp)
            ) {
                Text("Riwayat", color = EcoTextBlack)
            }
        }
    }
}