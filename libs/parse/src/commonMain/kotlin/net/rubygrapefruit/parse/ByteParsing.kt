package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.byte.ByteArrayInput
import net.rubygrapefruit.parse.byte.ByteInput

/**
 * Attempts to parse the given input. Fails when the parser cannot match the entire input.
 */
fun <OUT> Parser<ByteStream, OUT>.parse(input: ByteArray): ParseResult<OUT> {
    @Suppress("UNCHECKED_CAST")
    val parser = this as ConsumingParser<ByteInput, OUT>
    return parse(parser, ByteArrayInput(input))
}