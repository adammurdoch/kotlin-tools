@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.io

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import net.rubygrapefruit.error.ErrorCode
import platform.posix.errno
import platform.posix.strerror

class UnixErrorCode(private val code: Int) : ErrorCode {
    companion object {
        fun last() = UnixErrorCode(errno)
    }

    override val formatted: String
        get() = "errno: $code, ${strerror(code)?.toKString()}"
}