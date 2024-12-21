package net.rubygrapefruit.file

import net.rubygrapefruit.error.ErrorCode
import net.rubygrapefruit.error.NoErrorCode

class SymlinkPermissionException(message: String, errorCode: ErrorCode = NoErrorCode, cause: Throwable? = null) : FileSystemException(message, errorCode, cause)
