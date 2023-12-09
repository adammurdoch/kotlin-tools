package net.rubygrapefruit.io

/**
 * Thrown from an IO exception.
 */
open class IOException(message: String, cause: Throwable? = null) : Exception(message, cause)
