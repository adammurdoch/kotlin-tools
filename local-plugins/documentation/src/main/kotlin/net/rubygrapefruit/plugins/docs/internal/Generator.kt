package net.rubygrapefruit.plugins.docs.internal

import java.nio.file.Path

class Generator {
    private val parser = Parser()
    private val transformer = Transformer()
    private val renderer = MarkdownRenderer()

    fun generate(sourceFiles: List<Path>, variables: Map<String, String>, outputFile: Path, outputDir: Path) {
        val sourceInfo = parser.parse(sourceFiles)
        val outputFiles = transformer.transform(sourceInfo, variables, outputFile, outputDir)
        renderer.render(outputFiles)
    }
}