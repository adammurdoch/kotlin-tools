package net.rubygrapefruit.io

/**
 * Thrown from an IO operation.
 */
open class IOException(message: String, cause: Throwable? = null) : Exception(message, cause)
