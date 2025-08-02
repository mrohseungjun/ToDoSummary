package com.example.todosummer.core.common.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember

/**
 * 앱에서 사용하는 문자열 리소스
 */
interface StringResources {
    // 앱 일반
    val appName: String
    
    // 탐색
    val home: String
    val todos: String
    val settings: String
    
    // Todo 관련
    val addTodo: String
    val editTodo: String
    val deleteTodo: String
    val todoTitle: String
    val todoDescription: String
    val todoDueDate: String
    val todoPriority: String
    val todoCompleted: String
    val todoNotCompleted: String
    val todoEmpty: String
    
    // 우선순위
    val priorityLow: String
    val priorityMedium: String
    val priorityHigh: String
    
    // 설정
    val settingsTitle: String
    val settingsLanguage: String
    val settingsTheme: String
    val settingsThemeLight: String
    val settingsThemeDark: String
    val settingsThemeSystem: String
    
    // AI 요약
    val aiSummarize: String
    val aiSummarizing: String
    val aiSummaryTitle: String
    val aiSummaryError: String
    
    // 버튼
    val save: String
    val cancel: String
    val delete: String
    val confirm: String
}

/**
 * 영어 문자열 리소스
 */
class EnglishStringResources : StringResources {
    // 앱 일반
    override val appName = "Todo Summer"
    
    // 탐색
    override val home = "Home"
    override val todos = "Todos"
    override val settings = "Settings"
    
    // Todo 관련
    override val addTodo = "Add Todo"
    override val editTodo = "Edit Todo"
    override val deleteTodo = "Delete Todo"
    override val todoTitle = "Title"
    override val todoDescription = "Description"
    override val todoDueDate = "Due Date"
    override val todoPriority = "Priority"
    override val todoCompleted = "Completed"
    override val todoNotCompleted = "Not Completed"
    override val todoEmpty = "No todos yet. Add one!"
    
    // 우선순위
    override val priorityLow = "Low"
    override val priorityMedium = "Medium"
    override val priorityHigh = "High"
    
    // 설정
    override val settingsTitle = "Settings"
    override val settingsLanguage = "Language"
    override val settingsTheme = "Theme"
    override val settingsThemeLight = "Light"
    override val settingsThemeDark = "Dark"
    override val settingsThemeSystem = "System"
    
    // AI 요약
    override val aiSummarize = "Summarize with AI"
    override val aiSummarizing = "Summarizing..."
    override val aiSummaryTitle = "AI Summary"
    override val aiSummaryError = "Failed to generate summary"
    
    // 버튼
    override val save = "Save"
    override val cancel = "Cancel"
    override val delete = "Delete"
    override val confirm = "Confirm"
}

/**
 * 한국어 문자열 리소스
 */
class KoreanStringResources : StringResources {
    // 앱 일반
    override val appName = "투두 서머"
    
    // 탐색
    override val home = "홈"
    override val todos = "할 일"
    override val settings = "설정"
    
    // Todo 관련
    override val addTodo = "할 일 추가"
    override val editTodo = "할 일 수정"
    override val deleteTodo = "할 일 삭제"
    override val todoTitle = "제목"
    override val todoDescription = "설명"
    override val todoDueDate = "마감일"
    override val todoPriority = "우선순위"
    override val todoCompleted = "완료됨"
    override val todoNotCompleted = "미완료"
    override val todoEmpty = "할 일이 없습니다. 추가해보세요!"
    
    // 우선순위
    override val priorityLow = "낮음"
    override val priorityMedium = "중간"
    override val priorityHigh = "높음"
    
    // 설정
    override val settingsTitle = "설정"
    override val settingsLanguage = "언어"
    override val settingsTheme = "테마"
    override val settingsThemeLight = "라이트"
    override val settingsThemeDark = "다크"
    override val settingsThemeSystem = "시스템"
    
    // AI 요약
    override val aiSummarize = "AI로 요약하기"
    override val aiSummarizing = "요약 중..."
    override val aiSummaryTitle = "AI 요약"
    override val aiSummaryError = "요약 생성 실패"
    
    // 버튼
    override val save = "저장"
    override val cancel = "취소"
    override val delete = "삭제"
    override val confirm = "확인"
}

// 언어 설정을 위한 LocalComposition
val LocalStringResources = compositionLocalOf<StringResources> { EnglishStringResources() }

// 언어 모드 열거형
enum class LanguageMode {
    ENGLISH, KOREAN
}

/**
 * 언어 설정을 제공하는 컴포저블
 */
@Composable
fun ProvideStringResources(
    languageMode: LanguageMode,
    content: @Composable () -> Unit
) {
    val stringResources = remember(languageMode) {
        when (languageMode) {
            LanguageMode.ENGLISH -> EnglishStringResources()
            LanguageMode.KOREAN -> KoreanStringResources()
        }
    }
    
    CompositionLocalProvider(LocalStringResources provides stringResources) {
        content()
    }
}

/**
 * 현재 설정된 문자열 리소스를 가져오는 함수
 */
@Composable
fun stringResource(): StringResources {
    return LocalStringResources.current
}
