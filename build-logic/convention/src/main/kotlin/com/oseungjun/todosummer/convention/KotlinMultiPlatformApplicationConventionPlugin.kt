package com.oseungjun.todosummer.convention

import com.android.build.api.dsl.ApplicationExtension
import com.oseungjun.todosummer.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KotlinMultiPlatformApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            // Core plugins
            apply(libs.findPlugin("kotlinMultiplatform").get().get().pluginId)
            apply(libs.findPlugin("androidApplication").get().get().pluginId)
            // Convention plugins for Compose & Android defaults
            apply("todosummer.compose.multiplatform")
            apply("todosummer.kmp.android")
        }

        // Configure Kotlin Multiplatform targets (iOS frameworks only here)
        extensions.configure<KotlinMultiplatformExtension> {
            listOf(
                iosX64(),
                iosArm64(),
                iosSimulatorArm64()
            ).forEach { iosTarget ->
                iosTarget.binaries.framework {
                    // Keep a stable framework name for iOS app integration
                    baseName = if (project.name == "composeApp") "ComposeApp" else project.name
                    isStatic = true
                }
            }
        }

        // Android application configuration (only targetSdk; other defaults come from kmp.android)
        extensions.configure<ApplicationExtension> {
            defaultConfig {
                targetSdk = libs.findVersion("android-targetSdk").get().requiredVersion.toInt()
            }
        }
    }
}
