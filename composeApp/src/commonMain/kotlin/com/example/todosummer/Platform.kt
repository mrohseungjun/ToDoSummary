package com.example.todosummer

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform