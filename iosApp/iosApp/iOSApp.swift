import SwiftUI
import UserNotifications

@main
struct iOSApp: App {
    
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

// AppDelegate for notification handling
class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        // 알림 센터 delegate 설정
        UNUserNotificationCenter.current().delegate = self
        
        // 알림 권한 요청
        requestNotificationPermission()
        
        return true
    }
    
    private func requestNotificationPermission() {
        let center = UNUserNotificationCenter.current()
        center.requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
            if let error = error {
                print("[Permission] Notification permission error: \(error.localizedDescription)")
            } else {
                print("[Permission] Notification permission granted: \(granted)")
            }
        }
    }
    
    // 앱이 포그라운드에 있을 때도 알림 표시
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        print("[Notification] willPresent: \(notification.request.content.title)")
        // 배너, 사운드, 뱃지 모두 표시
        completionHandler([.banner, .sound, .badge])
    }
    
    // 알림 탭했을 때 처리
    func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {
        print("[Notification] didReceive: \(response.notification.request.content.title)")
        completionHandler()
    }
}