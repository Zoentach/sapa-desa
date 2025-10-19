package id.go.tapselkab.sapa_desa.ui.perangkat

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Warning
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
import id.go.tapselkab.sapa_desa.ui.component.dialog.LoadingScreen
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

    val isCameraReady by absensiViewModel.isCameraReady.collectAsState()

    // val cameraAvailableIndex by remember { mutableStateOf(CameraManager.findAvailableCameraIndex()) }

    var showCameraFaceRef by remember { mutableStateOf(false) }

    var showCamereAbsensi by remember { mutableStateOf(false) }

    var showAlertDialog by remember { mutableStateOf(false) }
    var showLoadingDialog by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        absensiViewModel.detectCamera()
//        withContext(Dispatchers.IO) {
//            val foundIndex = CameraManager.findAvailableCameraIndex()
//            if (foundIndex != null) {
//                val started = CameraManager.startCapture(foundIndex)
//                withContext(Dispatchers.Main) {
//                    cameraIndex = foundIndex
//                    cameraStartCapture = started
//                }
//            } else {
//                withContext(Dispatchers.Main) {
//                    println("Kamera tidak tersedia.")
//                }
//            }
//        }
    }

   LaunchedEffect(absensiResult) {
       if (absensiResult.status == AbsensiStatus.SUCCESS ||
           absensiResult.status == AbsensiStatus.FAILED
       ) {
           showLoadingDialog = false
           showAlertDialog = true
       } else if ( absensiResult.status == AbsensiStatus.LOADING ){
           showAlertDialog = false
           showLoadingDialog = true
       } else {
           showAlertDialog = false
           showLoadingDialog = false
       }
   }

    if (showAlertDialog) {
        val imageVector =
            if (absensiResult.status == AbsensiStatus.SUCCESS) Icons.Default.Done
            else Icons.Default.Warning

        val tint =
            if (absensiResult.status == AbsensiStatus.SUCCESS) Color.Green
            else Color.Red

        AlertBoxDialog(
            message = absensiResult.message,
            imageVector = imageVector,
            tint = tint,
            onDismiss = {
                showAlertDialog = false
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
                absensiViewModel.saveImageReference(folderName =  "${perangkat?.id}", fileName =  "${perangkat?.id}")
                showCameraFaceRef = false
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
               absensiViewModel.saveImageAbsensi(id = perangkat?.id ?: 0)
                showCamereAbsensi = false
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
                enabled = showCameraFaceRef == false && showCamereAbsensi == false && isCameraReady == true,
                viewModel = absensiViewModel
            ) {
                //val index = CameraManager.findAvailableCameraIndex()
                if (isCameraReady) {
                    showCamereAbsensi = true
                }
            }

//            Text(
//                text = absensiMessage,
//                fontSize = 14.sp,
//                fontWeight = FontWeight.Bold,
//                fontFamily = FontFamily(Font(Res.font.geofish)),
//            )


            RekapAbsensiHarian(
                perangkatId = perangkat?.id ?: 0,
                viewModel = absensiViewModel,
                modifier = Modifier.padding(horizontal =32.dp)
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

        if (showLoadingDialog) {
            LoadingScreen(
                message = absensiResult.message,
                onCancel = {
                    absensiViewModel.setAbsensiResult()
                    if (isCameraReady == false ) {
                        onNavigateBack()
                    }
                }
            )
        }
    }
}

@Composable
fun absensiButton(
    modifier: Modifier = Modifier,
    enabled:Boolean = true,
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
            },
            enabled = enabled
        ) {
            Text("Mulai Absen")
        }
    }
}



