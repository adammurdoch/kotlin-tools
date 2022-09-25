package net.rubygrapefruit.file

/**
 * An entry point into the local file system.
 */
expect class FileSystem {
    companion object {
        /**
         * The current directory of this process.
         */
        val currentDirectory: Directory

        /**
         * The current user's home directory.
         */
        val userHomeDirectory: Directory
    }
}