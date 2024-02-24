@file:OptIn(ExperimentalStdlibApi::class)

package net.rubygrapefruit.store

import net.rubygrapefruit.file.Directory
import net.rubygrapefruit.io.codec.SimpleCodec

internal class FileManager(
    private val store: Directory,
    private val codec: SimpleCodec,
) : AutoCloseable {
    private val files = mutableListOf<AutoCloseable>()

    fun logFile(generation: Int): LogFile {
        val file = LogFile(store.file("log_${generation}.bin"), codec)
        files.add(file)
        return file
    }

    fun dataFile(generation: Int): DataFile {
        val file = DataFile(store.file("data_${generation}.bin"), codec)
        files.add(file)
        return file
    }

    fun discard(file: AutoCloseable) {
        val removed = files.remove(file)
        require(removed)
        file.close()
    }

    override fun close() {
        for (file in files) {
            file.close()
        }
    }
}