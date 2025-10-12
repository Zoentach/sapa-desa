package id.go.tapselkab.sapa_desa.core.repository

import id.go.tapselkab.sapa_desa.core.data.network.AuthApiService
import id.go.tapselkab.database.sipature_db
import id.go.tapselkab.sapa_desa.core.data.network.GeoLocation
import id.go.tapselkab.sapa_desa.core.data.network.LocationApiService
import id.go.tapselkab.sapa_desa.ui.entity.VerifikasiAbsensiEntity
import id.go.tapselkab.sapa_desa.ui.entity.toEntity
import id.go.tapselkab.sapa_desa.ui.entity.toModel

class VerifikasiAbsensiRepository(
    private val db: sipature_db,
    private val api: AuthApiService,
    private val location: LocationApiService
) {

    suspend fun getVerifikasiAbsensi(userId: Long, token: String): VerifikasiAbsensiEntity? {
        return try {

            if (token.isNullOrBlank() || userId == null) {
                println("VerifikasiAbsensi fetch failed: Token atau User ID tidak tersedia")
                return null
            }

            // 1️Ambil dari lokal terlebih dahulu
            val localData = db.verifikasiAbsensiQueries
                .selectVerifikasiByUserId(userId)
                .executeAsOneOrNull()

            if (localData != null) {
                return VerifikasiAbsensiEntity(
                    userId = localData.user_id,
                    kodeDesa = localData.kode_desa,
                    kodeKecamatan = localData.kode_kecamatan,
                    macAddress = localData.mac_address,
                    latitude = localData.latitude,
                    longitude = localData.longitude,
                    syncStatus = localData.sync_status
                )
            }

            // Jika lokal kosong, ambil dari server
            val remoteData = api.getVerifikasiAbsensi(token)

            if (remoteData != null) {
                val entity = remoteData.firstOrNull()?.toEntity(1)

                // Simpan ke lokal
                db.verifikasiAbsensiQueries.insertOrReplaceVerifikasi(
                    user_id = entity?.userId,
                    kode_desa = entity?.kodeDesa ?: "",
                    kode_kecamatan = entity?.kodeKecamatan ?: "",
                    mac_address = entity?.macAddress,
                    latitude = entity?.latitude,
                    longitude = entity?.longitude,
                    sync_status = 1 // karena sudah sinkron dari server
                )

                // Kembalikan data yang baru disimpan
                return entity
            }

            // Jika di server juga tidak ada, return null
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
                db.verifikasiAbsensiQueries.insertOrReplaceVerifikasi(
                    user_id = verifikasi.userId,
                    kode_kecamatan = verifikasi.kodeKecamatan,
                    kode_desa = verifikasi.kodeDesa,
                    mac_address = verifikasi.macAddress,
                    latitude = verifikasi.latitude,
                    longitude = verifikasi.longitude,
                    sync_status = 1,

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
        db.verifikasiAbsensiQueries.insertOrReplaceVerifikasi(
            user_id = verifikasi.userId,
            kode_desa = verifikasi.kodeDesa,
            kode_kecamatan = verifikasi.kodeKecamatan,
            mac_address = verifikasi.macAddress,
            latitude = verifikasi.latitude,
            longitude = verifikasi.longitude,
            sync_status = verifikasi.syncStatus
        )
    }

    suspend fun deleteByUserId(userId: Long) {
        db.verifikasiAbsensiQueries.deleteVerifikasiByUserId(userId)
    }

    suspend fun deleteAll() {
        db.verifikasiAbsensiQueries.deleteAllVerifikasiAbsensi()
    }

    suspend fun getMyLocation(): GeoLocation {
        return try {
            location.getCurrentLocation()
        } catch (e: Exception) {
            throw e
        }
    }
}