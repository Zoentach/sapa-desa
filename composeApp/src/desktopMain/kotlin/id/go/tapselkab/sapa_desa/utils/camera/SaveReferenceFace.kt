package id.go.tapselkab.sapa_desa.utils.camera

import id.go.tapselkab.sapa_desa.utils.camera.CameraManager
import java.io.File


fun saveReferenceFace(folderName: String, fileName: String): Boolean {
    val refPath = System.getProperty("user.home") + "/.absensiApp/$folderName/"
    val dir = File(refPath)
    if (!dir.exists()) dir.mkdirs()

    val filePath = "$refPath/$fileName.jpg"
    return CameraManager.captureAndSave(filePath)
}