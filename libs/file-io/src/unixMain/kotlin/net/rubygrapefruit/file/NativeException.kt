package net.rubygrapefruit.file

class NativeException(message: String) : FileSystemException(message, UnixErrorCode.last())
