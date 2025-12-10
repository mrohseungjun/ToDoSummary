package com.oseungjun.todosummer.di

import org.koin.core.context.startKoin

actual fun initKoinIfNeeded() {
    // iOS에서도 동일하게 중복 초기화를 무시하도록 처리
    runCatching {
        startKoin { modules(appModules()) }
    }
}
