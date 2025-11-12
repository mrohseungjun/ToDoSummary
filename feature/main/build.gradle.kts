plugins {
    id("todosummer.kmp.feature")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.ui)
            implementation(projects.core.common)
            implementation(projects.core.data)

            // Feature dependencies
            implementation(projects.feature.todo)
            implementation(projects.feature.calendar)
            implementation(projects.feature.statistics)
            implementation(projects.feature.settings)

            // Compose runtime
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            
            // Koin
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
        }
    }
}
