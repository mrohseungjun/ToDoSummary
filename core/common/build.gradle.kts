import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("todosummer.kmp.library")
    id("todosummer.kmp.android")
    id("todosummer.compose.multiplatform")
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "common"
            isStatic = true
        }
    }
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            api(libs.kotlinx.datetime)
            // For CompositionLocal and @Composable used in StringResources
            implementation(compose.runtime)
        }
        
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
    
}

android {
    namespace = "com.oseungjun.todosummer.core.common"
}
