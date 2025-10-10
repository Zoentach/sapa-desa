package id.go.tapselkab.sapa_desa.utils.camera

import androidx.compose.runtime.*
import kotlinproject.composeapp.generated.resources.*
import org.jetbrains.compose.resources.*
import org.bytedeco.opencv.global.opencv_core.CV_32SC1
import org.bytedeco.opencv.global.opencv_face.*
import org.bytedeco.opencv.global.opencv_imgcodecs.*
import org.bytedeco.opencv.global.opencv_imgproc.*
import org.bytedeco.opencv.opencv_core.*
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier
import java.io.File
import kotlin.io.path.createTempFile

object FaceRecognizerManager {

    suspend fun loadHaarClassifier(): CascadeClassifier? {
        return try {
            val bytes = Res.readBytes("files/haarcascade_frontalface_default.xml")

            val tempFile = createTempFile(suffix = ".xml").toFile().apply {
                writeBytes(bytes)
                deleteOnExit()
            }

            CascadeClassifier(tempFile.absolutePath)
        } catch (e: Exception) {
            println("Gagal memuat Haar Cascade: ${e.message}")
            null
        }
    }

    suspend fun detectAndCropFace(inputPath: String, outputPath: String): Boolean {
        return try {
            val image = imread(inputPath)
            if (image.empty()) {
                println("Gambar tidak ditemukan di: $inputPath")
                return false
            }

            val gray = Mat()
            cvtColor(image, gray, COLOR_BGR2GRAY)

            val classifier = loadHaarClassifier()
            if (classifier == null || classifier.empty()) {
                println("Haar Cascade gagal dimuat.")
                return false
            }

            val faces = RectVector()
            classifier.detectMultiScale(gray, faces)

            if (faces.empty()) {
                println("Tidak ditemukan wajah pada: $inputPath")
                return false
            }

            val face = faces.get(0)
            val cropped = Mat(image, face)
            imwrite(outputPath, cropped)

            println("Wajah dicrop dan disimpan ke: $outputPath")
            true
        } catch (e: Exception) {
            println("Gagal mendeteksi dan memotong wajah: ${e.message}")
            false
        }
    }

    suspend fun cropAllFaces(faceRef: String, absensiFace: String):  Pair<Boolean, Double> {
        return try {
            val home = System.getProperty("user.home")
            val basePath = "$home/.absensiApp/$faceRef"

            val capturedPath = "$basePath/$absensiFace.jpg"
            val capturedCropPath = "$basePath/$absensiFace-cropped.jpg"
            val refPath = "$basePath/$faceRef.jpg"
            val refCropPath = "$basePath/$faceRef-cropped.jpg"

            val success1 = detectAndCropFace(capturedPath, capturedCropPath)
            val success2 = detectAndCropFace(refPath, refCropPath)

           if ( success1 && success2) {
               compareFaces(
                   referencePath = refCropPath,
                   capturedPath = capturedCropPath
               )
           }else {
               throw Exception ("Gagal melakukan Crop Wajah")
           }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun compareFaces(
        referencePath: String,
        capturedPath: String,
        threshold: Double = 60.0,
        maxConfidence: Double = 100.0,
    ): Pair<Boolean, Double> {
        return try {
            val refImage = imread(referencePath, IMREAD_GRAYSCALE)
            val capturedImage = imread(capturedPath, IMREAD_GRAYSCALE)

            if (refImage.empty() || capturedImage.empty()) {
                println("Salah satu gambar kosong.")
                return false to Double.MAX_VALUE
            }

            val labels = Mat(1, 1, CV_32SC1)
            labels.ptr(0).putInt(0)

            val images = MatVector(1)
            images.put(0, refImage)

            val recognizer: LBPHFaceRecognizer = LBPHFaceRecognizer.create()
            recognizer.train(images, labels)

            val predictedLabel = intArrayOf(-1)
            val confidence = DoubleArray(1)

            recognizer.predict(capturedImage, predictedLabel, confidence)

            // 💡 Konversi confidence menjadi persentase kemiripan
            val similarityPercent = ((1.0 - (confidence[0] / maxConfidence)) * 100.0)
                .coerceIn(0.0, 100.0)

            val isMatch = confidence[0] <= threshold

            println("Prediksi: label=${predictedLabel[0]}, confidence=${confidence[0]}")
            //(confidence[0] <= threshold) to confidence[0]
            isMatch to similarityPercent

        } catch (e: Exception) {
            println("Gagal membandingkan wajah: ${e.message}")
            false to Double.MAX_VALUE
        }
    }
}