package net.rubygrapefruit.file

import java.nio.file.Path
import kotlin.io.path.name
import kotlin.io.path.pathString

internal data class JvmElementPath(val delegate: Path) : ElementPath {
    override fun toString(): String {
        return absolutePath
    }

    override val name: String
        get() = delegate.name

    override val absolutePath: String
        get() = delegate.pathString

    override val parent: JvmElementPath?
        get() {
            val parent = delegate.parent
            return if (parent == null) {
                null
            } else {
                JvmElementPath(delegate)
            }
        }

    override fun resolve(path: String): JvmElementPath {
        return JvmElementPath(delegate.resolve(path))
    }

    override fun snapshot(): Result<ElementSnapshot> {
        return metadata(delegate).map { JvmSnapshot(this, it) }
    }
}