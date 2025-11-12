package com.example.todosummer.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todosummer.core.common.localization.ProvideStringResources
import com.example.todosummer.core.common.localization.getStringResources
import com.example.todosummer.core.common.localization.stringResource
import com.example.todosummer.core.ui.theme.AppTheme
import com.example.todosummer.core.ui.theme.ThemeMode
import com.example.todosummer.feature.calendar.presentation.CalendarRoute
import com.example.todosummer.feature.statistics.presentation.StatisticsRoute
import com.example.todosummer.feature.settings.SettingsScreen
import com.example.todosummer.feature.todo.presentation.TodoListRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.example.todosummer.core.data.preferences.UserPreferencesRepository
import org.koin.compose.koinInject

@Composable
fun MainScreen(
    appState: MainAppState,
    modifier: Modifier = Modifier,
) {
    val currentLanguage by appState.languageMode.collectAsState()
    val currentTheme by appState.themeMode.collectAsState()
    val stringResources = getStringResources(currentLanguage)

    var selectedTab by remember { mutableStateOf(MainTab.HOME) }

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
                        MainTab.HOME -> TodoListRoute(
                            onOpenStatistics = { selectedTab = MainTab.STATISTICS },
                            modifier = Modifier.padding(paddingValues)
                        )
                        MainTab.CALENDAR -> CalendarRoute(
                            modifier = Modifier.padding(paddingValues)
                        )
                        MainTab.STATISTICS -> StatisticsRoute(
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

@Preview
@Composable
fun MainScreenPreview() {
    // Preview용 임시 Repository (실제 앱에서는 Koin에서 주입)
    val preferencesRepository: UserPreferencesRepository = koinInject()
    val appState = remember { MainAppState(preferencesRepository) }
    MainScreen(
        appState = appState,
    )
}

@Composable
private fun MainBottomBar(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit
) {
    val strings = stringResource()

    NavigationBar(
        modifier = Modifier
            .navigationBarsPadding()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            ),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        val itemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = strings.navHome) },
            label = { Text(strings.navHome) },
            selected = selectedTab == MainTab.HOME,
            onClick = { onTabSelected(MainTab.HOME) },
            colors = itemColors,
            alwaysShowLabel = true
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.DateRange, contentDescription = strings.navCalendar) },
            label = { Text(strings.navCalendar) },
            selected = selectedTab == MainTab.CALENDAR,
            onClick = { onTabSelected(MainTab.CALENDAR) },
            colors = itemColors,
            alwaysShowLabel = true
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.BarChart, contentDescription = strings.statisticsTitle) },
            label = { Text(strings.statisticsTitle) },
            selected = selectedTab == MainTab.STATISTICS,
            onClick = { onTabSelected(MainTab.STATISTICS) },
            colors = itemColors,
            alwaysShowLabel = true
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = strings.settingsTitle) },
            label = { Text(strings.settingsTitle) },
            selected = selectedTab == MainTab.SETTINGS,
            onClick = { onTabSelected(MainTab.SETTINGS) },
            colors = itemColors,
            alwaysShowLabel = true
        )
    }
}

@Preview
@Composable
fun MainBottomBarPreview() {
    var selectedTab by remember { mutableStateOf(MainTab.HOME) }
    val stringResources = getStringResources(com.example.todosummer.core.common.localization.LanguageMode.ENGLISH)

    ProvideStringResources(stringResources) {
        AppTheme(themeMode = ThemeMode.LIGHT) {
            Surface {
                MainBottomBar(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }
        }
    }
}

enum class MainTab {
    HOME,
    CALENDAR,
    STATISTICS,
    SETTINGS
}

class MainAppState(
    private val preferencesRepository: UserPreferencesRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    val languageMode: StateFlow<com.example.todosummer.core.common.localization.LanguageMode> = 
        preferencesRepository.languageMode.stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = com.example.todosummer.core.common.localization.LanguageMode.KOREAN
        )

    val themeMode: StateFlow<ThemeMode> = 
        preferencesRepository.themeMode.stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = ThemeMode.DARK
        )

    fun setLanguageMode(mode: com.example.todosummer.core.common.localization.LanguageMode) {
        scope.launch {
            preferencesRepository.setLanguageMode(mode)
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        scope.launch {
            preferencesRepository.setThemeMode(mode)
        }
    }
}
