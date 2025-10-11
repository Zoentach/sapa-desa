package id.go.tapselkab.sapa_desa.ui.verifikasi

import androidx.lifecycle.ViewModel
import id.go.tapselkab.sapa_desa.core.data.network.GeoLocation
import id.go.tapselkab.sapa_desa.core.repository.VerifikasiAbsensiRepository
import id.go.tapselkab.sapa_desa.ui.entity.AbsensiResult
import id.go.tapselkab.sapa_desa.ui.entity.AbsensiStatus
import id.go.tapselkab.sapa_desa.ui.entity.VerifikasiAbsensiEntity
import id.go.tapselkab.sapa_desa.utils.macaddress.getMyMacAddress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class VerifikasiViewModel(
    val verifikasiRepo: VerifikasiAbsensiRepository
) : ViewModel() {

    private val _macAddress: MutableStateFlow<String?> = MutableStateFlow(getMyMacAddress())

    val macAddress = _macAddress.asStateFlow()

    private val _location: MutableStateFlow<GeoLocation> = MutableStateFlow(GeoLocation(0.0, 0.0))
    val location = _location.asStateFlow()


    //butuh rename keperluan track status upload saja
    private val _absensiResult = MutableStateFlow(AbsensiResult())
    val absensiResult = _absensiResult.asStateFlow()

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun init() {
        try {
            coroutineScope.launch {
                _location.value = verifikasiRepo.getMyLocation()

            }
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
    }

    fun sendVerifikasiAbsensi(token: String, verifikasi: VerifikasiAbsensiEntity) {
        try {

            _absensiResult.value = AbsensiResult(
                AbsensiStatus.LOADING,
                "Sedang mengupload"
            )


            coroutineScope.launch {
                val isSucces = verifikasiRepo.sendVerifikasiAbsensi(token = token, verifikasi = verifikasi)

                if (isSucces) {
                    _absensiResult.value = AbsensiResult(
                        AbsensiStatus.SUCCESS,
                        "Berhasil mengupload sinkronisasi"
                    )
                } else {
                    _absensiResult.value = AbsensiResult(
                        AbsensiStatus.FAILED,
                        "Kesalahan saat proses upload"
                    )
                }

            }
        } catch (e: Exception) {
            println("Error: ${e.message}")
            _absensiResult.value = AbsensiResult(
                AbsensiStatus.FAILED,
                "Kesalahan saat proses upload: ${e.message}"
            )
        }
    }

}