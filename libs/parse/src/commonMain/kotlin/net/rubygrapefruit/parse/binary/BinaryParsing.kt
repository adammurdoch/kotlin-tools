package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.parse

/**
 * Attempts to parse the given input. Fails when the parser cannot match the entire input.
 */
fun <OUT> Parser<BinaryInput, OUT>.parse(input: ByteArray): ParseResult<BinaryFailureContext, OUT> {
    return parse(this, ArrayByteStream(input), ::failureFormatter)
}

/**
 * Creates a [BinaryPushParser] that will attempt to match input as it becomes available.
 * Fails when the parser cannot match the entire input.
 */
fun <OUT> Parser<BinaryInput, OUT>.pushParser(): BinaryPushParser<OUT> {
    return DefaultBinaryPushParser(this, ::failureFormatter)
}

private fun failureFormatter(context: BinaryFailureContext, message: String): String {
    return "Offset: ${context.position.offset.value}: $message. Found: ${context.found}"
}
