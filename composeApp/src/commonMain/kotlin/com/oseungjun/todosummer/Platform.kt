package com.oseungjun.todosummer

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform