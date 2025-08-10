plugins {
    `kotlin-dsl`
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation(libs.findLibrary("android-gradle-plugin").get())
    implementation(libs.findLibrary("kotlin-gradle-plugin").get())
    implementation(libs.findLibrary("roborazzi-gradle-plugin").get())
    compileOnly(libs.findLibrary("compose-compiler-gradle-plugin").get())
    compileOnly(libs.findLibrary("compose-gradle-plugin").get())
    compileOnly(libs.findLibrary("detekt-gradle-plugin").get())
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        freeCompilerArgs.add("-Xjvm-default=all")
    }
}

gradlePlugin {
    plugins {
        create("todosummer.kmp.library") {
            id = "todosummer.kmp.library"
            implementationClass = "com.example.todosummer.primitive.KotlinMultiPlatformLibraryPlugin"
            displayName = "TodoSummer KMP Library Convention"
            description = "Applies Kotlin Multiplatform, Android Library, and Compose plugins with sane defaults"
        }
        create("todosummer.buildconfig") {
            id = "todosummer.buildconfig"
            implementationClass = "com.example.todosummer.primitive.BuildConfigPlugin"
            displayName = "TodoSummer BuildConfig Convention"
            description = "Generates BuildConfig for KMP modules and wires it into commonMain"
        }
        create("todosummer.kmp.feature") {
            id = "todosummer.kmp.feature"
            implementationClass = "com.example.todosummer.convention.KotlinMultiPlatformFeatureConventionPlugin"
            displayName = "TodoSummer KMP Feature Convention"
            description = "Applies common conventions for KMP feature modules"
        }
        create("todosummer.kmp.android") {
            id = "todosummer.kmp.android"
            implementationClass = "com.example.todosummer.primitive.KotlinMultiPlatformAndroidPlugin"
            displayName = "TodoSummer KMP Android Plugin"
            description = "Applies Android-specific configurations for KMP modules"
        }
        create("todosummer.kmp.ios") {
            id = "todosummer.kmp.ios"
            implementationClass = "com.example.todosummer.primitive.KotlinMultiPlatformiOSPlugin"
            displayName = "TodoSummer KMP iOS Plugin"
            description = "Applies iOS-specific configurations for KMP modules"
        }
        create("todosummer.compose.multiplatform") {
            id = "todosummer.compose.multiplatform"
            implementationClass = "com.example.todosummer.convention.ComposeMultiPlatformConventionPlugin"
            displayName = "TodoSummer Compose Multiplatform Convention"
            description = "Applies Compose Multiplatform configurations and dependencies"
        }
        create("todosummer.kmp.application") {
            id = "todosummer.kmp.application"
            implementationClass = "com.example.todosummer.convention.KotlinMultiPlatformApplicationConventionPlugin"
            displayName = "TodoSummer KMP Application Convention"
            description = "Applies KMP + Android application + Compose MPP defaults for app modules"
        }
    }
}
