package id.go.tapselkab.sapa_desa.ui.entity

//import id.go.tapselkab.database.Absensi
import id.go.tapselkab.sapa_desa.core.data.network.model.AbsensiRequest

data class AbsensiResult(
    val status: AbsensiStatus = AbsensiStatus.INITIAL,
    val message: String = ""
)

enum class AbsensiStatus {
    INITIAL, LOADING, SUCCESS, FAILED
}

data class AbsensiEntity(
    val id: Int,
    val perangkatId: Int,
    val tanggal: String? = null,  //format : "YYYY-MM-DD
    val statusKehadiran: String? = null,  //tidak dikirim ke server, sudah dapat dari nilai default server
    val keterangan: String? = null,
    val absensiPagi: String? = null,
    val absensiSore: String? = null,
    val keterlambatan: Int? = null,
    val pulangCepat: Int? = null,
    val syncStatus: Int = 0, // 0 = pagi dan sore belum, 1 = pagi sudah, 2 = pagi sudah dan sore belum
)

fun AbsensiEntity.toRequest(): AbsensiRequest {
    return AbsensiRequest(
        perangkat_id = this.perangkatId,
        tanggal = this.tanggal ?: "",
        absensi_pagi = this.absensiPagi,
        absensi_sore = this.absensiSore,
        keterlambatan = this.keterlambatan,
        pulang_cepat = this.pulangCepat,
    )
}