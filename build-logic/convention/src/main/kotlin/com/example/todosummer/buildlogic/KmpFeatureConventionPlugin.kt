package com.example.todosummer.buildlogic

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

        with(pluginManager) {
            apply(libs.findPlugin("kotlinMultiplatform").get().get().pluginId)
            apply(libs.findPlugin("androidLibrary").get().get().pluginId)
            apply(libs.findPlugin("composeMultiplatform").get().get().pluginId)
            apply(libs.findPlugin("composeCompiler").get().get().pluginId)
        }

        extensions.configure<KotlinMultiplatformExtension> { configureKmp(this) }
        extensions.configure<LibraryExtension> { configureAndroid(this) }
        configureDependencies()
    }

    private fun Project.configureKmp(kmp: KotlinMultiplatformExtension) = kmp.apply {
        androidTarget()

        listOf(
            iosX64(),
            iosArm64(),
            iosSimulatorArm64()
        ).forEach { iosTarget ->
            iosTarget.binaries.framework {
                baseName = project.name
                isStatic = true
            }
        }
    }

    private fun Project.configureAndroid(android: LibraryExtension) = android.apply {
        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
        compileSdk = libs.findVersion("android-compileSdk").get().requiredVersion.toInt()
        defaultConfig {
            minSdk = libs.findVersion("android-minSdk").get().requiredVersion.toInt()
            namespace = "com.example.todosummer.feature.${project.name}"
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
    }

    private fun Project.configureDependencies() {
        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

        dependencies {
            "commonMainImplementation"(project(":core:ui"))
            "commonMainImplementation"(project(":core:domain"))
            "commonMainImplementation"(project(":core:data"))
            "commonMainImplementation"(project(":core:common"))

            // Compose Multiplatform
            "commonMainImplementation"(libs.findLibrary("compose-runtime").get())
            "commonMainImplementation"(libs.findLibrary("compose-foundation").get())
            "commonMainImplementation"(libs.findLibrary("compose-material3").get())
            "commonMainImplementation"(libs.findLibrary("compose-components-resources").get())
            // Android-only extras
            "androidMainImplementation"(libs.findLibrary("compose-material-icons-extended").get())
            "androidMainImplementation"(libs.findLibrary("compose-ui-tooling-preview").get())

            "commonMainImplementation"(libs.findLibrary("kotlinx-coroutines-core").get())
            "commonMainImplementation"(libs.findLibrary("kotlinx-datetime").get())

            "commonTestImplementation"(libs.findLibrary("kotlin-test").get())

            // Android debug tooling
            "debugImplementation"(libs.findLibrary("compose-ui-tooling").get())
        }
    }
}
