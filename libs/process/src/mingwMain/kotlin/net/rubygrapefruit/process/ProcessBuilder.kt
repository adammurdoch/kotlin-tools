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
    val handle = memScoped {
        val formattedCommandLine = spec.commandLine.joinToString(" ")
        val startupInfo = alloc<STARTUPINFOW>()
        startupInfo.cb = sizeOf<STARTUPINFOW>().convert()
        startupInfo.dwFlags = 0.convert()
        startupInfo.lpReserved = null
        startupInfo.lpReserved2 = null
        startupInfo.cbReserved2 = 0.convert()
        val processInfo = alloc<PROCESS_INFORMATION>()
        formattedCommandLine.usePinned {
            if (CreateProcessW(
                    null,
                    it.addressOf(0).reinterpret(),
                    null,
                    null,
                    FALSE,
                    0.convert(),
                    null,
                    null,
                    startupInfo.ptr,
                    processInfo.ptr
                ) == 0
            ) {
                throw IOException("Could not create child process", WinErrorCode.last())
            }
        }
        CloseHandle(processInfo.hThread)
        processInfo.hProcess
    }

    return object : ProcessControl {
        override val stdout: RawSource
            get() = TODO("Not yet implemented")

        override val stdin: RawSink
            get() = TODO("Not yet implemented")

        override fun waitFor(): Int {
            try {
                if (WaitForSingleObject(handle, INFINITE) != WAIT_OBJECT_0) {
                    throw IOException("Could not wait for child process.", WinErrorCode.last())
                }
                return memScoped {
                    val exitCodeVar = alloc<DWORDVar>()
                    if (GetExitCodeProcess(handle, exitCodeVar.ptr) == 0) {
                        throw IOException("Could not query child process exit code.", WinErrorCode.last())
                    }
                    val exitCode = exitCodeVar.value.convert<Int>()
                    if (spec.checkExitCode && exitCode != 0) {
                        throw IOException("Command failed with exit code $exitCode")
                    }
                    exitCode
                }
            } finally {
                CloseHandle(handle)
            }
        }
    }
}