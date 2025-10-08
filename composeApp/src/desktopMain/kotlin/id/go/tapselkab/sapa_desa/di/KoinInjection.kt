package id.go.tapselkab.sapa_desa.di

import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(databaseModule, repositoryModule, viewModelModule)
    }
}
