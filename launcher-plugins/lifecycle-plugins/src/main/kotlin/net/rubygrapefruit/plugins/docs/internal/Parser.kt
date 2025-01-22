package net.rubygrapefruit.plugins.docs.internal

import org.commonmark.node.AbstractVisitor
import org.commonmark.node.Heading
import org.commonmark.parser.Parser
import java.nio.file.Path
import kotlin.io.path.readText

class Parser {
    /**
     * Parses a source model from the given source files.
     */
    fun parse(sourceFiles: List<Path>): List<SourceFile> {
        val parser = Parser.builder().build()
        return sourceFiles.map { sourceFile ->
            val node = parser.parse(sourceFile.readText())
            var title: Heading? = null
            node.accept(object : AbstractVisitor() {
                override fun visit(heading: Heading) {
                    if (heading.level == 1) {
                        title = heading
                    }
                }
            })
            SourceFile(sourceFile, node, title)
        }
    }
}