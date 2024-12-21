package net.rubygrapefruit.io

import net.rubygrapefruit.error.ErrorCode
import net.rubygrapefruit.error.NoErrorCode
import net.rubygrapefruit.io.stream.StreamSource

internal fun isNotFile(source: StreamSource) = IOException("Could not read from ${source.displayName} as it is not a file.")

internal fun readFile(source: StreamSource, errorCode: ErrorCode = NoErrorCode, cause: Throwable? = null) = IOException("Could not read from ${source.displayName}.", errorCode, cause)
