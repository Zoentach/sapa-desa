package id.go.tapselkab.sapa_desa.core.repository


import id.go.tapselkab.sapa_desa.core.DatabaseImporter
import id.go.tapselkab.database.sipature_db
import id.go.tapselkab.sapa_desa.core.data.network.AuthApiService
import id.go.tapselkab.sapa_desa.ui.entity.UserEntity
import id.go.tapselkab.sapa_desa.ui.entity.toEntity
import java.io.File

class AuthRepository(
    private val api: AuthApiService,
    private val db: sipature_db,
    private val databaseImporter: DatabaseImporter
) {
    /**
     * Melakukan proses login dengan email dan password ke API.
     */
    suspend fun login(email: String, password: String): Boolean {
        return try {
            val token = api.loginWithEmail(email, password)
            if (token.isNotBlank()) {
                getCurrentUser(token)
                true
            } else {
                throw Exception("Login failed: Token kosong")
            }
        } catch (e: Exception) {
            println("Login failed: ${e.message}")
            throw e
        }
    }

    /**
     * Mengimpor data dari file JSON lokal dan memasukkannya ke database.
     */
    suspend fun importDataFromJson(jsonFile: File): Boolean {
        // Panggil fungsi import dari DatabaseImporter yang telah kita buat
        val success = databaseImporter.importDataFromJson(jsonFile)
        if (success) {
            println("Data berhasil diimpor dari file JSON.")
        } else {
            println("Gagal mengimpor data dari file JSON.")
        }
        return success && isLoggedIn()
    }

    /**
     * Mengambil data pengguna dari API menggunakan token dan menyimpannya di database lokal.
     */
    suspend fun getCurrentUser(token: String) {
        try {
            val user = api.getUser(token)
            db.userQueries.deleteAllUser()
            db.userQueries.insertUser(
                id = user.id.toLong(),
                name = user.name,
                email = user.email,
                token = token,
            )
        } catch (e: Exception) {
            println("User fetch failed: ${e.message}")
        }
    }

    /**
     * Mengambil data pengguna dari database lokal.
     */
    suspend fun getCurrentUser(): UserEntity? {
        return try {
            val generatedUser = db.userQueries.selectAllUser().executeAsOneOrNull()
            // Konversi dari data class yang dihasilkan SQLDelight ke UserEntity
            generatedUser?.let { user ->
                UserEntity(
                    id = user.id.toInt(),
                    name = user.name,
                    email = user.email,
                    token = user.token
                )
            }
        } catch (e: Exception) {
            println("User fetch failed from local DB: ${e.message}")
            null
        }
    }

    /**
     * Memperbarui alamat MAC pengguna di database lokal dan API.
     */
    /*  suspend fun updateMacAddress(macAddress: String): Boolean {
          val user = getCurrentUser()
          val token = user?.token

          if (token.isNullOrBlank()) {
              println("User fetch failed: Token tidak tersedia")
              return false
          }

          return try {
              db.userQueries.updateUserMacAddressByKodeDesa(
                  mac_address = macAddress,
                  kode_desa = user.kodeDesa
              )

              if (token != "default") {
                  api.updateMacAddress(token = token, macAddress = macAddress)
              }
              true
          } catch (e: Exception) {
              println("Error updating MAC address: $e")
              false
          }
      }*/

    /**
     * Menghapus semua data pengguna dari database lokal (logout).
     */
    fun logout() {
        try {
            db.userQueries.deleteAllUser()
        } catch (e: Exception) {
            println("Error saat keluar: $e")
        }
    }

    /**
     * Memeriksa apakah ada pengguna yang login di database lokal.
     */
    fun isLoggedIn(): Boolean {
        val userList = db.userQueries.selectAllUser().executeAsList()
        return userList.isNotEmpty()
    }
}