plugins {
    id("todosummer.kmp.feature")
}
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.ui)
            implementation(projects.core.domain)
            // Koin Compose Multiplatform for DI in UI layer
            implementation(libs.koin.compose)
            // JetBrains KMP Lifecycle ViewModel
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.koin.compose.viewmodel)
            // Compose helper to obtain ViewModel in Composables
            implementation(libs.androidx.lifecycle.viewmodelCompose)
        }
    }
}