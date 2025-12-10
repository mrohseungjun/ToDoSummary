package com.oseungjun.todosummer.core.ui.strings

import java.util.Locale

actual fun getCurrentLanguage(): String {
    return Locale.getDefault().language
}
