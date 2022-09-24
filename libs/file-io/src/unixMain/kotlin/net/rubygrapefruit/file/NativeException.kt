package net.rubygrapefruit.file

import kotlinx.cinterop.toKString
import platform.posix.errno
import platform.posix.strerror

class NativeException(message: String, code: Int = errno) : FileSystemException(format(message, code))

private fun format(message: String, code: Int): String {
    require(message.endsWith('.'))
    return "$message errno: $code, ${strerror(code)?.toKString()}"
}
