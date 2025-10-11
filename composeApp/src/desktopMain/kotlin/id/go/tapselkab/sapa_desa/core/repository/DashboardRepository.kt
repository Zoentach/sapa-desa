package id.go.tapselkab.sapa_desa.core.repository

import id.go.tapselkab.sapa_desa.core.data.network.AuthApiService
import id.go.tapselkab.sapa_desa.ui.entity.PerangkatEntity
import id.go.tapselkab.sapa_desa.ui.entity.UserEntity
import id.go.tapselkab.sapa_desa.ui.entity.toEntity
import id.go.tapselkab.database.sipature_db
import java.lang.Exception

class DashboardRepository(
    private val api: AuthApiService,
    private val db: sipature_db,
) {

    suspend fun getPerangkatDesa(): List<PerangkatEntity>? {
        return try {
            val user = getCurrentUser()
            val token = user?.token

            if (token.isNullOrBlank()) {
                println("User fetch failed: Token tidak tersedia")
                return null
            }

            // Ambil data perangkat desa dari database lokal
            val perangkats = db.perangkatDesaQueries
                .selectAllPerangkatDesa().executeAsList()

            if (perangkats.isNotEmpty()) {
                // Jika data ada, kembalikan data dari database
                return perangkats.map { it.toEntity() }
            } else {
                // Jika data tidak ada, ambil dari API
                val apiPerangkats = api.getPerangkat(
                    token = token
                )

                // Jika API mengembalikan data, simpan ke database
                apiPerangkats?.forEach { model ->
                    db.perangkatDesaQueries.insertPerangkatDesa(
                        id = model.id.toLong(),
                        nama = model.nama,
                        nipd = model.nipd,
                        kodeKecamatan = model.kode_kecamatan,
                        kodeDesa = model.kode_desa,
                        kodeJabatan = model.kode_jabatan,
                        mulai = model.mulai,
                        berakhir = model.berakhir,
                        nik = model.nik,
                        tempatLahir = model.tempat_lahir,
                        tanggalLahir = model.tanggal_lahir,
                        skId = model.sk_id?.toLong(),
                        pendidikanId = model.pendidikan_id?.toLong(),
                        jenisKelamin = model.jenis_kelamin,
                        agama = model.agama,
                        noTelp = model.no_telp,
                        statusJabatan = model.status_jabatan,
                        statusKeaktifan = model.status_keaktifan,
                        namaJabatan = model.nama_jabatan
                    )
                }
                // Ambil kembali data yang sudah diinsert dan kembalikan
                return db.perangkatDesaQueries
                    .selectAllPerangkatDesa()
                    .executeAsList()
                    .map { it.toEntity() }
            }
        } catch (e: Exception) {
            println("User fetch failed: ${e.message}")
            null
        }
    }

    suspend fun perbaharuiAparaturDesa(): List<PerangkatEntity>? {
        return try {
            val user = getCurrentUser()
            val token = user?.token

            if (token.isNullOrBlank()) {
                println("User fetch failed: Token tidak tersedia")
                return null
            }

            // Hapus Semua perangkat
            val perangkats = db.perangkatDesaQueries
                .deleteAllPerangkatDesa()

            val apiPerangkats = api.getPerangkat(
                token = token
            )
            // Jika API mengembalikan data, simpan ke database
            apiPerangkats?.forEach { model ->
                db.perangkatDesaQueries.insertPerangkatDesa(
                    id = model.id.toLong(),
                    nama = model.nama,
                    nipd = model.nipd,
                    kodeKecamatan = model.kode_kecamatan,
                    kodeDesa = model.kode_desa,
                    kodeJabatan = model.kode_jabatan,
                    mulai = model.mulai,
                    berakhir = model.berakhir,
                    nik = model.nik,
                    tempatLahir = model.tempat_lahir,
                    tanggalLahir = model.tanggal_lahir,
                    skId = model.sk_id?.toLong(),
                    pendidikanId = model.pendidikan_id?.toLong(),
                    jenisKelamin = model.jenis_kelamin,
                    agama = model.agama,
                    noTelp = model.no_telp,
                    statusJabatan = model.status_jabatan,
                    statusKeaktifan = model.status_keaktifan,
                    namaJabatan = model.nama_jabatan
                )
            }

            // Ambil kembali data yang sudah diinsert dan kembalikan
            return db.perangkatDesaQueries
                .selectAllPerangkatDesa()
                .executeAsList()
                .map { it.toEntity() }

        } catch (e: Exception) {
            println("User fetch failed: ${e.message}")
            null
        }
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