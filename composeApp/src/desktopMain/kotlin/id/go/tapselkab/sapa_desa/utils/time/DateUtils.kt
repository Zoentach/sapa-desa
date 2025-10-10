package id.go.tapselkab.sapa_desa.utils.time

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.LocalTime

object DateUtils {

    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    /**
     * Konversi dari timeMillis ke string format "YYYY-MM-DD"
     * Cocok untuk disimpan ke MySQL (kolom DATE) atau dikirim ke server Laravel.
     */
    fun toDateString(timeMillis: Long): String {
        val instant = Instant.ofEpochMilli(timeMillis)
        val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
        return localDate.format(dateFormatter)
    }

    /**
     * Konversi dari timeMillis ke string format "HH:mm:ss"
     * Cocok untuk menyimpan jam absensi pagi/sore.
     */
    fun toTimeString(timeMillis: Long): String {
        val instant = Instant.ofEpochMilli(timeMillis)
        val localTime = instant.atZone(ZoneId.systemDefault()).toLocalTime()
        return localTime.format(timeFormatter)
    }

    /**
     * Konversi dari string tanggal (format "YYYY-MM-DD") ke timeMillis.
     * Berguna untuk parsing data dari server atau database.
     */
    fun fromDateString(dateString: String): Long {
        val localDate = LocalDate.parse(dateString, dateFormatter)
        return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    /**
     * Konversi dari string waktu (format "HH:mm:ss") ke timeMillis pada hari ini.
     */
    fun fromTimeString(timeString: String): Long {
        val localTime = LocalTime.parse(timeString, timeFormatter)
        val today = LocalDate.now()
        val instant = today.atTime(localTime).atZone(ZoneId.systemDefault()).toInstant()
        return instant.toEpochMilli()
    }
}