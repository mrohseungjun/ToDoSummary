package com.example.todosummer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.example.todosummer.core.data.preferences.initializeDataStore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // DataStore 초기화
        initializeDataStore(this)
        
        enableEdgeToEdge()

        setContent {
            val view = LocalView.current
            val darkTheme = isSystemInDarkTheme()
            
            SideEffect {
                val window = (view.context as ComponentActivity).window
                val insetsController = WindowCompat.getInsetsController(window, view)
                
                // 상태바 아이콘 색상 설정 (라이트 모드: 어두운 아이콘, 다크 모드: 밝은 아이콘)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                
                // 네비게이션 바 아이콘 색상 설정
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
            
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}