package id.go.tapselkab.sapa_desa.ui

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.logo_app
import id.go.tapselkab.sapa_desa.ui.entity.LoginStatus
import id.go.tapselkab.sapa_desa.ui.login.AuthViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@Composable
fun FlashScreen(
    viewModel: AuthViewModel = koinInject(),
    onNavigateToLogin: () -> Unit,
    onNavigateToDashboard: () -> Unit
) {

    onNavigateToLogin()

    val loginResult by viewModel.loginResult.collectAsState()

    // Cek token saat pertama kali masuk
    LaunchedEffect(Unit) {
        viewModel.checkSession()
    }

    LaunchedEffect(loginResult.status) {
        when (loginResult.status) {
            LoginStatus.SUCCESS -> onNavigateToDashboard()
            LoginStatus.FAILED -> onNavigateToLogin()
            else -> {} // Tunggu dulu
        }
    }

//    val infiniteTransition = rememberInfiniteTransition()
//    val animateColor by infiniteTransition.animateColor(
//        initialValue = Color.Red,
//        targetValue = Color.Blue,
//        animationSpec = infiniteRepeatable(
//            animation = tween(durationMillis = 1000, easing = LinearEasing),
//            repeatMode = RepeatMode.Restart
//        )
//    )

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(Res.drawable.logo_app),
            contentDescription = "Splash Logo",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = loginResult.message,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}