package id.go.tapselkab.sapa_desa.graph

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import id.go.tapselkab.sapa_desa.ui.graph.FlashScreen

val LocalNavigation = staticCompositionLocalOf<NavHostController> {
    error("no navController provide")
}

@Composable
fun DasGraph() {

    val navController = rememberNavController()

    CompositionLocalProvider(
        LocalNavigation provides navController,
    ) {


        NavHost(navController = navController, startDestination = FlashScreen) {
            flashScreen()
            login()
            credentialLogin()
            dashboard()
            perangkat()
            verifikasi()
        }
    }

}