package net.rubygrapefruit.plugins.docs.internal

import org.commonmark.renderer.markdown.MarkdownRenderer
import kotlin.io.path.bufferedWriter
import kotlin.io.path.relativeTo

class MarkdownRenderer {
    private val renderer = MarkdownRenderer.builder().build()

    fun render(files: List<OutputFile>) {
        for (file in files) {
            render(file)
        }
    }

    private fun render(output: OutputFile) {
        output.file.bufferedWriter().use { writer ->
            writer.write(
                """
                <!--
                  This document was generated from ${output.sourceFile.file.relativeTo(output.file.parent)} 
                -->


                """.trimIndent()
            )
            renderer.render(output.node, writer)
        }
    }
}