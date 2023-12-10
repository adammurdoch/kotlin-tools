package net.rubygrapefruit.process

import net.rubygrapefruit.io.stream.ReadDescriptor
import net.rubygrapefruit.io.stream.WriteDescriptor
import platform.posix.close

fun ReadDescriptor.close() {
    close(descriptor)
}

fun WriteDescriptor.close() {
    close(descriptor)
}