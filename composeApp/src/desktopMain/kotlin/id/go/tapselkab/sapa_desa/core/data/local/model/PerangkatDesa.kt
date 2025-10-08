package id.go.tapselkab.sapa_desa.core.data.local.model

import id.go.tapselkab.sapa_desa.core.data.model.PerangkatResponse


data class PerangkatDesa(
    val id: Int,
    val nama: String,
    val nipd: String? = null,
    val kode_kec: String,
    val kode_desa: String,
    val kode_jabatan: String,
    val mulai: String? = null,
    val berakhir: String? = null,
    val nik: String? = null,
    val tempat_lahir: String? = null,
    val tanggal_lahir: String? = null,
    val no_sk: String? = null,
    val pendidikan: String? = null,
    val jenis_kelamin: String? = null,
    val agama: String? = null,
    val no_telp: String? = null,
    val status: String? = null,
    val nama_jabatan: String
)


