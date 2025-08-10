plugins {
    id("todosummer.kmp.feature")
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            // Android-specific ML / AI dependencies
            implementation("org.tensorflow:tensorflow-lite:2.14.0")
            implementation("org.tensorflow:tensorflow-lite-gpu:2.14.0")
            implementation("org.tensorflow:tensorflow-lite-support:0.4.4")
            // Google Generative AI Client
            implementation("com.google.ai.client.generativeai:generativeai:0.2.0")
            // ML Kit GenAI API - 아직 공식 배포 전
            // implementation("com.google.mlkit:genai-summarization:0.1.0")
        }
    }
}
