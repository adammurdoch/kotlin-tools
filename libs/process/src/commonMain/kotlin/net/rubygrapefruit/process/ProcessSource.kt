package net.rubygrapefruit.process

import net.rubygrapefruit.io.stream.StreamSource

internal object ProcessSource : StreamSource {
    override val displayName: String
        get() = "child process"
}
