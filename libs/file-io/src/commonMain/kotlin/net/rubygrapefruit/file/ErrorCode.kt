package net.rubygrapefruit.file

internal interface ErrorCode {
    /**
     * Can be empty.
     */
    val formatted: String
}

internal object NoErrorCode : ErrorCode {
    override val formatted: String
        get() = ""
}
