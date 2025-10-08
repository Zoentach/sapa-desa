package id.go.tapselkab.sapa_desa.di

import id.go.tapselkab.sapa_desa.core.DatabaseImporter
import id.go.tapselkab.sapa_desa.core.repository.AttendanceRepository
import id.go.tapselkab.sapa_desa.core.repository.AuthRepository
import id.go.tapselkab.sapa_desa.core.repository.DashboardRepository
import org.koin.dsl.module

val repositoryModule = module {

    single { DatabaseImporter(get(),get(),get())}
    single { AttendanceRepository(get(), get(), get()) }
    single { AuthRepository(get(), get(), get()) }
    single { DashboardRepository(get(), get()) }
}