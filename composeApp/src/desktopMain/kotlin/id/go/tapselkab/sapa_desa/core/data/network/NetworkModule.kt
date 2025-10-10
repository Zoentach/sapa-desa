package id.go.tapselkab.sapa_desa.core.data.network

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object NetworkModule {
    // private const val BASE_URL = "http://192.168.1.171:8000" // Ganti jika produksi
    private const val BASE_URL = "https://sipaturedesa.id" // Ganti jika produksi

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    fun apiUrl(path: String): String = "$BASE_URL$path"

}
