package id.go.tapselkab.sapa_desa.utils.macaddress

import java.net.NetworkInterface

//fun getMyMacAddress(): String? {
//    try {
//        val interfaces = NetworkInterface.getNetworkInterfaces().toList()
//
//        for (iface in interfaces) {
//            // Skip loopback & virtual interface
//            if (iface.isLoopback || iface.isVirtual || !iface.isUp) continue
//
//            val mac = iface.hardwareAddress ?: continue
//            // Valid MAC dengan panjang 6 byte
//            if (mac.size >= 6) {
//                return mac.joinToString(":") { "%02X".format(it) }
//            }
//        }
//    } catch (e: Exception) {
//        e.printStackTrace()
//    }
//    return null
//}

fun getMyMacAddress(): String? {
    return try {
        val interfaces = NetworkInterface.getNetworkInterfaces().toList()
        for (iface in interfaces) {
            // Lewati interface virtual, loopback, dan yang tidak aktif
            if (iface.isLoopback || iface.isVirtual || !iface.isUp) continue

            // Ambil hanya dari interface yang namanya mengandung "eth" atau "wlan" atau "en"
            val name = iface.name.lowercase()
            if (!name.contains("eth") && !name.contains("wlan") && !name.contains("en")) continue

            val mac = iface.hardwareAddress ?: continue
            if (mac.size >= 6) {
                return mac.joinToString(":") { "%02X".format(it) }
            }
        }
        null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
