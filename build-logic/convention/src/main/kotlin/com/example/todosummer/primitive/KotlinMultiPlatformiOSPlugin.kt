package com.example.todosummer.primitive

import com.example.todosummer.Arch
import com.example.todosummer.activeArch
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

class KotlinMultiPlatformiOSPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        extensions.configure<KotlinMultiplatformExtension> {
            when (activeArch) {
                Arch.ARM -> {
                    iosSimulatorArm64()
                    iosArm64()
                }
                Arch.ARM_SIMULATOR_DEBUG -> {
                    iosSimulatorArm64()
                }
                Arch.X86_64 -> iosX64()
                Arch.ALL -> {
                    iosArm64()
                    iosX64()
                    iosSimulatorArm64()
                }
            }
            targets.withType<KotlinNativeTarget>().configureEach {
                compilations.configureEach {
                    compileTaskProvider.configure {
                        compilerOptions {
                            freeCompilerArgs.add("-Xexport-kdoc")
                        }
                    }
                }
            }
        }
    }
}
