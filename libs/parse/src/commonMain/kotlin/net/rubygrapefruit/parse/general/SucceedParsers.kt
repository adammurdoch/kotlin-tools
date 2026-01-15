package net.rubygrapefruit.parse.general

import net.rubygrapefruit.parse.Parser

/**
 * Returns a parser that always succeeds. Does not consume any input
 */
fun succeed(): Parser<Any, Unit> {
    return SucceedParser.of()
}

/**
 * Returns a parser that always succeeds and produces the given result. Does not consume any input
 */
fun <OUT> succeed(result: OUT): Parser<Any, OUT> {
    return SucceedParser(result)
}
