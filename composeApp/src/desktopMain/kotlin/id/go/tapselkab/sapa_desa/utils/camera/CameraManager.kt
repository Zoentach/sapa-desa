package id.go.tapselkab.sapa_desa.utils.camera

import org.bytedeco.opencv.global.opencv_imgcodecs.imwrite
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_videoio.VideoCapture
import java.awt.image.BufferedImage
import org.bytedeco.opencv.global.opencv_imgproc.cvtColor
import org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2RGB
import java.awt.image.DataBufferByte

object CameraManager {
    private var camera: VideoCapture? = null
    private var currentIndex: Int? = null

    /**
     * Mencari index kamera yang tersedia dari 0 sampai [maxIndex].
     */
    fun findAvailableCameraIndex(maxIndex: Int = 2): Int? {
        for (index in 0..maxIndex) {
            val cam = VideoCapture(index)
            if (cam.isOpened) {
                cam.release()
                return index
            }
            cam.release()
        }
        return null
    }

    /**
     * Mengecek apakah ada kamera yang tersedia.
     */
    fun hasAnyCameraAvailable(maxIndex: Int = 2): Boolean {
        return findAvailableCameraIndex(maxIndex) != null
    }

    /**
     * Membuka kamera dengan index tertentu.
     * Secara default akan cari kamera yang tersedia jika tidak diberikan index.
     */
    fun startCapture(index: Int? = null): Boolean {
        releaseCamera()
        val cameraIndex = index ?: findAvailableCameraIndex() ?: return false
        val cam = VideoCapture(cameraIndex)
        return if (cam.isOpened) {
            camera = cam
            currentIndex = cameraIndex
            true
        } else {
            false
        }
    }

    /**
     * Membaca satu frame dari kamera untuk ditampilkan sebagai preview.
     * @return BufferedImage atau null jika gagal.
     */
    fun readFrame(): BufferedImage? {
        val cam = camera ?: return null
        val mat = Mat()
        if (cam.read(mat) && !mat.empty()) {
            return mat.toBufferedImage()
        }
        return null
    }

    /**
     * Mengambil satu frame dari kamera dan menyimpannya ke file.
     */
    suspend fun captureAndSave(outputPath: String): Boolean {
        val cam = camera ?: return false
        val frame = Mat()

        return if (cam.read(frame) && !frame.empty()) {
            imwrite(outputPath, frame)
        } else {
            false
        }
    }

    /**
     * Menutup kamera jika sedang terbuka.
     */
    fun releaseCamera() {
        camera?.release()
        camera = null
        currentIndex = null
    }

    /**
     * Mengecek apakah kamera sedang terbuka.
     */
    fun isCameraOpen(): Boolean = camera?.isOpened == true

    /**
     * Mendapatkan index kamera yang saat ini aktif.
     */
    fun getCurrentCameraIndex(): Int? = currentIndex
}


fun Mat.toBufferedImage(): BufferedImage {
    val rgbMat = Mat()
    // Ubah dari BGR (OpenCV default) ke RGB
    cvtColor(this, rgbMat, COLOR_BGR2RGB)

    val width = rgbMat.cols()
    val height = rgbMat.rows()
    val channels = rgbMat.channels()
    val sourceData = ByteArray(width * height * channels)
    rgbMat.data().get(sourceData)

    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

    var index = 0
    for (y in 0 until height) {
        for (x in 0 until width) {
            val r = sourceData[index].toInt() and 0xFF
            val g = sourceData[index + 1].toInt() and 0xFF
            val b = sourceData[index + 2].toInt() and 0xFF
            val pixel = (r shl 16) or (g shl 8) or b
            image.setRGB(x, y, pixel)
            index += 3
        }
    }

    return image
}


