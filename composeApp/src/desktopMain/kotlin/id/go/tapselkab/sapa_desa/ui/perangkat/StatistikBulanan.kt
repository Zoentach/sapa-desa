package id.go.tapselkab.sapa_desa.ui.perangkat

import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import id.go.tapselkab.sapa_desa.ui.component.dialog.MonthPicker
import id.go.tapselkab.sapa_desa.ui.component.dialog.UploadLampiranDialog
import id.go.tapselkab.sapa_desa.ui.component.StatistikCard
import java.time.LocalDate


@Composable
fun StatistikBulanan(
    perangkatId: Int,
    viewModel: AbsensiViewModel,
    modifier: Modifier = Modifier,
) {

    LaunchedEffect(Unit) {
        viewModel.initScreen()
        viewModel.getAbsensiByPerangkatAndMonth(perangkatId = perangkatId)

    }

    val absensis by viewModel.absensis.collectAsState()


    val jumlahHadir = absensis.count {
        it.keterangan.equals("hadir", ignoreCase = true)
    }

    val jumlahCuti = absensis.count {
        it.keterangan.equals("cuti", ignoreCase = true)
    }

    val jumlahTugasLuar = absensis.count {
        it.keterangan.equals("tugas luar", ignoreCase = true)
    }

    val jumlahIzin = absensis.count {
        it.keterangan.equals("izin", ignoreCase = true) ||
                it.keterangan.equals("sakit", ignoreCase = true)
    }

    val totalKeterlambatan = absensis.sumOf {
        it.keterlambatan ?: 0
    }

    val totalPulangCepat = absensis.sumOf {
        it.pulangCepat ?: 0
    }

    val belumTerkirim = absensis.count {
        it.syncStatus == 0
    }


    val date by viewModel.thisMonth.collectAsState()

    var selectedMonth by remember { mutableStateOf(LocalDate.now().monthValue) }
    var selectedYear by remember { mutableStateOf(LocalDate.now().year) }
    var showUploadLampiran by remember { mutableStateOf(false) }
    var showMonthPicker by remember {
        mutableStateOf(false)
    }



    if (showUploadLampiran) {
        UploadLampiranDialog(
            onDismiss = {
                showUploadLampiran = false
            },
            onUpload = { date, jenis, filePath, file ->
                viewModel.ajukanAbsensiIzin(
                    perangkatId = perangkatId.toLong(),
                    tanggal = date,
                    keterangan = jenis,
                    lampiran = file
                )
                showUploadLampiran = false
            }
        )
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
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Rekap Absen $date",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        //Absen Belum Terkirim
        StatistikCard(
            judul = "Belum Terkirim",
            jumlah = belumTerkirim,
            satuan = "Hari",
            onClick = {

            },
        )

        Row(
            modifier = Modifier
                .wrapContentWidth()
                .padding(10.dp),
            //horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatistikCard(
                judul = "Jumlah Hadir",
                jumlah = jumlahHadir,
                satuan = "Hari",
                onClick = {},
                //   modifier = Modifier.weight(1f)
            )

            Spacer(
                modifier = Modifier.padding(10.dp)
            )


            StatistikCard(
                judul = "Tugas Luar",
                jumlah = jumlahTugasLuar,
                satuan = "Hari",
                onClick = {},
                //  modifier = Modifier.weight(1f)
            )

            Spacer(
                modifier = Modifier.padding(10.dp)
            )


            StatistikCard(
                judul = "Izin",
                jumlah = jumlahIzin,
                satuan = "Hari",
                onClick = {},
                // modifier = Modifier.weight(1f)
            )

        }


        Row(
            modifier = Modifier
                .wrapContentWidth()
                .padding(10.dp),
        ) {

            //Cuti
            StatistikCard(
                judul = "Cuti",
                jumlah = jumlahCuti,
                satuan = "Hari",
                onClick = {

                },
            )

            Spacer(
                modifier = Modifier.padding(10.dp)
            )

            //Terlambat
            StatistikCard(
                judul = "Terlambat",
                jumlah = totalKeterlambatan,
                satuan = "Menit",
                onClick = {

                },
            )

            Spacer(
                modifier = Modifier.padding(10.dp)
            )

            //Pulang Cepat
            StatistikCard(
                judul = "Pulang Cepat",
                jumlah = totalPulangCepat,
                satuan = "Menit",
                onClick = {

                },
            )
        }

    }


}