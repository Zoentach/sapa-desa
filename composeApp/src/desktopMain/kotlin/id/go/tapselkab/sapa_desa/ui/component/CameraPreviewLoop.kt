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
        // Cek pengaman: Jika kamera belum open, coba buka (fallback)
        if (!CameraManager.isCameraOpen()) {
            val idx = CameraManager.findAvailableCameraIndex()
            CameraManager.startCapture(idx)
        }

        while (CameraManager.isCameraOpen()) {
            val frame: BufferedImage? = CameraManager.readFrame()
            if (frame != null) {
                SwingUtilities.invokeLater {
                    // Cek size agar tidak error saat resize
                    val w = imageLabel.width.coerceAtLeast(1)
                    val h = imageLabel.height.coerceAtLeast(1)

                    val resized =
                        frame.getScaledInstance(w, h, BufferedImage.SCALE_FAST) // Pakai FAST agar lebih smooth
                    imageLabel.icon = ImageIcon(resized)
                }
            }
            delay(33L) // ~30 FPS
        }
    }

    SwingPanel(
        background = Color.Black,
        factory = { panel },
        modifier = Modifier
            .width(600.dp)
            .height(600.dp)
    )
}
