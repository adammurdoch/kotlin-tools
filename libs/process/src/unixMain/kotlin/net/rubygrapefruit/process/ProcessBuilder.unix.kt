@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.process

import kotlinx.cinterop.*
import net.rubygrapefruit.io.IOException
import net.rubygrapefruit.io.UnixErrorCode
import net.rubygrapefruit.io.stream.FileDescriptor
import platform.posix.*

internal actual fun start(spec: ProcessStartSpec): ProcessControl {
    val pipeDescriptors = if (spec.collectStdout) {
        memScoped {
            val desc = allocArray<IntVar>(2)
            if (pipe(desc) != 0) {
                throw IOException("Could not create child process pipe.", UnixErrorCode.last())
            }
            Descriptors(FileDescriptor(desc[0]), FileDescriptor(desc[1]))
        }
    } else {
        null
    }

    val pid = fork()
    return when {
        pid == 0 -> exec(spec, pipeDescriptors)
        pid > 0 -> processControl(pid, pipeDescriptors)
        else -> {
            pipeDescriptors?.close()
            throw IOException("Could not fork child process.", UnixErrorCode.last())
        }
    }
}

private class Descriptors(val read: FileDescriptor, val write: FileDescriptor) {
    fun close() {
        read.close()
        write.close()
    }
}

private fun processControl(pid: Int, pipe: Descriptors?): ProcessControl {
    return if (pipe != null) {
        pipe.write.close()
        UnixProcess(pid, pipe.read)
    } else {
        UnixProcess(pid, null)
    }
}

private fun exec(spec: ProcessStartSpec, pipe: Descriptors?): Nothing {
    memScoped {
        if (pipe != null) {
            pipe.read.close()
            if (dup2(pipe.write.descriptor, STDOUT_FILENO) == -1) {
                throw IOException("Could initialize stdout.", UnixErrorCode.last())
            }
        }
        val args = allocArray<CPointerVar<ByteVar>>(spec.commandLine.size + 1)
        for (index in spec.commandLine.indices) {
            args[index] = spec.commandLine[index].cstr.ptr
        }
        args[spec.commandLine.size] = null
        execvp(spec.commandLine.first(), args)

        throw IOException("Could not exec command.", UnixErrorCode.last())
    }
}