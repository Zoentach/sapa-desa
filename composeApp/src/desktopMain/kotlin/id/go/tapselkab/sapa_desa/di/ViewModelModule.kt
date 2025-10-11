package id.go.tapselkab.sapa_desa.di

import id.go.tapselkab.sapa_desa.ui.dashboard.DashboardViewModel
import id.go.tapselkab.sapa_desa.ui.perangkat.AbsensiViewModel
import id.go.tapselkab.sapa_desa.ui.login.AuthViewModel
import id.go.tapselkab.sapa_desa.ui.verifikasi.VerifikasiViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { AuthViewModel(get()) }
    viewModel { AbsensiViewModel(get()) } // get() otomatis ambil absensiRepository
    viewModel { DashboardViewModel(get(), get(), get()) }
    viewModel { VerifikasiViewModel(get()) }
}