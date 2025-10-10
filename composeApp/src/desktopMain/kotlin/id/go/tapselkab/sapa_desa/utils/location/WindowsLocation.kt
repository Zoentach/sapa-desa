package id.go.tapselkab.sapa_desa.utils.location

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import androidx.compose.runtime.*
import androidx.compose.material3.*

/**
 * Mencoba ambil lokasi perangkat secara otomatis:
 * 1️⃣ Windows Location API (PowerShell)
 * 2️⃣ IP-based Geolocation (ipapi.co)
 * 3️⃣ Manual input (jika dua-duanya gagal)
 */
suspend fun getCurrentLocation(): Pair<Double, Double>? {
    // 1️⃣ Coba ambil dari Windows Location API
    val windowsLoc = getWindowsLocation()
    if (windowsLoc != null) {
        println("✅ Lokasi dari Windows API: $windowsLoc")
        return windowsLoc
    }

//    // 2️⃣ Coba ambil dari IP-based API
//    val ipLoc = getIpLocation()
//    if (ipLoc != null) {
//        println("🌐 Lokasi dari IP: $ipLoc")
//        return ipLoc
//    }

    // 3️⃣ Jika masih gagal, minta input manual
    println("⚠️ Gagal ambil lokasi otomatis. Minta input manual.")
    return null // nanti bisa diganti popup dialog input manual di UI
}

/**
 * 🔹 Coba ambil lokasi dari Windows Location API lewat PowerShell.
 * Perlu Location Service aktif di Windows.
 */
private suspend fun getWindowsLocation(): Pair<Double, Double>? = withContext(Dispatchers.IO) {
    try {
        val command = arrayOf(
            "powershell.exe",
            "-Command",
            """
            Add-Type -AssemblyName System.Device;
            \$GeoWatcher = New-Object System.Device.Location.GeoCoordinateWatcher;
            \$GeoWatcher.Start();
            while((\$GeoWatcher.Status -ne 'Ready') -and (\$GeoWatcher.Permission -ne 'Denied')) { Start-Sleep -Milliseconds 200 };
            if (\$GeoWatcher.Permission -eq 'Denied') { exit };
            \$coord = \$GeoWatcher.Position.Location;
            Write-Output ("\$($coord.Latitude),\$($coord.Longitude)");
            """
        )

        val process = ProcessBuilder(*command).redirectErrorStream(true).start()
        val output = process.inputStream.bufferedReader().use(BufferedReader::readText).trim()
        process.waitFor()

        if (output.contains(",")) {
            val (lat, lon) = output.split(",").map { it.toDouble() }
            return@withContext Pair(lat, lon)
        }
    } catch (e: Exception) {
        println(" Windows Location API gagal: ${e.message}")
    }
    null
}

/**
 * 🌐 Coba ambil lokasi dari layanan ipapi.co (berdasarkan IP publik)
 */
/*
private suspend fun getIpLocation(): Pair<Double, Double>? = withContext(Dispatchers.IO) {
    try {
        val url = URL("https://ipapi.co/json/")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        conn.connectTimeout = 5000
        conn.readTimeout = 5000

        if (conn.responseCode == 200) {
            val response = conn.inputStream.bufferedReader().readText()
            val lat = "\"latitude\":\\s*([-\\d.]+)".toRegex().find(response)?.groupValues?.get(1)?.toDoubleOrNull()
            val lon = "\"longitude\":\\s*([-\\d.]+)".toRegex().find(response)?.groupValues?.get(1)?.toDoubleOrNull()
            if (lat != null && lon != null) {
                return@withContext Pair(lat, lon)
            }
        }
    } catch (e: Exception) {
        println("❌ IP-based location gagal: ${e.message}")
    }
    null
}*/
