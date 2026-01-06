package net.rubygrapefruit.parse.byte

import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.PullParser
import net.rubygrapefruit.parse.parse

/**
 * Attempts to parse the given input. Fails when the parser cannot match the entire input.
 */
fun <OUT> Parser<ByteInput, OUT>.parse(input: ByteArray): ParseResult<OUT> {
    @Suppress("UNCHECKED_CAST")
    val parser = this as PullParser<ByteStream, OUT>
    return parse(parser, ArrayByteStream(input))
}

fun <OUT> Parser<ByteInput, OUT>.pushParser(): BytePushParser<OUT> {
    @Suppress("UNCHECKED_CAST")
    val parser = this as PullParser<ByteStream, OUT>
    return DefaultBytePushParser(parser)
}