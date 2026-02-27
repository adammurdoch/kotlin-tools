package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.parse
import net.rubygrapefruit.parse.start

/**
 * Attempts to parse the given string. Fails when the parser cannot match the entire input.
 */
fun <OUT> Parser<TextInput, OUT>.parse(input: String): ParseResult<TextFailureContext, OUT> {
    val parser = start<CharStream, OUT>()
    return parse(parser, StringCharStream(input), ::failureFactory)
}

/**
 * Creates a [TextPushParser] that will attempt to match input as it becomes available.
 * Fails when the parser cannot match the entire input.
 */
fun <OUT> Parser<TextInput, OUT>.pushParser(): TextPushParser<OUT> {
    val parser = start<CharStream, OUT>()
    return DefaultTextPushParser(parser, ::failureFactory)
}

private fun failureFactory(context: TextFailureContext, message: String): String {
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
    return builder.toString()
}