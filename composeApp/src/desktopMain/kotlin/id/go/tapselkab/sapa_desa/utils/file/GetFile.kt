package id.go.tapselkab.sapa_desa.utils.file

import java.io.File

fun getFile(
    folderName: String, fileName: String
): File {

    val refPath = System.getProperty("user.home") + "/.absensiApp/$folderName/"
    val imageFile = File("$refPath/$fileName")

    return imageFile

}