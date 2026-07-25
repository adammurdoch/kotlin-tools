package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.Parser
import java.io.Reader


/**
 * Attempts to parse the given reader. Fails when the parser cannot match the entire input.
 */
fun <OUT> Parser<TextInput, OUT>.parse(reader: Reader): ParseResult<TextFailureContext, OUT> {
    val parser = pushParser()
    val failure = parser.takeFrom { buffer, offset, max ->
        reader.read(buffer, offset, max)
    }
    if (failure != null) {
        return failure
    }
    return parser.endOfInput()
}