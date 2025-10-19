package id.go.tapselkab.sapa_desa.ui.verifikasi

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.go.tapselkab.sapa_desa.ui.component.dialog.AlertBoxDialog
import id.go.tapselkab.sapa_desa.ui.component.dialog.LoadingDialog
import id.go.tapselkab.sapa_desa.ui.entity.AbsensiStatus
import id.go.tapselkab.sapa_desa.ui.entity.VerifikasiAbsensiEntity
import id.go.tapselkab.sapa_desa.ui.graph.VerifikasiAbsensi
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.geofish
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.koin.compose.koinInject


@Composable
fun VerifikasiAbsensiScreen(
    verifikasiAbsensi: VerifikasiAbsensi,
    onNavigateBack: () -> Unit,
    viewModel: VerifikasiViewModel = koinInject(),
) {
    val scope = rememberCoroutineScope()

    var kodeDesa by remember { mutableStateOf("") }
    var kodeKecamatan by remember { mutableStateOf("") }

    val macAddress by viewModel.macAddress.collectAsState()
    val location by viewModel.location.collectAsState()
    val uploadStatus by viewModel.absensiResult.collectAsState()

    var showAlertDialog by remember { mutableStateOf(false) }
    var showLoadingDialog by remember { mutableStateOf(false) }


    // Ambil MAC address dan lokasi otomatis saat screen dibuka
    LaunchedEffect(Unit) {
        viewModel.init()
    }

    LaunchedEffect(uploadStatus) {
        if (uploadStatus.status == AbsensiStatus.SUCCESS ||
            uploadStatus.status == AbsensiStatus.FAILED
        ) {
            showLoadingDialog = false
            showAlertDialog = true
        } else if (uploadStatus.status == AbsensiStatus.LOADING){
            showAlertDialog = false
            showLoadingDialog = true
        }else {
            showAlertDialog = false
            showLoadingDialog = false
        }
    }

    if (showAlertDialog) {
        val imageVector =
            if (uploadStatus.status == AbsensiStatus.SUCCESS) Icons.Default.Done
            else Icons.Default.Warning

        val tint =
            if (uploadStatus.status == AbsensiStatus.SUCCESS) Color.Green
            else Color.Red

        AlertBoxDialog(
            message = uploadStatus.message,
            imageVector = imageVector,
            tint = tint,
            onDismiss = {
                showAlertDialog = false
                onNavigateBack()
            }
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Card(
            modifier = Modifier
                .width(540.dp)
                .padding(16.dp),
            elevation = 8.dp,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Informasi Perangkat",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF34495E),
                    letterSpacing = 1.sp,
                    fontFamily = FontFamily(Font(Res.font.geofish))
                )

                Spacer(modifier = Modifier.height(16.dp))

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
                    value = macAddress.orEmpty(),
                    onValueChange = {},
                    label = { Text("MAC Address") },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = if (location.latitude == 0.0) "Lokasi tidak ditemukan" else location.latitude.toString(),
                    onValueChange = {},
                    label = { Text("Latitude") },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = if (location.longitude == 0.0) "Lokasi tidak ditemukan" else location.longitude.toString(),
                    onValueChange = {},
                    label = { Text("Longitude") },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        scope.launch {
                            val entity = VerifikasiAbsensiEntity(
                                userId = verifikasiAbsensi.userId.toLong(), // ganti dengan userId aktif
                                kodeDesa = kodeDesa,
                                kodeKecamatan = kodeKecamatan,
                                macAddress = macAddress,
                                latitude = location.latitude,
                                longitude = location.longitude,
                                syncStatus = 0
                            )

                            viewModel.sendVerifikasiAbsensi(
                                token = verifikasiAbsensi.token,
                                verifikasi = entity
                            )
                        }
                    },
                    enabled = uploadStatus.status != AbsensiStatus.LOADING && kodeDesa.isNotBlank() && kodeKecamatan.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sikronisasi")
                }

                Button(
                    onClick = {
                        onNavigateBack()
                    },
                    enabled = uploadStatus.status != AbsensiStatus.LOADING,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Kembali")
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (uploadStatus.status == AbsensiStatus.FAILED) {
                    Text(
                        text = uploadStatus.message,
                        color = Color.Red
                    )
                }
            }
        }
        if (showLoadingDialog) {
           LoadingDialog(
              message = uploadStatus.message,
               onCancel = {
                   viewModel.setUploadStatus()
               }
           )
        }
    }
}