package net.rubygrapefruit.file

import net.rubygrapefruit.io.stream.StreamSource

internal class FileSource(val path: ElementPath) : StreamSource {
    override val displayName: String
        get() = "file $path"
}
