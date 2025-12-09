package com.example.todosummer.core.ui.toast

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSTimer
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleAlert
import platform.UIKit.UIApplication

/**
 * iOS Toast 표시 (Alert 사용)
 */
actual fun showToast(message: String) {
    // iOS에서는 직접 호출 불가 - rememberToastState 사용
}

@Composable
actual fun rememberToastState(): ToastState {
    return remember { ToastState() }
}

actual class ToastState {
    private var currentAlert: UIAlertController? = null
    
    @OptIn(ExperimentalForeignApi::class)
    actual fun show(message: String) {
        // 이미 표시 중인 alert가 있으면 먼저 닫기
        currentAlert?.dismissViewControllerAnimated(false, null)
        currentAlert = null
        
        // 메시지가 비어있거나 null이면 표시하지 않음
        if (message.isBlank()) return
        
        val alert = UIAlertController.alertControllerWithTitle(
            title = null,
            message = message,
            preferredStyle = UIAlertControllerStyleAlert
        )
        
        currentAlert = alert
        
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        
        // 현재 표시 중인 ViewController 찾기
        var topController = rootViewController
        while (topController?.presentedViewController != null) {
            topController = topController.presentedViewController
        }
        
        topController?.presentViewController(
            alert,
            animated = true,
            completion = {
                // 1.5초 후 자동으로 닫기
                NSTimer.scheduledTimerWithTimeInterval(
                    interval = 1.5,
                    repeats = false
                ) { _ ->
                    alert.dismissViewControllerAnimated(true, null)
                    if (currentAlert == alert) {
                        currentAlert = null
                    }
                }
            }
        )
    }
}
