package net.rubygrapefruit.error

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.errno
import platform.posix.strerror

@OptIn(ExperimentalForeignApi::class)
class UnixErrorCode(private val code: Int) : ErrorCode {
    companion object {
        fun last() = UnixErrorCode(errno)
    }

    override val formatted: String
        get() = "errno: $code, ${strerror(code)?.toKString()}"
}