package id.go.tapselkab.sapa_desa.utils.export

import id.go.tapselkab.sapa_desa.ui.entity.AttendanceEntity
import id.go.tapselkab.sapa_desa.utils.file.PickFolder
import id.go.tapselkab.sapa_desa.utils.time.DateManager
import id.go.tapselkab.sapa_desa.utils.time.TimeManager

fun convertAttendanceToCSV(attendances: List<AttendanceEntity>): String {
    val header = "ID,User ID,Date,Morning,Afternoon,Late,Early,Sync Status\n"
    val rows = attendances.joinToString("\n") { att ->
        listOf(
            att.id,
            att.userId.toString(),
            DateManager.formatMillisToDate(att.date),
            if (att.attendanceMorning == null) "Tidak Absen" else TimeManager.formatMillisToHourMinute(att.attendanceMorning),
            if (att.attendanceAfternoon == null) "Tidak Absen" else TimeManager.formatMillisToHourMinute(att.attendanceAfternoon),
            att.late?.toString() ?: "",
            att.early?.toString() ?: "",
            att.syncStatus.toString()
        ).joinToString(",")
    }
    return header + rows
}

fun exportCSVToUserLocation(attendances: List<AttendanceEntity>) {
    val csvContent = convertAttendanceToCSV(attendances)
    val file = PickFolder()
    if (file != null) {
        file.writeText(csvContent)
        println("File berhasil disimpan di: ${file.absolutePath}")
    } else {
        println("Penyimpanan dibatalkan oleh pengguna.")
    }
}