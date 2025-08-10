plugins {
    id("todosummer.kmp.library")
    id("todosummer.kmp.android")
    id("todosummer.compose.multiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Compose core deps는 convention에서 주입됨(runtime/foundation/ui/resources)
            implementation(compose.material3)
        }
        androidMain.dependencies {
            // material-icons-extended를 libs.versions.toml에서 정의한 의존성으로 변경
            implementation(libs.compose.material.icons.extended)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.example.todosummer.core.designsystem"
}
