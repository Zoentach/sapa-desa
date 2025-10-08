package id.go.tapselkab.sapa_desa.ui.perangkat

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.geofish
import id.go.tapselkab.sapa_desa.ui.component.dialog.CameraDialog
import id.go.tapselkab.sapa_desa.ui.entity.PerangkatEntity
import id.go.tapselkab.sapa_desa.utils.camera.CameraManager
import id.go.tapselkab.sapa_desa.utils.camera.saveReferenceFace
import id.go.tapselkab.sapa_desa.utils.file.getFile
import id.go.tapselkab.sapa_desa.utils.time.TimeManager
import org.jetbrains.compose.resources.Font
import org.koin.compose.koinInject

@Composable
fun PerangkatScreen(
    attendanceViewModel: AttendanceViewModel = koinInject(),
    perangkat: PerangkatEntity?,
    onNavigateBack: () -> Unit
) {

    val absensiStatus by attendanceViewModel.absensiStatus.collectAsState()


    // val cameraAvailableIndex by remember { mutableStateOf(CameraManager.findAvailableCameraIndex()) }

    var showCameraFaceRef by remember { mutableStateOf(false) }

    var showCamereAbsensi by remember { mutableStateOf(false) }

    var absensiMessage by remember { mutableStateOf("Belum Absen") }

    LaunchedEffect(absensiStatus) {
        absensiMessage = absensiStatus.message
    }

    if (showCameraFaceRef) {
        CameraDialog(
            onDismiss = {
                CameraManager.releaseCamera()
                showCameraFaceRef = false
            },
            onCapture = {
                val saved = saveReferenceFace(folderName = "${perangkat?.id}", fileName = "${perangkat?.id}")
                if (saved) {
                    CameraManager.releaseCamera()
                    showCameraFaceRef = false
                }

            },
            actionText = "Ambil Gambar"
        )
    }

    if (showCamereAbsensi) {
        CameraDialog(
            onDismiss = {
                CameraManager.releaseCamera()
                showCamereAbsensi = false
            },
            onCapture = {
                val timeStamp = TimeManager.getCurrentTimeMillis()
                val saved = saveReferenceFace(folderName = "${perangkat?.id}", fileName = "$timeStamp")
                if (saved) {
                    CameraManager.releaseCamera()
                    showCamereAbsensi = false
                    attendanceViewModel.prosesAbsensi(
                        userId = perangkat?.id ?: 0,
                        kodeDesa = perangkat?.kodeDesa ?: "",
                        kodeKec = perangkat?.kodeKec ?: "",
                        timeStamp = timeStamp ?: 0
                    )
                }
            },
            actionText = "Mulai Absensi"
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ImagePerangkatCard(
                file = getFile(folderName = "${perangkat?.id}", fileName = "${perangkat?.id}.jpg"),
                onTakePicture = {
                    val index = CameraManager.findAvailableCameraIndex()
                    if (index != null && CameraManager.startCapture(index)) {
                        showCameraFaceRef = true
                    }
                }
            )
            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = perangkat?.nama.orEmpty(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(Res.font.geofish))

            )
            Text(
                text = perangkat?.jabatan.orEmpty(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6C7A89),
                fontFamily = FontFamily(Font(Res.font.geofish)),
            )

            AttendanceButton(
                modifier = Modifier
                    .padding(top = 10.dp),
                viewModel = attendanceViewModel
            ) {
                val index = CameraManager.findAvailableCameraIndex()
                if (index != null && CameraManager.startCapture(index)) {
                    showCamereAbsensi = true
                }
            }

            Text(
                text = absensiMessage,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(Res.font.geofish)),
            )


            RekapAbsensiHarian(
                userId = perangkat?.id ?: 0,
                viewModel = attendanceViewModel
            )
        }

        OutlinedButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(32.dp),
            onClick = {
                onNavigateBack()
            },
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = ""
            )

            Text(
                text = "Kembali"
            )


        }
    }
}

@Composable
fun AttendanceButton(
    modifier: Modifier = Modifier,
    viewModel: AttendanceViewModel,
    onClick: () -> Unit,
) {

    val buttonStatus by viewModel.buttonStatus.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.isMorningOrAfternoon()
    }

    val date by viewModel.thisDay.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.initScreen()
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = buttonStatus.message,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(Res.font.geofish)),
        )


        Text(
            text = date,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6C7A89),
            fontFamily = FontFamily(Font(Res.font.geofish)),
        )

        Spacer(
            modifier = Modifier
                .height(10.dp)
        )

        Button(
            modifier = Modifier
                .padding(vertical = 10.dp),
            onClick = {
                onClick()
            }
        ) {
            Text("Mulai Absen")
        }
    }
}



