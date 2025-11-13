package com.example.todosummer.splash

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.todosummer.R

@Composable
actual fun rememberSplashLogoPainter(): Painter? {
    return painterResource(id = R.drawable.ic_splash_logo)
}
