package com.example.todosummer

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.todosummer.core.common.localization.LanguageMode
import com.example.todosummer.core.common.localization.ProvideStringResources
import com.example.todosummer.core.common.localization.StringResources
import com.example.todosummer.core.common.localization.getStringResources
import com.example.todosummer.core.common.localization.stringResource
import com.example.todosummer.core.ui.settings.SettingsScreen
import com.example.todosummer.core.ui.theme.AppTheme
import com.example.todosummer.core.ui.theme.ThemeMode
import com.example.todosummer.feature.ai.presentation.SummaryScreen
import com.example.todosummer.feature.ai.presentation.SummaryViewModel
import com.example.todosummer.feature.todo.presentation.TodoListScreen
import com.example.todosummer.feature.todo.presentation.TodoViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 앱의 메인 화면
 */
@Composable
fun MainScreen(
    todoViewModel: TodoViewModel,
    summaryViewModel: SummaryViewModel,
    appState: AppState
) {
    val currentLanguage by appState.languageMode.collectAsState()
    val currentTheme by appState.themeMode.collectAsState()
    val stringResources = getStringResources(currentLanguage)
    
    var selectedTab by remember { mutableStateOf(Tab.TODO) }
    
    ProvideStringResources(stringResources) {
        AppTheme(themeMode = currentTheme) {
            Scaffold(
                bottomBar = {
                    BottomNavigation(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                }
            ) { paddingValues ->
                when (selectedTab) {
                    Tab.TODO -> TodoListScreen(
                        viewModel = todoViewModel,
                        modifier = Modifier.padding(paddingValues)
                    )
                    Tab.AI -> SummaryScreen(
                        viewModel = summaryViewModel,
                        modifier = Modifier.padding(paddingValues)
                    )
                    Tab.SETTINGS -> SettingsScreen(
                        currentLanguage = currentLanguage,
                        currentTheme = currentTheme,
                        onLanguageChange = { appState.setLanguageMode(it) },
                        onThemeChange = { appState.setThemeMode(it) },
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}

/**
 * 하단 네비게이션 바
 */
@Composable
private fun BottomNavigation(
    selectedTab: Tab,
    onTabSelected: (Tab) -> Unit
) {
    val strings = stringResource()
    
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = strings.todos) },
            label = { Text(strings.todos) },
            selected = selectedTab == Tab.TODO,
            onClick = { onTabSelected(Tab.TODO) }
        )
        
        NavigationBarItem(
            icon = { Icon(Icons.Default.Star, contentDescription = strings.aiSummaryTitle) },
            label = { Text(strings.aiSummaryTitle) },
            selected = selectedTab == Tab.AI,
            onClick = { onTabSelected(Tab.AI) }
        )
        
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = strings.settingsTitle) },
            label = { Text(strings.settingsTitle) },
            selected = selectedTab == Tab.SETTINGS,
            onClick = { onTabSelected(Tab.SETTINGS) }
        )
    }
}

/**
 * 앱 탭
 */
enum class Tab {
    TODO,
    AI,
    SETTINGS
}

/**
 * 앱 상태 관리 클래스
 */
class AppState {
    private val _languageMode = MutableStateFlow(LanguageMode.ENGLISH)
    val languageMode: StateFlow<LanguageMode> = _languageMode.asStateFlow()
    
    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()
    
    fun setLanguageMode(mode: LanguageMode) {
        _languageMode.value = mode
    }
    
    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }
}
