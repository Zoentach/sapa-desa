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
import id.go.tapselkab.sapa_desa.ui.component.dialog.AlertBoxDialog
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.geofish
import id.go.tapselkab.sapa_desa.ui.component.dialog.CameraDialog
import id.go.tapselkab.sapa_desa.ui.component.dialog.LoadingDialog
import id.go.tapselkab.sapa_desa.ui.component.dialog.UploadLampiranDialog
import id.go.tapselkab.sapa_desa.ui.entity.AbsensiStatus
import id.go.tapselkab.sapa_desa.ui.entity.PerangkatEntity
import id.go.tapselkab.sapa_desa.utils.camera.CameraManager
import id.go.tapselkab.sapa_desa.utils.camera.saveReferenceFace
import id.go.tapselkab.sapa_desa.utils.file.getFile
import id.go.tapselkab.sapa_desa.utils.time.TimeManager
import org.jetbrains.compose.resources.Font
import org.koin.compose.koinInject

@Composable
fun PerangkatScreen(
    absensiViewModel: AbsensiViewModel = koinInject(),
    perangkat: PerangkatEntity?,
    onNavigateBack: () -> Unit
) {

    val absensiResult by absensiViewModel.absensiResult.collectAsState()

    // val cameraAvailableIndex by remember { mutableStateOf(CameraManager.findAvailableCameraIndex()) }

    var showCameraFaceRef by remember { mutableStateOf(false) }

    var showCamereAbsensi by remember { mutableStateOf(false) }

    var absensiMessage by remember { mutableStateOf("Belum Absen") }

    if (absensiResult.status == AbsensiStatus.LOADING) {
        LoadingDialog(absensiResult.message)
    }

    if (absensiResult.status == AbsensiStatus.FAILED) {
        AlertBoxDialog(
            message = absensiResult.message,
            onDismiss = {
                absensiViewModel.setAbsensiResult()
            }
        )
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
                    absensiViewModel.prosesAbsensi(
                        perangkatId = perangkat?.id ?: 0,
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

            absensiButton(
                modifier = Modifier
                    .padding(top = 10.dp),
                viewModel = absensiViewModel
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
                perangkatId = perangkat?.id ?: 0,
                viewModel = absensiViewModel
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
fun absensiButton(
    modifier: Modifier = Modifier,
    viewModel: AbsensiViewModel,
    onClick: () -> Unit,
) {

    val buttonStatus by viewModel.buttonStatus.collectAsState()

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



