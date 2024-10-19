@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.process

import kotlinx.cinterop.*
import kotlinx.io.RawSink
import kotlinx.io.RawSource
import net.rubygrapefruit.io.IOException
import net.rubygrapefruit.io.WinErrorCode
import net.rubygrapefruit.io.stream.FileBackedRawSource
import platform.windows.*

internal actual fun start(spec: ProcessStartSpec): ProcessControl {
    val descriptors = if (spec.collectStdout) {
        memScoped {
            val securityAttributes = alloc<SECURITY_ATTRIBUTES>()
            securityAttributes.nLength = sizeOf<SECURITY_ATTRIBUTES>().convert()
            securityAttributes.bInheritHandle = TRUE
            securityAttributes.lpSecurityDescriptor = null
            val readHandle = alloc<HANDLEVar>()
            val writeHandle = alloc<HANDLEVar>()
            if (CreatePipe(readHandle.ptr, writeHandle.ptr, securityAttributes.ptr, 0.convert()) == 0) {
                throw IOException("Could not create pipe", WinErrorCode.last())
            }
            if (SetHandleInformation(readHandle.value, HANDLE_FLAG_INHERIT.convert(), 0.convert()) == 0) {
                throw IOException("Could set handle information", WinErrorCode.last())
            }
            Descriptors(readHandle.value!!, writeHandle.value!!)
        }
    } else {
        null
    }
    if (spec.receiveStdin) {
        TODO()
    }
    val handle = memScoped {
        val formattedCommandLine = spec.commandLine.joinToString(" ") { arg ->
            if (arg.contains(" ")) {
                "\"$arg\""
            } else {
                arg
            }
        }
        val startupInfo = alloc<STARTUPINFOW>()
        startupInfo.cb = sizeOf<STARTUPINFOW>().convert()
        startupInfo.dwFlags = 0.convert()
        startupInfo.lpReserved = null
        startupInfo.lpReserved2 = null
        startupInfo.cbReserved2 = 0.convert()
        if (descriptors != null) {
            startupInfo.dwFlags = STARTF_USESTDHANDLES.convert()
            startupInfo.hStdInput = GetStdHandle(STD_INPUT_HANDLE)
            startupInfo.hStdOutput = descriptors.write
            startupInfo.hStdError = GetStdHandle(STD_ERROR_HANDLE)
        }
        val processInfo = alloc<PROCESS_INFORMATION>()
        formattedCommandLine.usePinned { cl ->
            if (CreateProcessW(
                    null,
                    cl.addressOf(0).reinterpret(),
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
        if (descriptors != null) {
            CloseHandle(descriptors.write)
        }
        processInfo.hProcess
    }

    return object : ProcessControl {
        override val stdout: RawSource
            get() = FileBackedRawSource(ProcessSource, descriptors!!.read)

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
                if (descriptors != null) {
                    CloseHandle(descriptors.read)
                }
                CloseHandle(handle)
            }
        }
    }
}

internal class Descriptors(val read: HANDLE, val write: HANDLE)
