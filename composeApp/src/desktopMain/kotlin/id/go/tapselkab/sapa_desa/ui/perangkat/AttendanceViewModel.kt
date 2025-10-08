package id.go.tapselkab.sapa_desa.ui.perangkat

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import io.ktor.util.date.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import id.go.tapselkab.sapa_desa.core.repository.AttendanceRepository
import id.go.tapselkab.sapa_desa.ui.entity.AttendanceEntity
import id.go.tapselkab.sapa_desa.ui.entity.AttendanceResult
import id.go.tapselkab.sapa_desa.ui.entity.AttendanceStatus
import id.go.tapselkab.sapa_desa.ui.entity.toEntity
import id.go.tapselkab.sapa_desa.utils.camera.FaceRecognizerManager
import id.go.tapselkab.sapa_desa.utils.export.exportCSVToUserLocation
import id.go.tapselkab.sapa_desa.utils.file.getFile
import id.go.tapselkab.sapa_desa.utils.time.*
import id.go.tapselkab.sapa_desa.utils.time.DateManager.thisDay
import id.go.tapselkab.sapa_desa.utils.time.DateManager.thisMonth
import java.time.LocalDate

class AttendanceViewModel(val repository: AttendanceRepository) : ViewModel() {

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val _absensiStatus = MutableStateFlow(AttendanceResult())
    val absensiStatus = _absensiStatus.asStateFlow()
    private val _attendances: MutableStateFlow<List<AttendanceEntity>> = MutableStateFlow(emptyList())
    val attendances = _attendances.asStateFlow()

    private val _buttonStatus = MutableStateFlow(ButtonStatus())
    val buttonStatus = _buttonStatus.asStateFlow()

    private val _thisMonth: MutableStateFlow<String> = MutableStateFlow("")
    val thisMonth = _thisMonth.asStateFlow()


    private val _thisDay: MutableStateFlow<String> = MutableStateFlow("null")
    val thisDay = _thisDay.asStateFlow()


    fun initScreen() {
        isMorningOrAfternoon()
        _thisMonth.value = DateManager.getMillisAt0815().thisMonth()
        _thisDay.value = DateManager.getMillisAt0815().thisDay()
    }

    fun getUpdateAttendance(userId: Int, month: Int, year: Int) {
        try {
            _thisMonth.value = TimeManager.getThisMonthMillisAt(month, year).thisMonth()
            getAttendenceByUserAndMonth(userId = userId, month = month, year = year)
        } catch (e: Exception) {
            print(e.message)
        }
    }

    fun isMorningOrAfternoon() {

        val hour = TimeManager.isMorningOrAfternoon()

        when {
            // Jam 23.59 - 05.59
            hour == 23 || hour in 0..5 -> {
                _buttonStatus.value = ButtonStatus(active = false, "Belum Bisa Absen")
            }

            // Jam 06.00 - 11.59 (pagi)
            hour in 6..11 -> {
                _buttonStatus.value = ButtonStatus(active = true, "Silakan Absen Pagi")
            }

            // Jam 12.00 - 17.59 (sore)
            hour in 12..21 -> {
                _buttonStatus.value = ButtonStatus(true, "Silakan Absen Sore")
            }

            // Di luar jam absensi yang diizinkan
            else -> {
                print("Terjadi kesalahan")
            }
        }
    }


    fun prosesAbsensi(
        userId: Int, kodeDesa: String, kodeKec: String, timeStamp: Long
    ) {
        coroutineScope.launch {
            try {

                val (isMatch, confidence) = FaceRecognizerManager
                    .cropAllFaces("$userId", "$timeStamp")

                if (isMatch) {

                    saveAttendance(
                        userId = userId,
                        kodeDesa = kodeDesa,
                        kodeKec = kodeKec,
                        timeStamp = timeStamp
                    )

                    _absensiStatus.value = AttendanceResult(
                        status = AttendanceStatus.SUCCESS,
                        //  message = "Wajah cocok (confidence: %.2f)".format(confidence)
                        message = "Wajah cocok (Kemiripan: %.1f%%)".format(confidence)
                    )

                    getAttendenceByUserAndMonth(userId = userId)

                } else {
                    _absensiStatus.value = AttendanceResult(
                        status = AttendanceStatus.FAILED,
                        message = "Wajah tidak cocok (Kemiripan: %.1f%%)".format(confidence)
                    )
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _absensiStatus.value = AttendanceResult(
                    AttendanceStatus.FAILED,
                    "Terjadi kesalahan saat proses absensi: ${e.message}"
                )
            }
        }
    }

    private fun saveAttendance(
        userId: Int,
        kodeDesa: String,
        kodeKec: String,
        timeStamp: Long,
    ) {
        try {

            val hour = TimeManager.isMorningOrAfternoon(timeStamp ?: 0)
            val date = DateManager.getMillisAt0815()

            when {
                // Jam 23.59 - 05.59
                hour == 23 || hour in 0..5 -> {

                }

                // Jam 06.00 - 11.59 (pagi)
                hour in 6..11 -> {

                    val lateInMillis = timeStamp - DateManager.getMillisAt0815()
                    val lateInMinute = if (lateInMillis > 0) lateInMillis / (60 * 1000) else 0

                    if (!repository.isAttanceExist(userId = userId, date = date)) {
                        insertAttendance(
                            userId = userId,
                            kodeDesa = kodeDesa,
                            kodeKec = kodeKec,
                            date = date,
                            morning = timeStamp,
                            afternoon = null,
                            late = lateInMinute,
                            early = null,
                            syncStatus = 0
                        )
                    }
                }

                // Jam 12.00 - 17.59 (sore)
                hour in 12..21 -> {
                    val earlyInMillis = DateManager.getMillisAt1615() - timeStamp
                    val earlyInMinute = if (earlyInMillis > 0) earlyInMillis / (60 * 1000) else 0

                    print("$earlyInMinute")

                    if (repository.isAttanceExist(userId = userId, date = date)) {
                        updateAttendance(
                            userId = userId,
                            afternoon = timeStamp,
                            early = earlyInMinute,
                            syncStatus = 0,
                            date = date
                        )
                    } else {
                        insertAttendance(
                            userId = userId,
                            kodeDesa = kodeDesa,
                            kodeKec = kodeKec,
                            date = date,
                            morning = null,
                            afternoon = timeStamp,
                            late = null,
                            early = earlyInMinute,
                            syncStatus = 0
                        )
                    }
                }

                // Di luar jam absensi yang diizinkan
                else -> {
                    print("Terjadi kesalahan")
                }
            }

        } catch (e: Exception) {
            print(e.message.orEmpty())
        }
    }


    fun sendAttendanceToServer(attendance: AttendanceEntity) {
        coroutineScope.launch {
            try {
                val isSaved = repository.sendAttendanceToServer(
                    attendance = attendance,
                    imageMorning = if (attendance.attendanceMorning != null) getFile(
                        folderName = "${attendance.userId}",
                        fileName = "${attendance.attendanceMorning}.jpg"
                    ) else null,
                    imageAfternoon = if (attendance.attendanceAfternoon != null) getFile(
                        folderName = "${attendance.userId}",
                        fileName = "${attendance.attendanceAfternoon}.jpg"
                    ) else null
                )
                if (isSaved) {
                    updateAttendance(
                        userId = attendance.userId,
                        date = attendance.date,
                        syncStatus = 1
                    )
                    delay(1000)
                    getAttendenceByUserAndMonth(
                        userId = attendance.userId
                    )
                }
            } catch (e: Exception) {
                print("${e.message}")
            }
        }
    }


    private fun insertAttendance(
        userId: Int,
        kodeDesa: String,
        kodeKec: String,
        morning: Long?,
        date: Long?,
        afternoon: Long?,
        late: Long?,
        early: Long?,
        syncStatus: Int
    ) {
        try {
            repository.insertAttendance(
                userId = userId,
                kodeDesa = kodeDesa,
                kodeKec = kodeKec,
                date = date,
                morning = morning,
                afternoon = afternoon,
                late = late,
                early = early,
                syncStatus = syncStatus
            )
        } catch (e: Exception) {
            throw e
        }
    }

    private fun updateAttendance(
        userId: Int,
        afternoon: Long?,
        date: Long?,
        early: Long?,
        syncStatus: Int
    ) {
        try {
            repository.updateAfternoonAttendance(
                userId = userId,
                afternoon = afternoon,
                date = date,
                early = early,
                syncStatus = syncStatus

            )
        } catch (e: Exception) {
            throw e
        }
    }

    private fun updateAttendance(
        userId: Int,
        date: Long?,
        syncStatus: Int
    ) {
        try {
            repository.updateAttendanceSyncStatus(
                userId = userId.toLong(),
                date = date,
                syncStatus = syncStatus.toLong()
            )
        } catch (e: Exception) {

        }
    }


    fun getAttendenceByUserAndMonth(
        userId: Int,
        month: Int = LocalDate.now().monthValue,
        year: Int = LocalDate.now().year
    ) {
        try {

            val attendances = repository
                .getAttendanceByUserAndMonth(
                    userId = userId,
                    startDay = DateManager.startOfMonth(year, month),
                    endDay = DateManager.endOfMonth(year, month)
                )

            _attendances.value = attendances.map {
                it.toEntity()
            }
        } catch (e: Exception) {
            print(e.message.orEmpty())
        }
    }


    fun exportAttendanceToCSV(attendances: List<AttendanceEntity>) {
        try {
            exportCSVToUserLocation(attendances)
        } catch (e: Exception) {
            print(e.message)
        }
    }

}
