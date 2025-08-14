package com.example.todosummer.primitive

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import com.google.devtools.ksp.gradle.KspExtension

/**
 * Room Multiplatform을 위한 컨벤션 플러그인
 * KSP와 Room 의존성을 자동으로 설정
 */
class RoomMultiplatformPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Version Catalog (libs) 참조 가져오기
            val libs = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")
            pluginManager.apply("com.google.devtools.ksp")
            
            // Kotlin Multiplatform 플러그인이 적용된 이후에만 안전하게 구성
            pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
                extensions.configure(KotlinMultiplatformExtension::class.java) {
                    sourceSets.apply {
                        commonMain.dependencies {
                            implementation(libs.findLibrary("room-runtime").get())
                            implementation(libs.findLibrary("androidx-sqlite-bundled").get())
                        }
                    }
                }
            }
            
            dependencies {
                add("kspCommonMainMetadata", libs.findLibrary("room-compiler").get())
                add("kspAndroid", libs.findLibrary("room-compiler").get())
                add("kspIosX64", libs.findLibrary("room-compiler").get())
                add("kspIosArm64", libs.findLibrary("room-compiler").get())
                add("kspIosSimulatorArm64", libs.findLibrary("room-compiler").get())
            }
            
            // Room KSP 인자 설정: iOS 지원을 위해 generateKotlin 필수
            extensions.configure(KspExtension::class.java) {
                arg("room.schemaLocation", "$projectDir/schemas")
                arg("room.incremental", "true")
                arg("room.generateKotlin", "true")
            }
        }
    }
}
