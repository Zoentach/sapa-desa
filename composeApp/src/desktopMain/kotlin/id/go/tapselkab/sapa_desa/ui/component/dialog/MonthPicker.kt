package id.go.tapselkab.sapa_desa.ui.component.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.datetime.Month
import id.go.tapselkab.sapa_desa.utils.time.monthList
import java.time.Year
import java.time.LocalDate

@Composable
fun MonthPicker(
    currentMonth: Int,
    currentYear: Int,
    onDismiss: () -> Unit,
    onMonthYearSelected: (Int, Int) -> Unit
) {

    var selectedMonth by remember { mutableStateOf(currentMonth) }
    var selectedYear by remember { mutableStateOf(currentYear) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ganti dengan Dropdown/Picker sesuai kebutuhan
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Navigasi bulan
                IconButton(onClick = {
                    if (selectedMonth == 1) selectedYear--
                    selectedMonth = if (selectedMonth == 1) 12 else selectedMonth - 1
                }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Prev")
                }

                Text(
                    text = "${monthList[selectedMonth].month} $selectedYear",
                )

                IconButton(onClick = {
                    if (selectedMonth == 12) selectedYear++
                    selectedMonth = if (selectedMonth == 12) 1 else selectedMonth + 1
                }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next")
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Batal")
                }
                TextButton(onClick = {
                    onMonthYearSelected(monthList[selectedMonth].monthID, selectedYear)
                    onDismiss()
                }) {
                    Text("Pilih")
                }
            }
        }
    }
}