package id.go.tapselkab.sapa_desa.di

import app.cash.sqldelight.db.SqlDriver
import id.go.tapselkab.database.sipature_db
import id.go.tapselkab.sapa_desa.core.data.local.DatabaseDriverFactory
import id.go.tapselkab.sapa_desa.core.data.local.DatabaseDriverFactoryImpl
import id.go.tapselkab.sapa_desa.core.data.network.AuthApiService
import id.go.tapselkab.sapa_desa.core.data.network.AuthApiServiceImpl
import id.go.tapselkab.sapa_desa.core.data.token.FileTokenStorage
import id.go.tapselkab.sapa_desa.core.data.token.TokenStorage
import id.go.tapselkab.sapa_desa.core.repository.AttendanceRepository
import org.koin.dsl.module

val databaseModule = module {
    single<DatabaseDriverFactory> { DatabaseDriverFactoryImpl() }
    single<SqlDriver> { get<DatabaseDriverFactory>().createDriver() }
    single { sipature_db(get()) }
    single { get<sipature_db>().userQueries }
    single { get<sipature_db>().perangkatDesaQueries }


    // TokenStorage menggunakan FileTokenStorage
    single<TokenStorage> {
        FileTokenStorage()
    }

    // AuthApiService menggunakan implementasi AuthApiServiceImpl
    single<AuthApiService> {
        AuthApiServiceImpl()
    }

}