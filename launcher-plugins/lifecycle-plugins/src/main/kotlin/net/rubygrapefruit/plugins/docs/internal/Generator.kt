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
        outputFiles.entryFile.render()
        outputFiles.files.iterator().forEach { file ->
            file.render()
        }
    }

    private fun outputFiles(sourceInfo: List<SourceFile>, outputFile: Path, outputDir: Path): OutputFiles {
        val entryFile = sourceInfo.find { it.name == outputFile.name }
        if (entryFile == null) {
            throw IllegalArgumentException("Could not locate source file ${outputFile.name}")
        }
        entryFile.node.accept(object : AbstractVisitor() {
            override fun visit(link: Link) {
                val target = sourceInfo.find { it.name == link.destination }
                if (target != null) {
                    val targetFile = outputDir.resolve(target.name)
                    link.destination = targetFile.relativeTo(outputFile.parent).pathString
                }
            }
        })
        val otherFiles = sourceInfo.mapNotNull { sourceFile ->
            if (sourceFile == entryFile) {
                null
            } else {
                OutputFile(sourceFile.name, outputDir.resolve(sourceFile.name), sourceFile)
            }
        }
        return OutputFiles(OutputFile(entryFile.name, outputFile, entryFile), otherFiles)
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