package id.go.tapselkab.sapa_desa.ui.verifikasi

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.go.tapselkab.sapa_desa.ui.entity.VerifikasiAbsensiEntity
import kotlinx.coroutines.launch
import java.net.NetworkInterface

@Composable
fun VerifikasiAbsensiScreen(
    // onSubmit: (VerifikasiAbsensiEntity) -> Unit
) {
    val scope = rememberCoroutineScope()

    var kodeDesa by remember { mutableStateOf("") }
    var kodeKecamatan by remember { mutableStateOf("") }
    var macAddress by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Ambil MAC address dan lokasi otomatis saat screen dibuka
    LaunchedEffect(Unit) {
        //   macAddress = getMyMacAddress() ?: "Tidak tersedia"

        //  val location = getWindowsLocation()
//        if (location != null) {
//            latitude = location.first.toString()
//            longitude = location.second.toString()
//        } else {
//            latitude = ""
//            longitude = ""
//        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Verifikasi Absensi", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = kodeDesa,
            onValueChange = { kodeDesa = it },
            label = { Text("Kode Desa") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = kodeKecamatan,
            onValueChange = { kodeKecamatan = it },
            label = { Text("Kode Kecamatan") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = macAddress,
            onValueChange = {},
            label = { Text("MAC Address") },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = latitude,
            onValueChange = {},
            label = { Text("Latitude") },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = longitude,
            onValueChange = {},
            label = { Text("Longitude") },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    val entity = VerifikasiAbsensiEntity(
                        userId = 0, // ganti dengan userId aktif
                        kodeDesa = kodeDesa,
                        kodeKecamatan = kodeKecamatan,
                        macAddress = macAddress,
                        latitude = latitude.toDouble(),
                        longitude = longitude.toDouble(),
                        syncStatus = 0
                    )
                    // onSubmit(entity)
                    isLoading = false
                }
            },
            enabled = !isLoading && kodeDesa.isNotBlank() && kodeKecamatan.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Kirim Verifikasi")
            }
        }
    }
}