package net.rubygrapefruit.parse.general

import net.rubygrapefruit.parse.Parser

/**
 * Returns a parser that always succeeds. Does not consume any input or produce a result.
 */
fun succeed(): Parser<Any, Unit> {
    return SucceedParser.NoResult
}

/**
 * Returns a parser that always succeeds and produces the given result. Does not consume any input
 */
fun <OUT> succeed(result: OUT): Parser<Any, OUT> {
    return SucceedParser(result)
}

/**
 * Returns a parser that matches the end of input. Does not consume any input or produce a result
 */
fun endOfInput(): Parser<Any, Unit> {
    return EndOfInputParser(Unit)
}

/**
 * Returns a parser that matches the end of input and produces the given result. Does not consume any input.
 */
fun <OUT> endOfInput(result: OUT): Parser<Any, OUT> {
    return EndOfInputParser(result)
}