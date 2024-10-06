package net.rubygrapefruit.io

import net.rubygrapefruit.io.stream.StreamSource

internal fun isNotFile(source: StreamSource) = IOException("Could not read from ${source.displayName} as it is not a file.")

internal fun readFile(source: StreamSource, errorCode: ErrorCode) = IOException("Could not read from ${source.displayName}.", errorCode)
