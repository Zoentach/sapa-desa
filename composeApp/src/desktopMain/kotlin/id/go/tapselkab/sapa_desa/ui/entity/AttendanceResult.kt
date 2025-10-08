package id.go.tapselkab.sapa_desa.ui.entity

import id.go.tapselkab.database.Attendance
import id.go.tapselkab.sapa_desa.core.data.network.model.AttendanceRequest

data class AttendanceResult(
    val status: AttendanceStatus = AttendanceStatus.INITIAL,
    val message: String = ""
)

enum class AttendanceStatus {
    INITIAL, LOADING, SUCCESS, FAILED
}

data class AttendanceEntity(
    val id: Int,
    val userId: Int,
    val kodeDesa: String,
    val kodeKec: String,
    val date: Long?,
    val attendanceMorning: Long? = null,
    val attendanceAfternoon: Long? = null,
    val late: Int? = null,
    val early: Int? = null,
    val syncStatus: Int = 0, // 0 = pagi dan sore belum, 1 = pagi sudah, 2 = pagi sudah dan sore belum
)

fun AttendanceEntity.toRequest(): AttendanceRequest {
    return AttendanceRequest(
        user_id = this.userId,
        kode_desa = this.kodeDesa,
        kode_kec = this.kodeKec,
        date = this.date ?: 0,
        attendance_morning = this.attendanceMorning,
        attendance_afternoon = this.attendanceAfternoon,
        late = this.late,
        early = this.early,
    )
}


fun Attendance.toEntity(): AttendanceEntity {
    return AttendanceEntity(
        id = this.id.toInt(),
        userId = this.user_id.toInt(),
        kodeDesa = this.kode_desa,
        kodeKec = this.kode_kec,
        date = this.date,
        attendanceMorning = this.attendance_morning,
        attendanceAfternoon = this.attendance_afternoon,
        late = this.late?.toInt(),
        early = this.early?.toInt(),
        syncStatus = this.sync_status.toInt(),
    )
}