package id.go.tapselkab.sapa_desa.core.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import id.go.tapselkab.database.sipature_db
import java.io.File

interface DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

class DatabaseDriverFactoryImpl : DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {

        // Paksa load driver (hindari error DriverManager di Windows)
        Class.forName("org.sqlite.JDBC")

        // Pastikan SQLite ekstrak ke folder aman (bukan C:/Windows)
        val sqliteTemp = File(System.getProperty("user.home"), ".sqlite-temp")
        if (!sqliteTemp.exists()) sqliteTemp.mkdirs()
        System.setProperty("java.io.tmpdir", sqliteTemp.absolutePath)

        // Simpan database di user.home, bukan working dir
        val dbFile = File(System.getProperty("user.home"), "sipature.db")

        // Cetak lokasi file database untuk debugging
        println("Memeriksa file database di: ${dbFile.absolutePath}")

        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")

        if (!dbFile.exists()) {
            println("File database tidak ditemukan, membuat skema baru...")
            sipature_db.Schema.create(driver)
        } else {
            println("File database ditemukan, tidak membuat skema baru.")
        }

        return driver
    }
}
