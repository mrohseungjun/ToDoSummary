import java.util.Properties

plugins {
    id("todosummer.kmp.library")
    id("todosummer.kmp.android")
    id("todosummer.room.multiplatform")
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:domain"))
            implementation(project(":core:common"))
            implementation(project(":core:ui"))
            
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            
            // DataStore
            implementation(libs.androidx.datastore.preferences)
            
            // Koin
            implementation(libs.koin.core)
            
            // Ktor (HTTP Client)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
        }
        
        androidMain.dependencies {
            // Ktor Android Engine
            implementation(libs.ktor.client.cio)
            
            // Room
            implementation(libs.room.runtime)

            // DataStore Android
            implementation(libs.androidx.datastore.preferences.android)

            // Koin Android
            implementation(libs.koin.android)
        }
        
        iosMain.dependencies {
            // Ktor iOS Engine
            implementation(libs.ktor.client.darwin)
        }
        
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.koin.test)
        }
    }
}

// local.properties 에서 Gemini API 키를 읽어와 Android BuildConfig 로 전달
val localProps = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        load(file.inputStream())
    }
}
val geminiApiKey: String = localProps.getProperty("GEMINI_API_KEY") ?: ""

android {
    namespace = "com.oseungjun.todosummer.core.data"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField(
            "String",
            "GEMINI_API_KEY",
            "\"$geminiApiKey\""
        )
    }
}
