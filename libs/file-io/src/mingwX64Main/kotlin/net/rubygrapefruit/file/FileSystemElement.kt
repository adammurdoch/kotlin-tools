package net.rubygrapefruit.file

internal actual fun stat(file: String): FileSystemElementMetadata {
    TODO()
}

internal actual fun getUserHomeDir(): Directory {
    TODO()
}

internal actual fun getCurrentDir(): Directory {
    TODO()
}

internal actual fun createTempDir(baseDir: NativeDirectory): Directory {
    TODO()
}

internal actual fun createDir(dir: NativeDirectory) {
    TODO()
}

internal actual fun writeToFile(file: NativeRegularFile, text: String) {
    TODO()
}

internal actual fun readFromFile(file: NativeRegularFile): String {
    TODO()
}
