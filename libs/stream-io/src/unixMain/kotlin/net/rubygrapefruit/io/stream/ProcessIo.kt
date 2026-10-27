package net.rubygrapefruit.io.stream

import kotlinx.io.RawSink
import kotlinx.io.RawSource
import platform.posix.STDERR_FILENO
import platform.posix.STDIN_FILENO
import platform.posix.STDOUT_FILENO

actual val stdout: RawSink
    get() = FileDescriptorBackedRawSink(ThisProcess("standard output"), WriteDescriptor(STDOUT_FILENO))

actual val stderr: RawSink
    get() = FileDescriptorBackedRawSink(ThisProcess("standard error"), WriteDescriptor(STDERR_FILENO))

actual val stdin: RawSource
    get() = FileDescriptorBackedRawSource(ThisProcess("standard input"), ReadDescriptor(STDIN_FILENO))
