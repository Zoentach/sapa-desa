package id.go.tapselkab.sapa_desa.core.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
)

@Serializable
data class LoginResponse(
    val token: String
)

@Serializable
data class UserResponse(
    val id: Int,
    val name: String,
    val email: String,
)