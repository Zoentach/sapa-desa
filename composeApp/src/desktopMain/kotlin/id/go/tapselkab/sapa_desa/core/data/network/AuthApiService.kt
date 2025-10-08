package id.go.tapselkab.sapa_desa.core.data.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.io.IOException
import org.bytedeco.flycapture.FlyCapture2.MACAddress
import id.go.tapselkab.sapa_desa.core.data.model.*
import id.go.tapselkab.sapa_desa.core.data.network.model.AttendanceRequest
import java.io.File

interface AuthApiService {
    suspend fun loginWithEmail(email: String, password: String, deviceName: String = "SAPA DESA"): String

    //  suspend fun loginWithToken(token: String): Boolean
    suspend fun getUser(token: String): UserResponse

    suspend fun getPerangkat(token: String, kode_desa: String): List<PerangkatModel>?

    suspend fun insertAttendanceWithImages(
        token: String,
        attendance: AttendanceRequest,
        imageMorning: File?,
        imageAfternoon: File?
    ): Boolean

    suspend fun updateMacAddress(
        token: String,
        macAddress: String
    ): Boolean
}

class AuthApiServiceImpl(private val client: HttpClient = NetworkModule.client) : AuthApiService {
    override suspend fun loginWithEmail(email: String, password: String, deviceName: String): String {
        return try {
            val response: HttpResponse = client.post(NetworkModule.apiUrl("/api/sanctum/token")) {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(email, password, deviceName))
            }

            // Cek response code jika diperlukan
            if (!response.status.isSuccess()) {
                throw Exception("Login gagal: ${response.status}")
            }

            val tokenResponse: LoginResponse = response.body()
            tokenResponse.token

        } catch (e: io.ktor.client.network.sockets.SocketTimeoutException) {
            throw Exception("Koneksi timeout, periksa jaringan Anda")
        } catch (e: IOException) {
            throw Exception("Tidak ada koneksi internet atau server tidak dapat dijangkau")
        } catch (e: ClientRequestException) {
            // Kesalahan dari sisi client (4xx)
            throw Exception("Email atau password salah (4xx): ${e.response.status}")
        } catch (e: ServerResponseException) {
            // Kesalahan dari sisi server (5xx)
            throw Exception("Server error (5xx): ${e.response.status}")
        } catch (e: Exception) {
            throw Exception("Terjadi kesalahan saat login: ${e.message}")
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
                throw Exception("Gagal mengambil data user: ${response.status}")
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

    override suspend fun getPerangkat(token: String, kode_desa: String): List<PerangkatModel>? {
        return try {
            val response = client.get(NetworkModule.apiUrl("/api/perangkat-desa")) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                    append(HttpHeaders.Accept, "application/json")

                }
                parameter("kode_desa", kode_desa)
            }

            if (!response.status.isSuccess()) {
                throw Exception("Gagal mengambil data perangkat: ${response.status}")
            }

            val result = response.body<PerangkatResponse<List<PerangkatModel>>>()

            result.data

        } catch (e: ClientRequestException) {
            throw Exception("Token tidak valid atau expired")
        } catch (e: ServerResponseException) {
            throw Exception("Kesalahan server saat memuat user")
        } catch (e: Exception) {
            throw Exception("Terjadi kesalahan saat memuat data perangkat: ${e.message}")
        }
    }

    override suspend fun insertAttendanceWithImages(
        token: String,
        attendance: AttendanceRequest,
        imageMorning: File?,
        imageAfternoon: File?
    ): Boolean {

        return try {
            val response = client.submitFormWithBinaryData(
                url = NetworkModule.apiUrl("/api/attendance"),
                formData = formData {
                    append("user_id", attendance.user_id.toString())
                    append("kode_desa", attendance.kode_desa)
                    append("kode_kec", attendance.kode_kec)
                    append("date", attendance.date.toString())
                    attendance.attendance_morning?.let { append("attendance_morning", it.toString()) }
                    attendance.attendance_afternoon?.let { append("attendance_afternoon", it.toString()) }
                    attendance.late?.let { append("late", it.toString()) }
                    attendance.early?.let { append("early", it.toString()) }


                    imageMorning?.let {
                        append("image_morning", it.readBytes(), Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                            append(HttpHeaders.ContentDisposition, "filename=\"${it.name}\"")
                        })
                    }

                    imageAfternoon?.let {
                        append("image_afternoon", it.readBytes(), Headers.build {
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
                throw Exception("Gagal insert attendance: ${response.status}")
            }

            true
        } catch (e: Exception) {
            throw Exception("Gagal upload attendance: ${e.message}")
        }
    }

    override suspend fun updateMacAddress(token: String, macAddress: String): Boolean {
        return try {
            val response: HttpResponse = client.post(NetworkModule.apiUrl("/api/user/update-mac")) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                    append(HttpHeaders.ContentType, "application/json")
                }
                setBody("""{ "mac_address": "$macAddress" }""")
            }

            if (!response.status.isSuccess()) {
                throw Exception("Gagal update MAC address: ${response.status}")
            }

            true
        } catch (e: ClientRequestException) {
            throw Exception("Token tidak valid atau permintaan salah: ${e.response.status}")
        } catch (e: ServerResponseException) {
            throw Exception("Kesalahan server saat update MAC address")
        } catch (e: Exception) {
            throw Exception("Terjadi kesalahan saat update MAC address: ${e.message}")
        }
    }

}



