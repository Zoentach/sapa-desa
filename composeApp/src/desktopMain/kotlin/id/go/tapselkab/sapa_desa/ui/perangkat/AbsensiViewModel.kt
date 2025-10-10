package id.go.tapselkab.sapa_desa.ui.perangkat

import androidx.lifecycle.ViewModel
import id.go.tapselkab.sapa_desa.core.repository.AbsensiRepository
import id.go.tapselkab.sapa_desa.ui.entity.AbsensiEntity
import id.go.tapselkab.sapa_desa.ui.entity.AbsensiResult
import id.go.tapselkab.sapa_desa.ui.entity.AbsensiStatus
import id.go.tapselkab.sapa_desa.utils.camera.FaceRecognizerManager
import id.go.tapselkab.sapa_desa.utils.export.exportCSVToUserLocation
import id.go.tapselkab.sapa_desa.utils.file.getFile
import id.go.tapselkab.sapa_desa.utils.time.DateManager
import id.go.tapselkab.sapa_desa.utils.time.DateManager.thisDay
import id.go.tapselkab.sapa_desa.utils.time.DateManager.thisMonth
import id.go.tapselkab.sapa_desa.utils.time.DateUtils
import id.go.tapselkab.sapa_desa.utils.time.TimeManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

class AbsensiViewModel(private val repository: AbsensiRepository) : ViewModel() {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _absensiResult = MutableStateFlow(AbsensiResult())
    val absensiResult = _absensiResult.asStateFlow()

    private val _absensis = MutableStateFlow<List<AbsensiEntity>>(emptyList())
    val absensis = _absensis.asStateFlow()

    private val _buttonStatus = MutableStateFlow(ButtonStatus())
    val buttonStatus = _buttonStatus.asStateFlow()

    private val _thisMonth: MutableStateFlow<String> = MutableStateFlow("")
    val thisMonth = _thisMonth.asStateFlow()


    private val _thisDay: MutableStateFlow<String> = MutableStateFlow("null")
    val thisDay = _thisDay.asStateFlow()

    fun initScreen() {
        val hour = TimeManager.isMorningOrAfternoon()
        _buttonStatus.value = when {
            hour in 6..11 -> ButtonStatus(true, "Silakan Absen Pagi")
            hour in 12..21 -> ButtonStatus(true, "Silakan Absen Sore")
            else -> ButtonStatus(false, "Belum Bisa Absen")
        }

        _thisMonth.value = DateManager.getMillisAt0815().thisMonth()
        _thisDay.value = DateManager.getMillisAt0815().thisDay()
    }

    fun prosesAbsensi(perangkatId: Int, timeStamp: Long) {
        scope.launch {
            try {
                val (isMatch, confidence) = FaceRecognizerManager.cropAllFaces("$perangkatId", "$timeStamp")

                if (isMatch) {
                    saveAbsensi(perangkatId, timeStamp)
                    _absensiResult.value = AbsensiResult(
                        AbsensiStatus.SUCCESS,
                        "Wajah cocok (Kemiripan: %.1f%%)".format(confidence)
                    )
                    getAbsensiByPerangkatAndMonth(perangkatId)
                } else {
                    _absensiResult.value = AbsensiResult(
                        AbsensiStatus.FAILED,
                        "Wajah tidak cocok (Kemiripan: %.1f%%)".format(confidence)
                    )
                }

            } catch (e: Exception) {
                _absensiResult.value = AbsensiResult(
                    AbsensiStatus.FAILED,
                    "Kesalahan saat proses absensi: ${e.message}"
                )
            }
        }
    }

    private fun saveAbsensi(userId: Int, timeStamp: Long) {
        val hour = TimeManager.isMorningOrAfternoon(timeStamp)
        val date = DateManager.getMillisAt0815()

        val tanggal = DateUtils.toDateString(date)
        val jam = DateUtils.toTimeString(timeStamp)

        if (hour in 6..11) {
            val lateMinutes = ((timeStamp - DateManager.getMillisAt0815()) / 60000).coerceAtLeast(0)
            if (!repository.isAbsensiExist(userId.toLong(), tanggal)) {
                repository.insertAbsensi(
                    perangkatId = userId.toLong(),
                    tanggal = tanggal,
                    pagi = jam,
                    sore = null,
                    keterlambatan = lateMinutes,
                    pulangCepat = null,
                    syncStatus = 0
                )
            }
        } else if (hour in 12..21) {
            val earlyMinutes = ((DateManager.getMillisAt1615() - timeStamp) / 60000).coerceAtLeast(0)
            if (repository.isAbsensiExist(userId.toLong(), tanggal)) {
                repository.updateAfternoonAbsensi(
                    perangkatId = userId.toLong(),
                    tanggal = tanggal,
                    sore = jam,
                    pulangCepat = earlyMinutes,
                    syncStatus = 0
                )
            } else {
                repository.insertAbsensi(
                    perangkatId = userId.toLong(),
                    tanggal = tanggal,
                    pagi = null,
                    sore = jam,
                    keterlambatan = null,
                    pulangCepat = earlyMinutes,
                    syncStatus = 0
                )
            }
        }
    }

    fun getAbsensiByPerangkatAndMonth(
        perangkatId: Int,
        month: Int = LocalDate.now().monthValue,
        year: Int = LocalDate.now().year
    ) {

        _thisMonth.value = TimeManager.getThisMonthMillisAt(month, year).thisMonth()

        scope.launch {
            val absensis = repository.getAllAbsensi()
            _absensis.value = absensis.filter { absensi ->
                absensi.tanggal?.let { value -> DateUtils.isWithinMonth(value, month, year) } == true
                        && absensi.perangkatId == perangkatId
            }
        }
    }

    fun sendAbsensi(absensi: AbsensiEntity) {
        scope.launch {
            val success = repository.sendAbsensiToServer(
                absensi = absensi,
                gambarPagi = absensi.absensiPagi?.let { getFile("${absensi.perangkatId}", "$it.jpg") },
                gambarSore = absensi.absensiSore?.let { getFile("${absensi.perangkatId}", "$it.jpg") }
            )
            if (success) {
                absensi.tanggal?.let { repository.updateAbsensiSyncStatus(absensi.perangkatId.toLong(), it, 1) }
                getAbsensiByPerangkatAndMonth(absensi.perangkatId)
            }
        }
    }

    fun exportAbsensi(absensis: List<AbsensiEntity>) {
        exportCSVToUserLocation(absensis)
    }
}