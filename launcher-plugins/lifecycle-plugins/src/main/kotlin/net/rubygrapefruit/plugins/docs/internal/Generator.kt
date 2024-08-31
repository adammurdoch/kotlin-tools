package net.rubygrapefruit.plugins.docs.internal

import org.commonmark.node.AbstractVisitor
import org.commonmark.node.Link
import org.commonmark.node.Node
import org.commonmark.node.Text
import org.commonmark.parser.Parser
import org.commonmark.renderer.markdown.MarkdownRenderer
import java.nio.file.Path
import java.util.regex.Pattern
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
                    link.destination = mapLink(link.destination)
                }

                override fun visit(text: Text) {
                    val pattern = Pattern.compile("\\(([^)]+)\\)")
                    val matcher = pattern.matcher(text.literal)
                    var prev: Node? = null
                    var pos = 0
                    while (pos < text.literal.length) {
                        if (!matcher.find(pos)) {
                            if (prev != null) {
                                prev.insertAfter(Text(text.literal.substring(pos)))
                            }
                            // Else, no matches in string
                            break
                        }

                        val destination = matcher.group(1)
                        val link = Link()
                        link.destination = mapLink(destination)
                        link.appendChild(Text(destination))

                        val fragment = text.literal.substring(pos, matcher.start())
                        if (prev == null) {
                            prev = Text(fragment)
                            text.insertAfter(prev)
                            text.unlink()
                        } else if (fragment.isNotEmpty()) {
                            val next = Text(fragment)
                            prev.insertAfter(next)
                            prev = next
                        }

                        prev.insertAfter(link)
                        pos = matcher.end()
                        prev = link
                    }
                }

                private fun mapLink(destination: String): String {
                    val target = locations[outputFile.sourceFile.file.parent.resolve(destination)]
                    if (target != null) {
                        return target.file.relativeTo(outputFile.file.parent).pathString
                    } else {
                        throw IllegalArgumentException("Could not locate target for link: ${destination}")
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