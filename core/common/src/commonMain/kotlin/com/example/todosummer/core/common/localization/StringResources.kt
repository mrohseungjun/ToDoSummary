package com.example.todosummer.core.common.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf

/**
 * 앱에서 사용하는 문자열 리소스 인터페이스
 */
interface StringResources {
    // 공통
    val appName: String
    val ok: String
    val cancel: String
    val confirm: String
    val save: String
    val delete: String
    val edit: String
    val loading: String
    val error: String
    
    // Todo 관련
    val todos: String
    val todo: String
    val addTodo: String
    val editTodo: String
    val deleteTodo: String
    val todoTitle: String
    val todoDescription: String
    val todoPriority: String
    val todoDueDate: String
    val todoEmpty: String
    val todoEmptyTitle: String
    val todoEmptyBody: String
    val todoCompleted: String
    val todoIncomplete: String
    val todoCreatedAt: String
    val todoUpdatedAt: String
    val todoTags: String
    
    // 우선순위
    val priorityLow: String
    val priorityMedium: String
    val priorityHigh: String
    
    // 통계 관련
    val statisticsTitle: String
    val statisticsGenerate: String
    val statisticsGenerating: String
    val statisticsResult: String
    val statisticsLoadingMessage: String
    val statisticsLoadModel: String
    
    // 설정 관련
    val settingsTitle: String
    val settingsLanguage: String
    val settingsTheme: String
    val settingsThemeLight: String
    val settingsThemeDark: String
    val settingsThemeSystem: String
}

/**
 * 영어 문자열 리소스
 */
class EnglishStringResources : StringResources {
    // 공통
    override val appName: String = "Todo Summer"
    override val ok: String = "OK"
    override val cancel: String = "Cancel"
    override val confirm: String = "Confirm"
    override val save: String = "Save"
    override val delete: String = "Delete"
    override val edit: String = "Edit"
    override val loading: String = "Loading..."
    override val error: String = "Error"
    
    // Todo 관련
    override val todos: String = "Todos"
    override val todo: String = "Todo"
    override val addTodo: String = "Add Todo"
    override val editTodo: String = "Edit Todo"
    override val deleteTodo: String = "Delete Todo"
    override val todoTitle: String = "Title"
    override val todoDescription: String = "Description"
    override val todoPriority: String = "Priority"
    override val todoDueDate: String = "Due Date"
    override val todoEmpty: String = "No todos yet. Add one by clicking the + button."
    override val todoEmptyTitle: String = "All tasks completed!"
    override val todoEmptyBody: String = "Add a new task to plan the rest of your day."
    override val todoCompleted: String = "Completed"
    override val todoIncomplete: String = "Incomplete"
    override val todoCreatedAt: String = "Created at"
    override val todoUpdatedAt: String = "Updated at"
    override val todoTags: String = "Tags"
    
    // 우선순위
    override val priorityLow: String = "Low"
    override val priorityMedium: String = "Medium"
    override val priorityHigh: String = "High"
    
    // 통계 관련
    override val statisticsTitle: String = "Statistics"
    override val statisticsGenerate: String = "Generate"
    override val statisticsGenerating: String = "Generating..."
    override val statisticsResult: String = "Statistics Result"
    override val statisticsLoadingMessage: String = "Loading statistics model..."
    override val statisticsLoadModel: String = "Load statistics model"
    
    // 설정 관련
    override val settingsTitle: String = "Settings"
    override val settingsLanguage: String = "Language"
    override val settingsTheme: String = "Theme"
    override val settingsThemeLight: String = "Light"
    override val settingsThemeDark: String = "Dark"
    override val settingsThemeSystem: String = "System Default"
}

/**
 * 한국어 문자열 리소스
 */
class KoreanStringResources : StringResources {
    // 공통
    override val appName: String = "투두 서머"
    override val ok: String = "확인"
    override val cancel: String = "취소"
    override val confirm: String = "확인"
    override val save: String = "저장"
    override val delete: String = "삭제"
    override val edit: String = "편집"
    override val loading: String = "로딩 중..."
    override val error: String = "오류"
    
    // Todo 관련
    override val todos: String = "할 일 목록"
    override val todo: String = "할 일"
    override val addTodo: String = "할 일 추가"
    override val editTodo: String = "할 일 편집"
    override val deleteTodo: String = "할 일 삭제"
    override val todoTitle: String = "제목"
    override val todoDescription: String = "설명"
    override val todoPriority: String = "우선순위"
    override val todoDueDate: String = "마감일"
    override val todoEmpty: String = "아직 할 일이 없습니다. + 버튼을 클릭하여 추가하세요."
    override val todoEmptyTitle: String = "오늘의 모든 할 일을 완료했어요! 🎉"
    override val todoEmptyBody: String = "새로운 할 일을 추가하여 하루를 계획해보세요."
    override val todoCompleted: String = "완료됨"
    override val todoIncomplete: String = "미완료"
    override val todoCreatedAt: String = "생성 시간"
    override val todoUpdatedAt: String = "수정 시간"
    override val todoTags: String = "태그"
    
    // 우선순위
    override val priorityLow: String = "낮음"
    override val priorityMedium: String = "중간"
    override val priorityHigh: String = "높음"
    
    // 통계 관련
    override val statisticsTitle: String = "통계"
    override val statisticsGenerate: String = "생성하기"
    override val statisticsGenerating: String = "생성 중..."
    override val statisticsResult: String = "통계 결과"
    override val statisticsLoadingMessage: String = "통계 모델을 로드하는 중입니다..."
    override val statisticsLoadModel: String = "통계 모델 로드하기"
    
    // 설정 관련
    override val settingsTitle: String = "설정"
    override val settingsLanguage: String = "언어"
    override val settingsTheme: String = "테마"
    override val settingsThemeLight: String = "라이트 모드"
    override val settingsThemeDark: String = "다크 모드"
    override val settingsThemeSystem: String = "시스템 기본값"
}

/**
 * 현재 언어 모드에 따라 문자열 리소스를 반환합니다.
 */
fun getStringResources(languageMode: LanguageMode): StringResources {
    return when (languageMode) {
        LanguageMode.KOREAN -> KoreanStringResources()
        LanguageMode.ENGLISH -> EnglishStringResources()
    }
}

/**
 * 현재 문자열 리소스를 저장하는 CompositionLocal
 */
val LocalStringResources = compositionLocalOf<StringResources> { EnglishStringResources() }

/**
 * 문자열 리소스를 제공하는 Composable
 */
@Composable
fun ProvideStringResources(
    stringResources: StringResources,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalStringResources provides stringResources) {
        content()
    }
}

/**
 * 현재 문자열 리소스를 반환하는 Composable
 */
@Composable
fun stringResource(): StringResources {
    return LocalStringResources.current
}
