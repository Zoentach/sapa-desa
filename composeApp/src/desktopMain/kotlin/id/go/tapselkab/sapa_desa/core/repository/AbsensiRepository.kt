package id.go.tapselkab.sapa_desa.core.repository

import id.go.tapselkab.database.sipature_db
import id.go.tapselkab.sapa_desa.core.data.network.AuthApiService
import id.go.tapselkab.sapa_desa.core.data.token.TokenStorage
import id.go.tapselkab.sapa_desa.ui.entity.AbsensiEntity
import id.go.tapselkab.sapa_desa.ui.entity.UserEntity
import id.go.tapselkab.sapa_desa.ui.entity.toRequest
import id.go.tapselkab.sapa_desa.utils.time.DateUtils
import java.io.File
import java.lang.Exception

class AbsensiRepository(
    private val api: AuthApiService,
    private val db: sipature_db,
) {

    suspend fun sendAbsensiToServer(
        absensi: AbsensiEntity,
        gambarPagi: File?,
        gambarSore: File?
    ): Boolean {

        val user = getCurrentUser()

        val token = user?.token ?: ""

        return try {
            api.insertAbsensiWithImages(
                token = token,
                absensi = absensi.toRequest(),
                gambarPagi = gambarPagi,
                gambarSore = gambarSore
            )
        } catch (e: Exception) {
            throw e
        }
    }

    fun insertAbsensi(
        perangkatId: Long,
        tanggal: String,
        pagi: String?,
        sore: String?,
        keterlambatan: Long?,
        pulangCepat: Long?,
        syncStatus: Long
    ) {
        db.absensiQueries.insertAbsensi(
            perangkat_id = perangkatId,
            tanggal = tanggal,
            absensi_pagi = pagi,
            absensi_sore = sore,
            keterlambatan = keterlambatan,
            pulang_cepat = pulangCepat,
            sync_status = syncStatus,
            kode_desa = null,
            kode_kecamatan = null
        )
    }

    fun updateAfternoonAbsensi(
        tanggal: String,
        perangkatId: Long,
        sore: String,
        pulangCepat: Long?,
        syncStatus: Long
    ) {

        db.absensiQueries.updateAbsensiAfternoonByUserAndDate(
            absensi_sore = sore,
            pulang_cepat = pulangCepat,
            sync_status = syncStatus,
            perangkat_id = perangkatId,
            tanggal = tanggal
        )
    }

    fun updateAbsensiSyncStatus(
        perangkatId: Long,
        tanggal: String,
        syncStatus: Long
    ) {

        //val stringTanggal = DateUtils.toDateString(tanggal)
        db.absensiQueries.updateAbsensiSyncStatus(
            sync_status = syncStatus,
            perangkat_id = perangkatId,
            tanggal = tanggal
        )
    }

    fun isAbsensiExist(perangkatId: Long, tanggal: String): Boolean {
        val count = db.absensiQueries.selectByDate(
            tanggal = tanggal,
            perangkat_id = perangkatId
        ).executeAsOne()

        return count > 0
    }

    fun deleteAbsensiById(id: Long) {
        db.absensiQueries.deleteAbsensiById(id)
    }

    fun getAllAbsensi(): List<AbsensiEntity> {
        return db.absensiQueries.selectAllAbsensi().executeAsList().map { absensi ->
            AbsensiEntity(
                id = absensi.id.toInt(),
                perangkatId = absensi.perangkat_id.toInt(),
                tanggal = absensi.tanggal,
                absensiPagi = absensi.absensi_pagi,
                absensiSore = absensi.absensi_sore,
                keterlambatan = absensi.keterlambatan?.toInt(),
                pulangCepat = absensi.pulang_cepat?.toInt(),
                syncStatus = absensi.sync_status.toInt()
            )
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
            println("User fetch failed from local DB: ${e.message}")
            null
        }
    }
}