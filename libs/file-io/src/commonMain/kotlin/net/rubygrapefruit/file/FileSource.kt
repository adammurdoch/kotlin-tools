package net.rubygrapefruit.file

import net.rubygrapefruit.error.ErrorCode
import net.rubygrapefruit.io.IOException
import net.rubygrapefruit.io.stream.StreamSource

internal class FileSource(val file: RegularFile) : StreamSource {
    override val displayName: String
        get() = "file ${file.absolutePath}"

    override fun readFailed(cause: Throwable): IOException {
        return readFile(file, cause = cause)
    }

    override fun readFailed(errorCode: ErrorCode): IOException {
        return readFile(file, errorCode = errorCode)
    }
}
