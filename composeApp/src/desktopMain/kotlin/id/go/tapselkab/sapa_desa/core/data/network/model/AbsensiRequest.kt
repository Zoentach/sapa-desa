package id.go.tapselkab.sapa_desa.core.data.network.model

@kotlinx.serialization.Serializable

data class AbsensiRequest(
    val perangkat_id: Int,
    val tanggal: String, //format : "YYYY-MM-DD
    val absensi_pagi: String? = null,
    val absensi_sore: String? = null,
    val keterlambatan: Int? = null,
    val pulang_cepat: Int? = null,
    val gambar_pagi: String? = null,
    val gambar_sore: String? = null,
    //  val keterangan:String? = null,
    //  val lampiran:String? = null
)