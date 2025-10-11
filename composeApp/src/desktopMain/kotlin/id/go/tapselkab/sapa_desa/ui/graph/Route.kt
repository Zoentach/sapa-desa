package id.go.tapselkab.sapa_desa.ui.graph

import kotlinx.serialization.Serializable
import id.go.tapselkab.sapa_desa.ui.entity.PerangkatEntity

@Serializable
object FlashScreen

@Serializable
object Login

@Serializable
object CredentialLogin

@Serializable
object Dashboard

@Serializable
data class VerifikasiAbsensi(val userId: Int, val token: String)

//navigasi