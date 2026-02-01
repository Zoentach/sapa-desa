package id.go.tapselkab.sapa_desa.ui.perangkat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material3.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.sp
import id.go.tapselkab.sapa_desa.ui.component.dialog.MonthPicker
import id.go.tapselkab.sapa_desa.ui.component.dialog.UploadLampiranDialog
import id.go.tapselkab.sapa_desa.ui.component.StatistikCard
import id.go.tapselkab.sapa_desa.ui.component.gradient.gradient
import java.time.LocalDate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.geofish
import androidx.compose.ui.text.font.FontFamily
import org.jetbrains.compose.resources.Font


@Composable
fun StatistikBulanan(
    perangkatId: Int,
    viewModel: AbsensiViewModel,
    modifier: Modifier = Modifier,
    onShowRekapAbsensi:() -> Unit
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

        //belum terkirim
        AnimatedVisibility(belumTerkirim > 0) {
                Card(
                    modifier = Modifier
                        .widthIn(min = 350.dp) // Card dipaksa lebar
                        .padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                             .widthIn(min = 350.dp) // <--- GANTI INI (Sebelumnya wrapContentWidth)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        // Pilihan A: Konten kumpul di tengah-tengah (seperti request Anda)
                        horizontalArrangement = Arrangement.Center

                    // Pilihan B (Alternatif): Teks di kiri, Tombol di kanan (biasanya lebih rapi untuk dashboard)
                    // horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Column(
                            // Column cukup wrapContent karena dia ada di dalam Row yang sudah diatur
                            modifier = Modifier.wrapContentWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Belum Terkirim",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontFamily = FontFamily(Font(Res.font.geofish))
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "$belumTerkirim",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                )
                        }

                        // Spacer ini memberi jarak antara Kolom Teks dan Tombol
                        Spacer(modifier = Modifier.width(24.dp)) // Perlebar sedikit agar tidak terlalu mepet

                        Button(
                            onClick = {
                                viewModel.sendAllAbsensi()
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.heightIn(min = 45.dp)
                        ) {
                            Text(
                                text = "Kirim",
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 16.sp
                            )
                        }
                    }

            }
        }

        //Header
        Card(
            onClick = {},
            modifier = modifier
                .wrapContentWidth()
                .heightIn(80.dp)
                .padding(10.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {

            //header
            Column(
                modifier = Modifier
                    .background(gradient)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Rekap Absen $date",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White,
                    )

                OutlinedButton(
                    onClick = onShowRekapAbsensi,
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(
                        text = "Lihat Riwayat Absen", // Tambahkan spasi agar lebih enak dibaca
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

        }




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