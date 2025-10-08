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
import id.go.tapselkab.sapa_desa.ui.entity.AttendanceEntity
import id.go.tapselkab.sapa_desa.utils.time.DateManager
import org.koin.compose.koinInject
import java.time.LocalDate

@Composable
fun RekapAbsensiHarian(
    userId: Int,
    viewModel: AttendanceViewModel,
) {

    LaunchedEffect(Unit) {
        viewModel.initScreen()
        viewModel.getAttendenceByUserAndMonth(userId = userId)

    }

    val attendances by viewModel.attendances.collectAsState()

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
                viewModel.getUpdateAttendance(
                    userId = userId,
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
                    viewModel.exportAttendanceToCSV(attendances)
                },
                content = {
                    Text("Export")
                }
            )
        }
        Spacer(Modifier.height(6.dp))
        HeaderTableAbsensi()
        attendances.forEach {
            BodyTableAbsensi(
                attendance = it,
                onSendAttendance = { attendance ->
                    viewModel.sendAttendanceToServer(attendance)
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
    attendance: AttendanceEntity,
    onSendAttendance: (attendance: AttendanceEntity) -> Unit
) {

    Row {

        Text(
            modifier = Modifier
                .weight(1f)
                .border(2.dp, Color.Black)
                .padding(6.dp),
            text = DateManager.formatMillisToDate(attendance.date),
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.weight(1f)
                .border(2.dp, Color.Black)
                .padding(6.dp),
            text = attanceTextStatus(attendance = attendance.attendanceMorning, date = attendance.date ?: 0),
            textAlign = TextAlign.Center
        )

        Text(
            modifier = Modifier.weight(1f)
                .border(2.dp, Color.Black)
                .padding(6.dp),
            text = if (attendance.late == null) "~" else "${attendance.late} Menit",
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.weight(1f)
                .border(2.dp, Color.Black)
                .padding(6.dp),
            text = attanceTextStatus(attendance = attendance.attendanceAfternoon, date = attendance.date ?: 0),
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.weight(1f)
                .border(2.dp, Color.Black)
                .padding(6.dp),
            text = if (attendance.early == null) "~" else "${attendance.early} Menit",
            textAlign = TextAlign.Center
        )

        Text(
            modifier = Modifier.weight(1f)
                .clickable {
                    onSendAttendance(attendance)
//                    when (attendance.syncStatus) {
//                        0 -> {
//                            onSendAttendance(attendance)
//                        }
//                    }
                }
                .border(2.dp, Color.Black)
                .padding(6.dp),
            text = if (attendance.syncStatus == 0) "Kirim" else "Terkirim",
            textAlign = TextAlign.Center,
            color = if (attendance.syncStatus == 0) Color.Blue else Color.Gray,
            fontWeight = FontWeight.ExtraBold
        )
    }

}
