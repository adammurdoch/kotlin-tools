import platform.AppKit.NSApplication
import sample.AppDelegate

fun main() {
    println("native UI")
    val application = NSApplication.sharedApplication
    val delegate = AppDelegate()
    application.delegate = delegate
    application.run()
    println("finished")
}