package net.rubygrapefruit.parse.byte

import net.rubygrapefruit.parse.ConsumingParser
import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.Parser

/**
 * Attempts to parse the given input. Fails when the parser cannot match the entire input.
 */
fun <OUT> Parser<ByteStream, OUT>.parse(input: ByteArray): ParseResult<OUT> {
    @Suppress("UNCHECKED_CAST")
    val parser = this as ConsumingParser<ByteInput, OUT>
    return net.rubygrapefruit.parse.parse(parser, ByteArrayInput(input))
}