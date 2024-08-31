package net.rubygrapefruit.plugins.docs.internal

import org.commonmark.node.Node

class OutputFile(val path: String, val sourceFile: SourceFile) {
    val node: Node
        get() = sourceFile.node
}
