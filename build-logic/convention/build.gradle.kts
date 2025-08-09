plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        create("todosummer.kmp.library") {
            id = "todosummer.kmp.library"
            implementationClass = "com.example.todosummer.buildlogic.KmpLibraryConventionPlugin"
            displayName = "TodoSummer KMP Library Convention"
            description = "Applies Kotlin Multiplatform, Android Library, and Compose plugins with sane defaults"
        }
        create("todosummer.buildconfig") {
            id = "todosummer.buildconfig"
            implementationClass = "com.example.todosummer.buildlogic.BuildConfigConventionPlugin"
            displayName = "TodoSummer BuildConfig Convention"
            description = "Generates BuildConfig for KMP modules and wires it into commonMain"
        }
    }
}

dependencies {
    implementation("com.android.tools.build:gradle:8.7.3")
    // Match project Kotlin version for KMP DSL compatibility
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.0")
}
