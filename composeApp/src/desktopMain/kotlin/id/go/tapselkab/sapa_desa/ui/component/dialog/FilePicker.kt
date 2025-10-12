package id.go.tapselkab.sapa_desa.ui.component.dialog


import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.awt.FileDialog
import java.io.File

@Composable
fun PdfFilePicker(
    selectedFile: String = "",
    onFileSelected: (File) -> Unit
) {
    var filePath by remember { mutableStateOf(selectedFile) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = filePath,
            onValueChange = {},
            label = { Text("Lampiran PDF") },
            readOnly = true,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Button(onClick = {
            val dialog = FileDialog(null as java.awt.Frame?, "Pilih file PDF", FileDialog.LOAD)
            dialog.isVisible = true
            dialog.file?.let { fileName ->
                val file = File(dialog.directory, fileName)
                if (file.extension.lowercase() == "pdf") {
                    filePath = file.absolutePath
                    onFileSelected(file)
                } else {
                    // Bisa tampilkan alert: hanya PDF
                    println("Harus file PDF")
                }
            }
        }) {
            Text("Pilih File")
        }
    }
}