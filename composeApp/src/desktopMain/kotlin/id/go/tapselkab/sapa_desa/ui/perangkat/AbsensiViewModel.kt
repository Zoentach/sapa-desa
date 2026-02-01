package id.go.tapselkab.sapa_desa.ui.perangkat

import androidx.lifecycle.ViewModel
import id.go.tapselkab.sapa_desa.core.repository.AbsensiRepository
import id.go.tapselkab.sapa_desa.core.repository.VerifikasiAbsensiRepository
import id.go.tapselkab.sapa_desa.ui.entity.AbsensiEntity
import id.go.tapselkab.sapa_desa.ui.entity.AbsensiResult
import id.go.tapselkab.sapa_desa.ui.entity.AbsensiStatus
import id.go.tapselkab.sapa_desa.utils.camera.FaceRecognizerManager
import id.go.tapselkab.sapa_desa.utils.export.exportCSVToUserLocation
import id.go.tapselkab.sapa_desa.utils.file.getFile
import id.go.tapselkab.sapa_desa.utils.macaddress.getMyMacAddress
import id.go.tapselkab.sapa_desa.utils.time.DateManager
import id.go.tapselkab.sapa_desa.utils.time.DateManager.thisDay
import id.go.tapselkab.sapa_desa.utils.time.DateManager.thisMonth
import id.go.tapselkab.sapa_desa.utils.time.DateUtils
import id.go.tapselkab.sapa_desa.utils.time.TimeManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.time.LocalDate
import id.go.tapselkab.sapa_desa.utils.camera.CameraManager
import id.go.tapselkab.sapa_desa.utils.camera.saveReferenceFace

class AbsensiViewModel(
    private val repository: AbsensiRepository,
    private val verifikasiRepo: VerifikasiAbsensiRepository
) : ViewModel() {

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

    private val _isCameraReady: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isCameraReady = _isCameraReady.asStateFlow()

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


    fun setAbsensiResult() {
        _absensiResult.value = AbsensiResult(
            status = AbsensiStatus.INITIAL,
            message = ""
        )
    }

    // Di dalam AbsensiViewModel
    fun setCameraReady(isReady: Boolean) {
        _isCameraReady.value = isReady
    }

    fun detectCamera() {
        scope.launch {
            try {
                _absensiResult.value = AbsensiResult(
                    status = AbsensiStatus.LOADING,
                    message = "Sedang mendeteksi kamera"
                )
                val foundIndex = CameraManager.findAvailableCameraIndex()
                if (foundIndex != null) {
                    val started = CameraManager.startCapture(foundIndex)

                    _isCameraReady.value = started

                    _absensiResult.value = AbsensiResult(
                        status = AbsensiStatus.INITIAL,
                        message = ""
                    )

                } else {
                    _absensiResult.value = AbsensiResult(
                        status = AbsensiStatus.FAILED,
                        message = "Kamera tidak ditemukan"
                    )
                }
            } catch (e: Exception) {
                _absensiResult.value = AbsensiResult(
                    status = AbsensiStatus.FAILED,
                    message = "Kamera tidak ditemukan"
                )
            }
        }
    }

    fun saveImageReference(folderName: String, fileName: String) {

        scope.launch {


            try {
                val saved = saveReferenceFace(folderName = folderName, fileName = fileName)

                if (saved) {

//                   // harus disini karna logika savereference melakukan cek apakah kamera aktif atau tidak
//                    CameraManager.releaseCamera()
//                    _isCameraReady.value = false

                    _absensiResult.value = AbsensiResult(
                        status = AbsensiStatus.SUCCESS,
                        message = "Berhasil menyimpan foto Referensi"
                    )
                }
            } catch (e: Exception) {
                _absensiResult.value = AbsensiResult(
                    status = AbsensiStatus.FAILED,
                    message = e.message.orEmpty()
                )
            }

        }

    }

    fun saveImageAbsensi(id: Int) {

        scope.launch {

            try {
                val timeStamp = TimeManager.getCurrentTimeMillis()

                val folderName = "$id"
                val fileName = "$timeStamp"

                val saved = saveReferenceFace(folderName = folderName, fileName = fileName)

                if (saved) {

                    //harus disini karna logika savereference melakukan cek apakah kamera aktif atau tidak
                    //CameraManager.releaseCamera()
                    //_isCameraReady.value = false

                    prosesAbsensi(
                        perangkatId = id,
                        timeStamp = timeStamp ?: 0
                    )
                }

            } catch (e: Exception) {
                _absensiResult.value = AbsensiResult(
                    status = AbsensiStatus.FAILED,
                    message = e.message.orEmpty()
                )
            }

        }

    }

    private suspend fun prosesAbsensi(perangkatId: Int, timeStamp: Long) {

        //  scope.launch {

        _absensiResult.value = AbsensiResult(
            status = AbsensiStatus.LOADING,
            message = "Sedang memproses kehadiran ..."
        )

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
                "Error: ${e.message}"
            )
        }
        //   }
    }

    private suspend fun saveAbsensi(userId: Int, timeStamp: Long) {
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

            val gambarPagiRef = DateUtils.combineDateAndTimeToMillis(
                date = absensi.tanggal.orEmpty(),
                time = absensi.absensiPagi.orEmpty()
            )

            val gambarSoreRef = DateUtils.combineDateAndTimeToMillis(
                date = absensi.tanggal.orEmpty(),
                time = absensi.absensiSore.orEmpty()
            )

            try {
                _absensiResult.value = AbsensiResult(
                    status = AbsensiStatus.LOADING,
                    message = "Sedang mengirim absen..."
                )
                val macAddress = getMyMacAddress()
                val location = verifikasiRepo.getMyLocation()

                val success = repository.sendAbsensiToServer(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    macAddress = macAddress.orEmpty(),
                    absensi = absensi,
                    gambarPagi = absensi.absensiPagi?.let { getFile("${absensi.perangkatId}", "$gambarPagiRef.jpg") },
                    gambarSore = absensi.absensiSore?.let { getFile("${absensi.perangkatId}", "$gambarSoreRef.jpg") }
                )
                if (success) {
                    absensi.tanggal?.let { repository.updateAbsensiSyncStatus(absensi.perangkatId.toLong(), it, 1) }
                    getAbsensiByPerangkatAndMonth(absensi.perangkatId)

                    _absensiResult.value = AbsensiResult(
                        status = AbsensiStatus.SUCCESS,
                        message = "Berhasil"
                    )

                }
            } catch (e: Exception) {
                _absensiResult.value = AbsensiResult(
                    status = AbsensiStatus.FAILED,
                    message = "Error: " + e.message.orEmpty()
                )
            }
        }
    }

    fun sendAllAbsensi() {
    scope.launch {
        // 1. Ambil list absen yang belum terkirim (status 0)
        // Asumsi: absensis adalah StateFlow/MutableStateFlow di ViewModel
        val listBelumTerkirim = absensis.value.filter { it.syncStatus == 0 }

        // Jika tidak ada data, langsung return atau kasih info
        if (listBelumTerkirim.isEmpty()) {
            _absensiResult.value = AbsensiResult(
                status = AbsensiStatus.SUCCESS, // Atau status IDLE
                message = "Semua data sudah terkirim."
            )
            return@launch
        }

        // 2. Set status LOADING dengan jumlah data
        val totalData = listBelumTerkirim.size
        _absensiResult.value = AbsensiResult(
            status = AbsensiStatus.LOADING,
            message = "Sedang mengirim $totalData absen..."
        )

        var successCount = 0
        var failCount = 0

        try {
            // 3. Siapkan data pendukung (Lokasi & Mac) SEKALI SAJA agar efisien
            val macAddress = getMyMacAddress().orEmpty()
            val location = verifikasiRepo.getMyLocation() // Suspend function

            // 4. Loop dan kirim satu per satu
            for (absensi in listBelumTerkirim) {
                try {
                    // Siapkan referensi gambar (Logika sama seperti sendAbsensi)
                    val gambarPagiRef = DateUtils.combineDateAndTimeToMillis(
                        date = absensi.tanggal.orEmpty(),
                        time = absensi.absensiPagi.orEmpty()
                    )
                    val gambarSoreRef = DateUtils.combineDateAndTimeToMillis(
                        date = absensi.tanggal.orEmpty(),
                        time = absensi.absensiSore.orEmpty()
                    )

                    // Panggil Repository
                    val success = repository.sendAbsensiToServer(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        macAddress = macAddress,
                        absensi = absensi,
                        gambarPagi = absensi.absensiPagi?.let {
                            getFile("${absensi.perangkatId}", "$gambarPagiRef.jpg")
                        },
                        gambarSore = absensi.absensiSore?.let {
                            getFile("${absensi.perangkatId}", "$gambarSoreRef.jpg")
                        }
                    )

                    if (success) {
                        // Update status lokal jika berhasil
                        absensi.tanggal?.let {
                            repository.updateAbsensiSyncStatus(absensi.perangkatId.toLong(), it, 1)
                        }
                        successCount++
                    } else {
                        failCount++
                    }

                } catch (e: Exception) {
                    // Jika satu gagal, catat error tapi LANJUTKAN ke item berikutnya
                    failCount++
                    e.printStackTrace()
                }
            }

            // 5. Refresh data lokal agar UI terupdate (menghilangkan badge merah/counter)
            // Asumsi: mengambil data berdasarkan perangkatId dari item pertama atau user session
            listBelumTerkirim.firstOrNull()?.let {
                getAbsensiByPerangkatAndMonth(it.perangkatId)
            }

            // 6. Tampilkan Hasil Akhir
            if (failCount == 0) {
                _absensiResult.value = AbsensiResult(
                    status = AbsensiStatus.SUCCESS,
                    message = "Berhasil mengirim $successCount absen"
                )
            } else {
                // Jika ada sebagian yang gagal
                _absensiResult.value = AbsensiResult(
                    status = AbsensiStatus.FAILED, // Atau WARNING jika kamu punya
                    message = "Berhasil: $successCount, Gagal: $failCount"
                )
            }

        } catch (e: Exception) {
            // Error global (misal gagal dapat lokasi di awal)
            _absensiResult.value = AbsensiResult(
                status = AbsensiStatus.FAILED,
                message = "Gagal memulai pengiriman: ${e.message}"
            )
        }
    }
}

    fun ajukanAbsensiIzin(
        perangkatId: Long,
        tanggal: String,
        keterangan: String,
        lampiran: File
    ) {

        _absensiResult.value = AbsensiResult(
            status = AbsensiStatus.LOADING,
            message = "Sedang memproses kehadiran ..."
        )

        scope.launch {
            try {
                val success = repository.ajukanAbsensiIzin(
                    perangkatId = perangkatId,
                    tanggal = tanggal,
                    keterangan = keterangan,
                    lampiran = lampiran
                )

                if (success) {
                    repository.insertAbsensiIzin(
                        perangkatId = perangkatId,
                        tanggal = tanggal,
                        keterangan = keterangan,
                        syncStatus = 1
                    )

                    getAbsensiByPerangkatAndMonth(perangkatId.toInt())
                }

                _absensiResult.value = AbsensiResult(
                    status = AbsensiStatus.SUCCESS,
                    message = "Berhasil"
                )

            } catch (e: Exception) {
                _absensiResult.value = AbsensiResult(
                    status = AbsensiStatus.FAILED,
                    message = "Error: " + e.message.orEmpty()
                )
            }
        }

    }

    fun exportAbsensi(absensis: List<AbsensiEntity>) {
        exportCSVToUserLocation(absensis)
    }
}