package sample

import platform.AppKit.NSApplication

fun main(args: Array<String>) {
    val application = NSApplication.sharedApplication
    application.delegate = AppDelegate()
    application.run()
}
