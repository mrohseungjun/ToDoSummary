package com.example.todosummer.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.todosummer.core.common.localization.LanguageMode
import com.example.todosummer.core.common.localization.stringResource
import com.example.todosummer.core.ui.AppIcons
import com.example.todosummer.core.ui.components.AppTopBar
import com.example.todosummer.core.ui.theme.Dimens
import com.example.todosummer.core.ui.theme.ThemeMode

/**
 * 앱 설정 화면 (feature/settings)
 */
@Composable
fun SettingsScreen(
    currentLanguage: LanguageMode,
    currentTheme: ThemeMode,
    onLanguageChange: (LanguageMode) -> Unit,
    onThemeChange: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = stringResource()

    Scaffold(
        topBar = { AppTopBar(title = strings.settingsTitle) }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(Dimens.spacing16)
                .verticalScroll(rememberScrollState())
        ) {
            // 언어 설정
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(Dimens.spacing16)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = AppIcons.Language,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.width(Dimens.spacing8))

                        Text(
                            text = strings.settingsLanguage,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(Dimens.spacing16))

                    // 영어 옵션
                    LanguageOption(
                        selected = currentLanguage == LanguageMode.ENGLISH,
                        onClick = { onLanguageChange(LanguageMode.ENGLISH) },
                        text = "English"
                    )

                    // 한국어 옵션
                    LanguageOption(
                        selected = currentLanguage == LanguageMode.KOREAN,
                        onClick = { onLanguageChange(LanguageMode.KOREAN) },
                        text = "한국어"
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.spacing16))

            // 테마 설정
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(Dimens.spacing16)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = AppIcons.DarkMode,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.width(Dimens.spacing8))

                        Text(
                            text = strings.settingsTheme,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(Dimens.spacing16))

                    // 라이트 모드 옵션
                    ThemeOption(
                        selected = currentTheme == ThemeMode.LIGHT,
                        onClick = { onThemeChange(ThemeMode.LIGHT) },
                        text = strings.settingsThemeLight
                    )

                    // 다크 모드 옵션
                    ThemeOption(
                        selected = currentTheme == ThemeMode.DARK,
                        onClick = { onThemeChange(ThemeMode.DARK) },
                        text = strings.settingsThemeDark
                    )

                    // 시스템 모드 옵션
                    ThemeOption(
                        selected = currentTheme == ThemeMode.SYSTEM,
                        onClick = { onThemeChange(ThemeMode.SYSTEM) },
                        text = strings.settingsThemeSystem
                    )
                }
            }
        }
    }
}

/**
 * 언어 옵션 항목
 */
@Composable
private fun LanguageOption(
    selected: Boolean,
    onClick: () -> Unit,
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.spacing8),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )

        Spacer(modifier = Modifier.width(Dimens.spacing8))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

/**
 * 테마 옵션 항목
 */
@Composable
private fun ThemeOption(
    selected: Boolean,
    onClick: () -> Unit,
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.spacing8),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )

        Spacer(modifier = Modifier.width(Dimens.spacing8))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
