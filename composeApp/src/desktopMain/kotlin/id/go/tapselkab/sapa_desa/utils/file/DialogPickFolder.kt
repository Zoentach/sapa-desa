package id.go.tapselkab.sapa_desa.utils.file

import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

fun PickFolder(): File? {
    val fileChooser = JFileChooser()
    fileChooser.dialogTitle = "Simpan File CSV"
    fileChooser.fileFilter = FileNameExtensionFilter("CSV files", "csv")
    val result = fileChooser.showSaveDialog(null)
    return if (result == JFileChooser.APPROVE_OPTION) {
        var file = fileChooser.selectedFile
        if (!file.name.endsWith(".csv")) {
            file = File(file.absolutePath + ".csv")
        }
        file
    } else null
}