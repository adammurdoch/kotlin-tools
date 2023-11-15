package net.rubygrapefruit.file

import java.nio.file.Path
import kotlin.io.path.name
import kotlin.io.path.pathString

internal class JvmElementPath(val delegate: Path): ElementPath {
    override fun toString(): String {
        return absolutePath
    }

    override val name: String
        get() = delegate.name

    override val absolutePath: String
        get() = delegate.pathString
}