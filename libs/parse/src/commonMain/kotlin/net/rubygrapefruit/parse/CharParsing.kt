package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.char.CharInput
import net.rubygrapefruit.parse.char.StringInput

/**
 * Attempts to parse the given input. Fails when the parser cannot match the entire input.
 */
fun <OUT> Parser<CharStream, OUT>.parse(input: String): ParseResult<OUT> {
    @Suppress("UNCHECKED_CAST")
    val parser = this as ConsumingParser<CharInput, OUT>
    return parse(parser, StringInput(input))
}