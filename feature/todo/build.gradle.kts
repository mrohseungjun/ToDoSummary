plugins {
    id("todosummer.kmp.feature")
}
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.ui)
        }
    }
}