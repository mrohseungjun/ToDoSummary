plugins {
    id("todosummer.kmp.feature")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.ui)
            implementation(projects.core.common)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        androidMain.dependencies {
            // Android-specific ML / AI dependencies
            implementation("org.tensorflow:tensorflow-lite:2.17.0")
            // Google Generative AI Client
            implementation("com.google.ai.client.generativeai:generativeai:0.2.0")
            // Compose runtime
        // ML Kit GenAI API - 아직 공식 배포 전
            // implementation("com.google.mlkit:genai-summarization:0.1.0")
        }
    }
}
