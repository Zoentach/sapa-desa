package id.go.tapselkab.sapa_desa.utils.connection

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

suspend fun isInternetConnected(): Boolean {
    val client = HttpClient(CIO)
    return try {
        val response = client.get("https://google.com")
        response.status == HttpStatusCode.OK
    } catch (e: Exception) {
        false
    } finally {
        client.close()
    }
}