package id.go.tapselkab.sapa_desa.ui.component.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinproject.composeapp.generated.resources.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font

@Composable
fun PengaturanDialog(
    onDismiss: () -> Unit,
    onPerbaharuiClick: () -> Unit = {},
    onSinkronisasiClick: () -> Unit = {},
    onKeluarClick: () -> Unit = {},
) {
    Dialog(
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
        onDismissRequest = {

            onDismiss()
        }
    ) {
        PengaturanScreen(
            onPerbaharuiClick = {
                onPerbaharuiClick()
            },
            onSinkronisasiClick = {
                onSinkronisasiClick()
            },
            onKeluarClick = {
                onKeluarClick()
            },
            onCloseClick = {
                onDismiss()
            }
        )
    }
}

@Composable
fun PengaturanScreen(
    onPerbaharuiClick: () -> Unit = {},
    onSinkronisasiClick: () -> Unit = {},
    onKeluarClick: () -> Unit = {},
    onCloseClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .width(400.dp)
                .wrapContentHeight(),
            elevation = 8.dp,
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Tombol keluar di pojok kanan atas
                IconButton(
                    onClick = onCloseClick,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup")
                }

                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Pengaturan",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C3E50),
                        letterSpacing = 1.2.sp,
                        fontFamily = FontFamily(Font(Res.font.geofish))
                    )

                    Divider()

                    Spacer(Modifier.height(8.dp))

                    PengaturanItem(
                        text = "Perbaharui Aparatur Desa",
                        icon = Icons.Default.Refresh,
                        onClick = onPerbaharuiClick
                    )

                    PengaturanItem(
                        text = "Sinkronisasi Ulang Perangkat",
                        icon = Icons.Default.Sync,
                        onClick = onSinkronisasiClick
                    )

                    PengaturanItem(
                        text = "Keluar",
                        icon = Icons.AutoMirrored.Filled.ExitToApp,
                        onClick = onKeluarClick
                    )
                }
            }
        }
    }
}

@Composable
fun PengaturanItem(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text
        )

        IconButton(onClick = onClick) {
            Icon(icon, contentDescription = text)
        }
    }
}