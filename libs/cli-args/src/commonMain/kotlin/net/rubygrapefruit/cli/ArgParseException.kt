package net.rubygrapefruit.cli

open class ArgParseException internal constructor(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)
