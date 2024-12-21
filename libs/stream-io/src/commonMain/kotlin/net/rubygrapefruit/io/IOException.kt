package net.rubygrapefruit.io

import net.rubygrapefruit.error.ErrorCode
import net.rubygrapefruit.error.NoErrorCode

/**
 * Thrown from an IO operation.
 */
open class IOException(message: String, errorCode: ErrorCode = NoErrorCode, cause: Throwable? = null) : Exception(errorCode.applyTo(message), cause)
