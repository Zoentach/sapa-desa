import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import kotlin.math.sign

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.compose.navigation)
            implementation(libs.compose.lifecycle.viewmodel)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.sqldelight.runtime)
            implementation(libs.kotlinx.datetime)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)

            implementation(libs.ktor.client.cio)
            implementation(libs.sqldelight.driver)

            // OpenCV & JavaCV lengkap

            implementation("org.bytedeco:javacv-platform:1.5.11")
            implementation("org.bytedeco:opencv-platform:4.10.0-1.5.11")

        }
    }
}


compose.desktop {
    application {
        mainClass = "id.go.tapselkab.sapa_desa.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Msi)
            packageName = "id.go.tapselkab.sapa_desa"
            packageVersion = "1.0.0"

            modules("java.sql")

        }

        // Lokasi temp SQLite JDBC
        //val sqliteTempDir = file("C:/sqlite-temp")

//        gradle.projectsEvaluated {
//            if (!sqliteTempDir.exists()) {
//                sqliteTempDir.mkdirs()
//                println("Folder temp untuk SQLite JDBC dibuat: $sqliteTempDir")
//            }
//        }
//
//        tasks.withType<JavaExec> {
//            jvmArgs("-Djava.io.tmpdir=${sqliteTempDir.absolutePath}")
//        }
    }
}


sqldelight {
    databases {
        create("sipature_db") {
            packageName.set("id.go.tapselkab.database")
        }
    }
}