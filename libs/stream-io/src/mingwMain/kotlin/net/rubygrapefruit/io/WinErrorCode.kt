package net.rubygrapefruit.io

import platform.windows.DWORD
import platform.windows.GetLastError

class WinErrorCode(private val code: DWORD) : ErrorCode {
    companion object {
        fun last() = WinErrorCode(GetLastError())
    }

    override val formatted: String
        get() = "LastError: $code"
}