package id.go.tapselkab.sapa_desa.ui.perangkat

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.profile_default
import org.jetbrains.compose.resources.painterResource
import java.io.File

@Composable
fun ImagePerangkatCard(
    modifier: Modifier = Modifier,
    file: File,
    onTakePicture: () -> Unit
) {

    Card(
        modifier = modifier
    ) {
        if (!file.exists()) {
            Column(
                modifier = Modifier
                    .padding(8.dp),
                horizontalAlignment = Alignment
                    .CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    modifier = Modifier
                        .size(80.dp),
                    painter = painterResource(Res.drawable.profile_default),
                    contentDescription = ""
                )
                OutlinedButton(
                    onClick = {
                        onTakePicture()
                    }
                ) {
                    Text("Ambil Gambar")
                }
            }
        } else {
            val imageBitmap: ImageBitmap? by remember {
                mutableStateOf(
                    try {
                        val byteArray = file.readBytes()
                        val skiaImage = org.jetbrains.skia.Image.makeFromEncoded(byteArray)
                        skiaImage.toComposeImageBitmap()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                )
            }

            imageBitmap?.let {
                Image(
                    modifier = Modifier.size(200.dp),
                    bitmap = it,
                    contentDescription = "Reference Face",
                    contentScale = ContentScale.Crop,
                )
            } ?: Text("Gagal memuat gambar.")
        }
    }

}

@Composable
fun ImagePerangkatCard(
    modifier: Modifier = Modifier,
    file: File,
) {

    if (!file.exists()) {
        Image(
            modifier = modifier,
            painter = painterResource(Res.drawable.profile_default),
            contentDescription = ""
        )
    } else {
        val imageBitmap: ImageBitmap? by remember {
            mutableStateOf(
                try {
                    val byteArray = file.readBytes()
                    val skiaImage = org.jetbrains.skia.Image.makeFromEncoded(byteArray)
                    skiaImage.toComposeImageBitmap()
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            )
        }

        imageBitmap?.let {
            Image(
                modifier = modifier,
                bitmap = it,
                contentDescription = "Reference Face",
                contentScale = ContentScale.Crop,
            )
        } ?: Text("Gagal memuat gambar.")
    }

}
