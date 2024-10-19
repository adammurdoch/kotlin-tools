package net.rubygrapefruit.io.stream

import net.rubygrapefruit.io.ErrorCode
import net.rubygrapefruit.io.IOException
import net.rubygrapefruit.io.readFile

interface StreamSource {
    val displayName: String

    fun readFailed(errorCode: ErrorCode): IOException {
        return readFile(this, errorCode = errorCode)
    }

    fun readFailed(cause: Throwable): IOException {
        return readFile(this, cause = cause)
    }
}