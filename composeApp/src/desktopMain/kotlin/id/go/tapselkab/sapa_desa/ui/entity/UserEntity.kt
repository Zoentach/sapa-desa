package id.go.tapselkab.sapa_desa.ui.entity

import id.go.tapselkab.sapa_desa.core.data.model.UserResponse

data class UserEntity(
    val id: Int,
    val name: String,
    val email: String,
    val token: String?,
)

fun UserResponse.toEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        name = this.name,
        email = this.email,
        token = null
    )
}

