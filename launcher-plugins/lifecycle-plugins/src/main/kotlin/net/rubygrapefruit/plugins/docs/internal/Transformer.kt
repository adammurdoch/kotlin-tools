package net.rubygrapefruit.plugins.docs.internal

import org.commonmark.node.AbstractVisitor
import org.commonmark.node.Link
import org.commonmark.node.Node
import org.commonmark.node.Text
import java.nio.file.Path
import java.util.regex.Pattern
import kotlin.io.path.name
import kotlin.io.path.pathString
import kotlin.io.path.relativeTo

class Transformer {
    /**
     * Transforms the source model into an output model that can be rendered.
     */
    fun transform(sourceFiles: List<SourceFile>, outputFile: Path, outputDir: Path): List<OutputFile> {
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
                    link.destination = mapLink(link.destination).first
                }

                override fun visit(text: Text) {
                    val pattern = Pattern.compile("\\[\\[([^]]+)]]")
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
                        val target = mapLink(destination)
                        link.destination = target.first
                        val title = target.second.sourceFile.title
                        if (title != null) {
                            title.accept(object : AbstractVisitor() {
                                override fun visit(text: Text) {
                                    link.appendChild(Text(text.literal))
                                }
                            })
                        } else {
                            link.appendChild(Text(destination))
                        }

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

                private fun mapLink(destination: String): Pair<String, OutputFile> {
                    val target = locations[outputFile.sourceFile.file.parent.resolve(destination)]
                    if (target != null) {
                        return Pair(target.file.relativeTo(outputFile.file.parent).pathString, target)
                    } else {
                        throw IllegalArgumentException("Could not locate target for link: ${destination}")
                    }
                }
            })
        }
        return outputFiles
    }
}