package net.rubygrapefruit.file

import kotlinx.cinterop.toKString
import platform.posix.errno
import platform.posix.strerror

class NativeException(message: String) : FileSystemException(format(message))

private fun format(message: String): String {
    require(message.endsWith('.'))
    val code = errno
    return "$message errno: $code, ${strerror(code)?.toKString()}"
}
