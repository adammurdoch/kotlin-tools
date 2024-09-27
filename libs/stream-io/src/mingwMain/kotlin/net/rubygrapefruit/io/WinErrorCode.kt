package net.rubygrapefruit.io

import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import platform.windows.DWORD
import platform.windows.FORMAT_MESSAGE_ALLOCATE_BUFFER
import platform.windows.FORMAT_MESSAGE_FROM_SYSTEM
import platform.windows.FormatMessageW
import platform.windows.GetLastError
import platform.windows.LANG_USER_DEFAULT
import platform.windows.LocalFree
import platform.windows.WCHARVar

@OptIn(ExperimentalForeignApi::class)
class WinErrorCode(private val code: DWORD) : ErrorCode {
    companion object {
        fun last() = WinErrorCode(GetLastError())
    }

    override val formatted: String
        get() {
            return memScoped {
                val buffer = alloc<CPointerVar<WCHARVar>>()
                val result = FormatMessageW(
                    FORMAT_MESSAGE_FROM_SYSTEM.or(FORMAT_MESSAGE_ALLOCATE_BUFFER).convert(),
                    null,
                    code,
                    LANG_USER_DEFAULT.convert(),
                    buffer.ptr.reinterpret(),
                    0.convert(),
                    null
                )
                if (result == 0u) {
                    throw RuntimeException("Could not determine error message for code $code (cause: ${GetLastError()})")
                }
                try {
                    val errorMessage = buffer.value!!.toKString()
                    "LastError: $errorMessage ($code)"
                } finally {
                    LocalFree(buffer.value)
                }
            }
        }
}