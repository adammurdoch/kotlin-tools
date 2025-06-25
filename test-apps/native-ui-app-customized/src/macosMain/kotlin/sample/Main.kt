package sample

import platform.AppKit.NSApplication

fun main(args: Array<String>) {
    println("args: ${args.toList()}")
    val application = NSApplication.sharedApplication
    application.delegate = AppDelegate()
    application.run()
}
