package net.rubygrapefruit.process

import platform.posix.errno
import platform.posix.fork
import kotlin.system.exitProcess

internal actual fun start(spec: ProcessStartSpec): Process {
    val pid = fork()
    if (pid == 0) {
        println("child process!")
        exitProcess(0)
    } else if (pid > 0) {
        return object : Process {
            override fun waitFor() {
            }
        }
    } else {
        throw RuntimeException("Could not fork process. errno = $errno")
    }
}