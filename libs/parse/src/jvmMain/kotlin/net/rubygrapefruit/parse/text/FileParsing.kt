package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.Parser
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.Charset

/**
 * Attempts to parse the given file as text. Fails when the parser cannot match the entire input.
 */
fun <OUT> Parser<TextInput, OUT>.parse(file: File, charset: Charset): ParseResult<TextFailureContext, OUT> {
    return file.reader(charset).use { reader -> parse(reader) }
}

private fun <OUT> Parser<TextInput, OUT>.parse(reader: InputStreamReader): ParseResult<TextFailureContext, OUT> {
    val parser = pushParser()
    val buffer = CharArray(1024)
    while (true) {
        val nread = reader.read(buffer)
        if (nread < 0) {
            break
        }
        val failure = parser.input(buffer, 0, nread)
        if (failure != null) {
            return failure
        }
    }
    return parser.endOfInput()
}