@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.process

import kotlinx.cinterop.*
import kotlinx.io.RawSink
import kotlinx.io.RawSource
import net.rubygrapefruit.io.IOException
import net.rubygrapefruit.io.WinErrorCode
import platform.windows.*

internal actual fun start(spec: ProcessStartSpec): ProcessControl {
    if (spec.collectStdout) {
        TODO()
    }
    if (spec.receiveStdin) {
        TODO()
    }
    return memScoped {
        val formattedCommandLine = spec.commandLine.joinToString(" ")
        val info = alloc<PROCESS_INFORMATION>()
        formattedCommandLine.usePinned {
            if (CreateProcessW(spec.commandLine.first(), it.addressOf(0).reinterpret(), null, null, 0, 0.convert(), null, null, null, info.ptr) == 0) {
                throw IOException("Could not create child process", WinErrorCode.last())
            }
        }
        CloseHandle(info.hThread)

        object : ProcessControl {
            override val stdout: RawSource
                get() = TODO("Not yet implemented")

            override val stdin: RawSink
                get() = TODO("Not yet implemented")

            override fun waitFor(): Int {
                if (WaitForSingleObject(info.hProcess, INFINITE) != WAIT_OBJECT_0) {
                    throw IOException("Could not wait for child process.", WinErrorCode.last())
                }
                CloseHandle(info.hProcess)
                return 0
            }
        }
    }
}