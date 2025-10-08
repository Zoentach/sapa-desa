package id.go.tapselkab.sapa_desa.core.repository

import id.go.tapselkab.database.sipature_db
import id.go.tapselkab.database.Attendance
import id.go.tapselkab.sapa_desa.core.data.network.AuthApiService
import id.go.tapselkab.sapa_desa.core.data.token.TokenStorage
import id.go.tapselkab.sapa_desa.ui.entity.AttendanceEntity
import id.go.tapselkab.sapa_desa.ui.entity.toRequest
import java.io.File
import java.lang.Exception

class AttendanceRepository(
    private val api: AuthApiService,
    private val db: sipature_db,
    private val tokenStorage: TokenStorage
) {


    suspend fun sendAttendanceToServer(
        attendance: AttendanceEntity,
        imageMorning: File?,
        imageAfternoon: File?
    ): Boolean {
        return try {
            val token = tokenStorage.get()

            if (token.isNullOrBlank()) {
                println("User fetch failed: Token tidak tersedia")
                return false
            }
            api.insertAttendanceWithImages(
                token = token,
                attendance = attendance.toRequest(),
                imageMorning = imageMorning,
                imageAfternoon = imageAfternoon
            )

        } catch (e: Exception) {
            throw e
        }
    }

    fun updateAfternoonAttendance(
        date: Long?,
        userId: Int,
        afternoon: Long?,
        early: Long?,
        syncStatus: Int
    ) {
        try {
            db.attendanceQueries.updateAttendanceAfternoonByUserAndDate(
                attendance_afternoon = afternoon,
                early = early,
                sync_status = syncStatus.toLong(),
                user_id = userId.toLong(),
                date = date
            )
        } catch (e: Exception) {
            throw e
        }
    }

    fun updateAttendanceSyncStatus(
        userId: Long,
        date: Long?,
        syncStatus: Long
    ) {
        try {
            db.attendanceQueries.updateAttendanceSyncStatus(
                user_id = userId,
                date = date,
                sync_status = syncStatus
            )
        } catch (e: Exception) {
            throw e
        }
    }

    fun getAll(): List<Attendance> {
        return db.attendanceQueries.selectAllAttendance().executeAsList()
    }

    fun getAttendanceByUserAndMonth(userId: Int, startDay: Long, endDay: Long): List<Attendance> {
        return db.attendanceQueries
            .selectAttendanceByUserAndMonth(user_id = userId.toLong(), startDay, endDay)
            .executeAsList()
    }

    fun isAttanceExist(userId: Int, date: Long): Boolean {
        val count = db.attendanceQueries.selectByDate(user_id = userId.toLong(), date = date).executeAsOne()

        return count > 0
    }

//    fun getAttendanceByUserAndMonth(
//        userId: Long,
//        month: Int,
//        year: Int
//    ): List<Attendance> {
//
//
//        val endOfMonth = startOfMonth.plus(1, DateTimeUnit.MONTH, TimeZone.currentSystemDefault())
//            .minus(1, DateTimeUnit.MILLISECOND)
//            .toEpochMilliseconds()
//
//        return database.attendanceQueries.selectAttendanceByUserAndMonth(
//            userId = userId,
//            start = startOfMonth,
//            end = endOfMonth
//        ).executeAsList()
//    }

//    fun getById(id: String): Attendance? {
//        return db.attendanceQueries.selectAttendanceById(id).executeAsOneOrNull()
//    }

    fun insertAttendance(
        userId: Int,
        kodeDesa: String,
        kodeKec: String,
        date: Long?,
        morning: Long?,
        afternoon: Long?,
        late: Long?,
        early: Long?,
        syncStatus: Int
    ) {
        db.attendanceQueries.insertAttendance(
            id = null,
            user_id = userId.toLong(),
            kode_desa = kodeDesa,
            kode_kec = kodeKec,
            date = date,
            attendance_morning = morning,
            attendance_afternoon = afternoon,
            late = late,
            early = early,
            sync_status = syncStatus.toLong()
        )
    }

    fun deleteById(id: Int) {
        db.attendanceQueries.deleteAttendanceById(id.toLong())
    }
}
