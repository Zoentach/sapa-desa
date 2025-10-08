package id.go.tapselkab.sapa_desa

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import id.go.tapselkab.sapa_desa.di.initKoin
import org.bytedeco.javacpp.Loader
import org.bytedeco.opencv.global.opencv_core
import id.go.tapselkab.sapa_desa.utils.preload.preloadOpenCv

//import nu.pattern.OpenCV


fun main() {
    preloadOpenCv()
    initKoin()

    application {


        val windowState = rememberWindowState(
            placement = WindowPlacement.Maximized,
        )


        Window(
            onCloseRequest = ::exitApplication,
            title = "SiUpdate",
            undecorated = false,
            state = windowState
        ) {

            App()
        }
    }
}