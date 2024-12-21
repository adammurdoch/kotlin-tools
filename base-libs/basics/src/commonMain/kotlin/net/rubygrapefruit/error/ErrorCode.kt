package net.rubygrapefruit.error

/**
 * A failure represented by an error code.
 */
interface ErrorCode {
    /**
     * Can be empty.
     */
    val formatted: String
}

data object NoErrorCode : ErrorCode {
    override val formatted: String
        get() = ""
}
