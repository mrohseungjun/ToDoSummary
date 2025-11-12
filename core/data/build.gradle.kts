import org.jetbrains.kotlin.gradle.dsl.JvmTarget

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
        }
        
        androidMain.dependencies {
            // Room
            implementation(libs.room.runtime)

            // DataStore Android
            implementation(libs.androidx.datastore.preferences.android)

            // Koin Android
            implementation(libs.koin.android)
        }
        
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.koin.test)
        }
    }
}

android {
    namespace = "com.example.todosummer.core.data"
}
