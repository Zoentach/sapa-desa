package id.go.tapselkab.sapa_desa.core.data.local.model

import kotlinx.serialization.Serializable

@Serializable
data class VerifikasiAbsensiModel(
    val user_id: Long,
    val kode_kecamatan: String,
    val kode_desa: String,
    val mac_address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
)

@Serializable
data class VerifikasiAbsensiResponse<T>(
    val status: Boolean,
    val message: String? = null,
    val data: T? = null
)