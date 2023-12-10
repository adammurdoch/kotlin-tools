package net.rubygrapefruit.process

import net.rubygrapefruit.io.stream.FileDescriptor
import platform.posix.close

fun FileDescriptor.close() {
    close(descriptor)
}