package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.Parser
import java.io.File

/**
 * Attempts to parse the given file. Fails when the parser cannot match the entire input.
 */
fun <OUT> Parser<BinaryInput, OUT>.parse(file: File): ParseResult<BinaryFailureContext, OUT> {
    return file.inputStream().use { inputStream -> parse(inputStream) }
}
