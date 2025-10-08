package id.go.tapselkab.sapa_desa.utils.time

import id.go.tapselkab.sapa_desa.utils.time.DateManager.myLocalDate
import java.text.SimpleDateFormat
import java.util.*

object TimeManager {

    fun SaveAttendance() {

        val currentTimeMillis = getCurrentTimeMillis() ?: throw Exception("Gagal mendapatkan waktu")

        val calendar = Calendar.getInstance().apply {
            timeInMillis = currentTimeMillis
        }

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        when {
            // Jam 23.59 - 05.59
            hour == 23 || hour in 0..5 -> {
                throw Exception("Belum bisa melakukan absen di jam ini")
            }

            // Jam 06.00 - 11.59 (pagi)
            hour in 6..11 -> {
                morningAttendance(hour, minute)
            }

            // Jam 12.00 - 17.59 (sore)
            hour in 12..21 -> {
                afternoonAttendance(hour, minute)
            }

            // Di luar jam absensi yang diizinkan
            else -> {
                throw Exception("Waktu absen tidak valid: $hour:$minute")
            }
        }

    }

    fun morningAttendance(
        hours: Int, minute: Int
    ) {

    }

    fun afternoonAttendance(
        hours: Int, minute: Int
    ) {

    }

    fun isMorningOrAfternoon(): Int {
        val currentTimeMillis = getCurrentTimeMillis() ?: throw Exception("Gagal mendapatkan waktu")
        val calendar = Calendar.getInstance().apply {
            timeInMillis = currentTimeMillis
        }
        return calendar.get(Calendar.HOUR_OF_DAY)
    }

    fun isMorningOrAfternoon(millis: Long): Int {

        val calendar = Calendar.getInstance().apply {
            timeInMillis = millis
        }
        return calendar.get(Calendar.HOUR_OF_DAY)
    }

    fun getCurrentTimeMillis(): Long? {
        return try {
            System.currentTimeMillis()
        } catch (e: Exception) {
            throw e
        }
    }

    fun getTodayMillisAt(hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    fun getThisMonthMillisAt(month: Int, year: Int): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1) // untuk konsistensi, ambil dari awal bulan
            set(Calendar.MONTH, month - 1) // -1 karena bulan di Calendar dimulai dari 0
            set(Calendar.YEAR, year)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    fun formatMillisToHourMinute(millis: Long): String {
        val formatter = SimpleDateFormat("HH:mm", myLocalDate())
        return formatter.format(Date(millis))
    }

}