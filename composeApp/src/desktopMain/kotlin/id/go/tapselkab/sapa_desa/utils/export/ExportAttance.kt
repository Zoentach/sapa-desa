package id.go.tapselkab.sapa_desa.utils.export

import id.go.tapselkab.sapa_desa.ui.entity.AbsensiEntity
import id.go.tapselkab.sapa_desa.utils.file.PickFolder
import id.go.tapselkab.sapa_desa.utils.time.DateManager
import id.go.tapselkab.sapa_desa.utils.time.TimeManager

fun convertabsensiToCSV(absensis: List<AbsensiEntity>): String {
    val header = "ID,User ID,Date,Morning,Afternoon,Late,Early,Sync Status\n"
    val rows = absensis.joinToString("\n") { att ->
        listOf(
            att.id,
            att.perangkatId.toString(),
            att.tanggal,
            if (att.absensiPagi == null) "Tidak Absen" else att.absensiPagi,
            if (att.absensiSore == null) "Tidak Absen" else att.absensiSore,
            att.keterlambatan?.toString() ?: "",
            att.pulangCepat?.toString() ?: "",
            att.syncStatus.toString()
        ).joinToString(",")
    }
    return header + rows
}

fun exportCSVToUserLocation(absensis: List<AbsensiEntity>) {
    val csvContent = convertabsensiToCSV(absensis)
    val file = PickFolder()
    if (file != null) {
        file.writeText(csvContent)
        println("File berhasil disimpan di: ${file.absolutePath}")
    } else {
        println("Penyimpanan dibatalkan oleh pengguna.")
    }
}