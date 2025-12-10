package com.oseungjun.todosummer.convention

import com.oseungjun.todosummer.libs
import com.oseungjun.todosummer.primitive.composeMultiplatformDependencies
import org.gradle.api.Plugin
import org.gradle.api.Project

class ComposeMultiPlatformConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply(libs.findPlugin("composeMultiplatform").get().get().pluginId)
            apply(libs.findPlugin("composeCompiler").get().get().pluginId)
        }

        composeMultiplatformDependencies()
    }
}
