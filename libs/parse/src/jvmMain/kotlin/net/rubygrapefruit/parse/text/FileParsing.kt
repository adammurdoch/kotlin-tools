package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.Parser
import java.io.File
import java.io.Reader
import java.nio.charset.Charset

/**
 * Attempts to parse the given file. Fails when the parser cannot match the entire input.
 */
fun <OUT> Parser<TextInput, OUT>.parse(file: File, charset: Charset): ParseResult<TextFailureContext, OUT> {
    return file.reader(charset).use { reader -> parse(reader) }
}

/**
 * Attempts to parse the given reader. Fails when the parser cannot match the entire input.
 */
fun <OUT> Parser<TextInput, OUT>.parse(reader: Reader): ParseResult<TextFailureContext, OUT> {
    val parser = pushParser()
    val buffer = CharArray(16 * 1024)
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