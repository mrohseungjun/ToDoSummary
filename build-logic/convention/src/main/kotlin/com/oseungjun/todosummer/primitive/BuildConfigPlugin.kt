package com.oseungjun.todosummer.primitive

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.DefaultTask
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import java.io.File

abstract class GenerateBuildConfigTask : DefaultTask() {
    @get:Input
    abstract val packageName: Property<String>

    @get:Input
    abstract val appName: Property<String>

    @get:Input
    abstract val baseUrl: Property<String>

    @get:Input
    abstract val versionName: Property<String>

    @get:Input
    abstract val versionCode: Property<String>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val outDir = outputDir.get().asFile
        val pkg = packageName.get()
        val pkgDir = File(outDir, pkg.replace('.', '/'))
        pkgDir.mkdirs()
        val outFile = File(pkgDir, "BuildConfig.kt")
        val versionCodeLiteral = versionCode.get().toIntOrNull() ?: 1
        outFile.writeText(
            """
            package $pkg

            object BuildConfig {
                const val APP_NAME: String = "${appName.get()}"
                const val BASE_URL: String = "${baseUrl.get()}"
                const val VERSION_NAME: String = "${versionName.get()}"
                const val VERSION_CODE: Int = $versionCodeLiteral
            }
            """.trimIndent()
        )
    }
}

class BuildConfigPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val generateTask = tasks.register("generateBuildConfig", GenerateBuildConfigTask::class.java) {
            packageName.set(providers.gradleProperty("BUILD_CONFIG_PACKAGE").orElse("com.oseungjun.todosummer.core.buildconfig"))
            appName.set(providers.gradleProperty("APP_NAME").orElse("TodoSummer"))
            baseUrl.set(providers.gradleProperty("BASE_URL").orElse("https://example.com"))
            versionName.set(providers.gradleProperty("VERSION_NAME").orElse("1.0.0"))
            versionCode.set(providers.gradleProperty("VERSION_CODE").orElse("1"))
            outputDir.set(layout.buildDirectory.dir("generated/buildconfig/commonMain"))
        }

        // Hook generated sources into KMP source set
        plugins.withId("org.jetbrains.kotlin.multiplatform") {
            val kmp = extensions.getByType<KotlinMultiplatformExtension>()
            kmp.sourceSets.getByName("commonMain").kotlin.srcDir(layout.buildDirectory.dir("generated/buildconfig/commonMain"))
        }

        // Ensure generation runs before build
        tasks.named("build").configure { dependsOn(generateTask) }
    }
}
