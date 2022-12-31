package sample

import platform.AppKit.*
import platform.Foundation.NSMakeRect
import platform.Foundation.NSNotification
import platform.darwin.NSObject

class AppDelegate : NSObject(), NSApplicationDelegateProtocol {
    override fun applicationDidFinishLaunching(notification: NSNotification) {
        val appName = "Test App"
        val menubar = NSMenu()
        val appMenuItem = NSMenuItem()
        menubar.addItem(appMenuItem)
        NSApp?.mainMenu = menubar

        val appMenu = NSMenu()
        appMenu.addItem(NSMenuItem().also { it.title = "one" })
        appMenu.addItem(NSMenuItem().also { it.title = "two" })

        appMenuItem.setSubmenu(appMenu)

        val window = NSWindow()
        window.title = appName
        window.styleMask = NSWindowStyleMaskResizable
            .or(NSWindowStyleMaskTitled)
            .or(NSWindowStyleMaskClosable)
            .or(NSWindowStyleMaskMiniaturizable)
        val frame = NSMakeRect(0.toDouble(), 0.toDouble(), 800.toDouble(), 500.toDouble())
        window.setFrame(frame, false)
        window.center()
        window.makeKeyAndOrderFront(null)
        NSApp?.activateIgnoringOtherApps(true)
    }

    override fun applicationShouldTerminateAfterLastWindowClosed(sender: NSApplication): Boolean {
        return true
    }
}