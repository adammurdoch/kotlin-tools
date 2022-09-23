package net.rubygrapefruit.file

internal actual fun stat(file: FileSystemElement): FileSystemElementMetadata {
    TODO()
}

internal actual fun getUserHomeDir(): Directory {
    TODO()
}

internal actual fun getCurrentDir(): Directory {
    TODO()
}

internal actual fun createTempDir(baseDir: Directory): Directory {
    TODO()
}

internal actual fun createDir(dir: Directory) {
    TODO()
}

internal actual fun writeToFile(file: RegularFile, text: String) {
    TODO()
}
