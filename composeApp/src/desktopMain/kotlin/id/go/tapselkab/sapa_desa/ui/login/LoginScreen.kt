package id.go.tapselkab.sapa_desa.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.logo_app
import id.go.tapselkab.sapa_desa.ui.entity.LoginStatus
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = koinInject(),
    onNavigateToDashboard: () -> Unit,
    onNavigateToCredentialLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }


    val loginResult by viewModel.loginResult.collectAsState()

    LaunchedEffect(loginResult.status) {
        if (loginResult.status == LoginStatus.SUCCESS) {
            onNavigateToDashboard()
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Card(
            modifier = Modifier
                .width(540.dp)
                .padding(16.dp),
            elevation = 8.dp,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(Res.drawable.logo_app),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .width(500.dp)
                        .padding(end = 32.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Kata Sandi") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                        IconButton(
                            onClick = { showPassword = !showPassword }
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = if (showPassword) "Hide password" else "Show password"
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        viewModel.login(email, password)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Masuk")
                }

                Spacer(modifier = Modifier.height(20.dp))

                TextButton(
                    onClick = {
                        onNavigateToCredentialLogin()
                    },

                    ) {
                    Text("Akses jaringan tidak ada? Masuk dengan kredensial Admin")
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (loginResult.status == LoginStatus.FAILED) {
                    Text(
                        text = loginResult.message,
                        color = Color.Red
                    )
                }
            }
        }

        if (loginResult.status == LoginStatus.LOADING) {
            CircularProgressIndicator()
        }
    }
}