package com.example.todosummer.core.ui.strings

/**
 * 다국어 문자열 리소스
 * expect/actual 패턴으로 플랫폼별 로케일 확인
 */
expect fun getCurrentLanguage(): String

object Strings {
    // DatePicker
    val selectDueDate: String
        get() = when (getCurrentLanguage()) {
            "ko" -> "마감일 선택"
            else -> "Select Due Date"
        }
    
    val selectDueTime: String
        get() = when (getCurrentLanguage()) {
            "ko" -> "마감 시간 선택"
            else -> "Select Due Time"
        }
    
    val next: String
        get() = when (getCurrentLanguage()) {
            "ko" -> "다음"
            else -> "Next"
        }
    
    val previous: String
        get() = when (getCurrentLanguage()) {
            "ko" -> "이전"
            else -> "Previous"
        }
    
    val confirm: String
        get() = when (getCurrentLanguage()) {
            "ko" -> "확인"
            else -> "Confirm"
        }
    
    val cancel: String
        get() = when (getCurrentLanguage()) {
            "ko" -> "취소"
            else -> "Cancel"
        }
    
    val notSet: String
        get() = when (getCurrentLanguage()) {
            "ko" -> "설정 안 함"
            else -> "Not Set"
        }
    
    val advancedSettings: String
        get() = when (getCurrentLanguage()) {
            "ko" -> "상세 설정"
            else -> "Advanced Settings"
        }
    
    val dueDate: String
        get() = when (getCurrentLanguage()) {
            "ko" -> "마감일"
            else -> "Due Date"
        }
    
    val reminder: String
        get() = when (getCurrentLanguage()) {
            "ko" -> "알림"
            else -> "Reminder"
        }
    
    val setDueDateFirst: String
        get() = when (getCurrentLanguage()) {
            "ko" -> "마감일을 먼저 설정하세요"
            else -> "Set due date first"
        }
    
    val selectReminderTime: String
        get() = when (getCurrentLanguage()) {
            "ko" -> "알림 시간 선택"
            else -> "Select Reminder Time"
        }
    
    val atDueTime: String
        get() = when (getCurrentLanguage()) {
            "ko" -> "마감 시간"
            else -> "At due time"
        }
    
    fun minutesBefore(minutes: Int): String = when (getCurrentLanguage()) {
        "ko" -> "${minutes}분 전"
        else -> "$minutes min before"
    }
    
    val oneHourBefore: String
        get() = when (getCurrentLanguage()) {
            "ko" -> "1시간 전"
            else -> "1 hour before"
        }
    
    // Date formatting
    fun formatDateKorean(year: Int, month: Int, day: Int): String = 
        "${year}년 ${month}월 ${day}일"
    
    fun formatDateEnglish(year: Int, month: Int, day: Int): String {
        val monthName = when (month) {
            1 -> "Jan"
            2 -> "Feb"
            3 -> "Mar"
            4 -> "Apr"
            5 -> "May"
            6 -> "Jun"
            7 -> "Jul"
            8 -> "Aug"
            9 -> "Sep"
            10 -> "Oct"
            11 -> "Nov"
            12 -> "Dec"
            else -> ""
        }
        return "$monthName $day, $year"
    }
    
    fun formatDate(year: Int, month: Int, day: Int): String = when (getCurrentLanguage()) {
        "ko" -> formatDateKorean(year, month, day)
        else -> formatDateEnglish(year, month, day)
    }
}
