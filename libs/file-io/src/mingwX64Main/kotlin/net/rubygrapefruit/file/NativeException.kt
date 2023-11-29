package net.rubygrapefruit.file

internal class NativeException(message: String) : FileSystemException(message, WinErrorCode.last())
