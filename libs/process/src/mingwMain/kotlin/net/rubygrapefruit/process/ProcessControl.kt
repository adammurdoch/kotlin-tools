@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.process

import kotlinx.cinterop.*
import kotlinx.io.RawSink
import kotlinx.io.RawSource
import net.rubygrapefruit.io.IOException
import net.rubygrapefruit.io.WinErrorCode
import net.rubygrapefruit.io.stream.FileBackedRawSink
import net.rubygrapefruit.io.stream.FileBackedRawSource
import platform.windows.*

internal actual fun start(spec: ProcessStartSpec): ProcessControl {
    val stdoutDescriptors = if (spec.collectStdout) {
        val pipe = pipe()
        if (SetHandleInformation(pipe.read, HANDLE_FLAG_INHERIT.convert(), 0.convert()) == 0) {
            throw IOException("Could set handle information", WinErrorCode.last())
        }
        pipe
    } else {
        null
    }
    val stdinDescriptors = if (spec.receiveStdin) {
        val pipe = pipe()
        if (SetHandleInformation(pipe.write, HANDLE_FLAG_INHERIT.convert(), 0.convert()) == 0) {
            throw IOException("Could set handle information", WinErrorCode.last())
        }
        pipe
    } else {
        null
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
        startupInfo.hStdInput = null
        startupInfo.hStdOutput = null
        startupInfo.hStdError = null
        if (stdoutDescriptors != null) {
            startupInfo.dwFlags = STARTF_USESTDHANDLES.convert()
            startupInfo.hStdOutput = stdoutDescriptors.write
        }
        if (stdinDescriptors != null) {
            startupInfo.dwFlags = STARTF_USESTDHANDLES.convert()
            startupInfo.hStdInput = stdinDescriptors.read
        }
        val processInfo = alloc<PROCESS_INFORMATION>()
        formattedCommandLine.usePinned { cl ->
            if (CreateProcessW(
                    null,
                    cl.addressOf(0).reinterpret(),
                    null,
                    null,
                    if (stdoutDescriptors != null) TRUE else FALSE,
                    0.convert(),
                    null,
                    spec.directory?.absolutePath,
                    startupInfo.ptr,
                    processInfo.ptr
                ) == 0
            ) {
                throw IOException("Could not create child process", WinErrorCode.last())
            }
        }
        CloseHandle(processInfo.hThread)
        if (stdoutDescriptors != null) {
            // TODO - close this on error
            CloseHandle(stdoutDescriptors.write)
        }
        if (stdinDescriptors != null) {
            // TODO - close this on error
            CloseHandle(stdinDescriptors.read)
        }
        processInfo.hProcess
    }

    return object : ProcessControl {
        override val stdout: RawSource
            get() = FileBackedRawSource(ProcessSource, stdoutDescriptors!!.read)

        override val stdin: RawSink
            get() = FileBackedRawSink(ProcessSource, stdinDescriptors!!.write)

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
                if (stdoutDescriptors != null) {
                    CloseHandle(stdoutDescriptors.read)
                }
                if (stdinDescriptors != null) {
                    CloseHandle(stdinDescriptors.write)
                }
                CloseHandle(handle)
            }
        }
    }
}

private fun pipe(): Descriptors {
    return memScoped {
        val securityAttributes = alloc<SECURITY_ATTRIBUTES>()
        securityAttributes.nLength = sizeOf<SECURITY_ATTRIBUTES>().convert()
        securityAttributes.bInheritHandle = TRUE
        securityAttributes.lpSecurityDescriptor = null
        val readHandle = alloc<HANDLEVar>()
        val writeHandle = alloc<HANDLEVar>()
        if (CreatePipe(readHandle.ptr, writeHandle.ptr, securityAttributes.ptr, 0.convert()) == 0) {
            throw IOException("Could not create pipe", WinErrorCode.last())
        }
        Descriptors(readHandle.value!!, writeHandle.value!!)
    }
}

internal class Descriptors(val read: HANDLE, val write: HANDLE)
