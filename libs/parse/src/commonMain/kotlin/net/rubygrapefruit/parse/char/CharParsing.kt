package net.rubygrapefruit.parse.char

import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.start
import net.rubygrapefruit.parse.parse

/**
 * Attempts to parse the given input. Fails when the parser cannot match the entire input.
 */
fun <OUT> Parser<CharInput, OUT>.parse(input: String): ParseResult<CharPosition, OUT> {
    val parser = start<CharStream, OUT>()
    return parse(parser, StringCharStream(input))
}

/**
 * Creates a [CharPushParser] that will attempt to match input as it becomes available.
 * Fails when the parser cannot match the entire input.
 */
fun <OUT> Parser<CharInput, OUT>.pushParser(): CharPushParser<OUT> {
    val parser = start<CharStream, OUT>()
    return DefaultCharPushParser(parser)
}
