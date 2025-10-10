package id.go.tapselkab.sapa_desa.core.data.model

import id.go.tapselkab.sapa_desa.core.data.local.model.PerangkatDesa
import kotlinx.serialization.Serializable
import java.util.Date


@Serializable
data class PerangkatResponse<T>(
    val status: String,
    val data: T
)

@Serializable
data class PerangkatModel(
    val id: Int,
    val nama: String = "",
    val nipd: String?,
    val kode_kecamatan: String = "",
    val kode_desa: String = "",
    val kode_jabatan: String = "",
    val mulai: String?,
    val berakhir: String?,
    val nik: String?,
    val tempat_lahir: String?,
    val tanggal_lahir: String?,
    val sk_id: Int?,
    val pendidikan_id: Int?,
    val jenis_kelamin: String?,
    val agama: String?,
    val no_telp: String?,
    val status_jabatan: String?,
    val status_keaktifan: String?,
    val nama_jabatan: String = ""
)

// Fungsi ekstensi yang diperbaiki untuk memetakan PerangkatModel ke PerangkatDesa
fun PerangkatModel.toDbObject(): PerangkatDesa {
    return PerangkatDesa(
        id = this.id, // ID akan diatur oleh database
        nama = this.nama,
        nipd = this.nipd,
        kode_kecamatan = this.kode_kecamatan,
        kode_desa = this.kode_desa,
        kode_jabatan = this.kode_jabatan,
        mulai = this.mulai,
        berakhir = this.berakhir,
        nik = this.nik,
        tempat_lahir = this.tempat_lahir,
        tanggal_lahir = this.tanggal_lahir,
        sk_id = this.sk_id,
        pendidikan_id = this.pendidikan_id,
        jenis_kelamin = this.jenis_kelamin,
        agama = this.agama,
        no_telp = this.no_telp,
        status_jabatan = this.status_jabatan,
        status_keaktifan: this.status_keaktifan,
    nama_jabatan = this.nama_jabatan
    )
}