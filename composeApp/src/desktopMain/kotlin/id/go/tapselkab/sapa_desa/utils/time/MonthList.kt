package id.go.tapselkab.sapa_desa.utils.time

data class MonthEntity(
    val monthID: Int,
    val month: String
)

val monthList = listOf(
    MonthEntity(
        0, "-"
    ),
    MonthEntity(
        1, "Januari"
    ),
    MonthEntity(
        2, "Februari"
    ),
    MonthEntity(
        3, "Maret"
    ),
    MonthEntity(
        4, "April"
    ),
    MonthEntity(
        5, "Mei"
    ),
    MonthEntity(
        6, "Juni"
    ),
    MonthEntity(
        7, "Juli"
    ),
    MonthEntity(
        8, "Agustus"
    ),
    MonthEntity(
        9, "September"
    ),
    MonthEntity(
        10, "Oktober"
    ),
    MonthEntity(
        11, "November"
    ),
    MonthEntity(
        12, "Desember"
    ),
)