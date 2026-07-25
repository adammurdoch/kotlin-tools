package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.Parser
import java.io.InputStream

/**
 * Attempts to parse the input stream. Fails when the parser cannot match the entire input.
 */
fun <OUT> Parser<BinaryInput, OUT>.parse(inputStream: InputStream): ParseResult<BinaryFailureContext, OUT> {
    val parser = pushParser()
    val failure = parser.takeFrom { buffer, offset, max ->
        inputStream.read(buffer, offset, max)
    }
    if (failure != null) {
        return failure
    }
    return parser.endOfInput()
}
