package net.rubygrapefruit.error

/**
 * A failure represented by an error code.
 */
interface ErrorCode {
    /**
     * Can be empty.
     */
    val formatted: String

    /**
     * Returns the given error message with details of the error code appended.
     */
    fun applyTo(message: String): String {
        val formattedCode = formatted
        return if (formattedCode.isEmpty()) {
            message
        } else if (message.endsWith(".")) {
            "$message $formattedCode"
        } else {
            "${message}. $formattedCode"
        }
    }
}

data object NoErrorCode : ErrorCode {
    override val formatted: String
        get() = ""

    override fun applyTo(message: String): String {
        return message
    }
}
