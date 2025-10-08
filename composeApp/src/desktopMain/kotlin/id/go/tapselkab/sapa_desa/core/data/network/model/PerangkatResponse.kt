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
    val kode_kec: String = "",
    val kode_desa: String = "",
    val kode_jabatan: String = "",
    val mulai: String?,
    val berakhir: String?,
    val nik: String?,
    val tempat_lahir: String?,
    val tanggal_lahir: String?,
    val no_sk: String?,
    val pendidikan: String?,
    val jenis_kelamin: String?,
    val agama: String?,
    val no_telp: String?,
    val status: String?,
    val nama_jabatan: String = ""
)

// Fungsi ekstensi yang diperbaiki untuk memetakan PerangkatModel ke PerangkatDesa
fun PerangkatModel.toDbObject(): PerangkatDesa {
    return PerangkatDesa(
        id = this.id, // ID akan diatur oleh database
        nama = this.nama,
        nipd = this.nipd,
        kode_kec = this.kode_kec,
        kode_desa = this.kode_desa,
        kode_jabatan = this.kode_jabatan,
        mulai = this.mulai,
        berakhir = this.berakhir,
        nik = this.nik,
        tempat_lahir = this.tempat_lahir,
        tanggal_lahir = this.tanggal_lahir,
        no_sk = this.no_sk,
        pendidikan = this.pendidikan,
        jenis_kelamin = this.jenis_kelamin,
        agama = this.agama,
        no_telp = this.no_telp,
        status = this.status,
        nama_jabatan = this.nama_jabatan
    )
}