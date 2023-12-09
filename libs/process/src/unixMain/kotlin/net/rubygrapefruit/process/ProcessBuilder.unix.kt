@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.process

import kotlinx.cinterop.*
import platform.posix.errno
import platform.posix.execvp
import platform.posix.fork

internal actual fun start(spec: ProcessStartSpec): Process {
    val pid = fork()
    when {
        pid == 0 -> exec(spec)
        pid > 0 -> return UnixProcess(pid)
        else -> throw RuntimeException("Could not fork process. errno = $errno")
    }
}

private fun exec(spec: ProcessStartSpec): Nothing {
    memScoped {
        val args = allocArray<CPointerVar<ByteVar>>(spec.commandLine.size + 1)
        for (index in spec.commandLine.indices) {
            args[index] = spec.commandLine[index].cstr.ptr
        }
        args[spec.commandLine.size] = null
        execvp(spec.commandLine.first(), args)
        throw RuntimeException("Could not exec command. errno = $errno")
    }
}