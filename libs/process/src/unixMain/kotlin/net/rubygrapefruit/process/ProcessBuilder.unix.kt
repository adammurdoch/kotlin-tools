@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.process

import kotlinx.cinterop.*
import platform.posix.*

internal actual fun start(spec: ProcessStartSpec): ProcessControl {
    val pipeDescriptors = if (spec.collectStdout) {
        memScoped {
            val desc = allocArray<IntVar>(2)
            pipe(desc)
            Descriptors(desc[0], desc[1])
        }
    } else {
        null
    }

    val pid = fork()
    return when {
        pid == 0 -> exec(spec, pipeDescriptors)
        pid > 0 -> processControl(pid, pipeDescriptors)
        else -> throw RuntimeException("Could not fork process. errno = $errno")
    }
}

private class Descriptors(val read: Int, val write: Int)

private fun processControl(pid: Int, pipe: Descriptors?): ProcessControl {
    return if (pipe != null) {
        close(pipe.write)
        UnixProcess(pid, pipe.read)
    } else {
        UnixProcess(pid, null)
    }
}

private fun exec(spec: ProcessStartSpec, pipe: Descriptors?): Nothing {
    memScoped {
        if (pipe != null) {
            close(pipe.read)
            dup2(pipe.write, STDOUT_FILENO)
        }
        val args = allocArray<CPointerVar<ByteVar>>(spec.commandLine.size + 1)
        for (index in spec.commandLine.indices) {
            args[index] = spec.commandLine[index].cstr.ptr
        }
        args[spec.commandLine.size] = null
        execvp(spec.commandLine.first(), args)
        throw RuntimeException("Could not exec command. errno = $errno")
    }
}