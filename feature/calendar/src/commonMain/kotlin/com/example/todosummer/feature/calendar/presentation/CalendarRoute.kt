package com.example.todosummer.feature.calendar.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import kotlinx.datetime.LocalDate
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CalendarRoute(
    onAddTodoForDate: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: CalendarViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    
    CalendarScreen(
        state = state,
        onIntent = viewModel::onIntent,
        onAddTodoForDate = onAddTodoForDate,
        modifier = modifier
    )
}
