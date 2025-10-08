package id.go.tapselkab.sapa_desa.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import id.go.tapselkab.sapa_desa.core.repository.AuthRepository
import id.go.tapselkab.sapa_desa.ui.entity.LoginResult
import id.go.tapselkab.sapa_desa.ui.entity.LoginStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class AuthViewModel(val repository: AuthRepository) : ViewModel() {

    private val _loginResult = MutableStateFlow(LoginResult())
    val loginResult = _loginResult.asStateFlow()

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // UI states
    private val _credentialFilePath = MutableStateFlow("")
    val credentialFilePath: StateFlow<String> = _credentialFilePath.asStateFlow()

    fun onFileSelected(filePath: String) {
        _credentialFilePath.value = filePath
    }

    fun login(
        email: String,
        password: String
    ) {
        coroutineScope.launch {
            try {
                _loginResult.value = LoginResult(
                    status = LoginStatus.LOADING,
                    message = "Sedang loading..."
                )
                val login = repository.login(email, password)
                if (login) {
                    _loginResult.value = LoginResult(
                        status = LoginStatus.SUCCESS,
                        message = "Berhasil Login"
                    )
                } else {
                    _loginResult.value = LoginResult(
                        status = LoginStatus.FAILED,
                        message = "Gagal Login"
                    )
                }
            } catch (e: Exception) {
                _loginResult.value = LoginResult(
                    status = LoginStatus.FAILED,
                    message = e.message.orEmpty()
                )
            }
        }

    }

    fun checkSession() {
        coroutineScope.launch {
            try {
                _loginResult.value = LoginResult(
                    status = LoginStatus.LOADING,
                    message = "Sedang loading..."
                )
                val isLogin = repository.isLoggedIn()
                if (isLogin) {
                    _loginResult.value = LoginResult(
                        status = LoginStatus.SUCCESS,
                        message = "Berhasil login"
                    )
                } else {
                    _loginResult.value = LoginResult(
                        status = LoginStatus.FAILED,
                        message = "Gagal Login"
                    )
                }
            } catch (e: Exception) {
                _loginResult.value = LoginResult(
                    status = LoginStatus.FAILED,
                    message = e.message.orEmpty()
                )
            }
        }
    }


    suspend fun importAndLogin() {

        if (credentialFilePath.value.isBlank()) {
            return
        }
        _loginResult.value = LoginResult(
            status = LoginStatus.LOADING,
            message = "Sedang loading..."
        )
        try {
            val file = File(credentialFilePath.value)
            val success = repository.importDataFromJson(file)

            if (success) {
                // After successful import, check if user exists in DB
                val user = repository.getCurrentUser()
                if (user != null) {
                    _loginResult.value = LoginResult(
                        status = LoginStatus.SUCCESS,
                        message = "Berhasil Login"
                    )
                } else {
                    _loginResult.value = LoginResult(
                        status = LoginStatus.FAILED,
                        message = "Gagal Login: Pengguna Tidak Ditemukan"
                    )
                }
            } else {
                _loginResult.value = LoginResult(
                    status = LoginStatus.FAILED,
                    message = "Gagal Login"
                )
            }
        } catch (e: Exception) {
            _loginResult.value = LoginResult(
                status = LoginStatus.FAILED,
                message = "Gagal Login: $e"
            )
        }
    }
}

