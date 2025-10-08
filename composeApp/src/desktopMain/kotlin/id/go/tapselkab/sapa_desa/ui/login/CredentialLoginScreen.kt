package id.go.tapselkab.sapa_desa.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import id.go.tapselkab.sapa_desa.ui.entity.LoginStatus
import androidx.compose.ui.graphics.Color

@Composable
fun CredentialLoginScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = koinInject()
) {
    val credentialFilePath by viewModel.credentialFilePath.collectAsState()

    val scope = rememberCoroutineScope()

    // Monitor login status
    val loginResult by viewModel.loginResult.collectAsState()

    LaunchedEffect(loginResult.status) {
        if (loginResult.status == LoginStatus.SUCCESS) {
            onNavigateToDashboard()
        }
    }


    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
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
                Text(
                    text = "Hubungi Admin Untuk Mendapatkan Kredensial",
                    fontSize = 36.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = credentialFilePath,
                        onValueChange = {}, // Read-only from UI
                        label = { Text("Kredensial") },
                        enabled = false,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            // Dialog untuk memilih file JSON
                            val fileDialog = FileDialog(null as Frame?, "Pilih file JSON", FileDialog.LOAD)
                            fileDialog.file = "*.json"
                            fileDialog.isVisible = true

                            fileDialog.directory?.let { dir ->
                                fileDialog.file?.let { file ->
                                    val filePath = File(dir, file).absolutePath
                                    viewModel.onFileSelected(filePath)
                                }
                            }
                        }
                    ) {
                        Text("Pilih")
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        scope.launch {
                            viewModel.importAndLogin()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = loginResult.status != LoginStatus.LOADING
                ) {

                    Text("Masuk")

                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("atau")
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        onNavigateToLogin()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = loginResult.status != LoginStatus.LOADING
                ) {

                    Text("Kembali")

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