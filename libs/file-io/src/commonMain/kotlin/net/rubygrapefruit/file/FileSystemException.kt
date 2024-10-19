package net.rubygrapefruit.file

import net.rubygrapefruit.io.ErrorCode
import net.rubygrapefruit.io.IOException

/**
 * An exception thrown when some operation on the file system fails.
 */
open class FileSystemException(message: String, cause: Throwable? = null) : IOException(message, cause = cause) {
    internal constructor(message: String, errorCode: ErrorCode, cause: Throwable? = null) : this(format(message, errorCode), cause)

    private companion object {
        private fun format(message: String, errorCode: ErrorCode): String {
            val formattedCode = errorCode.formatted
            return if (formattedCode.isEmpty()) {
                message
            } else if (message.endsWith(".")) {
                "$message $formattedCode"
            } else {
                "${message}. $formattedCode"
            }
        }
    }
}
