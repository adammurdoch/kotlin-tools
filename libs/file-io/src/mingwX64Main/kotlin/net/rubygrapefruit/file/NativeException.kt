package net.rubygrapefruit.file

import net.rubygrapefruit.error.WinErrorCode

internal class NativeException(message: String) : FileSystemException(message, WinErrorCode.last())
