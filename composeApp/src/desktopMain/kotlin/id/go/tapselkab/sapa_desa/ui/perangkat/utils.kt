import id.go.tapselkab.sapa_desa.utils.time.DateManager
import id.go.tapselkab.sapa_desa.utils.time.TimeManager

fun attanceTextStatus(attendance: Long?, date: Long): String {
    val thisDay = DateManager.getMillisAt0815()
    return if (attendance == null) {
        if (thisDay == date) {
            "Belum Absen"
        } else {
            "Tidak Absen"
        }
    } else {
        TimeManager.formatMillisToHourMinute(
            attendance
        )
    }
}