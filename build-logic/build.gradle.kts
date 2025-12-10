plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.android.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.roborazzi.gradle.plugin)
    // Needed to apply KSP plugin from convention plugins
    implementation(libs.ksp.gradle.plugin)
    implementation(libs.compose.compiler.gradle.plugin)
    implementation(libs.compose.gradle.plugin)
    implementation(libs.detekt.gradle.plugin)
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        freeCompilerArgs.add("-Xjvm-default=all")
    }
}

// Ensure we compile sources under convention/src/main/kotlin
sourceSets {
    named("main") {
        // Kotlin sources are treated as part of the Java source set here
        java.srcDir("convention/src/main/kotlin")
    }
}

gradlePlugin {
    plugins {
        register("todosummer.kmp.library") {
            id = "todosummer.kmp.library"
            implementationClass = "com.oseungjun.todosummer.primitive.KotlinMultiPlatformLibraryPlugin"
            displayName = "TodoSummer KMP Library Convention"
            description = "Applies Kotlin Multiplatform, Android Library, and Compose plugins with sane defaults"
        }
        register("todosummer.buildconfig") {
            id = "todosummer.buildconfig"
            implementationClass = "com.oseungjun.todosummer.primitive.BuildConfigPlugin"
            displayName = "TodoSummer BuildConfig Convention"
            description = "Generates BuildConfig for KMP modules and wires it into commonMain"
        }
        register("todosummer.kmp.feature") {
            id = "todosummer.kmp.feature"
            implementationClass = "com.oseungjun.todosummer.convention.KotlinMultiPlatformFeatureConventionPlugin"
            displayName = "TodoSummer KMP Feature Convention"
            description = "Applies common conventions for KMP feature modules"
        }
        register("todosummer.kmp.android") {
            id = "todosummer.kmp.android"
            implementationClass = "com.oseungjun.todosummer.primitive.KotlinMultiPlatformAndroidPlugin"
            displayName = "TodoSummer KMP Android Plugin"
            description = "Applies Android-specific configurations for KMP modules"
        }
        register("todosummer.kmp.ios") {
            id = "todosummer.kmp.ios"
            implementationClass = "com.oseungjun.todosummer.primitive.KotlinMultiPlatformiOSPlugin"
            displayName = "TodoSummer KMP iOS Plugin"
            description = "Applies iOS-specific configurations for KMP modules"
        }
        register("todosummer.compose.multiplatform") {
            id = "todosummer.compose.multiplatform"
            implementationClass = "com.oseungjun.todosummer.convention.ComposeMultiPlatformConventionPlugin"
            displayName = "TodoSummer Compose Multiplatform Convention"
            description = "Applies Compose Multiplatform with sane defaults"
        }
        register("todosummer.room.multiplatform") {
            id = "todosummer.room.multiplatform"
            implementationClass = "com.oseungjun.todosummer.primitive.RoomMultiplatformPlugin"
            displayName = "TodoSummer Room Multiplatform Convention"
            description = "Applies Room Multiplatform with KSP and proper dependencies"
        }
        register("todosummer.kmp.application") {
            id = "todosummer.kmp.application"
            implementationClass = "com.oseungjun.todosummer.convention.KotlinMultiPlatformApplicationConventionPlugin"
            displayName = "TodoSummer KMP Application Convention"
            description = "Applies KMP + Android application + Compose MPP defaults for app modules"
        }
    }
}
