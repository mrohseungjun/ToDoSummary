package com.oseungjun.todosummer.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * 스플래시 화면
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    logoPainter: Painter? = null
) {
    // 애니메이션 상태: 0 = 초기, 1 = 페이드인 완료, 2 = 페이드아웃 시작
    var animationPhase by remember { mutableStateOf(0) }
    
    // 페이드인/아웃 애니메이션
    val alphaAnim by animateFloatAsState(
        targetValue = when (animationPhase) {
            0 -> 0f      // 초기: 투명
            1 -> 1f      // 페이드인: 불투명
            else -> 0f   // 페이드아웃: 투명
        },
        animationSpec = tween(
            durationMillis = if (animationPhase == 2) 500 else 800,
            easing = FastOutSlowInEasing
        ),
        finishedListener = { 
            if (animationPhase == 2) {
                onSplashFinished()
            }
        }
    )
    
    // 스케일 애니메이션
    val scaleAnim by animateFloatAsState(
        targetValue = when (animationPhase) {
            0 -> 0.8f
            1 -> 1f
            else -> 1.1f  // 페이드아웃 시 살짝 확대
        },
        animationSpec = tween(
            durationMillis = if (animationPhase == 2) 500 else 800,
            easing = FastOutSlowInEasing
        )
    )
    
    LaunchedEffect(Unit) {
        animationPhase = 1  // 페이드인 시작
        delay(1800)         // 1.8초 대기
        animationPhase = 2  // 페이드아웃 시작
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.alpha(alphaAnim)
        ) {
            if (logoPainter != null) {
                Image(
                    painter = logoPainter,
                    contentDescription = null,
                    modifier = Modifier
                        .size((200 * scaleAnim).dp)
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            Text(
                text = "할 일을 스마트하게 관리하세요",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1D1F)
            )
        }
    }
}
