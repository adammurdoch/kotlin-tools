package net.rubygrapefruit.plugins.docs.internal

import org.commonmark.parser.Parser
import org.commonmark.renderer.markdown.MarkdownRenderer
import java.nio.file.Path
import kotlin.io.path.bufferedWriter
import kotlin.io.path.name
import kotlin.io.path.readText

class Generator {
    fun generate(sourceFiles: List<Path>, outputFile: Path) {
        val sourceInfo = parse(sourceFiles)
        outputFile.bufferedWriter().use { writer ->
            val readme = sourceInfo.find { it.name == outputFile.name }
            if (readme != null) {
                val renderer = MarkdownRenderer.builder().build();
                renderer.render(readme.node, writer)
            }
        }
    }

    private fun parse(sourceFiles: List<Path>): List<SourceFile> {
        val parser = Parser.builder().build()
        return sourceFiles.map { sourceFile ->
            SourceFile(sourceFile.name, parser.parse(sourceFile.readText()))
        }
    }
}