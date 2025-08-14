package com.example.todosummer.primitive

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import com.example.todosummer.libs
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.compose.ComposeExtension

internal fun Project.composeMultiplatformDependencies() {
    val composeDependencies = extensions.getByType<ComposeExtension>().dependencies

    extensions.configure<KotlinMultiplatformExtension> {
        sourceSets.apply {
            commonMain {
                dependencies {
                    implementation(composeDependencies.runtime)
                    implementation(composeDependencies.foundation)
                    implementation(composeDependencies.ui)
                    implementation(composeDependencies.components.resources)
                    implementation(composeDependencies.components.uiToolingPreview)
                    implementation(libs.findLibrary("compose-material3").get())
                    implementation(libs.findLibrary("compose-material-icons-extended").get())
                }
            }
        }
    }

    dependencies {
        "debugImplementation"(libs.findLibrary("compose-ui-tooling").get())
        "debugImplementation"(libs.findLibrary("compose-ui-tooling-preview").get())
    }
}
