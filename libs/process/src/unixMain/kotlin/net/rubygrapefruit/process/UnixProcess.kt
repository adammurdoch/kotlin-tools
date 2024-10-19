@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.process

import kotlinx.cinterop.*
import kotlinx.io.RawSink
import kotlinx.io.RawSource
import net.rubygrapefruit.io.IOException
import net.rubygrapefruit.io.UnixErrorCode
import net.rubygrapefruit.io.stream.*
import platform.posix.EINTR
import platform.posix.errno
import platform.posix.waitpid

internal class UnixProcess(
    private val spec: ProcessStartSpec,
    private val pid: Int,
    private val stdoutDescriptor: ReadDescriptor?,
    private val stdinDescriptor: WriteDescriptor?
) : ProcessControl {
    override val stdout: RawSource
        get() = FileDescriptorBackedRawSource(ProcessSource, stdoutDescriptor!!)

    override val stdin: RawSink
        get() = FileDescriptorBackedRawSink(ProcessSource, stdinDescriptor!!)

    override fun waitFor(): Int {
        try {
            return memScoped {
                waitAndGetExitCode()
            }
        } finally {
            stdoutDescriptor?.close()
            stdinDescriptor?.close()
        }
    }

    private fun MemScope.waitAndGetExitCode(): Int {
        val exitCode = alloc<IntVar>()
        while (true) {
            val result = waitpid(pid, exitCode.ptr, 0)
            if (result == pid) {
                if (spec.checkExitCode && exitCode.value != 0) {
                    throw IOException("Command failed with exit code ${exitCode.value}")
                }
                return exitCode.value.and(0xFF00).ushr(8)
            } else if (errno != EINTR) {
                throw IOException("Could not wait for child process.", UnixErrorCode.last())
            }
            // Interrupted, continue
        }
    }

    private object ProcessSource : StreamSource {
        override val displayName: String
            get() = "child process"
    }
}