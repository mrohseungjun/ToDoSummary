package com.oseungjun.todosummer.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.oseungjun.todosummer.core.common.localization.LanguageMode
import com.oseungjun.todosummer.core.common.localization.stringResource
import com.oseungjun.todosummer.core.ui.AppIcons
import com.oseungjun.todosummer.core.ui.theme.ThemeMode
import com.oseungjun.todosummer.core.ui.util.rememberRestartActivity
import com.oseungjun.todosummer.core.ui.util.rememberSaveLanguage

/**
 * 앱 설정 화면 (feature/settings)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentLanguage: LanguageMode,
    currentTheme: ThemeMode,
    onLanguageChange: (LanguageMode) -> Unit,
    onThemeChange: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = stringResource()
    val restartActivity = rememberRestartActivity()
    val saveLanguage = rememberSaveLanguage()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 헤더
        Text(
            text = strings.settingsTitle,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        // 언어 설정
        SettingSection(
            title = strings.settingsLanguage,
            icon = AppIcons.Language
        ) {
            ModernOption(
                selected = currentLanguage == LanguageMode.ENGLISH,
                onClick = { 
                    if (currentLanguage != LanguageMode.ENGLISH) {
                        saveLanguage(LanguageMode.ENGLISH)
                        onLanguageChange(LanguageMode.ENGLISH)
                        restartActivity()
                    }
                },
                text = "English"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            ModernOption(
                selected = currentLanguage == LanguageMode.KOREAN,
                onClick = { 
                    if (currentLanguage != LanguageMode.KOREAN) {
                        saveLanguage(LanguageMode.KOREAN)
                        onLanguageChange(LanguageMode.KOREAN)
                        restartActivity()
                    }
                },
                text = "한국어"
            )
        }
        
        // 테마 설정
        SettingSection(
            title = strings.settingsTheme,
            icon = AppIcons.DarkMode
        ) {
            ModernOption(
                selected = currentTheme == ThemeMode.LIGHT,
                onClick = { onThemeChange(ThemeMode.LIGHT) },
                text = strings.settingsThemeLight
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            ModernOption(
                selected = currentTheme == ThemeMode.DARK,
                onClick = { onThemeChange(ThemeMode.DARK) },
                text = strings.settingsThemeDark
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            ModernOption(
                selected = currentTheme == ThemeMode.SYSTEM,
                onClick = { onThemeChange(ThemeMode.SYSTEM) },
                text = strings.settingsThemeSystem
            )
        }
    }
}

@Composable
private fun SettingSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            content()
        }
    }
}

@Composable
private fun ModernOption(
    selected: Boolean,
    onClick: () -> Unit,
    text: String
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (selected) 4.dp else 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
            
            if (selected) {
                Icon(
                    imageVector = AppIcons.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
