package id.go.tapselkab.sapa_desa.graph

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import id.go.tapselkab.sapa_desa.ui.FlashScreen
import id.go.tapselkab.sapa_desa.ui.perangkat.PerangkatScreen
import id.go.tapselkab.sapa_desa.ui.dashboard.DashboardScreen
import id.go.tapselkab.sapa_desa.ui.entity.PerangkatEntity
import id.go.tapselkab.sapa_desa.ui.graph.*
import id.go.tapselkab.sapa_desa.ui.login.CredentialLoginScreen
import id.go.tapselkab.sapa_desa.ui.login.LoginScreen
import id.go.tapselkab.sapa_desa.ui.verifikasi.VerifikasiAbsensiScreen


fun NavGraphBuilder.flashScreen() {
    composable<FlashScreen> {

        val navigate = LocalNavigation.current

        FlashScreen(
            onNavigateToDashboard = { navigate.navigate(Dashboard) },
            onNavigateToLogin = { navigate.navigate(Login) }
        )

    }
}

fun NavGraphBuilder.login() {
    composable<Login> {

        val navigate = LocalNavigation.current

        LoginScreen(
            onNavigateToDashboard = { navigate.navigate(Dashboard) },
            onNavigateToCredentialLogin = { navigate.navigate(CredentialLogin) }
        )
    }
}

fun NavGraphBuilder.credentialLogin() {

    composable<CredentialLogin> {

        val navigate = LocalNavigation.current

        CredentialLoginScreen(
            onNavigateToDashboard = {
                navigate.navigate(Dashboard)
            },
            onNavigateToLogin = {
                navigate.navigateUp()
            }
        )
    }

}

fun NavGraphBuilder.dashboard() {
    composable<Dashboard> {
        val navigate = LocalNavigation.current

        DashboardScreen(
            onNavigateToPerangkat = { perangkat ->
                navigate.navigate(
                    perangkat
                )
            },
            onNavigateToVerifikasiAbsensi = {
                navigate.navigate(
                    VerifikasiAbsensi
                )
            },
            onNavigateBack = {
                navigate.navigateUp()
            }
        )
    }
}


fun NavGraphBuilder.verifikasi() {
    composable<VerifikasiAbsensi> {

        val navigate = LocalNavigation.current

        VerifikasiAbsensiScreen()

    }
}

fun NavGraphBuilder.perangkat() {
    composable<PerangkatEntity> { backStackEntry ->

        val perangkat = backStackEntry.toRoute<PerangkatEntity>()
        val navigate = LocalNavigation.current

        PerangkatScreen(
            perangkat = perangkat,
            onNavigateBack = {
                navigate.navigateUp()
            }
        )
    }
}

