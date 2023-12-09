package net.rubygrapefruit.file

import net.rubygrapefruit.io.UnixErrorCode

internal class NativeException(message: String) : FileSystemException(message, UnixErrorCode.last())
