package id.go.tapselkab.sapa_desa.ui.perangkat


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
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
import attanceTextStatus
import id.go.tapselkab.sapa_desa.di.viewModelModule
import id.go.tapselkab.sapa_desa.ui.component.dialog.MonthPicker
import id.go.tapselkab.sapa_desa.ui.entity.AbsensiEntity
import id.go.tapselkab.sapa_desa.utils.time.DateManager
import org.koin.compose.koinInject
import java.time.LocalDate

@Composable
fun RekapAbsensiHarian(
    perangkatId: Int,
    viewModel: AbsensiViewModel,
) {

    LaunchedEffect(Unit) {
        viewModel.initScreen()
        viewModel.getAbsensiByPerangkatAndMonth(perangkatId = perangkatId)

    }

    val absensis by viewModel.absensis.collectAsState()

    val date by viewModel.thisMonth.collectAsState()

    var selectedMonth by remember { mutableStateOf(LocalDate.now().monthValue) }
    var selectedYear by remember { mutableStateOf(LocalDate.now().year) }

    var showMonthPicker by remember {
        mutableStateOf(false)
    }

    if (showMonthPicker) {
        MonthPicker(
            currentMonth = selectedMonth,
            currentYear = selectedYear,
            onDismiss = {
                showMonthPicker = false
            },
            onMonthYearSelected = { month, year ->
                selectedMonth = month
                selectedYear = year
                viewModel.getAbsensiByPerangkatAndMonth(
                    perangkatId = perangkatId,
                    month = month,
                    year = year
                ) // Buat fungsi ini untuk update state

            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rekap Absen Harian : $date",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                IconButton(
                    onClick = {
                        showMonthPicker = true
                    },
                    content = {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null
                        )
                    }
                )
            }

            OutlinedButton(
                onClick = {
                    viewModel.exportAbsensi(absensis)
                },
                content = {
                    Text("Export")
                }
            )
        }
        Spacer(Modifier.height(6.dp))
        HeaderTableAbsensi()
        absensis.forEach {
            BodyTableAbsensi(
                absensi = it,
                onSendabsensi = { absensi ->
                    viewModel.sendAbsensi(absensi)
                }
            )
        }
    }
}

@Composable
fun HeaderTableAbsensi() {
    Row {
        Text(
            modifier = Modifier
                .weight(1f)
                .border(2.dp, Color.Black)
                .padding(6.dp),
            text = "Tanggal",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.weight(1f)
                .border(2.dp, Color.Black)
                .padding(6.dp),
            text = "Pagi",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            modifier = Modifier.weight(1f)
                .border(2.dp, Color.Black)
                .padding(6.dp),
            text = "Terlambat",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.weight(1f)
                .border(2.dp, Color.Black)
                .padding(6.dp),
            text = "Sore",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.weight(1f)
                .border(2.dp, Color.Black)
                .padding(6.dp),
            text = "Pulang Cepat",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            modifier = Modifier.weight(1f)
                .border(2.dp, Color.Black)
                .padding(6.dp),
            text = "Status",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun BodyTableAbsensi(
    absensi: AbsensiEntity,
    onSendabsensi: (absensi: AbsensiEntity) -> Unit
) {

    Row {

        Text(
            modifier = Modifier
                .weight(1f)
                .border(2.dp, Color.Black)
                .padding(6.dp),
            text = absensi.tanggal.orEmpty(),
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.weight(1f)
                .border(2.dp, Color.Black)
                .padding(6.dp),
            text = absensi.absensiPagi.orEmpty(),
            textAlign = TextAlign.Center
        )

        Text(
            modifier = Modifier.weight(1f)
                .border(2.dp, Color.Black)
                .padding(6.dp),
            text = if (absensi.keterlambatan == null) "~" else "${absensi.keterlambatan} Menit",
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.weight(1f)
                .border(2.dp, Color.Black)
                .padding(6.dp),
            text = absensi.absensiSore.orEmpty(),
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.weight(1f)
                .border(2.dp, Color.Black)
                .padding(6.dp),
            text = if (absensi.pulangCepat == null) "~" else "${absensi.pulangCepat} Menit",
            textAlign = TextAlign.Center
        )

        Text(
            modifier = Modifier.weight(1f)
                .clickable {
                    onSendabsensi(absensi)
//                    when (absensi.syncStatus) {
//                        0 -> {
//                            onSendabsensi(absensi)
//                        }
//                    }
                }
                .border(2.dp, Color.Black)
                .padding(6.dp),
            text = if (absensi.syncStatus == 0) "Kirim" else "Terkirim",
            textAlign = TextAlign.Center,
            color = if (absensi.syncStatus == 0) Color.Blue else Color.Gray,
            fontWeight = FontWeight.ExtraBold
        )
    }

}
