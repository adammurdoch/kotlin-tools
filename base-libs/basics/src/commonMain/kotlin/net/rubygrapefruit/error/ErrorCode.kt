package net.rubygrapefruit.error

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
