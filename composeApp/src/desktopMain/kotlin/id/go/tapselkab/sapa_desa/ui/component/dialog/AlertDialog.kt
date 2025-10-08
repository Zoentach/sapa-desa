package id.go.tapselkab.sapa_desa.ui.component.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.geofish
import org.jetbrains.compose.resources.Font

@Composable
fun AlertDialogCustom(
    message: String,
    onCancel: () -> Unit,
    onOke: () -> Unit
) {

    Dialog(
        onDismissRequest = {
            onCancel()
        }
    ) {
        Card (){
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = message,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C3E50),
                    letterSpacing = 1.2.sp,
                    fontFamily = FontFamily(Font(Res.font.geofish)),
                    modifier = Modifier
                        .padding(32.dp)
                )

                Spacer(
                    modifier = Modifier.padding(vertical = 32.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    TextButton(
                        onClick = {
                            onCancel()
                        }
                    ) {
                        Text(
                            text = "Batal"
                        )
                    }

                    TextButton(
                        onClick = {
                            onOke()
                        }
                    ) {
                        Text(
                            text = "Oke"
                        )
                    }
                }
            }
        }
    }
}