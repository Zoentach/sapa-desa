package id.go.tapselkab.sapa_desa.ui.entity

import id.go.tapselkab.sapa_desa.core.data.local.model.PerangkatDesa
import kotlinx.serialization.Serializable
import id.go.tapselkab.sapa_desa.core.data.model.PerangkatModel

import id.go.tapselkab.database.Perangkat_desa

@Serializable
data class PerangkatEntity(
    val id: Int,
    val nama: String,
    val kodeJabatan: String,
    val jabatan: String,
    val kodeDesa: String,
    val kodeKec: String
)

//fun PerangkatDesa.toEntity(): PerangkatEntity {
//    return PerangkatEntity(
//        id = this.id,
//        nama = this.nama.orEmpty(),
//        kodeJabatan = this.kode_jabatan.orEmpty(),
//        jabatan = this.nama_jabatan.orEmpty(),
//        kodeDesa = PerangkatDesa.kode_desa,
//        kodeKec = PerangkatDesa.kode_kec
//    )
//}

// Ini adalah fungsi konversi yang benar untuk data dari database.
fun Perangkat_desa.toEntity(): PerangkatEntity {
    return PerangkatEntity(
        id = this.id.toInt(),
        nama = this.nama.orEmpty(),
        kodeJabatan = this.kode_jabatan.orEmpty(),
        jabatan = this.nama_jabatan.orEmpty(),
        kodeDesa = this.kode_desa.orEmpty(),
        kodeKec = this.kode_kec.orEmpty()
    )
}
