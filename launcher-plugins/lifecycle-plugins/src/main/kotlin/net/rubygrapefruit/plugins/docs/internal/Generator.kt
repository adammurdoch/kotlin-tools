package net.rubygrapefruit.plugins.docs.internal

import org.commonmark.node.AbstractVisitor
import org.commonmark.node.Link
import org.commonmark.parser.Parser
import org.commonmark.renderer.markdown.MarkdownRenderer
import java.nio.file.Path
import kotlin.io.path.bufferedWriter
import kotlin.io.path.name
import kotlin.io.path.readText

class Generator {
    fun generate(sourceFiles: List<Path>, outputFile: Path) {
        val sourceInfo = parse(sourceFiles)
        val entryFile = this@Generator.entryFile(sourceInfo, outputFile)
        outputFile.bufferedWriter().use { writer ->
            if (entryFile != null) {
                val renderer = MarkdownRenderer.builder().build();
                renderer.render(entryFile.node, writer)
            }
        }
    }

    private fun entryFile(
        sourceInfo: List<SourceFile>,
        outputFile: Path
    ): SourceFile? {
        val sourceFile = sourceInfo.find { it.name == outputFile.name }
        return if (sourceFile != null) {
            sourceFile.node.accept(object : AbstractVisitor() {
                override fun visit(link: Link) {
                    val target = sourceInfo.find { it.name == link.destination }
                    if (target != null) {
                        link.destination = "src/docs/${target.name}"
                    }
                }
            })
            sourceFile
        } else {
            null
        }
    }

    private fun parse(sourceFiles: List<Path>): List<SourceFile> {
        val parser = Parser.builder().build()
        return sourceFiles.map { sourceFile ->
            SourceFile(sourceFile.name, parser.parse(sourceFile.readText()))
        }
    }
}