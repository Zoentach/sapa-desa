package id.go.tapselkab.sapa_desa.ui.dashboard

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import id.go.tapselkab.sapa_desa.core.repository.AuthRepository
import id.go.tapselkab.sapa_desa.core.repository.DashboardRepository
import id.go.tapselkab.sapa_desa.core.repository.VerifikasiAbsensiRepository
import id.go.tapselkab.sapa_desa.ui.entity.PerangkatEntity
import id.go.tapselkab.sapa_desa.ui.entity.UserEntity
import id.go.tapselkab.sapa_desa.ui.entity.VerifikasiAbsensiEntity

class DashboardViewModel(
    val repository: AuthRepository,
    val dashboarRepository: DashboardRepository,
    val verifikasiRepo: VerifikasiAbsensiRepository
) : ViewModel() {

    private val _perangkatDesa: MutableStateFlow<List<PerangkatEntity>> = MutableStateFlow(emptyList())
    val perangkatDesa = _perangkatDesa.asStateFlow()

    private val _verifikasiAbsensi: MutableStateFlow<VerifikasiAbsensiEntity?> = MutableStateFlow(null)
    val verifikasiAbsensi = _verifikasiAbsensi.asStateFlow()

    private val _currentUser: MutableStateFlow<UserEntity?> = MutableStateFlow(null)
    val currentUser = _currentUser.asStateFlow()

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun getCurrentUser() {
        coroutineScope.launch {
            try {
                val user = repository.getCurrentUser()

                val id: Int? = user?.id
                val token: String? = user?.token

                user?.let {
                    getPerangkatDesa()
                }


                if (id != null && token != null) {
                    getVerifikasiAbsensi(id = id, token = token)
                }


                _currentUser.value = user

            } catch (e: Exception) {
                print(e.message)
            }
        }
    }

    fun getPerangkatDesa() {
        coroutineScope.launch {
            try {
                _perangkatDesa.value = dashboarRepository.getPerangkatDesa() ?: emptyList()

                println(_perangkatDesa.value.toString())
            } catch (e: Exception) {
                print(e.message)
            }
        }
    }

    fun getVerifikasiAbsensi(id: Int, token: String) {
        coroutineScope.launch {
            try {
                _verifikasiAbsensi.value = verifikasiRepo.getVerifikasiAbsensi(userId = id.toLong(), token = token)
            } catch (e: Exception) {
                print(e.message)
            }
        }
    }

    fun logOut() {
        coroutineScope.launch {
            try {
                repository.logout()
            } catch (e: Exception) {
                print(e.message)
            }
        }
    }
}