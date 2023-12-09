package net.rubygrapefruit.io.stream

import net.rubygrapefruit.io.DefaultFailure
import net.rubygrapefruit.io.DefaultSuccess
import net.rubygrapefruit.io.IOException

sealed interface ReadResult

class ReadBytes(count: Int) : ReadResult, DefaultSuccess<Int, IOException>(count)

data object EndOfStream : ReadResult, DefaultSuccess<Unit, IOException>(Unit)

class ReadFailed(exception: IOException) : ReadResult, DefaultFailure<Unit, IOException>(exception)
