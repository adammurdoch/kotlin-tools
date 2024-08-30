package net.rubygrapefruit.plugins.docs.internal

import java.nio.file.Path
import kotlin.io.path.bufferedWriter

class Generator {
    fun generate(outputFile: Path) {
        outputFile.bufferedWriter().use { writer ->
            writer.write("This is the README")
        }
    }
}