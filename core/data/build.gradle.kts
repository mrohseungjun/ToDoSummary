import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("todosummer.kmp.library")
    id("todosummer.kmp.android")
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:domain"))
            implementation(project(":core:common"))
            
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            
            // Koin
            implementation(libs.koin.core)
        }
        
        androidMain.dependencies {
            // Room
            implementation(libs.room.runtime)
            implementation(libs.room.ktx)
            
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
