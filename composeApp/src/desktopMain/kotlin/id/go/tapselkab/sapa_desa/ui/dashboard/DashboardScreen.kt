package id.go.tapselkab.sapa_desa.ui.dashboard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinproject.composeapp.generated.resources.*
import kotlinproject.composeapp.generated.resources.Res
import id.go.tapselkab.sapa_desa.ui.component.dialog.AlertDialogCustom
import id.go.tapselkab.sapa_desa.ui.component.dialog.PengaturanDialog
import id.go.tapselkab.sapa_desa.ui.entity.PerangkatEntity
import id.go.tapselkab.sapa_desa.ui.perangkat.ImagePerangkatCard
import id.go.tapselkab.sapa_desa.utils.file.getFile
import org.jetbrains.compose.resources.Font
import org.koin.compose.koinInject
import java.io.File

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = koinInject(),
    onNavigateToPerangkat: (perangkat: PerangkatEntity) -> Unit,
    onNavigateToVerifikasiAbsensi: (userId: Int, token: String) -> Unit,
    onNavigateBack: () -> Unit
) {


    val perangkatDesa by viewModel.perangkatDesa.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val verifikasiAbsensi by viewModel.verifikasiAbsensi.collectAsState()

    var showAlertDialog by remember { mutableStateOf(false) }
    var verifikasiAlertDialog by remember { mutableStateOf(false) }
    var alertMessage by remember { mutableStateOf("") }
    var showPengaturan by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getCurrentUser()
    }

    if (verifikasiAlertDialog) {

        AlertDialogCustom(
            message = "Daftarkan perangkat ini ke Server ?",
            onCancel = {
                verifikasiAlertDialog = false
            },
            onOke = {
                verifikasiAlertDialog = false
                onNavigateToVerifikasiAbsensi(
                    currentUser?.id ?: 0,
                    currentUser?.token ?: ""
                )
            },
        )
    }

    if (showAlertDialog) {

        AlertDialogCustom(
            message = alertMessage,
            onCancel = {
                showAlertDialog = false
            },
            onOke = {
                showAlertDialog = false
            },
        )
    }

    if (showPengaturan) {
        PengaturanDialog(
            onPerbaharuiClick = {
                showPengaturan = false
                viewModel.perbaharuiPerangkatDesa()
            },
            onSinkronisasiClick = {
                showPengaturan = false
                onNavigateToVerifikasiAbsensi(
                    currentUser?.id ?: 0,
                    currentUser?.token ?: ""
                )
            },
            onKeluarClick = {
                showPengaturan = false
                if (currentUser != null) {
                    currentUser?.let { user ->
                        if (user.token == "default") {
                            alertMessage = "Anda login dengan kredensial, keluar aplikasi tidak diperlukan"
                            showAlertDialog = true
                        } else {
                            viewModel.logOut()
                            onNavigateBack()
                        }
                    }
                } else {
                    viewModel.logOut()
                    onNavigateBack()
                }
            },
            onDismiss = {
                showPengaturan = false
            }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            headerDashBoard(
                onPengaturan = {
                    showPengaturan = !showPengaturan
                },
            )

            Column(
                modifier = Modifier.fillMaxWidth()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HeaderText(currentUser?.name ?: "")

                if (perangkatDesa.isEmpty()) {
                    OutlinedButton(
                        modifier = Modifier
                            .padding(32.dp),
                        onClick = {
                            onNavigateToVerifikasiAbsensi(
                                currentUser?.id ?: 0,
                                currentUser?.token ?: ""
                            )

                        },
                    ) {

                        Text(
                            text = "Lanjut Sinkronisasi"
                        )

//                        Icon(
//                            imageVector = Icons.AutoMirrored.Filled.Logout,
//                            contentDescription = ""
//                        )
                    }
                } else {
                    BaganPerangkat(
                        perangkats = perangkatDesa,
                        onSelected = {
                            if (verifikasiAbsensi != null) {
                                onNavigateToPerangkat(it)
                            } else {
                                verifikasiAlertDialog = true
                            }
                        }
                    )
                }

            }
        }
    }
}

@Composable
fun headerDashBoard(
    onPengaturan: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp), // padding opsional
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {

        OutlinedButton(
            modifier = Modifier
                .padding(32.dp),
            onClick = {
                onPengaturan()
            },
        ) {

            Text(
                text = "Pengaturan"
            )

            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = ""
            )
        }
    }
}


@Composable
fun HeaderText(
    name: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sistem Absensi".uppercase(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2C3E50),
            letterSpacing = 1.2.sp,
            fontFamily = FontFamily(Font(Res.font.geofish))
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Pemerintah Desa".uppercase(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF34495E),
            letterSpacing = 1.sp,
            fontFamily = FontFamily(Font(Res.font.geofish))
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = name.uppercase(),
            fontSize = 22.sp,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF6C7A89),
            letterSpacing = 0.8.sp,
            fontFamily = FontFamily(Font(Res.font.geofish))
        )
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun perangkatCard(
    modifier: Modifier,
    perangkat: PerangkatEntity,
    file: File,
    onSelected: (id: Int) -> Unit
) {

    Card(
        shape = RoundedCornerShape(10.dp),
        onClick = {
            onSelected(perangkat.id)
        },
        border = BorderStroke(width = 4.dp, color = Color.DarkGray)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            ImagePerangkatCard(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(4.dp)),
                file = file
            )
            Spacer(
                modifier = Modifier.width(4.dp)
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = perangkat.nama,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = perangkat.jabatan,
                    fontSize = 12.sp,
                )
            }
        }
    }
}

@Composable
fun BaganPerangkat(
    perangkats: List<PerangkatEntity>,
    onSelected: (perangkat: PerangkatEntity) -> Unit
) {


    val kepalaDesa = perangkats.firstOrNull { it.kodeJabatan == "PD01" }
    val sekDes = perangkats.firstOrNull { it.kodeJabatan == "PD02" }
    val kaurTasum = perangkats.firstOrNull { it.kodeJabatan == "PD03" }
    val kaurKeuangan = perangkats.firstOrNull() { it.kodeJabatan == "PD04" }
    val kaurPerencanaan = perangkats.firstOrNull() { it.kodeJabatan == "PD05" }
    val kaurKepe = perangkats.firstOrNull() { it.kodeJabatan == "PD45" }

    val kasiPemerintahan = perangkats.firstOrNull { it.kodeJabatan == "PD06" }
    val kasiKes = perangkats.firstOrNull { it.kodeJabatan == "PD07" }
    val kasiPel = perangkats.firstOrNull { it.kodeJabatan == "PD08" }
    val kasiKesPel = perangkats.firstOrNull { it.kodeJabatan == "PD78" }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        if (kepalaDesa != null)
            KepalaDesa(
                kepalaDesa = kepalaDesa,
                onSelected = {
                    onSelected(it)
                }
            )


        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            //kasi
            Kasi(
                modifier = Modifier.weight(1f),
                kasiKes = kasiKes,
                kasiPel = kasiPel,
                kasiPemerintahan = kasiPemerintahan,
                kasiKesPel = kasiKesPel,
                onSelected = {
                    onSelected(it)
                }
            )

            //sekdes dan kaur
            SekdesAndKaur(
                modifier = Modifier.weight(1f),
                sekdes = sekDes,
                kaurTatum = kaurTasum,
                kaurKeuangan = kaurKeuangan,
                kaurPerencanaan = kaurPerencanaan,
                kaurKepe = kaurKepe,
                onSelected = {
                    onSelected(it)
                }
            )
        }
    }
}

@Composable
fun KepalaDesa(
    kepalaDesa: PerangkatEntity?,
    onSelected: (perangkat: PerangkatEntity) -> Unit
) {
    //kepala desa
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 32.dp,
                bottom = 16.dp
            )
    ) {
        Spacer(
            modifier = Modifier.weight(1f)
        )
        Spacer(
            modifier = Modifier.weight(1f)
        )
        if (kepalaDesa != null) {
            perangkatCard(
                modifier = Modifier.weight(1f),
                perangkat = kepalaDesa,
                file = getFile(folderName = "${kepalaDesa.id}", fileName = "${kepalaDesa.id}.jpg"),
                onSelected = {
                    onSelected(kepalaDesa)
                }
            )
        } else {
            Spacer(
                modifier = Modifier.weight(2f)
            )
        }
        Spacer(
            modifier = Modifier.weight(1f)
        )
        Spacer(
            modifier = Modifier.weight(1f)
        )
    }
}


@Composable
fun Kasi(
    modifier: Modifier = Modifier,
    kasiKes: PerangkatEntity?,
    kasiPel: PerangkatEntity?,
    kasiPemerintahan: PerangkatEntity?,
    kasiKesPel: PerangkatEntity?,
    onSelected: (perangkat: PerangkatEntity) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement
            .SpaceAround
    )
    {
        //kasi pemerintahan
        if (kasiPemerintahan != null) {
            perangkatCard(
                modifier = Modifier,
                perangkat = kasiPemerintahan,
                file = getFile(folderName = "${kasiPemerintahan.id}", fileName = "${kasiPemerintahan.id}.jpg"),
                onSelected = {
                    onSelected(kasiPemerintahan)
                }
            )
        }

        //kasi Kesejahteraan dan Pelayanan
        if (kasiKesPel != null) {
            perangkatCard(
                modifier = Modifier,
                perangkat = kasiKesPel,
                file = getFile(folderName = "${kasiKesPel.id}", fileName = "${kasiKesPel.id}.jpg"),
                onSelected = {
                    onSelected(kasiKesPel)
                }
            )
        } else {
            //kasi kesejahteraan
            if (kasiKes != null) {
                perangkatCard(
                    modifier = Modifier,
                    perangkat = kasiKes,
                    file = getFile(folderName = "${kasiKes.id}", fileName = "${kasiKes.id}.jpg"),
                    onSelected = {
                        onSelected(kasiKes)
                    }
                )
            }

            //kasi pelayanan
            if (kasiPel != null) {
                perangkatCard(
                    modifier = Modifier,
                    perangkat = kasiPel,
                    file = getFile(folderName = "${kasiPel.id}", fileName = "${kasiPel.id}.jpg"),
                    onSelected = {
                        onSelected(kasiPel)
                    }
                )
            }
        }
    }
}


@Composable
fun Kaur(
    modifier: Modifier = Modifier,
    kaurTatum: PerangkatEntity?,
    kaurKeuangan: PerangkatEntity?,
    kaurPerencanaan: PerangkatEntity?,
    kaurKepe: PerangkatEntity?,
    onSelected: (perangkat: PerangkatEntity) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement
            .SpaceAround
    )
    {
        //kaur tata usaha dan umum
        if (kaurTatum != null) {
            perangkatCard(
                modifier = Modifier,
                perangkat = kaurTatum,
                file = getFile(folderName = "${kaurTatum.id}", fileName = "${kaurTatum.id}.jpg"),
                onSelected = {
                    onSelected(kaurTatum)
                }
            )
        }

        //kaur keuangan
        if (kaurKepe != null) {
            perangkatCard(
                modifier = Modifier,
                perangkat = kaurKepe,
                file = getFile(folderName = "${kaurKepe.id}", fileName = "${kaurKepe.id}.jpg"),
                onSelected = {
                    onSelected(kaurKepe)
                }
            )
        } else {
            //kasi kesejahteraan
            if (kaurKeuangan != null) {
                perangkatCard(
                    modifier = Modifier,
                    perangkat = kaurKeuangan,
                    file = getFile(folderName = "${kaurKeuangan.id}", fileName = "${kaurKeuangan.id}.jpg"),
                    onSelected = {
                        onSelected(kaurKeuangan)
                    }
                )
            }

            //kaur tata usaha dan umum
            if (kaurPerencanaan != null) {
                perangkatCard(
                    modifier = Modifier,
                    perangkat = kaurPerencanaan,
                    file = getFile(folderName = "${kaurPerencanaan.id}", fileName = "${kaurPerencanaan.id}.jpg"),
                    onSelected = {
                        onSelected(kaurPerencanaan)
                    }
                )
            }
        }
    }
}

@Composable
fun SekdesAndKaur(
    modifier: Modifier = Modifier,
    sekdes: PerangkatEntity?,
    kaurTatum: PerangkatEntity?,
    kaurKeuangan: PerangkatEntity?,
    kaurPerencanaan: PerangkatEntity?,
    kaurKepe: PerangkatEntity?,
    onSelected: (perangkat: PerangkatEntity) -> Unit
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        //kasi pemerintahan
        if (sekdes != null) {
            perangkatCard(
                modifier = Modifier,
                perangkat = sekdes,
                file = getFile(folderName = "${sekdes.id}", fileName = "${sekdes.id}.jpg"),
                onSelected = {
                    onSelected(sekdes)
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Kaur(
            modifier = Modifier.fillMaxWidth(),
            kaurTatum = kaurTatum,
            kaurKeuangan = kaurKeuangan,
            kaurPerencanaan = kaurPerencanaan,
            kaurKepe = kaurKepe,
            onSelected = {
                onSelected(it)
            }
        )


    }

}
