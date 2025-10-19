package id.go.tapselkab.sapa_desa.core.data.network

import id.go.tapselkab.sapa_desa.core.data.local.model.VerifikasiAbsensiModel
import id.go.tapselkab.sapa_desa.core.data.local.model.VerifikasiAbsensiResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.io.IOException
import id.go.tapselkab.sapa_desa.core.data.model.*
import id.go.tapselkab.sapa_desa.core.data.network.model.AbsensiRequest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File

interface AuthApiService {
    suspend fun loginWithEmail(email: String, password: String): String

    suspend fun getUser(token: String): UserResponse

    suspend fun getPerangkat(token: String): List<PerangkatModel>?

    suspend fun getVerifikasiAbsensi(token: String): VerifikasiAbsensiModel?

    suspend fun sendVerifikasiAbsensi(token: String, data: VerifikasiAbsensiModel): Boolean

    suspend fun insertAbsensiWithImages(
        token: String,
        latitude: Double,
        longitude: Double,
        macAddress: String,
        absensi: AbsensiRequest,
        gambarPagi: File?,
        gambarSore: File?
    ): Boolean

    suspend fun insertAbsensiWithLampiran(
        token: String,
        perangkatId: Long,
        tanggal: String,
        keterangan: String,
        lampiran: File
    ): Boolean

}

class AuthApiServiceImpl(private val client: HttpClient = NetworkModule.client) : AuthApiService {

    override suspend fun loginWithEmail(email: String, password: String): String {
        return try {
            val response: HttpResponse = client.post(NetworkModule.apiUrl("/api/sanctum/token")) {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(email, password))
            }

            if (!response.status.isSuccess()) {
                // Baca dan ambil pesan error dari response body
                val errorResponse = response.bodyAsText()
                val jsonError = Json.parseToJsonElement(errorResponse).jsonObject

                // Ambil pesan error
                val errorMessage = jsonError["message"]?.jsonPrimitive?.content

                // Menampilkan pesan error
                throw Exception("$errorMessage")
            }

            val tokenResponse: LoginResponse = response.body()
            tokenResponse.token

        } catch (e: io.ktor.client.network.sockets.SocketTimeoutException) {
            throw e
        } catch (e: IOException) {
            throw e
        } catch (e: ClientRequestException) {
            throw e
        } catch (e: ServerResponseException) {
            throw e
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getUser(token: String): UserResponse {
        return try {
            val response = client.get(NetworkModule.apiUrl("/api/user")) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                    append(HttpHeaders.Accept, "application/json")
                }
            }

            if (!response.status.isSuccess()) {
                // Baca dan ambil pesan error dari response body
                val errorResponse = response.bodyAsText()
                val jsonError = Json.parseToJsonElement(errorResponse).jsonObject

                // Ambil pesan error
                val errorMessage = jsonError["message"]?.jsonPrimitive?.content

                // Menampilkan pesan error
                throw Exception("$errorMessage")
            }

            response.body()

        } catch (e: ClientRequestException) {
            throw Exception("Token tidak valid atau expired")
        } catch (e: ServerResponseException) {
            throw Exception("Kesalahan server saat memuat user")
        } catch (e: Exception) {
            throw Exception("Terjadi kesalahan saat memuat user: ${e.message}")
        }
    }

    override suspend fun getPerangkat(token: String): List<PerangkatModel>? {
        return try {
            val response = client.get(NetworkModule.apiUrl("/api/perangkat-desa")) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                    append(HttpHeaders.Accept, "application/json")

                }
                // parameter("kode_desa", kode_desa)
            }

            if (!response.status.isSuccess()) {
                // Baca dan ambil pesan error dari response body
                val errorResponse = response.bodyAsText()
                val jsonError = Json.parseToJsonElement(errorResponse).jsonObject

                // Ambil pesan error
                val errorMessage = jsonError["message"]?.jsonPrimitive?.content

                // Menampilkan pesan error
                throw Exception("$errorMessage")
            }

            val result = response.body<PerangkatResponse<List<PerangkatModel>>>()

            result.data

        } catch (e: ClientRequestException) {
            throw e
        } catch (e: ServerResponseException) {
            throw e
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getVerifikasiAbsensi(token: String): VerifikasiAbsensiModel? {
        return try {
            val response = client.get(NetworkModule.apiUrl("/api/verifikasi-absensi")) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                    append(HttpHeaders.Accept, "application/json")
                }
            }

            val result = response.body<VerifikasiAbsensiResponse<VerifikasiAbsensiModel>>()

            if (!response.status.isSuccess()) {
                throw Exception("${response.status}")
            }



            result.data

        } catch (e: ClientRequestException) {
            throw e
        } catch (e: ServerResponseException) {
            throw e
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun sendVerifikasiAbsensi(
        token: String,
        data: VerifikasiAbsensiModel
    ): Boolean {
        return try {
            val response = client.submitFormWithBinaryData(
                url = NetworkModule.apiUrl("/api/verifikasi-absensi"),
                formData = formData {
                    append("user_id", data.user_id.toString())
                    append("kode_kecamatan", data.kode_kecamatan)
                    append("kode_desa", data.kode_desa)
                    append("mac_address", data.mac_address.toString())

                    // latitude & longitude bisa null, jadi pakai let
                    append("latitude", data.latitude.toString())
                    append("longitude", data.longitude.toString())
                }
            ) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                    append(HttpHeaders.Accept, "application/json")
                }
            }

            if (!response.status.isSuccess()) {
                // Baca dan ambil pesan error dari response body
                val errorResponse = response.bodyAsText()
                val jsonError = Json.parseToJsonElement(errorResponse).jsonObject

                // Ambil pesan error
                val errorMessage = jsonError["message"]?.jsonPrimitive?.content

                // Menampilkan pesan error
                throw Exception("$errorMessage")
            }

            true

        } catch (e: Exception) {
            throw e
        }
    }


    override suspend fun insertAbsensiWithImages(
        token: String,
        latitude: Double,
        longitude: Double,
        macAddress: String,
        absensi: AbsensiRequest,
        gambarPagi: File?,
        gambarSore: File?
    ): Boolean {

        return try {
            val response = client.submitFormWithBinaryData(
                url = NetworkModule.apiUrl("/api/absensi"),
                formData = formData {

                    append("latitude", latitude.toString())
                    append("longitude", longitude.toString())
                    append("mac_address", macAddress)

                    append("perangkat_id", absensi.perangkat_id.toString())
                    append("tanggal", absensi.tanggal)
                    absensi.absensi_pagi?.let { append("absensi_pagi", it) }
                    absensi.absensi_sore?.let { append("absensi_sore", it) }
                    absensi.keterlambatan?.let { append("keterlambatan", it.toString()) }
                    absensi.pulang_cepat?.let { append("pulang_cepat", it.toString()) }


                    gambarPagi?.let {
                        append("gambar_pagi", it.readBytes(), Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                            append(HttpHeaders.ContentDisposition, "filename=\"${it.name}\"")
                        })
                    }

                    gambarSore?.let {
                        append("gambar_sore", it.readBytes(), Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                            append(HttpHeaders.ContentDisposition, "filename=\"${it.name}\"")
                        })
                    }
                }
            ) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }

            if (!response.status.isSuccess()) {
                // Baca dan ambil pesan error dari response body
                val errorResponse = response.bodyAsText()
                val jsonError = Json.parseToJsonElement(errorResponse).jsonObject

                // Ambil pesan error
                val errorMessage = jsonError["message"]?.jsonPrimitive?.content

                // Menampilkan pesan error
                throw Exception("$errorMessage")
            }

            true
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun insertAbsensiWithLampiran(
        token: String,
        perangkatId: Long,
        tanggal: String,
        keterangan: String,
        lampiran: File
    ): Boolean {
        return try {
            val response = client.submitFormWithBinaryData(
                url = NetworkModule.apiUrl("/api/absensi/lampiran"),
                formData = formData {
                    append("perangkat_id", perangkatId.toString())
                    append("tanggal", tanggal)
                    append("keterangan", keterangan)

                    // Upload file PDF
                    append("lampiran", lampiran.readBytes(), Headers.build {
                        append(HttpHeaders.ContentType, "application/pdf")
                        append(HttpHeaders.ContentDisposition, "filename=\"${lampiran.name}\"")
                    })
                }
            ) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }

            if (!response.status.isSuccess()) {
                // Baca dan ambil pesan error dari response body
                val errorResponse = response.bodyAsText()
                val jsonError = Json.parseToJsonElement(errorResponse).jsonObject

                // Ambil pesan error
                val errorMessage = jsonError["message"]?.jsonPrimitive?.content

                // Menampilkan pesan error
                throw Exception("$errorMessage")
            }

            true
        } catch (e: Exception) {
            throw e
        }
    }

}



