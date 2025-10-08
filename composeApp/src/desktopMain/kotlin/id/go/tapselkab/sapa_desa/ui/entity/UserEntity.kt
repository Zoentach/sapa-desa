package id.go.tapselkab.sapa_desa.ui.entity

import id.go.tapselkab.sapa_desa.core.data.model.UserResponse

data class UserEntity(
    val id: Int,
    val name: String,
    val email: String,
    val kodeDesa: String,
    val kodeKec: String,
    val token: String?,
    val macAddress: String?
)

fun UserResponse.toEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        name = this.name,
        email = this.email,
        kodeDesa = this.kode_desa,
        kodeKec = this.kode_kec,
        macAddress = this.mac_address,
        token = null
    )
}

