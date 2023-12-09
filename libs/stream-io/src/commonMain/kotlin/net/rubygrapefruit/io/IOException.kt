package net.rubygrapefruit.io

/**
 * Thrown from an IO operation.
 */
open class IOException(message: String, cause: Throwable? = null) : Exception(message, cause) {

    constructor(message: String, errorCode: ErrorCode) : this(format(message, errorCode))

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
