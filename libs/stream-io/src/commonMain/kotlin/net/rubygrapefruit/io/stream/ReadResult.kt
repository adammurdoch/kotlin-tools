package net.rubygrapefruit.io.stream

import net.rubygrapefruit.io.DefaultFailure
import net.rubygrapefruit.io.DefaultSuccess
import net.rubygrapefruit.io.ErrorCode
import net.rubygrapefruit.io.IOException

sealed interface ReadResult

class ReadBytes(count: Int) : ReadResult, DefaultSuccess<Int, IOException>(count)

data object EndOfStream : ReadResult, DefaultSuccess<Unit, IOException>(Unit)

class ReadFailed(exception: IOException) : ReadResult, DefaultFailure<Unit, IOException>(exception) {
    companion object {

        fun isNotFile(source: StreamSource) = ReadFailed(IOException("Could not read from ${source.displayName} as it is not a file."))

        fun readFile(source: StreamSource, errorCode: ErrorCode) = ReadFailed(IOException("Could not read from ${source.displayName}.", errorCode))
    }
}

interface StreamSource {
    val displayName: String
}