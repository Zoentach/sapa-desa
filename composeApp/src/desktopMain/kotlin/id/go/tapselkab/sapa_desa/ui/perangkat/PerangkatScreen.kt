package id.go.tapselkab.sapa_desa.ui.perangkat

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import id.go.tapselkab.sapa_desa.ui.component.dialog.AlertBoxDialog
import id.go.tapselkab.sapa_desa.ui.component.dialog.CameraDialog
import id.go.tapselkab.sapa_desa.ui.component.dialog.LoadingScreen
import id.go.tapselkab.sapa_desa.ui.component.dialog.MonthPicker
import id.go.tapselkab.sapa_desa.ui.component.dialog.UploadLampiranDialog
import id.go.tapselkab.sapa_desa.ui.entity.AbsensiStatus
import id.go.tapselkab.sapa_desa.ui.entity.PerangkatEntity
import id.go.tapselkab.sapa_desa.ui.table_rekap_absensi.RekapAbsensiHarian
import id.go.tapselkab.sapa_desa.utils.camera.CameraManager
import id.go.tapselkab.sapa_desa.utils.file.getFile
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.geofish
import org.jetbrains.compose.resources.Font
import org.koin.compose.koinInject
import java.time.LocalDate

@Composable
fun PerangkatScreen(
    absensiViewModel: AbsensiViewModel = koinInject(),
    perangkat: PerangkatEntity?,
    onNavigateBack: () -> Unit
) {
    val absensiResult by absensiViewModel.absensiResult.collectAsState()
    val isCameraReady by absensiViewModel.isCameraReady.collectAsState()

    // State Dialog & UI
    var showCameraFaceRef by remember { mutableStateOf(false) }
    var showCamereAbsensi by remember { mutableStateOf(false) }
    var showAlertDialog by remember { mutableStateOf(false) }
    var showLoadingDialog by remember { mutableStateOf(false) }

    // State Dialog Rekap (BARU)
    var showRekapAbsensiDialog by remember { mutableStateOf(false) }

    var selectedMonth by remember { mutableStateOf(LocalDate.now().monthValue) }
    var selectedYear by remember { mutableStateOf(LocalDate.now().year) }
    var showUploadLampiran by remember { mutableStateOf(false) }
    var showMonthPicker by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    DisposableEffect(Unit) {
        onDispose { CameraManager.releaseCamera() }
    }

    LaunchedEffect(Unit) {
        absensiViewModel.detectCamera()
    }

    LaunchedEffect(absensiResult) {
        if (absensiResult.status == AbsensiStatus.SUCCESS || absensiResult.status == AbsensiStatus.FAILED) {
            showLoadingDialog = false
            showAlertDialog = true
        } else if (absensiResult.status == AbsensiStatus.LOADING) {
            showAlertDialog = false
            showLoadingDialog = true
        } else {
            showAlertDialog = false
            showLoadingDialog = false
        }
    }

    // --- DIALOGS SECTION ---

    // 1. Dialog Rekap Absensi (BARU DITAMBAHKAN)
    if (showRekapAbsensiDialog) {
        RekapAbsensiDialogWrapper(
            perangkatId = perangkat?.id ?: 0,
            viewModel = absensiViewModel,
            onDismiss = { showRekapAbsensiDialog = false }
        )
    }

    if (showUploadLampiran) {
        UploadLampiranDialog(
            onDismiss = { showUploadLampiran = false },
            onUpload = { date, jenis, _, file ->
                absensiViewModel.ajukanAbsensiIzin((perangkat?.id ?: 0).toLong(), date, jenis, file)
                showUploadLampiran = false
            }
        )
    }

    if (showMonthPicker) {
        MonthPicker(
            currentMonth = selectedMonth,
            currentYear = selectedYear,
            onDismiss = { showMonthPicker = false },
            onMonthYearSelected = { month, year ->
                selectedMonth = month
                selectedYear = year
                absensiViewModel.getAbsensiByPerangkatAndMonth(perangkat?.id ?: 0, month, year)
            }
        )
    }

    if (showAlertDialog) {
        val imageVector = if (absensiResult.status == AbsensiStatus.SUCCESS) Icons.Default.Done else Icons.Default.Warning
        val tint = if (absensiResult.status == AbsensiStatus.SUCCESS) Color.Green else Color.Red
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
            onDismiss = { showCameraFaceRef = false },
            onCapture = {
                showCameraFaceRef = false
                absensiViewModel.saveImageReference("${perangkat?.id}", "${perangkat?.id}")
            },
            actionText = "Ambil Gambar"
        )
    }

    if (showCamereAbsensi) {
        CameraDialog(
            onDismiss = { showCamereAbsensi = false },
            onCapture = {
                absensiViewModel.saveImageAbsensi(perangkat?.id ?: 0)
                showCamereAbsensi = false
            },
            actionText = "Mulai Absensi"
        )
    }

    // --- MAIN UI SECTION ---

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(top = 16.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // CARD PROFILE
            Card(
                modifier = Modifier
                    .widthIn(min = 350.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .widthIn(min = 350.dp)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    ImagePerangkatCard(
                        file = getFile("${perangkat?.id}", "${perangkat?.id}.jpg"),
                        onTakePicture = {
                            if (isCameraReady) showCameraFaceRef = true else absensiViewModel.detectCamera()
                        }
                    )

                    Spacer(modifier = Modifier.width(20.dp))

                    Column(
                        modifier = Modifier.wrapContentWidth(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = perangkat?.nama.orEmpty(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontFamily = FontFamily(Font(Res.font.geofish))
                        )

                        Text(
                            text = perangkat?.jabatan.orEmpty(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6C7A89),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontFamily = FontFamily(Font(Res.font.geofish)),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        AbsensiButton(
                            enabled = !showCameraFaceRef && !showCamereAbsensi,
                            viewModel = absensiViewModel,
                            onAbsen = {
                                if (isCameraReady) showCamereAbsensi = true else absensiViewModel.detectCamera()
                            },
                            onIzin = { showUploadLampiran = !showUploadLampiran }
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            // STATISTIK
            StatistikBulanan(
                perangkatId = perangkat?.id ?: 0,
                viewModel = absensiViewModel,
                modifier = Modifier.padding(horizontal = 32.dp),
                onShowRekapAbsensi =  {
                    showRekapAbsensiDialog = true
                }
            )
        }

        // SCROLLBAR
        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(scrollState),
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight()
        )

        // TOMBOL KEMBALI
        OutlinedButton(
            modifier = Modifier.align(Alignment.TopEnd).padding(32.dp),
            onClick = { onNavigateBack() },
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Kembali")
        }

        if (showLoadingDialog) {
            LoadingScreen(
                message = absensiResult.message,
                onCancel = {
                    absensiViewModel.setAbsensiResult()
                    if (!isCameraReady) onNavigateBack()
                }
            )
        }
    }
}

// --- WRAPPER DIALOG COMPONENT ---
// Ini membuat tampilan RekapAbsensiHarian menjadi Popup Full Screen yang rapi
@Composable
fun RekapAbsensiDialogWrapper(
    perangkatId: Int,
    viewModel: AbsensiViewModel,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        // Properties ini PENTING agar dialog bisa melebar penuh
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f) // Lebar 95% layar
                .fillMaxHeight(0.90f) // Tinggi 90% layar
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column {
                    // Header Dialog dengan Tombol Close
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Tutup")
                        }
                    }

                    // Panggil Content Asli Anda
                    RekapAbsensiHarian(
                        perangkatId = perangkatId,
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}