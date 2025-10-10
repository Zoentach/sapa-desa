package id.go.tapselkab.sapa_desa.ui.entity

import id.go.tapselkab.sapa_desa.core.data.local.model.VerifikasiAbsensiModel

data class VerifikasiAbsensiEntity(
    val userId: Long,
    val kodeKecamatan: String,
    val kodeDesa: String,
    val macAddress: String?,
    val latitude: Double?,
    val longitude: Double?,
    val syncStatus: Long = 0
)

fun VerifikasiAbsensiEntity.toModel(): VerifikasiAbsensiModel {
    return VerifikasiAbsensiModel(
        user_id = userId,
        kode_desa = kodeDesa,
        kode_kecamatan = kodeKecamatan,
        mac_address = macAddress,
        latitude = latitude,
        longitude = longitude
    )
}

fun VerifikasiAbsensiModel.toEntity(syncStatus: Long = 0): VerifikasiAbsensiEntity {
    return VerifikasiAbsensiEntity(
        userId = user_id,
        kodeDesa = kode_desa,
        kodeKecamatan = kode_kecamatan,
        macAddress = mac_address,
        latitude = latitude,
        longitude = longitude,
        syncStatus = syncStatus
    )
}