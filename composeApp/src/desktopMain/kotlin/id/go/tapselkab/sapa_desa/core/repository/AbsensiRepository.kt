package id.go.tapselkab.sapa_desa.core.repository

import id.go.tapselkab.database.sipature_db
import id.go.tapselkab.sapa_desa.core.data.network.AuthApiService
import id.go.tapselkab.sapa_desa.core.data.token.TokenStorage
import id.go.tapselkab.sapa_desa.ui.entity.AbsensiEntity
import id.go.tapselkab.sapa_desa.ui.entity.toRequest
import id.go.tapselkab.sapa_desa.utils.time.DateUtils
import java.io.File
import java.lang.Exception

class AbsensiRepository(
    private val api: AuthApiService,
    private val db: sipature_db,
    private val tokenStorage: TokenStorage
) {

    suspend fun sendAbsensiToServer(
        absensi: AbsensiEntity,
        gambarPagi: File?,
        gambarSore: File?
    ): Boolean {
        return try {
            val token = tokenStorage.get()

            if (token.isNullOrBlank()) {
                println("User fetch failed: Token tidak tersedia")
                return false
            }
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
        tanggal: Long,
        pagi: Long?,
        sore: Long?,
        keterlambatan: Long?,
        pulangCepat: Long?,
        syncStatus: Long
    ) {
        val stringTanggal = DateUtils.toDateString(tanggal)
        val stringPagi = if (pagi != null) DateUtils.toTimeString(pagi) else null
        val stringSore = if (sore != null) DateUtils.toTimeString(sore) else null

        db.absensiQueries.insertAbsensi(
            perangkat_id = perangkatId,
            tanggal = stringTanggal,
            absensi_pagi = stringPagi,
            absensi_sore = stringSore,
            keterlambatan = keterlambatan,
            pulang_cepat = pulangCepat,
            sync_status = syncStatus,
            kode_desa = null,
            kode_kecamatan = null
        )
    }

    fun updateAfternoonAbsensi(
        tanggal: Long,
        perangkatId: Long,
        sore: Long?,
        pulangCepat: Long?,
        syncStatus: Long
    ) {
        val stringSore = if (sore != null) DateUtils.toTimeString(sore) else null
        val stringTanggal = DateUtils.toDateString(tanggal)

        db.absensiQueries.updateAbsensiAfternoonByUserAndDate(
            absensi_sore = stringSore,
            pulang_cepat = pulangCepat,
            sync_status = syncStatus,
            perangkat_id = perangkatId,
            tanggal = stringTanggal
        )
    }

    fun updateAbsensiSyncStatus(
        perangkatId: Long,
        tanggal: Long,
        syncStatus: Long
    ) {

        val stringTanggal = DateUtils.toDateString(tanggal)

        db.absensiQueries.updateAbsensiSyncStatus(
            sync_status = syncStatus,
            perangkat_id = perangkatId,
            tanggal = stringTanggal
        )
    }

//    fun isAbsensiExist(userId: Long, date: Long?): Boolean {
//        val count = db.absensiQueries.selectByDate(
//            tanggal = date,
//            perangkat_id = userId
//        ).executeAsOne()
//
//        return count > 0
//    }

//    fun deleteAbsensiById(id: Long) {
//        db.absensiQueries.deleteAbsensiById(id)
//    }

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
}