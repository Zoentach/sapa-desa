package id.go.tapselkab.sapa_desa.ui.component.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import id.go.tapselkab.sapa_desa.utils.time.DateManager
import id.go.tapselkab.sapa_desa.utils.time.DateUtils
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.geofish
import org.jetbrains.compose.resources.Font
import java.awt.FileDialog
import java.io.File


@Composable
fun UploadLampiranDialog(
    onDismiss: () -> Unit,
    onUpload: (date: String, jenis: String, filePath: String, file: File) -> Unit
) {
    Dialog(
        onDismissRequest = {

        }
    ) {
        UploadLampiranScreen(
            onDismiss = onDismiss,
            onUpload = onUpload
        )
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun UploadLampiranScreen(
    onDismiss: () -> Unit,
    onUpload: (date: String, jenis: String, filePath: String, file: File) -> Unit
) {

    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())

    var selectedDate by remember { mutableStateOf("") }
    var selectedJenis by remember { mutableStateOf("") }
    var filePath by remember { mutableStateOf("") }
    var selectedFile by remember { mutableStateOf<File?>(null) }

    val jenisOptions = listOf("Izin", "Sakit", "Cuti", "Tugas Luar")
    var expanded by remember { mutableStateOf(false) }


    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {

                    val date = datePickerState.selectedDateMillis
                    selectedDate = date?.let { DateUtils.toDateString(it) } ?: ""

                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.width(400.dp),
            elevation = 8.dp,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Pengajuan Izin",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C3E50),
                    letterSpacing = 1.2.sp,
                    fontFamily = FontFamily(Font(Res.font.geofish)),
                    modifier = Modifier
                        .padding(32.dp)
                )


                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = selectedDate,
                        onValueChange = {},
                        label = { Text("Tanggal") },
                        readOnly = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(onClick = {

                        // val date =
                        showDatePicker = !showDatePicker
                        //  if (date.isNotEmpty()) selectedDate = date
                    }) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Pilih tanggal"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ⬇️ Dropdown jenis lampiran
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedJenis,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Jenis Lampiran") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            //.menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        jenisOptions.forEach { option ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedJenis = option
                                    expanded = false
                                }
                            ) {
                                Text(text = option)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                //  ROW untuk file picker PDF
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = filePath.takeIf { it.isNotEmpty() }?.substringAfterLast('/') ?: "",
                        onValueChange = {},
                        label = { Text("Lampiran (PDF)") },
                        readOnly = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = {
                        //  val file =
                        val dialog = FileDialog(null as java.awt.Frame?, "Pilih file PDF", FileDialog.LOAD)
                        dialog.isVisible = true
                        dialog.file?.let { fileName ->
                            val file = File(dialog.directory, fileName)
                            if (file.extension.lowercase() == "pdf") {
                                filePath = file.absolutePath
                                selectedFile = file
                            } else {
                                // Bisa tampilkan alert: hanya PDF
                                println("Harus file PDF")
                            }
                        }
                        // if (file.isNotEmpty()) filePath = file
                    }) {
                        Icon(
                            imageVector = Icons.Default.UploadFile,
                            contentDescription = "Pilih File"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Tombol Upload dan Batal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),

                    ) {
                        Text("Batal")
                    }

                    Button(
                        onClick = {
                            if (selectedDate.isNotEmpty() && selectedJenis.isNotEmpty() && selectedFile != null) {
                                onUpload(selectedDate, selectedJenis, filePath, selectedFile!!)
                            }
                        },
                        enabled = selectedDate.isNotEmpty() &&
                                  selectedJenis.isNotEmpty() &&
                                  filePath.isNotEmpty() &&
                                  selectedFile != null,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Upload")
                    }
                }
            }
        }
    }
}