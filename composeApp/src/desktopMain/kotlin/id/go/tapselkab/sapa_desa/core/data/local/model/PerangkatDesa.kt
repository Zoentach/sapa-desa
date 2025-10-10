package id.go.tapselkab.sapa_desa.core.data.local.model

import id.go.tapselkab.sapa_desa.core.data.model.PerangkatResponse


data class PerangkatDesa(
    val id: Int,
    val nama: String,
    val nipd: String? = null,
    val kode_kecamatan: String,
    val kode_desa: String,
    val kode_jabatan: String,
    val mulai: String? = null,
    val berakhir: String? = null,
    val nik: String? = null,
    val tempat_lahir: String? = null,
    val tanggal_lahir: String? = null,
    val sk_id: Int? = null,
    val pendidikan_id: Int? = null,
    val jenis_kelamin: String? = null,
    val agama: String? = null,
    val no_telp: String? = null,
    val status_jabatan: String? = null,
    val status_keaktifan: String? = null,
    val nama_jabatan: String
)


