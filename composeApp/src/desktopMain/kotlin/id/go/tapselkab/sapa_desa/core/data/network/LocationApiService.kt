package id.go.tapselkab.sapa_desa.core.data.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class IpApiResponse(
    val latitude: Double? = null,
    val longitude: Double? = null
)

data class GeoLocation(
    val latitude: Double,
    val longitude: Double
)

interface LocationApiService {
    suspend fun getCurrentLocation(): GeoLocation
}

class LocationApiServiceImpl(
    private val client: HttpClient = NetworkModule.client
) : LocationApiService {

    override suspend fun getCurrentLocation(): GeoLocation {
        return try {
            val response: HttpResponse = client.get(urlString = "https://ipwho.is/") {
                headers {
                    append(HttpHeaders.Accept, "application/json")
                }
            }

            if (!response.status.isSuccess()) {
                println("Gagal mengambil lokasi IP: ${response.status}")
                return GeoLocation(0.0, 0.0)
            }

            val data: IpApiResponse = response.body()
            println("Lokasi IP: (${data.latitude}, ${data.longitude})")

            GeoLocation(
                latitude = data.latitude ?: 0.0,
                longitude = data.longitude ?: 0.0
            )
        } catch (e: Exception) {
            println("Gagal ambil lokasi IP: ${e.message}")
            GeoLocation(0.0, 0.0)
        }
    }
}
