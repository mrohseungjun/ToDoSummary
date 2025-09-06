package com.example.todosummer.core.ui.components

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun AppFab(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit
) {
    FloatingActionButton(onClick = onClick) {
        Icon(imageVector = icon, contentDescription = contentDescription)
    }
}
