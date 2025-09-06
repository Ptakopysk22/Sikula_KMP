import SwiftUI
import GoogleSignIn
import Firebase

class AppDelegate: NSObject, UIApplicationDelegate {
    
    func application(_ application: UIApplication,
                       didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        FirebaseApp.configure()
        //set colors
        // Barvy
        let navBarColor = UIColor(red: 223/255, green: 227/255, blue: 255/255, alpha: 1.0) // 0xFFDFE3FF
        let tabBarColor = UIColor(red: 42/255, green: 52/255, blue: 120/255, alpha: 1.0) // 0xFF2A3478
        
        // UINavigationBar
        let navBarAppearance = UINavigationBarAppearance()
        navBarAppearance.configureWithOpaqueBackground()
        navBarAppearance.backgroundColor = navBarColor
        navBarAppearance.titleTextAttributes = [.foregroundColor: UIColor.black]
        
        UINavigationBar.appearance().standardAppearance = navBarAppearance
        UINavigationBar.appearance().scrollEdgeAppearance = navBarAppearance
        
        // UITabBar
        let tabBarAppearance = UITabBarAppearance()
        tabBarAppearance.configureWithOpaqueBackground()
        tabBarAppearance.backgroundColor = tabBarColor
        
        UITabBar.appearance().standardAppearance = tabBarAppearance
        if #available(iOS 15.0, *) {
            UITabBar.appearance().scrollEdgeAppearance = tabBarAppearance
        }
        
        // Status bar (jen když máš Info.plist nastavený správně)
        UIApplication.shared.statusBarStyle = .darkContent
        //set colors end
        return true
      }

    func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey : Any] = [:]) -> Bool {
      var handled: Bool
        
      handled = GIDSignIn.sharedInstance.handle(url)
      if handled {
        return true
          
      }

      // Handle other custom URL types.

      // If not handled by this app, return false.
      return false
    }


}

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
        
       var body: some Scene {
          WindowGroup {
                ContentView().onOpenURL(perform: { url in
                    GIDSignIn.sharedInstance.handle(url)
                })
          }
       }
}
