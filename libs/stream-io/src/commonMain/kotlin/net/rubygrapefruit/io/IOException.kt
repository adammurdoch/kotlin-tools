package net.rubygrapefruit.io

/**
 * Thrown from an IO operation.
 */
open class IOException(message: String, errorCode: ErrorCode = NoErrorCode, cause: Throwable? = null) : Exception(format(message, errorCode), cause) {
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
