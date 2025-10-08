package id.go.tapselkab.sapa_desa.core.repository

import id.go.tapselkab.sapa_desa.core.data.network.AuthApiService
import id.go.tapselkab.sapa_desa.core.data.token.TokenStorage
import id.go.tapselkab.sapa_desa.ui.entity.PerangkatEntity
import id.go.tapselkab.sapa_desa.ui.entity.UserEntity
import id.go.tapselkab.sapa_desa.ui.entity.toEntity
import id.go.tapselkab.database.sipature_db
import java.lang.Exception

class DashboardRepository(
    private val api: AuthApiService,
    private val db: sipature_db,
) {

    suspend fun getPerangkatDesa(kodeDesa: String): List<PerangkatEntity>? {
        return try {
            val user = getCurrentUser()
            val token = user?.token

            if (token.isNullOrBlank()) {
                println("User fetch failed: Token tidak tersedia")
                return null
            }

            // Ambil data perangkat desa dari database lokal
            val perangkats = db.perangkatDesaQueries.selectAllPerangkatDesa(kode_desa = kodeDesa).executeAsList()

            if (perangkats.isNotEmpty()) {
                // Jika data ada, kembalikan data dari database
                return perangkats.map { it.toEntity() }
            } else {
                // Jika data tidak ada, ambil dari API
                val apiPerangkats = api.getPerangkat(token = token, kode_desa = kodeDesa)

                // Jika API mengembalikan data, simpan ke database
                apiPerangkats?.forEach { model ->
                    db.perangkatDesaQueries.insertPerangkatDesa(
                        id = model.id.toLong(),
                        nama = model.nama,
                        nipd = model.nipd,
                        kode_kec = model.kode_kec,
                        kode_desa = model.kode_desa,
                        kode_jabatan = model.kode_jabatan,
                        mulai = model.mulai,
                        berakhir = model.berakhir,
                        nik = model.nik,
                        tempat_lahir = model.tempat_lahir,
                        tanggal_lahir = model.tanggal_lahir,
                        no_sk = model.no_sk,
                        pendidikan = model.pendidikan,
                        jenis_kelamin = model.jenis_kelamin,
                        agama = model.agama,
                        no_telp = model.no_telp,
                        status = model.status,
                        nama_jabatan = model.nama_jabatan
                    )
                }

                // Ambil kembali data yang sudah diinsert dan kembalikan
                return db.perangkatDesaQueries.selectAllPerangkatDesa(kode_desa = kodeDesa).executeAsList().map { it.toEntity() }
            }
        } catch (e: Exception) {
            println("User fetch failed: ${e.message}")
            null
        }
    }

    suspend fun getCurrentUser(): UserEntity? {
        return try {
            val generatedUser = db.userQueries.selectAllUser().executeAsOneOrNull()
            // Konversi dari data class yang dihasilkan SQLDelight ke UserEntity
            generatedUser?.let {
                UserEntity(
                    id = it.id.toInt(),
                    name = it.name,
                    email = it.email,
                    kodeDesa = it.kode_desa,
                    kodeKec = it.kode_kec,
                    macAddress = it.mac_address,
                    token = it.token
                )
            }
        } catch (e: Exception) {
            println("User fetch failed from local DB: ${e.message}")
            null
        }
    }

}