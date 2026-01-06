package net.rubygrapefruit.parse.char

import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.PullParser
import net.rubygrapefruit.parse.parse

/**
 * Attempts to parse the given input. Fails when the parser cannot match the entire input.
 */
fun <OUT> Parser<CharInput, OUT>.parse(input: String): ParseResult<OUT> {
    @Suppress("UNCHECKED_CAST")
    val parser = this as PullParser<CharStream, OUT>
    return parse(parser, StringCharStream(input))
}

/**
 * Creates a [CharPushParser] that will attempt to match input as it becomes available.
 * Fails when the parser cannot match the entire input.
 */
fun <OUT> Parser<CharInput, OUT>.pushParser(): CharPushParser<OUT> {
    @Suppress("UNCHECKED_CAST")
    val parser = this as PullParser<CharStream, OUT>
    return DefaultCharPushParser(parser)
}