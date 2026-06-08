package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.Parser
import java.io.File
import java.nio.charset.Charset

/**
 * Attempts to parse the given file. Fails when the parser cannot match the entire input.
 */
fun <OUT> Parser<TextInput, OUT>.parse(file: File, charset: Charset): ParseResult<TextFailureContext, OUT> {
    return file.reader(charset).use { reader -> parse(reader) }
}
