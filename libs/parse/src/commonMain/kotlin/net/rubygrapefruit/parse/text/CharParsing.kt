package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.parse
import net.rubygrapefruit.parse.start

/**
 * Attempts to parse the given string. Fails when the parser cannot match the entire input.
 */
fun <OUT> Parser<CharInput, OUT>.parse(input: String): ParseResult<CharFailureContext, OUT> {
    val parser = start<CharStream, OUT>()
    return parse(parser, StringCharStream(input), ::failureFactory)
}

/**
 * Creates a [CharPushParser] that will attempt to match input as it becomes available.
 * Fails when the parser cannot match the entire input.
 */
fun <OUT> Parser<CharInput, OUT>.pushParser(): CharPushParser<OUT> {
    val parser = start<CharStream, OUT>()
    return DefaultCharPushParser(parser)
}

internal fun failureFactory(input: AdvancingCharStream, index: Int, message: String): ParseResult.Fail<CharFailureContext> {
    return ParseResult.Fail(input.contextAt(index), message) { context, message ->
        val builder = StringBuilder()
        val formattedLine = context.position.line.toString()
        builder.append(formattedLine)
        builder.append(" | ")
        builder.append(context.lineText)
        builder.append('\n')
        repeat(formattedLine.length + 2 + context.position.col) {
            builder.append(' ')
        }
        builder.append('^')
        builder.append('\n')
        builder.append(message)
        builder.toString()
    }
}