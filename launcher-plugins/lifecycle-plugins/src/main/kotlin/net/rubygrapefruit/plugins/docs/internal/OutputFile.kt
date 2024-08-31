package net.rubygrapefruit.plugins.docs.internal

import org.commonmark.node.Node
import java.nio.file.Path

class OutputFile(val path: String, val file: Path, val sourceFile: SourceFile) {
    val node: Node
        get() = sourceFile.node
}
