package net.rubygrapefruit.file

private class WinFileSystem : FileSystem {
    override val currentDirectory: Directory = TODO()

    override val userHomeDirectory: Directory = TODO()
}

actual val fileSystem: FileSystem = WinFileSystem()
