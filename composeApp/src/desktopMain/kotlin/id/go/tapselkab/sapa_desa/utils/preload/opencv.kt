package id.go.tapselkab.sapa_desa.utils.preload

import org.bytedeco.opencv.global.opencv_core
import org.bytedeco.opencv.global.opencv_imgproc
import org.bytedeco.opencv.global.opencv_objdetect
import org.bytedeco.opencv.global.opencv_face
import org.bytedeco.opencv.helper.opencv_core.*
import org.bytedeco.javacpp.Loader

fun preloadOpenCv() {
    Loader.load(opencv_core::class.java)
    Loader.load(opencv_imgproc::class.java)
    Loader.load(opencv_objdetect::class.java)
    Loader.load(opencv_face::class.java)

}