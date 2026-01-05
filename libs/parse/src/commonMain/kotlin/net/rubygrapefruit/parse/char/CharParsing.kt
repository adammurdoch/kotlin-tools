package net.rubygrapefruit.parse.char

import net.rubygrapefruit.parse.ConsumingParser
import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.Parser

/**
 * Attempts to parse the given input. Fails when the parser cannot match the entire input.
 */
fun <OUT> Parser<CharStream, OUT>.parse(input: String): ParseResult<OUT> {
    @Suppress("UNCHECKED_CAST")
    val parser = this as ConsumingParser<CharInput, OUT>
    return net.rubygrapefruit.parse.parse(parser, StringInput(input))
}