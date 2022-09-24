package net.rubygrapefruit.file

actual class FileSystem {
    actual companion object {
        actual val currentDirectory: Directory
            get() = getCurrentDir()

        actual val userHomeDirectory: Directory
            get() = getUserHomeDir()
    }
}