package net.rubygrapefruit.plugins.docs.internal

import org.commonmark.node.Heading
import org.commonmark.node.Node
import java.nio.file.Path
import kotlin.io.path.name

class SourceFile(val file: Path, val node: Node, val title: Heading?) {
    val name: String
        get() = file.name
}
