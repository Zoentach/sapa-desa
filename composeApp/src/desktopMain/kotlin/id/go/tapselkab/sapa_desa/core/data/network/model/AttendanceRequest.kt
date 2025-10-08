package id.go.tapselkab.sapa_desa.core.data.network.model

@kotlinx.serialization.Serializable
data class AttendanceRequest(
    val user_id: Int,
    val kode_desa: String,
    val kode_kec: String,
    val date: Long,
    val attendance_morning: Long? = null,
    val attendance_afternoon: Long? = null,
    val late: Int? = null,
    val early: Int? = null,
    val image: String? = null
)