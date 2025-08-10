package com.example.todosummer.convention

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

class KotlinMultiPlatformFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

        with(pluginManager) {
            // Use our convention plugins instead of direct AGP/KMP/Compose IDs
            apply("todosummer.kmp.library")
            apply("todosummer.kmp.android")
            apply("todosummer.compose.multiplatform")
        }

        extensions.configure<KotlinMultiplatformExtension> { configureKmp(this) }
        extensions.configure<LibraryExtension> { configureAndroid(this) }
        configureDependencies()
    }

    private fun Project.configureKmp(kmp: KotlinMultiplatformExtension) = kmp.apply {
        // Android target and common settings come from todosummer.kmp.library / kmp.android
        // Configure iOS frameworks for existing iOS targets
        targets.withType(KotlinNativeTarget::class.java).configureEach {
            // Only apply to Apple (iOS) targets
            if (konanTarget.family.isAppleFamily) {
                binaries.framework {
                    baseName = project.name
                    isStatic = true
                }
            }
        }
    }

    private fun Project.configureAndroid(android: LibraryExtension) = android.apply {
        // compileSdk/minSdk/compileOptions are handled by todosummer.kmp.android
        defaultConfig {
            namespace = "com.example.todosummer.feature.${project.name}"
        }
    }

    private fun Project.configureDependencies() {
        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

        dependencies {
            // Keep feature modules minimal: rely on core modules for common deps
            "commonMainImplementation"(project(":core:ui"))
            "commonMainImplementation"(project(":core:domain"))
            "commonMainImplementation"(project(":core:common"))

            "commonTestImplementation"(libs.findLibrary("kotlin-test").get())
        }
    }
}
