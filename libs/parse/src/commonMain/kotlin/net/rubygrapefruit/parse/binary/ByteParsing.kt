package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.start
import net.rubygrapefruit.parse.parse

/**
 * Attempts to parse the given input. Fails when the parser cannot match the entire input.
 */
fun <OUT> Parser<ByteInput, OUT>.parse(input: ByteArray): ParseResult<BytePosition, OUT> {
    val parser = start<ByteStream, OUT>()
    return parse(parser, ArrayByteStream(input))
}

/**
 * Creates a [BytePushParser] that will attempt to match input as it becomes available.
 * Fails when the parser cannot match the entire input.
 */
fun <OUT> Parser<ByteInput, OUT>.pushParser(): BytePushParser<OUT> {
    val parser = start<ByteStream, OUT>()
    return DefaultBytePushParser(parser)
}