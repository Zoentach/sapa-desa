package id.go.tapselkab.sapa_desa

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import id.go.tapselkab.sapa_desa.graph.DasGraph
import id.go.tapselkab.sapa_desa.ui.component.GridBackground
import org.jetbrains.compose.ui.tooling.preview.Preview



@Composable
@Preview
fun App() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = Color.White,
                )
        ) {

            GridBackground()

            DasGraph()
        }
    }
}