package id.go.tapselkab.sapa_desa.ui.component.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import id.go.tapselkab.sapa_desa.ui.component.CameraPreviewLoop
import id.go.tapselkab.sapa_desa.utils.camera.CameraManager
import java.io.File

@Composable
fun CameraDialog(
    onDismiss: () -> Unit,
    onCapture: () -> Unit,
    actionText: String
) {
    Dialog(
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
        onDismissRequest = {

            onDismiss()
        }
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Tampilan Kamera", style = MaterialTheme.typography.headlineMedium)

                CameraPreviewLoop()

                Spacer(Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        onCapture()
                    }) {
                        Text(actionText)
                    }

                    Button(onClick = {
                        onDismiss()
                    }) {
                        Text("Batal")
                    }
                }
            }
        }
    }
}
