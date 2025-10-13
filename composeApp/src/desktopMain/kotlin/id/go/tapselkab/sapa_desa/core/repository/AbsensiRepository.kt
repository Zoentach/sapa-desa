package id.go.tapselkab.sapa_desa.core.repository

import id.go.tapselkab.database.sipature_db
import id.go.tapselkab.sapa_desa.core.data.network.AuthApiService
import id.go.tapselkab.sapa_desa.ui.entity.AbsensiEntity
import id.go.tapselkab.sapa_desa.ui.entity.UserEntity
import id.go.tapselkab.sapa_desa.ui.entity.toRequest
import java.io.File
import java.lang.Exception

class AbsensiRepository(
    private val api: AuthApiService,
    private val db: sipature_db,
) {

    suspend fun sendAbsensiToServer(
        latitude: Double,
        longitude: Double,
        macAddress: String,
        absensi: AbsensiEntity,
        gambarPagi: File?,
        gambarSore: File?
    ): Boolean {

        val user = getCurrentUser()
        val token = user?.token ?: ""

        return try {
            api.insertAbsensiWithImages(
                token = token,
                latitude = latitude,
                longitude = longitude,
                macAddress = macAddress,
                absensi = absensi.toRequest(),
                gambarPagi = gambarPagi,
                gambarSore = gambarSore
            )
        } catch (e: Exception) {
            throw e
        }
    }


    suspend fun ajukanAbsensiIzin(
        perangkatId: Long,
        tanggal: String,
        keterangan: String,
        lampiran: File
    ): Boolean {

        val user = getCurrentUser()
        val token = user?.token ?: ""

        return try {
            api.insertAbsensiWithLampiran(
                token = token,
                perangkatId = perangkatId,
                tanggal = tanggal,
                keterangan = keterangan,
                lampiran = lampiran
            )
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun insertAbsensi(
        perangkatId: Long,
        tanggal: String,
        pagi: String?,
        sore: String?,
        keterlambatan: Long?,
        pulangCepat: Long?,
        syncStatus: Long
    ) {
        try {
            db.absensiQueries.insertOrReplaceAbsensi(
                perangkat_id = perangkatId,
                tanggal = tanggal,
                absensi_pagi = pagi,
                absensi_sore = sore,
                keterlambatan = keterlambatan,
                pulang_cepat = pulangCepat,
                keterangan = "Hadir",
                status_kehadiran = "Disetujui",
                sync_status = syncStatus,
            )
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun insertAbsensiIzin(
        perangkatId: Long,
        tanggal: String,
        keterangan: String,
        syncStatus: Long
    ) {
        try {
            db.absensiQueries.insertOrReplaceAbsensi(
                perangkat_id = perangkatId,
                tanggal = tanggal,
                absensi_pagi = null,
                absensi_sore = null,
                keterangan = keterangan,
                status_kehadiran = "Pending",
                keterlambatan = 0,
                pulang_cepat = 0,
                sync_status = syncStatus,
            )
        } catch (e: Exception) {
            throw e
        }
    }


    suspend fun updateAfternoonAbsensi(
        tanggal: String,
        perangkatId: Long,
        sore: String,
        pulangCepat: Long?,
        syncStatus: Long
    ) {
        try {
            db.absensiQueries.updateAbsensiAfternoonByUserAndDate(
                absensi_sore = sore,
                pulang_cepat = pulangCepat,
                sync_status = syncStatus,
                perangkat_id = perangkatId,
                tanggal = tanggal
            )
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun updateAbsensiSyncStatus(
        perangkatId: Long,
        tanggal: String,
        syncStatus: Long
    ) {

        try {

            //val stringTanggal = DateUtils.toDateString(tanggal)
            db.absensiQueries.updateAbsensiSyncStatus(
                sync_status = syncStatus,
                perangkat_id = perangkatId,
                tanggal = tanggal
            )
        } catch (e: Exception) {
            throw e
        }
    }

    fun isAbsensiExist(perangkatId: Long, tanggal: String): Boolean {
        val count = db.absensiQueries.selectByDate(
            tanggal = tanggal,
            perangkat_id = perangkatId
        ).executeAsOne()

        return count > 0
    }


    fun getAllAbsensi(): List<AbsensiEntity> {
        try {
            return db.absensiQueries.selectAllAbsensi().executeAsList().map { absensi ->
                AbsensiEntity(
                    id = absensi.id.toInt(),
                    perangkatId = absensi.perangkat_id.toInt(),
                    tanggal = absensi.tanggal,
                    absensiPagi = absensi.absensi_pagi,
                    absensiSore = absensi.absensi_sore,
                    keterlambatan = absensi.keterlambatan?.toInt(),
                    pulangCepat = absensi.pulang_cepat?.toInt(),
                    syncStatus = absensi.sync_status.toInt(),
                    keterangan = absensi.keterangan,
                    statusKehadiran = absensi.status_kehadiran
                )
            }
        } catch (e: Exception) {
            throw e
        }
    }

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
            throw e
        }
    }
}