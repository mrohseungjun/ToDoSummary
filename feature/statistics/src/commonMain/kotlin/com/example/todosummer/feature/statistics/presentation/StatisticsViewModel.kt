package com.example.todosummer.feature.statistics.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todosummer.feature.statistics.domain.StatisticsGenerator
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 통계 생성 기능을 위한 ViewModel
 */
class StatisticsViewModel(
    private val statisticsGenerator: StatisticsGenerator
) : ViewModel() {
    private val _state = MutableStateFlow(StatisticsState())
    val state: StateFlow<StatisticsState> = _state.asStateFlow()
    private var streamJob: Job? = null

    fun loadModel() {
        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            statisticsGenerator.loadModel()
                .onSuccess {
                    _state.update { it.copy(isModelLoaded = true, isLoading = false) }
                }
                .onFailure { error ->
                    _state.update { it.copy(error = error.message ?: "Unknown error", isLoading = false) }
                }
        }
    }

    fun summarize(text: String, maxLength: Int = 100) {
        if (text.isBlank()) {
            _state.update { it.copy(error = "Text cannot be empty") }
            return
        }
        
        _state.update { it.copy(isGenerating = true, error = null) }

        viewModelScope.launch {
            statisticsGenerator.generateStatistics(text, maxLength)
                .onSuccess { statistics ->
                    _state.update { 
                        it.copy(
                            statistics = statistics,
                            isGenerating = false
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { 
                        it.copy(
                            isGenerating = false,
                            error = error.message ?: "Failed to generate statistics"
                        )
                    }
                }
        }
    }
    
    fun generateStatisticsStream(text: String, maxLength: Int = 100) {
        if (text.isBlank()) {
            _state.update { it.copy(error = "Text cannot be empty") }
            return
        }
        
        _state.update { it.copy(isGenerating = true, error = null, statistics = "") }
        streamJob = viewModelScope.launch {
            try {
                statisticsGenerator.generateStatisticsStream(text, maxLength).collect { partial ->
                    _state.update { 
                        it.copy(
                            statistics = it.statistics + partial
                        )
                    }
                }
                
                _state.update { it.copy(isGenerating = false) }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isGenerating = false,
                        error = e.message ?: "Failed to generate statistics"
                    )
                }
            }
        }
    }
}

/**
 * 통계 생성 상태
 */
data class StatisticsState(
    val isModelLoaded: Boolean = false,
    val isLoading: Boolean = false,
    val isGenerating: Boolean = false,
    val statistics: String = "",
    val error: String? = null
)
