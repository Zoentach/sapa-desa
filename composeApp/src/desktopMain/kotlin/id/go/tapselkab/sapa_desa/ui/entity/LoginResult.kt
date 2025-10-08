package id.go.tapselkab.sapa_desa.ui.entity

data class LoginResult(
    val status: LoginStatus = LoginStatus.INITIAL,
    val message: String = ""
)

enum class LoginStatus {
    INITIAL, LOADING, SUCCESS, FAILED
}