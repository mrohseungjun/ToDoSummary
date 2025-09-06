package com.example.todosummer.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.todosummer.core.common.localization.ProvideStringResources
import com.example.todosummer.core.common.localization.getStringResources
import com.example.todosummer.core.common.localization.stringResource
import com.example.todosummer.core.ui.theme.AppTheme
import com.example.todosummer.core.ui.theme.ThemeMode
import com.example.todosummer.feature.ai.presentation.SummaryScreen
import com.example.todosummer.feature.ai.presentation.SummaryViewModel
import com.example.todosummer.feature.settings.SettingsScreen
import com.example.todosummer.feature.todo.presentation.TodoListRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Composable
fun MainScreen(
    summaryViewModel: SummaryViewModel,
    appState: MainAppState,
    modifier: Modifier = Modifier,
) {
    val currentLanguage by appState.languageMode.collectAsState()
    val currentTheme by appState.themeMode.collectAsState()
    val stringResources = getStringResources(currentLanguage)

    var selectedTab by remember { mutableStateOf(MainTab.TODO) }

    ProvideStringResources(stringResources) {
        AppTheme(themeMode = currentTheme) {
            Scaffold(
                bottomBar = {
                    MainBottomBar(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                }
            ) { paddingValues ->
                Surface(modifier = modifier.fillMaxSize()) {
                    when (selectedTab) {
                        MainTab.TODO -> TodoListRoute(
                            onOpenAISummary = { selectedTab = MainTab.AI },
                            modifier = Modifier.padding(paddingValues)
                        )
                        MainTab.AI -> SummaryScreen(
                            viewModel = summaryViewModel,
                            modifier = Modifier.padding(paddingValues)
                        )
                        MainTab.SETTINGS -> SettingsScreen(
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
}

@Composable
private fun MainBottomBar(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit
) {
    val strings = stringResource()

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = strings.todos) },
            label = { Text(strings.todos) },
            selected = selectedTab == MainTab.TODO,
            onClick = { onTabSelected(MainTab.TODO) }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Star, contentDescription = strings.aiSummaryTitle) },
            label = { Text(strings.aiSummaryTitle) },
            selected = selectedTab == MainTab.AI,
            onClick = { onTabSelected(MainTab.AI) }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = strings.settingsTitle) },
            label = { Text(strings.settingsTitle) },
            selected = selectedTab == MainTab.SETTINGS,
            onClick = { onTabSelected(MainTab.SETTINGS) }
        )
    }
}

enum class MainTab {
    TODO,
    AI,
    SETTINGS
}

class MainAppState {
    private val _languageMode = MutableStateFlow(com.example.todosummer.core.common.localization.LanguageMode.ENGLISH)
    val languageMode: StateFlow<com.example.todosummer.core.common.localization.LanguageMode> = _languageMode.asStateFlow()

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    fun setLanguageMode(mode: com.example.todosummer.core.common.localization.LanguageMode) {
        _languageMode.value = mode
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }
}
