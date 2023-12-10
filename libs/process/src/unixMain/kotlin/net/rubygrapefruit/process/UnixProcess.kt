@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.process

import kotlinx.cinterop.*
import net.rubygrapefruit.io.stream.FileDescriptorBackedReadStream
import net.rubygrapefruit.io.stream.ReadStream
import net.rubygrapefruit.io.stream.Source
import platform.posix.EINTR
import platform.posix.close
import platform.posix.errno
import platform.posix.waitpid

internal class UnixProcess(private val pid: Int, private val stdoutDescriptor: Int?) : ProcessControl {
    override val stdout: ReadStream
        get() = FileDescriptorBackedReadStream(ProcessSource, stdoutDescriptor!!)

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
        if (stdoutDescriptor != null) {
            close(stdoutDescriptor)
        }
    }

    private object ProcessSource : Source {
        override val displayName: String
            get() = "child process"
    }
}