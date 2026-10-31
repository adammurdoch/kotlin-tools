package net.rubygrapefruit.cli.parser

/**
 * Applies the given parsers in any order.
 */
fun <A, B, OUT> allOf(a: Parser<A>, b: Parser<B>, map: (A, B) -> OUT): Parser<OUT> {
    return AllOfParser(a as ParserInternal<A>, b as ParserInternal<B>, map)
}