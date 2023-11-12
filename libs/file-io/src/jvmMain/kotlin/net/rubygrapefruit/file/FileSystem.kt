package net.rubygrapefruit.file

import java.nio.file.Path

private class JvmFileSystem : FileSystem {
    /**
     * The current directory of this process.
     */
    override val currentDirectory: Directory
        get() = JvmDirectory(Path.of(".").toAbsolutePath().normalize())

    /**
     * The user's home directory.
     */
    override val userHomeDirectory: Directory
        get() = JvmDirectory(Path.of(System.getProperty("user.home")).toAbsolutePath().normalize())
}

actual val fileSystem: FileSystem = JvmFileSystem()
