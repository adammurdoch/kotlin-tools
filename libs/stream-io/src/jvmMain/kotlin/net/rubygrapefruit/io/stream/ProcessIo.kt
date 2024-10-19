package net.rubygrapefruit.io.stream

import kotlinx.io.RawSink
import kotlinx.io.RawSource

actual val stdout: RawSink
    get() = OutputStreamBackedRawSink(System.out)

actual val stderr: RawSink
    get() = OutputStreamBackedRawSink(System.err)

actual val stdin: RawSource
    get() = InputStreamBackedRawSource(ThisProcess("standard input"), System.`in`)
