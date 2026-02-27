package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.parse
import net.rubygrapefruit.parse.start

/**
 * Attempts to parse the given input. Fails when the parser cannot match the entire input.
 */
fun <OUT> Parser<BinaryInput, OUT>.parse(input: ByteArray): ParseResult<BinaryFailureContext, OUT> {
    val parser = start<ByteStream, OUT>()
    return parse(parser, ArrayByteStream(input), ::failureFormatter)
}

/**
 * Creates a [BinaryPushParser] that will attempt to match input as it becomes available.
 * Fails when the parser cannot match the entire input.
 */
fun <OUT> Parser<BinaryInput, OUT>.pushParser(): BinaryPushParser<OUT> {
    val parser = start<ByteStream, OUT>()
    return DefaultBinaryPushParser(parser, ::failureFormatter)
}

private fun failureFormatter(context: BinaryFailureContext, message: String): String {
    return "Offset: ${context.position.offset}: $message, found: ${context.found}"
}
