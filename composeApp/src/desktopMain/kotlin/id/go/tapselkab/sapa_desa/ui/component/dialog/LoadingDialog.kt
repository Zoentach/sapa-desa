package id.go.tapselkab.sapa_desa.ui.component.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.geofish
import org.jetbrains.compose.resources.Font


@Composable
fun LoadingDialog(
    message: String,
    onCancel: () -> Unit
) {
    Dialog(
        onDismissRequest = {
            onCancel()
        }
    ) {
        LoadingScreen(
            message = message,
            onCancel = {
                onCancel()
            }
        )
    }
}

@Composable
fun LoadingScreen(
    message: String,
    onCancel: () -> Unit
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
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(Modifier.height(10.dp))
                Text(
                    text = message,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(Res.font.geofish))
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    TextButton(
                        onClick = {

                        }
                    ) {

                    }

                    TextButton(
                        onClick = {
                            onCancel()
                        }
                    ) {
                        Text(
                            text = "Batal"
                        )
                    }
                }

            }
        }
    }
}