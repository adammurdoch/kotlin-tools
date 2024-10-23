package net.rubygrapefruit.file

/**
 * An entry point into the local file system.
 */
interface FileSystem {
    /**
     * The current directory of this process.
     */
    val currentDirectory: Directory

    /**
     * The current user's home directory.
     */
    val userHomeDirectory: Directory

    companion object {
        val local: FileSystem get() = fileSystem
    }
}

expect val fileSystem: FileSystem
