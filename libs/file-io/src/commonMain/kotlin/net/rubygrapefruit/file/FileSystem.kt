package net.rubygrapefruit.file

expect class FileSystem {
    companion object {
        /**
         * The current directory of this process.
         */
        val currentDirectory: Directory

        /**
         * The user's home directory.
         */
        val userHomeDirectory: Directory
    }
}