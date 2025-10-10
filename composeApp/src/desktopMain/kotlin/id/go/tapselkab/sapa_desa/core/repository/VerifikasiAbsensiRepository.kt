package id.go.tapselkab.sapa_desa.core.repository


import id.go.tapselkab.sapa_desa.core.data.network.AuthApiService
import id.go.tapselkab.database.sipature_db
import id.go.tapselkab.sapa_desa.ui.entity.UserEntity
import id.go.tapselkab.sapa_desa.ui.entity.VerifikasiAbsensiEntity
import id.go.tapselkab.sapa_desa.ui.entity.toEntity
import id.go.tapselkab.sapa_desa.ui.entity.toModel

class VerifikasiAbsensiRepository(
    private val api: AuthApiService,
    private val db: sipature_db,
) {
    // Ambil verifikasi berdasarkan userId
    suspend fun getVerifikasiAbsensi(userId: Long, token: String): VerifikasiAbsensiEntity? {
        return try {

            // Ambil dari lokal terlebih dahulu
            val localData = db.verifikasiAbsensiQueries
                .selectByUserId(userId)
                .executeAsOneOrNull()

            if (localData != null) {
                // Jika data ada di lokal, langsung kembalikan
                return localData
            }

            //Jika lokal kosong, ambil dari server
            val remoteData = api.getVerifikasiAbsensi(token)

            if (remoteData != null) {
                val entity = remoteData.firstOrNull()?.toEntity(1)

                // Simpan ke lokal
                db.verifikasiAbsensiQueries.insertOrReplace(
                    user_id = entity?.userId,
                    kode_desa = entity?.kodeDesa,
                    kode_kecamatan = entity?.kodeKecamatan,
                    mac_address = entity?.macAddress,
                    latitude = entity?.latitude,
                    longitude = entity?.longitude,
                    syncStatus = 1
                )

                // Kembalikan data yang baru disimpan
                return entity
            }

            // 3️⃣ Jika di server juga tidak ada, return null
            null

        } catch (e: Exception) {
            println("Gagal mengambil VerifikasiAbsensi: ${e.message}")
            null
        }
    }

    // Kirim verifikasi ke server dan update syncStatus
    suspend fun sendVerifikasiAbsensi(
        token: String,
        verifikasi: VerifikasiAbsensiEntity
    ): Boolean {
        return try {
            val response = api.sendVerifikasiAbsensi(
                token = token,
                data = verifikasi.toModel()
            )

            if (response) {
                db.verifikasiAbsensiQueries.updateSyncStatusByUserId(
                    syncStatus = 1,
                    user_id = verifikasi.userId
                )
                true
            } else {
                false
            }
        } catch (e: Exception) {
            println("Gagal mengirim verifikasi absensi: ${e.message}")
            false
        }
    }

    // Simpan / perbarui lokal
    suspend fun saveLocal(verifikasi: VerifikasiAbsensiEntity) {
        db.verifikasiAbsensiQueries.insertOrReplace(
            user_id = verifikasi.userId,
            kode_desa = verifikasi.kodeDesa,
            kode_kecamatan = verifikasi.kodeKecamatan,
            mac_address = verifikasi.macAddress,
            latitude = verifikasi.latitude,
            longitude = verifikasi.longitude,
            syncStatus = verifikasi.syncStatus
        )
    }

    suspend fun deleteByUserId(userId: Long) {
        db.verifikasiAbsensiQueries.deleteByUserId(userId)
    }

    suspend fun deleteAll() {
        db.verifikasiAbsensiQueries.deleteAll()
    }

    suspend fun getCurrentUser(): UserEntity? {
        return try {
            val generatedUser = db.userQueries
                .selectAllUser()
                .executeAsOneOrNull()
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
}