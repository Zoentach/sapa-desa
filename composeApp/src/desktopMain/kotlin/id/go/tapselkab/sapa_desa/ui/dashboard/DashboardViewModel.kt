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
import id.go.tapselkab.sapa_desa.ui.entity.PerangkatEntity
import id.go.tapselkab.sapa_desa.ui.entity.UserEntity
import id.go.tapselkab.sapa_desa.utils.macaddress.getMyMacAddress

class DashboardViewModel(
    val repository: AuthRepository,
    val dashboarRepository: DashboardRepository
) : ViewModel() {

    private val _perangkatDesa: MutableStateFlow<List<PerangkatEntity>> = MutableStateFlow(emptyList())
    val perangkatDesa = _perangkatDesa.asStateFlow()

    private val _currentUser: MutableStateFlow<UserEntity?> = MutableStateFlow(null)
    val currentUser = _currentUser.asStateFlow()

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _macAddress: MutableStateFlow<String?> = MutableStateFlow(null)
    val macAddress = _macAddress.asStateFlow()

    fun getMacAddress() {
        try {
            _macAddress.value = getMyMacAddress()
        } catch (e: Exception) {
            print(e.message)
        }
    }

    fun getCurrentUser() {
        coroutineScope.launch {
            try {
                val user = repository.getCurrentUser()
                user?.kodeDesa?.let {
                    getPerangkatDesa(it)
                }

                _currentUser.value = user

            } catch (e: Exception) {
                print(e.message)
            }
        }
    }

    fun getPerangkatDesa(kodeDesa: String) {
        coroutineScope.launch {
            try {
                _perangkatDesa.value = dashboarRepository.getPerangkatDesa(kodeDesa) ?: emptyList()
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


    fun updateMacAddress(macAddress: String) {
        coroutineScope.launch {
            try {
                val updated = repository.updateMacAddress(macAddress)

                if (updated == true) {
                    getCurrentUser()
                }
            } catch (e: Exception) {
                print(e.message)
            }
        }
    }
    
}