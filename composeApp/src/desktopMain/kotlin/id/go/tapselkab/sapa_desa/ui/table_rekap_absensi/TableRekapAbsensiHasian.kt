package id.go.tapselkab.sapa_desa.ui.table_rekap_absensi

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.go.tapselkab.sapa_desa.ui.component.dialog.MonthPicker
import id.go.tapselkab.sapa_desa.ui.component.dialog.UploadLampiranDialog
import id.go.tapselkab.sapa_desa.ui.entity.AbsensiEntity
import id.go.tapselkab.sapa_desa.ui.perangkat.AbsensiViewModel
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class) // Diperlukan untuk stickyHeader
@Composable
fun RekapAbsensiHarian(
    perangkatId: Int,
    viewModel: AbsensiViewModel,
    modifier: Modifier = Modifier,
) {
    // State Collection
    val absensis by viewModel.absensis.collectAsState()
    val currentDateStr by viewModel.thisMonth.collectAsState()

    // Local UI State
    var selectedMonth by remember { mutableStateOf(LocalDate.now().monthValue) }
    var selectedYear by remember { mutableStateOf(LocalDate.now().year) }
    var showMonthPicker by remember { mutableStateOf(false) }

    // Init Data
    LaunchedEffect(perangkatId) {
        viewModel.initScreen()
        viewModel.getAbsensiByPerangkatAndMonth(perangkatId = perangkatId)
    }

    if (showMonthPicker) {
        MonthPicker(
            currentMonth = selectedMonth,
            currentYear = selectedYear,
            onDismiss = { showMonthPicker = false },
            onMonthYearSelected = { month, year ->
                selectedMonth = month
                selectedYear = year
                viewModel.getAbsensiByPerangkatAndMonth(perangkatId, month, year)
            }
        )
    }

    // --- MAIN LAYOUT ---
    // Kita gunakan Box sebagai container utama
    Box(
        modifier = modifier
            .fillMaxSize() // Mengisi seluruh ukuran window/layar
            .padding(16.dp)
    ) {

        // LazyColumn menangani scroll untuk SEMUA elemen
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {

            // 1. BAGIAN JUDUL & TOMBOL (Akan ikut ter-scroll ke atas)
            item {
                TopBarSection(
                    dateString = currentDateStr,
                    onCalendarClick = { showMonthPicker = true },
                    onExportClick = { viewModel.exportAbsensi(absensis) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 2. HEADER TABEL (STICKY/MENEMPEL)
            // stickyHeader membuat header ini tetap terlihat di atas saat list di scroll
            stickyHeader {
                // Penting: Beri background color agar tulisan list tidak terlihat "tembus" di belakang header
                Box(modifier = Modifier.background(MaterialTheme.colors.surface)) {
                    HeaderTableAbsensi()
                }
            }

            // 3. ISI TABEL (DATA)
            items(absensis) { item ->
                BodyTableAbsensi(
                    absensi = item,
                    onSendAbsensi = { viewModel.sendAbsensi(it) }
                )
            }

            // Spacer bawah agar data terakhir tidak tertutup navigation bar atau border bawah
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// --- Komponen Pendukung (Helper) ---

@Composable
private fun TopBarSection(
    dateString: String,
    onCalendarClick: () -> Unit,
    onExportClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Rekap Absen : $dateString",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            IconButton(onClick = onCalendarClick) {
                Icon(Icons.Default.CalendarMonth, contentDescription = "Pilih Bulan")
            }
        }
        OutlinedButton(
            onClick = onExportClick,
            enabled = false
        ) {
            Text("Ekspor")
        }
    }
}

@Composable
fun HeaderTableAbsensi() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            // Border hanya di header atau di container utama, sesuaikan selera
            .border(1.dp, Color.Black)
    ) {
        TableCell(text = "Tanggal", isHeader = true)
        TableCell(text = "Pagi", isHeader = true)
        TableCell(text = "Terlambat", isHeader = true)
        TableCell(text = "Sore", isHeader = true)
        TableCell(text = "Plg Cepat", isHeader = true)
        TableCell(text = "Status", isHeader = true)
    }
}

@Composable
fun BodyTableAbsensi(
    absensi: AbsensiEntity,
    onSendAbsensi: (AbsensiEntity) -> Unit
) {
    // Menggunakan border bawah saja untuk baris data agar lebih rapi (opsional)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = Color.Black) // Border kotak per baris
    ) {
        TableCell(text = absensi.tanggal.orEmpty())
        TableCell(text = absensi.absensiPagi.orEmpty())
        TableCell(text = formatDuration(absensi.keterlambatan))
        TableCell(text = absensi.absensiSore.orEmpty())
        TableCell(text = formatDuration(absensi.pulangCepat))

        val isNotSynced = absensi.syncStatus == 0
        TableCell(
            text = if (isNotSynced) "Belum Terkirim" else absensi.keterangan.orEmpty(),
            textColor = if (isNotSynced) Color.Blue else Color.Green,
            isBold = true,
           // modifier = Modifier.clickable { onSendAbsensi(absensi) }
        )
    }
}

@Composable
fun RowScope.TableCell(
    text: String,
    modifier: Modifier = Modifier,
    isHeader: Boolean = false,
    textColor: Color = Color.Unspecified,
    isBold: Boolean = false
) {
    Text(
        text = text,
        color = textColor,
        fontWeight = if (isHeader || isBold) FontWeight.Bold else FontWeight.Normal,
        textAlign = TextAlign.Center,
        fontSize = 12.sp,
        modifier = modifier
            .weight(1f)
            .padding(8.dp) // Padding text di dalam sel
            .border(0.dp, Color.Transparent) // Trik agar layouting weight bekerja sempurna
    )
    // Divider Vertikal (Garis pemisah antar kolom)
    Spacer(modifier = Modifier.width(1.dp).fillMaxHeight().background(Color.Black))
}

private fun formatDuration(minutes: Int?): String {
    return if (minutes == null || minutes == 0) "~" else "$minutes Menit"
}