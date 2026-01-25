package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.Parser

/**
 * Returns a parser that applies the given parser and maps its result.
 */
fun <IN, INTERMEDIATE, OUT> map(parser: Parser<IN, INTERMEDIATE>, map: (INTERMEDIATE) -> OUT): Parser<IN, OUT> {
    return MapParser(parser, map)
}