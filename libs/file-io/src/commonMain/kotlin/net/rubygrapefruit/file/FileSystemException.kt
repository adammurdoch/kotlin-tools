package net.rubygrapefruit.file

import net.rubygrapefruit.error.ErrorCode
import net.rubygrapefruit.io.IOException

/**
 * An exception thrown when some operation on the file system fails.
 */
open class FileSystemException(message: String, cause: Throwable? = null) : IOException(message, cause = cause) {
    internal constructor(message: String, errorCode: ErrorCode, cause: Throwable? = null) : this(errorCode.applyTo(message), cause)
}
