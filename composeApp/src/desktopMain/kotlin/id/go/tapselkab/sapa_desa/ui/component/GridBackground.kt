package id.go.tapselkab.sapa_desa.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.logo_app
import kotlinproject.composeapp.generated.resources.logo_tapsel
import org.jetbrains.compose.resources.painterResource


@Composable
fun GridBackground(
    gridColor: Color = Color.Gray.copy(alpha = 0.2f),
    spacing: Dp = 40.dp,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Canvas(modifier = Modifier.fillMaxSize()) {
            val step = spacing.toPx()
            val width = size.width
            val height = size.height

            // Vertical lines
            var x = 0f
            while (x <= width) {
                drawLine(
                    color = gridColor,
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 1f
                )
                x += step
            }

            // Horizontal lines
            var y = 0f
            while (y <= height) {
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f
                )
                y += step
            }
        }

        // Gambar PNG dengan transparansi
        Image(
            painter = painterResource(Res.drawable.logo_tapsel),
            contentDescription = "Logo Tapsel",
            modifier = Modifier.size(640.dp),
            alpha = 0.1f
        )
    }
}
