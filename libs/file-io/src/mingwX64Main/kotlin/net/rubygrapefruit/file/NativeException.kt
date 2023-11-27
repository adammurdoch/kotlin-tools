package net.rubygrapefruit.file

class NativeException(message: String) : FileSystemException(message, WinErrorCode.last())
