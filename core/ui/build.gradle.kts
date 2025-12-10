import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    id("todosummer.kmp.library")
    id("todosummer.kmp.android")
    id("todosummer.compose.multiplatform")
}

kotlin {

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:common"))
            
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            // MPP-compatible Material Icons Extended (exposed to consumers)
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.materialIconsExtended)
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.oseungjun.todosummer.core.ui"
}
