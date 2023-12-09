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

        fun isNotFile(path: String ) = ReadFailed(IOException("Could not read from file $path as it is not a file."))

        fun readFile(path: String, errorCode: ErrorCode) = ReadFailed(IOException("Could not read from file $path.", errorCode))
    }
}
