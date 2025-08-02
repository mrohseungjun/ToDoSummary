package com.example.todosummer.feature.ai.presentation

import com.example.todosummer.feature.ai.domain.SummaryGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * AI 요약 기능을 위한 ViewModel
 */
class SummaryViewModel(
    private val summaryGenerator: SummaryGenerator,
    private val coroutineScope: CoroutineScope
) {
    private val _state = MutableStateFlow(SummaryState())
    val state: StateFlow<SummaryState> = _state.asStateFlow()
    
    init {
        loadModel()
    }
    
    /**
     * AI 모델을 로드합니다.
     */
    fun loadModel() {
        _state.update { it.copy(isLoading = true, error = null) }
        
        coroutineScope.launch {
            summaryGenerator.loadModel()
                .onSuccess {
                    _state.update { it.copy(isModelLoaded = true, isLoading = false) }
                }
                .onFailure { error ->
                    _state.update { 
                        it.copy(
                            isModelLoaded = false, 
                            isLoading = false,
                            error = error.message ?: "Failed to load model"
                        )
                    }
                }
        }
    }
    
    /**
     * 텍스트를 요약합니다.
     */
    fun summarize(text: String, maxLength: Int = 100) {
        if (text.isBlank()) {
            _state.update { it.copy(error = "Text cannot be empty") }
            return
        }
        
        _state.update { it.copy(isGenerating = true, error = null) }
        
        coroutineScope.launch {
            summaryGenerator.generateSummary(text, maxLength)
                .onSuccess { summary ->
                    _state.update { 
                        it.copy(
                            summary = summary,
                            isGenerating = false
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { 
                        it.copy(
                            isGenerating = false,
                            error = error.message ?: "Failed to generate summary"
                        )
                    }
                }
        }
    }
    
    /**
     * 스트리밍 방식으로 텍스트를 요약합니다.
     */
    fun summarizeStream(text: String, maxLength: Int = 100) {
        if (text.isBlank()) {
            _state.update { it.copy(error = "Text cannot be empty") }
            return
        }
        
        _state.update { it.copy(isGenerating = true, error = null, summary = "") }
        
        coroutineScope.launch {
            try {
                summaryGenerator.generateSummaryStream(text, maxLength).collect { partialSummary ->
                    _state.update { 
                        it.copy(
                            summary = it.summary + partialSummary
                        )
                    }
                }
                
                _state.update { it.copy(isGenerating = false) }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isGenerating = false,
                        error = e.message ?: "Failed to generate summary"
                    )
                }
            }
        }
    }
}

/**
 * AI 요약 상태
 */
data class SummaryState(
    val isModelLoaded: Boolean = false,
    val isLoading: Boolean = false,
    val isGenerating: Boolean = false,
    val summary: String = "",
    val error: String? = null
)
