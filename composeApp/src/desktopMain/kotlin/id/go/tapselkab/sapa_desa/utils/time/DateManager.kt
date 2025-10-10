package id.go.tapselkab.sapa_desa.utils.time


import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import java.text.SimpleDateFormat
import java.util.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import id.go.tapselkab.sapa_desa.utils.model.DayOfMonth


object DateManager {


    fun getMillisAt0815(): Long {
        return LocalDate.now()
            .atTime(LocalTime.of(8, 0)) // jam 06:00
            .atZone(myLocalZoneId())
            .toInstant()
            .toEpochMilli()
    }

    fun getMillisAt1615(): Long {
        return LocalDate.now()
            .atTime(LocalTime.of(16, 0)) // jam 06:00
            .atZone(myLocalZoneId())
            .toInstant()
            .toEpochMilli()
    }

    fun Long.thisDay(): String {
        return try {
            val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", myLocalDate())
            dateFormat.format(this)
        } catch (e: Exception) {
            throw e
        }
    }

    fun Long.thisMonth(): String {
        return try {
            val dateFormat = SimpleDateFormat("MMMM yyyy", myLocalDate())
            dateFormat.format(this)
        } catch (e: Exception) {
            throw e
        }
    }

    fun thisMonth(): String {
        return try {
            val dateFormat = SimpleDateFormat("MMMM yyyy", myLocalDate())
            dateFormat.format(myLocalDate())
        } catch (e: Exception) {
            throw e
        }
    }

    fun getDaysInMonthMillis(): List<DayOfMonth> {
        val myLocalZoneId = { ZoneId.systemDefault() } // Asumsi fungsi ini sudah ada
        val today = LocalDate.now()
        val firstDayOfMonth = today.withDayOfMonth(1)
        //val lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth())
        val daysList = mutableListOf<DayOfMonth>()

        var currentDay = firstDayOfMonth
        while (!currentDay.isAfter(today)) {
            val timeMillis = currentDay
                .atTime(LocalTime.of(8, 0)) // Waktu 08:00
                .atZone(myLocalZoneId())
                .toInstant()
                .toEpochMilli()

            val isWeekend = currentDay.dayOfWeek == DayOfWeek.SATURDAY || currentDay.dayOfWeek == DayOfWeek.SUNDAY

            daysList.add(DayOfMonth(timeMillis, isWeekend))
            currentDay = currentDay.plus(1, ChronoUnit.DAYS)
        }

        return daysList
    }

    fun LocalDate.thisDay(): String {
        return try {
            val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", myLocalDate())
            val date = Date()
            dateFormat.format(date)
        } catch (e: Exception) {
            throw e
        }
    }

    fun formatMillisToDate(millis: Long?): String {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", myLocalDate())
        if (millis == null) {
            return "Tidak Diketahui"
        } else {
            val instant = Instant.fromEpochMilliseconds(millis)
            return dateFormat.format(Date.from(instant.toJavaInstant()))
        }
    }

    fun myLocalDate() = Locale.Builder()
        .setLanguage("id")
        .setRegion("ID")
        .build() // Menggunakan lokal Indonesia

    fun oneMonthPeriod() = DateTimePeriod(months = 1)
    fun oneSecondPeriod() = DateTimePeriod(seconds = 1)
    fun myLocalTimeZone() = TimeZone.of("Asia/Jakarta")
    fun myLocalZoneId() = ZoneId.of("Asia/Jakarta")

    fun startOfMonth(year: Int, month: Int) = LocalDate(year, month, 1)
        .atStartOfDayIn(myLocalTimeZone())
        .toEpochMilliseconds()

    fun endOfMonth(year: Int, month: Int) = LocalDate(year, month, 1)
        .atStartOfDayIn(myLocalTimeZone())
        .plus(oneMonthPeriod(), myLocalTimeZone())
        .minus(oneSecondPeriod(), myLocalTimeZone())
        .toEpochMilliseconds()

}


