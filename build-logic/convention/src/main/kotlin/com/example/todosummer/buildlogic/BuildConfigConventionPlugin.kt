package com.example.todosummer.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import java.io.File

class BuildConfigConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val packageName = (findProperty("BUILD_CONFIG_PACKAGE") as String?)
            ?: "com.example.todosummer.core.buildconfig"
        val outputDir = layout.buildDirectory.dir("generated/buildconfig/commonMain").get().asFile

        val generateTask = tasks.register("generateBuildConfig") {
            outputs.dir(outputDir)
            doLast {
                val appName = (findProperty("APP_NAME") as String?) ?: "TodoSummer"
                val baseUrl = (findProperty("BASE_URL") as String?) ?: "https://example.com"
                val versionName = (findProperty("VERSION_NAME") as String?) ?: "1.0.0"
                val versionCode = (findProperty("VERSION_CODE") as String?) ?: "1"

                val packageDir = File(outputDir, packageName.replace('.', '/'))
                packageDir.mkdirs()
                val outFile = File(packageDir, "BuildConfig.kt")
                outFile.writeText(
                    """
                    package $packageName

                    object BuildConfig {
                        const val APP_NAME: String = "$appName"
                        const val BASE_URL: String = "$baseUrl"
                        const val VERSION_NAME: String = "$versionName"
                        const val VERSION_CODE: Int = $versionCode
                    }
                    """.trimIndent()
                )
            }
        }

        // Hook generated sources into KMP source set
        plugins.withId("org.jetbrains.kotlin.multiplatform") {
            val kmp = extensions.getByType<KotlinMultiplatformExtension>()
            kmp.sourceSets.getByName("commonMain").kotlin.srcDir(outputDir)
        }

        // Ensure generation runs before build
        tasks.named("build").configure { dependsOn(generateTask) }
    }
}
