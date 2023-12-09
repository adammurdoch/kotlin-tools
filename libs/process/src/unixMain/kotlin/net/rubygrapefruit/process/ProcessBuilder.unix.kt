@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.process

import kotlinx.cinterop.*
import platform.posix.*

internal actual fun start(spec: ProcessStartSpec): Process {
    val pid = fork()
    if (pid == 0) {
        memScoped {
            val args = allocArray<CPointerVar<ByteVar>>(spec.commandLine.size + 1)
            for (index in spec.commandLine.indices) {
                args[index] = spec.commandLine[index].cstr.ptr
            }
            args[spec.commandLine.size] = null
            execvp(spec.commandLine.first(), args)
            throw RuntimeException("Could not exec command. errno = $errno")
        }
    } else if (pid > 0) {
        return object : Process {
            override fun waitFor() {
                memScoped {
                    val exitCode = alloc<IntVar>()
                    while (true) {
                        val result = waitpid(pid, exitCode.ptr, 0)
                        if (result == pid) {
                            if (exitCode.value != 0) {
                                throw RuntimeException("Command failed with exit code ${exitCode.value}")
                            }
                            return
                        } else if (errno != EINTR) {
                            throw RuntimeException("Could not wait for child process. errno = $errno")
                        }
                        // Interrupted, continue
                    }
                }
            }
        }
    } else {
        throw RuntimeException("Could not fork process. errno = $errno")
    }
}