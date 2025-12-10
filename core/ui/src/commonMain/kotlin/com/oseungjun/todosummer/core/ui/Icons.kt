package com.oseungjun.todosummer.core.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Material Icons를 중앙에서 관리하는 객체
 * feature 모듈에서 Icons를 쉽게 사용할 수 있도록 제공
 */
object AppIcons {
    val Add: ImageVector = Icons.Filled.Add
    val Check: ImageVector = Icons.Filled.Check
    val Delete: ImageVector = Icons.Filled.Delete
    val Edit: ImageVector = Icons.Filled.Edit
    val Settings: ImageVector = Icons.Filled.Settings
    val DarkMode: ImageVector = Icons.Filled.DarkMode
    val Language: ImageVector = Icons.Filled.Language
    val CheckCircle: ImageVector = Icons.Outlined.CheckCircle
    val Circle: ImageVector = Icons.Outlined.Circle
}
