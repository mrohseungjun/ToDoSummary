package com.oseungjun.todosummer.splash

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.painterResource
import todosummer.composeapp.generated.resources.Res
import todosummer.composeapp.generated.resources.splash_logo

@Composable
actual fun rememberSplashLogoPainter(): Painter? {
    return runCatching {
        painterResource(Res.drawable.splash_logo)
    }.getOrNull()
}
