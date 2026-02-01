package id.go.tapselkab.sapa_desa.ui.perangkat


import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextAlign
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.geofish
import org.jetbrains.compose.resources.Font

@Composable
fun AbsensiButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    viewModel: AbsensiViewModel,
    onAbsen: () -> Unit,
    onIzin: () -> Unit
) {
    val buttonStatus by viewModel.buttonStatus.collectAsState()
    val date by viewModel.thisDay.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initScreen()
    }

    Column(
        modifier =  modifier.wrapContentWidth(),
        horizontalAlignment = Alignment.Start // Rata Kiri untuk teks status & tanggal
    ) {
        // --- Teks Status ---
        Text(
            text = buttonStatus.message,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(Res.font.geofish)),
            // textAlign = TextAlign.Start
        )

        // --- Teks Tanggal ---
        Text(
            text = date,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6C7A89),
            fontFamily = FontFamily(Font(Res.font.geofish)),
            // textAlign = TextAlign.Start,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // --- Baris Tombol ---
        Row(
            modifier =  Modifier.wrapContentWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp) // Jarak antar tombol
        ) {
            // Tombol 1: Mulai Absen
            Button(
                onClick = onAbsen,
                enabled = enabled,
                shape = RoundedCornerShape(8.dp), // Sedikit rounded agar modern
                modifier = Modifier
                    .heightIn(min = 45.dp) // Tinggi minimal agar tombol Izin tidak terlihat gepeng
            ) {
                Text(
                    text = "Mulai Absen",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }

            // Tombol 2: Izin/Tugas Luar
            OutlinedButton(
                onClick = onIzin,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .heightIn(min = 45.dp) // Samakan tingginya
            ) {
                Text(
                    text = "Izin / Tugas Luar", // Tambahkan spasi agar lebih enak dibaca
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,

                )
            }
        }
    }
}
