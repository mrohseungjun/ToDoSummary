package com.oseungjun.todosummer.primitive

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KotlinMultiPlatformLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        pluginManager.apply("com.android.library")

        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
        val compileSdkInt = libs.findVersion("android-compileSdk").get().requiredVersion.toString().toInt()
        val minSdkInt = libs.findVersion("android-minSdk").get().requiredVersion.toString().toInt()

        extensions.configure<KotlinMultiplatformExtension> {
            androidTarget()

            iosX64()
            iosArm64()
            iosSimulatorArm64()
        }

        extensions.configure<LibraryExtension> {
            this.compileSdk = compileSdkInt
            defaultConfig {
                minSdk = minSdkInt
            }
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }
        }
    }
}
