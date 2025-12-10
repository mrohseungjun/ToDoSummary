plugins {
    id("todosummer.kmp.library")
    id("todosummer.buildconfig")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // No external deps; exposes generated BuildConfig only
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.oseungjun.todosummer.core.buildconfig"
}
