package net.rubygrapefruit.file

import net.rubygrapefruit.error.UnixErrorCode

internal class NativeException(message: String) : FileSystemException(message, UnixErrorCode.last())
