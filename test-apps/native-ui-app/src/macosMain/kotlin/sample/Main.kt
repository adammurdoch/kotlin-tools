package sample

import platform.AppKit.NSApplication

fun main() {
    val application = NSApplication.sharedApplication
    application.delegate = AppDelegate()
    application.run()
}
