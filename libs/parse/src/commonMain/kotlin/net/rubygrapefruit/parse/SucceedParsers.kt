package net.rubygrapefruit.parse

/**
 * Returns a parser that always succeeds.
 */
fun succeed(): Parser<Any, Unit> {
    return SucceedParser(Unit)
}

/**
 * Returns a parser that always succeeds and produces the given result.
 */
fun <OUT> succeed(result: OUT): Parser<Any, OUT> {
    return SucceedParser(result)
}
