package id.go.tapselkab.sapa_desa.core.data.network.model

import kotlinx.serialization.Serializable


data class GeoLocation(
    val latitude: Double, val longitude: Double
)

@Serializable
data class IpApiResponse(
    val latitude: Double? = null, val longitude: Double? = null
)