package com.oseungjun.todosummer

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.oseungjun.todosummer.core.data.preferences.initializeDataStore
import com.oseungjun.todosummer.core.data.preferences.LanguagePreferences
import com.oseungjun.todosummer.core.data.notification.initializeNotificationScheduler
import java.util.Locale

class MainActivity : ComponentActivity() {
    
    // 알림 권한 요청 런처
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // 권한 허용/거부 결과 처리 (필요시 로그 또는 UI 피드백)
        println("[Permission] POST_NOTIFICATIONS granted=$isGranted")
    }
    
    override fun attachBaseContext(newBase: Context) {
        // 앱 내 언어 설정에 따라 Locale 적용
        val locale = LanguagePreferences.getLocale(newBase)
        Locale.setDefault(locale)
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // DataStore 초기화
        initializeDataStore(this)
        
        // NotificationScheduler 초기화
        initializeNotificationScheduler(this)
        
        // 알림 권한 요청 (Android 13+)
        requestNotificationPermission()
        
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
    
    /**
     * 알림 권한 요청 (Android 13+)
     */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // 이미 권한 있음
                    println("[Permission] POST_NOTIFICATIONS already granted")
                }
                ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) -> {
                    // 권한 설명이 필요한 경우에도 일단 요청
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    // 권한 요청
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}