package net.rubygrapefruit.file

import platform.windows.GetLastError

class NativeException(message: String): FileSystemException(format(message))

private fun format(message: String): String {
    require(message.endsWith('.'))
    return "$message LastError: ${GetLastError()}"
}
