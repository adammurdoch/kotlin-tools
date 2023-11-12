package net.rubygrapefruit.file

private class NativeFileSystem : FileSystem {
    override val currentDirectory: Directory
        get() = getCurrentDir()

    override val userHomeDirectory: Directory
        get() = getUserHomeDir()
}

actual val fileSystem: FileSystem = NativeFileSystem()
