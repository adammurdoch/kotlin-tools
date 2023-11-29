package net.rubygrapefruit.file

import platform.windows.DWORD
import platform.windows.GetLastError

internal class WinErrorCode(private val code: DWORD): ErrorCode {
    companion object {
        fun last() = WinErrorCode(GetLastError())
    }

    override val formatted: String
        get() = "LastError: $code"
}