package net.rubygrapefruit.file

import java.nio.file.Path

actual class FileSystem {
    actual companion object {
        /**
         * The current directory of this process.
         */
        actual val currentDirectory: Directory
            get() = Directory(Path.of(".").toAbsolutePath().normalize())

        /**
         * The user's home directory.
         */
        actual val userHomeDirectory: Directory
            get() = Directory(Path.of(System.getProperty("user.home")).toAbsolutePath().normalize())
    }
}