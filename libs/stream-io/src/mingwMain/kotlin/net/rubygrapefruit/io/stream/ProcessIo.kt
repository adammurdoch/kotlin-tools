package net.rubygrapefruit.io.stream

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.io.RawSink
import kotlinx.io.RawSource
import platform.windows.GetStdHandle
import platform.windows.STD_ERROR_HANDLE
import platform.windows.STD_INPUT_HANDLE
import platform.windows.STD_OUTPUT_HANDLE

@OptIn(ExperimentalForeignApi::class)
actual val stdout: RawSink
    get() = FileBackedRawSink(ThisProcess("standard output"), GetStdHandle(STD_OUTPUT_HANDLE)!!)

@OptIn(ExperimentalForeignApi::class)
actual val stderr: RawSink
    get() = FileBackedRawSink(ThisProcess("standard error"), GetStdHandle(STD_ERROR_HANDLE)!!)

@OptIn(ExperimentalForeignApi::class)
actual val stdin: RawSource
    get() = FileBackedRawSource(ThisProcess("standard input"), GetStdHandle(STD_INPUT_HANDLE)!!)
