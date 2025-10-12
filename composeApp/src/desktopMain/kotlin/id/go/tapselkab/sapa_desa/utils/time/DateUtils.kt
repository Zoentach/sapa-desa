package id.go.tapselkab.sapa_desa.utils.time

import kotlinx.datetime.TimeZone
import java.time.*
import java.time.format.DateTimeFormatter

object DateUtils {

    //  fun myLocalTimeZone() = TimeZone.of("Asia/Jakarta")
    fun myLocalZoneId(): ZoneId = ZoneId.of("Asia/Jakarta")

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    /** Mengubah timestamp (millis) ke string tanggal (Asia/Jakarta) */
    fun toDateString(millis: Long): String {
        val instant = Instant.ofEpochMilli(millis)
        val localDate = instant.atZone(myLocalZoneId()).toLocalDate()
        return dateFormatter.format(localDate)
    }

    /**  Mengubah timestamp (millis) ke string waktu (Asia/Jakarta) */
    fun toTimeString(millis: Long): String {

        val instant = Instant.ofEpochMilli(millis)
        val localTime = instant.atZone(myLocalZoneId()).toLocalTime()
        return timeFormatter.format(localTime)
    }

    /**Menggabungkan tanggal + jam menjadi timestamp (millis, Asia/Jakarta) */
    fun combineDateAndTimeToMillis(date: String?, time: String?): Long? {

        if (date.isNullOrBlank() || time.isNullOrBlank()) {
            return null
        }

        val localDate = LocalDate.parse(date, dateFormatter)
        val localTime = LocalTime.parse(time, timeFormatter)
        val localDateTime = LocalDateTime.of(localDate, localTime)
        return localDateTime.atZone(myLocalZoneId()).toInstant().toEpochMilli()
    }

    fun isWithinMonth(dateString: String, month: Int, year: Int): Boolean {
        return try {
            val date = LocalDate.parse(dateString)
            date.month.value == month && date.year == year
        } catch (e: Exception) {
            false
        }
    }
}