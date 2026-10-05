package sample

import platform.AppKit.NSApplication

fun main(args: Array<String>) {
    println("started with args: ${args.toList()}")
    val application = NSApplication.sharedApplication
    application.delegate = AppDelegate()
    application.run()
}
