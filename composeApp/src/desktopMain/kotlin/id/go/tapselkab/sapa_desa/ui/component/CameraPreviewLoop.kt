package id.go.tapselkab.sapa_desa.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import id.go.tapselkab.sapa_desa.utils.camera.CameraManager
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingUtilities
import java.awt.BorderLayout
import java.awt.image.BufferedImage

@Composable
fun CameraPreviewLoop() {
    val imageLabel = remember { JLabel() }
    val panel = remember {
        JPanel(BorderLayout()).apply {
            add(imageLabel, BorderLayout.CENTER)
        }
    }

    LaunchedEffect(Unit) {
        while (CameraManager.isCameraOpen()) {
            val frame: BufferedImage? = CameraManager.readFrame()
            frame?.let {
                SwingUtilities.invokeLater {
                    // Resize gambar ke ukuran label
                    val resized = it.getScaledInstance(
                        imageLabel.width.coerceAtLeast(1), // Hindari width = 0
                        imageLabel.height.coerceAtLeast(1),
                        BufferedImage.SCALE_SMOOTH
                    )
                    imageLabel.icon = ImageIcon(resized)
                }
            }
            delay(33L)
        }
    }

    SwingPanel(
        background = Color.Black,
        factory = { panel },
        modifier = Modifier
            .width(1067.dp)
            .height(600.dp)
    )
}
