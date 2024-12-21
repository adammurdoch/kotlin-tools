package net.rubygrapefruit.io

import kotlinx.cinterop.*
import net.rubygrapefruit.error.ErrorCode
import platform.windows.*

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