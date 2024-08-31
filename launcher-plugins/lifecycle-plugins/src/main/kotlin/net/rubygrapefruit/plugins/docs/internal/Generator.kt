package net.rubygrapefruit.plugins.docs.internal

import org.commonmark.node.AbstractVisitor
import org.commonmark.node.Link
import org.commonmark.parser.Parser
import org.commonmark.renderer.markdown.MarkdownRenderer
import java.nio.file.Path
import kotlin.io.path.bufferedWriter
import kotlin.io.path.name
import kotlin.io.path.pathString
import kotlin.io.path.readText
import kotlin.io.path.relativeTo

class Generator {
    val renderer = MarkdownRenderer.builder().build();

    fun generate(sourceFiles: List<Path>, outputFile: Path, outputDir: Path) {
        val sourceInfo = parse(sourceFiles)
        val outputFiles = outputFiles(sourceInfo, outputFile, outputDir)
        for (file in outputFiles) {
            file.render()
        }
    }

    private fun outputFiles(sourceFiles: List<SourceFile>, outputFile: Path, outputDir: Path): List<OutputFile> {
        val entryFile = sourceFiles.find { it.name == outputFile.name }
        if (entryFile == null) {
            throw IllegalArgumentException("Could not locate source file ${outputFile.name}")
        }

        val outputFiles = sourceFiles.map { sourceFile ->
            if (sourceFile == entryFile) {
                OutputFile(outputFile, sourceFile)
            } else {
                OutputFile(outputDir.resolve(sourceFile.name), sourceFile)
            }
        }
        val locations = outputFiles.associateBy { it.sourceFile.file }

        for (outputFile in outputFiles) {
            outputFile.sourceFile.node.accept(object : AbstractVisitor() {
                override fun visit(link: Link) {
                    val target = locations[outputFile.sourceFile.file.parent.resolve(link.destination)]
                    if (target != null) {
                        link.destination = target.file.relativeTo(outputFile.file.parent).pathString
                    } else {
                        throw IllegalArgumentException("Could not locate target for link: ${link.destination}")
                    }
                }
            })
        }
        return outputFiles
    }

    private fun parse(sourceFiles: List<Path>): List<SourceFile> {
        val parser = Parser.builder().build()
        return sourceFiles.map { sourceFile ->
            SourceFile(sourceFile, parser.parse(sourceFile.readText()))
        }
    }

    private fun OutputFile.render() {
        file.bufferedWriter().use { writer ->
            writer.write(
                """
                <!--
                  This document was generated from ${sourceFile.file.relativeTo(file.parent)} 
                -->


                """.trimIndent()
            )
            renderer.render(node, writer)
        }
    }
}