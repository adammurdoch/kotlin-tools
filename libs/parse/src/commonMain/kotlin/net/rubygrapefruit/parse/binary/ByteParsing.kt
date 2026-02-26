package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.parse
import net.rubygrapefruit.parse.start

/**
 * Attempts to parse the given input. Fails when the parser cannot match the entire input.
 */
fun <OUT> Parser<BinaryInput, OUT>.parse(input: ByteArray): ParseResult<ByteFailureContext, OUT> {
    val parser = start<ByteStream, OUT>()
    return parse(parser, ArrayByteStream(input), ::failureFactory)
}

/**
 * Creates a [BytePushParser] that will attempt to match input as it becomes available.
 * Fails when the parser cannot match the entire input.
 */
fun <OUT> Parser<BinaryInput, OUT>.pushParser(): BytePushParser<OUT> {
    val parser = start<ByteStream, OUT>()
    return DefaultBytePushParser(parser)
}

internal fun failureFactory(input: AdvancingByteStream, index: Int, message: String): ParseResult.Fail<ByteFailureContext> {
    return ParseResult.Fail(input.contextAt(index), message) { context, message -> "Offset: ${context.position.offset}: $message" }
}