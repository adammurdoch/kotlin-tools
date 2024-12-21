@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.process

import kotlinx.cinterop.*
import net.rubygrapefruit.error.UnixErrorCode
import net.rubygrapefruit.io.IOException
import net.rubygrapefruit.io.stream.ReadDescriptor
import net.rubygrapefruit.io.stream.WriteDescriptor
import platform.posix.*

internal actual fun start(spec: ProcessStartSpec): ProcessControl {
    val stdoutPipe = createPipe(spec.collectStdout)
    val stdinPipe = createPipe(spec.receiveStdin)
    val pid = fork()
    return when {
        pid == 0 -> exec(spec, stdoutPipe, stdinPipe)
        pid > 0 -> processControl(spec, pid, stdoutPipe, stdinPipe)
        else -> {
            stdoutPipe?.close()
            throw IOException("Could not fork child process.", UnixErrorCode.last())
        }
    }
}

private fun createPipe(enabled: Boolean): Descriptors? {
    return if (enabled) {
        memScoped {
            val desc = allocArray<IntVar>(2)
            if (pipe(desc) != 0) {
                throw IOException("Could not create child process pipe.", UnixErrorCode.last())
            }
            Descriptors(ReadDescriptor(desc[0]), WriteDescriptor(desc[1]))
        }
    } else {
        null
    }
}

private class Descriptors(val read: ReadDescriptor, val write: WriteDescriptor) {
    fun close() {
        read.close()
        write.close()
    }
}

private fun processControl(spec: ProcessStartSpec, pid: Int, stdoutPipe: Descriptors?, stdinPipe: Descriptors?): ProcessControl {
    if (stdoutPipe != null) {
        stdoutPipe.write.close()
    }
    if (stdinPipe != null) {
        stdinPipe.read.close()
    }
    return UnixProcess(spec, pid, stdoutPipe?.read, stdinPipe?.write)
}

private fun exec(spec: ProcessStartSpec, stdoutPipe: Descriptors?, stdinPipe: Descriptors?): Nothing {
    memScoped {
        if (stdoutPipe != null) {
            stdoutPipe.read.close()
            if (dup2(stdoutPipe.write.descriptor, STDOUT_FILENO) == -1) {
                throw IOException("Could initialize stdout.", UnixErrorCode.last())
            }
        }
        if (stdinPipe != null) {
            stdinPipe.write.close()
            if (dup2(stdinPipe.read.descriptor, STDIN_FILENO) == -1) {
                throw IOException("Could initialize stdin.", UnixErrorCode.last())
            }
        }
        if (spec.directory != null) {
            if (chdir(spec.directory.absolutePath) != 0) {
                throw IOException("Could change working directory to ${spec.directory.absolutePath}.", UnixErrorCode.last())
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