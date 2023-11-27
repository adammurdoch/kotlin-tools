package net.rubygrapefruit.file

internal class WinPath(override val absolutePath: String): ElementPath {
    override val name: String
        get() = TODO("Not yet implemented")

    override val parent: ElementPath?
        get() = TODO("Not yet implemented")

    override fun resolve(path: String): WinPath {
        TODO("Not yet implemented")
    }

    override fun snapshot(): Result<ElementSnapshot> {
        TODO("Not yet implemented")
    }
}