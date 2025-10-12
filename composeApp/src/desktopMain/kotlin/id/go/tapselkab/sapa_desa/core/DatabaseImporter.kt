package id.go.tapselkab.sapa_desa.core

import id.go.tapselkab.database.sipature_db
import id.go.tapselkab.database.UserQueries
import id.go.tapselkab.database.PerangkatDesaQueries
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException

/**
 * Kelas ini bertanggung jawab untuk membaca, mengurai, dan memasukkan
 * data dari file JSON ke dalam database lokal menggunakan transaksi.
 */
class DatabaseImporter(
    private val sipature_db: sipature_db,
    private val userQueries: UserQueries,
    private val perangkatDesaQueries: PerangkatDesaQueries
) {

    // Buat satu instance Json yang bisa digunakan kembali.
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    // Data classes untuk mencocokkan struktur JSON Anda
    @Serializable
    data class UserJson(
        val id: Int,
        val name: String,
        val email: String,
        val kode_desa: String,
        val kode_kecamatan: String
    )

    @Serializable
    data class PerangkatDesaJson(
        val id: Int,
        val nama: String,
        val nipd: String? = null,
        val kode_kecamatan: String,
        val kode_desa: String,
        val kode_jabatan: String,
        val mulai: String? = null,
        val berakhir: String? = null,
        val nik: String? = null,
        val tempat_lahir: String? = null,
        val tanggal_lahir: String? = null,
        val sk_id: Int? = null,
        val pendidikan_id: String? = null,
        val jenis_kelamin: String? = null,
        val agama: String? = null,
        val no_telp: String? = null,
        val nama_jabatan: String? = null,
        val status_jabatan: String? = null,
        val status_keaktifan: String? = null
    )

    @Serializable
    data class DataWrapper(
        val users: List<UserJson>,
        val perangkat_desa: List<PerangkatDesaJson>
    )

    /**
     * Membaca dan mengurai file JSON, lalu memasukkan datanya ke database lokal.
     * @param jsonFile Path ke file JSON.
     * @return true jika berhasil, false jika terjadi kesalahan.
     */
    fun importDataFromJson(jsonFile: File): Boolean {
        return try {
            val jsonString = jsonFile.readText()
            val data = json.decodeFromString<DataWrapper>(jsonString) // Menggunakan instance Json yang sudah dibuat

            sipature_db.transaction {
                // Hapus data lama untuk memastikan data bersih
                userQueries.deleteAllUser()
                perangkatDesaQueries.deleteAllPerangkatDesa()

                // Masukkan data user dari JSON
                data.users.forEach { userJson ->
                    userQueries.insertUser(
                        id = userJson.id.toLong(),
                        email = userJson.email,
                        name = userJson.name,
                        token = "default", // Mengatur token ke 'default'
                    )
                }

                // Masukkan data perangkat desa dari JSON
                data.perangkat_desa.forEach { perangkatJson ->
                    perangkatDesaQueries.insertPerangkatDesa(
                        id = perangkatJson.id.toLong(),
                        nama = perangkatJson.nama,
                        nipd = perangkatJson.nipd,
                        kodeKecamatan = perangkatJson.kode_kecamatan,
                        kodeDesa = perangkatJson.kode_desa,
                        kodeJabatan = perangkatJson.kode_jabatan,
                        mulai = perangkatJson.mulai,
                        berakhir = perangkatJson.berakhir,
                        nik = perangkatJson.nik,
                        tempatLahir = perangkatJson.tempat_lahir,
                        tanggalLahir = perangkatJson.tanggal_lahir,
                        skId = perangkatJson.sk_id?.toLong(),
                        pendidikanId = perangkatJson.pendidikan_id?.toLong(),
                        jenisKelamin = perangkatJson.jenis_kelamin,
                        agama = perangkatJson.agama,
                        noTelp = perangkatJson.no_telp,
                        statusJabatan = perangkatJson.status_jabatan,
                        statusKeaktifan = perangkatJson.status_keaktifan,
                        namaJabatan = perangkatJson.nama_jabatan
                    )
                }
            }
            true
        } catch (e: IOException) {
            println("Kesalahan saat membaca file: ${e.message}")
            false
        } catch (e: Exception) {
            println("Kesalahan saat mengurai atau memasukkan data: ${e.message}")
            false
        }
    }
}
