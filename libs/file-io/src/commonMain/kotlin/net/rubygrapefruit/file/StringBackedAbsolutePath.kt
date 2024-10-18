package net.rubygrapefruit.file

import kotlin.collections.toMutableList
import kotlin.text.split
import kotlin.text.substringAfterLast

internal abstract class StringBackedAbsolutePath : ElementPath {
    override val name: String
        get() = absolutePath.substringAfterLast(separator)

    abstract override val parent: StringBackedAbsolutePath?

    protected abstract val separator: Char

    protected abstract fun isAbsolute(path: String): Boolean

    abstract fun child(name: String): StringBackedAbsolutePath

    protected fun resolve(base: StringBackedAbsolutePath, path: String): StringBackedAbsolutePath {
        val elements = path.split(separator).toMutableList()
        var current = base
        for (element in elements) {
            if (element == "" || element == ".") {
                continue
            }
            if (element == "..") {
                current = current.parent ?: current
            } else {
                current = current.child(element)
            }
        }
        return current
    }
}