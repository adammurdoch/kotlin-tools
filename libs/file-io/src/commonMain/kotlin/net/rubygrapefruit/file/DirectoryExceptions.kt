package net.rubygrapefruit.file

import net.rubygrapefruit.io.ErrorCode
import net.rubygrapefruit.io.NoErrorCode

class MissingDirectoryException(message: String, errorCode: ErrorCode = NoErrorCode, cause: Throwable? = null) : FileSystemException(message, errorCode, cause)

class DirectoryPermissionException(message: String, errorCode: ErrorCode = NoErrorCode, cause: Throwable? = null) : FileSystemException(message, errorCode, cause)

internal class NotADirectoryException(message: String, errorCode: ErrorCode = NoErrorCode, cause: Throwable? = null) : FileSystemException(message, errorCode, cause)
