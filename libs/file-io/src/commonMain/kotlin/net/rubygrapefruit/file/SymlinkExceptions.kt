package net.rubygrapefruit.file

import net.rubygrapefruit.io.ErrorCode
import net.rubygrapefruit.io.NoErrorCode

class SymlinkPermissionException(message: String, errorCode: ErrorCode = NoErrorCode, cause: Throwable? = null) : FileSystemException(message, errorCode, cause)
