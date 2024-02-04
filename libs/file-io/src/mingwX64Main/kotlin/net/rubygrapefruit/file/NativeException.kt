package net.rubygrapefruit.file

import net.rubygrapefruit.io.WinErrorCode

internal class NativeException(message: String) : FileSystemException(message, WinErrorCode.last())
