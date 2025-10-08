package id.go.tapselkab.sapa_desa.utils


import org.bytedeco.opencv.global.opencv_core
import org.bytedeco.javacpp.Loader
import java.io.File
import java.lang.reflect.Field

fun setupOpenCVCache() {

    val cacheDir = File(System.getProperty("user.home"), ".javacpp/cache")

    if (!cacheDir.exists()) {
        println("Folder cache tidak ditemukan: $cacheDir")
        return
    }

    // Urutan prioritas loading DLL
    val priorityOrder = listOf(
        "vcruntime",        // library runtime Visual C++
        "msvcp",            // library C++ Microsoft
        "openblas",         // OpenBLAS dulu
        "opencv_core",      // Core OpenCV
        "opencv_imgproc",   // Modul image processing
        "opencv_"           // Modul OpenCV lainnya
    )

    println("Mencari DLL di $cacheDir ...")

    // Cari semua DLL dan urutkan sesuai prioritas
    val dllFiles = cacheDir.walkTopDown()
        .filter { it.isFile && it.extension.equals("dll", ignoreCase = true) }
        .sortedWith(compareBy { dll ->
            val name = dll.nameWithoutExtension.lowercase()
            priorityOrder.indexOfFirst { name.contains(it) }
                .takeIf { it >= 0 } ?: Int.MAX_VALUE
        })

    println("🔄 Memulai load DLL sesuai urutan prioritas...")

    dllFiles.forEach { dll ->
        try {
            System.load(dll.absolutePath)
            println("Loaded: ${dll.name}")
        } catch (e: UnsatisfiedLinkError) {
            println("Gagal load: ${dll.name} - ${e.message}")
        }
    }

    println("Proses load DLL selesai.")
}