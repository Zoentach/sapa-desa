package id.go.tapselkab.sapa_desa.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.go.tapselkab.sapa_desa.ui.component.gradient.gradient

@Composable
fun StatistikCard(
    judul: String,
    jumlah: Int,
    satuan: String = "",
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {


    Card(
        onClick = onClick,
        modifier = modifier
            .wrapContentWidth()
            .height(140.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .widthIn(min = 140.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = judul,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    modifier = Modifier.wrapContentWidth(),
                    //   horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = jumlah.toString(),
                        color = Color.White,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (satuan.isNotBlank()) {
                        Text(
                            text = satuan,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
