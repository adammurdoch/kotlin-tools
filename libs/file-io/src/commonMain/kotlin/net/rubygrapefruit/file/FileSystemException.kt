package net.rubygrapefruit.file

/**
 * An exception thrown when some operation on the file system fails.
 */
open class FileSystemException(message: String, cause: Throwable? = null) : Exception(message, cause) {
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
