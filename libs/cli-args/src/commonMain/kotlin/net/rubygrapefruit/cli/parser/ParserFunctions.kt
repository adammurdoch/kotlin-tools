package net.rubygrapefruit.cli.parser

/**
 * Applies the given parsers to produce a value of type [OUT].
 *
 * Parameters must appear in the order specified, and options can appear in any order.
 */
fun <A, B, OUT> allOf(a: Parser<A>, b: Parser<B>, map: (A, B) -> OUT): Parser<OUT> {
    @Suppress("UNCHECKED_CAST")
    return AllOfParser(listOf(a, b).cast()) { results ->
        map(results[0].result as A, results[1].result as B)
    }
}

/**
 * Applies the given parsers to produce a value of type [OUT].
 *
 * Parameters must appear in the order specified, and options can appear in any order.
 */
fun <A, B, C, OUT> allOf(a: Parser<A>, b: Parser<B>, c: Parser<C>, map: (A, B, C) -> OUT): Parser<OUT> {
    @Suppress("UNCHECKED_CAST")
    return AllOfParser(listOf(a, b, c).cast()) { results ->
        map(results[0].result as A, results[1].result as B, results[2].result as C)
    }
}

fun <OUT> oneOf(vararg choices: Parser<OUT>): Parser<OUT> {
    return OneOfParser(choices.toList().cast())
}

@Suppress("UNCHECKED_CAST")
private fun <OUT> List<Parser<OUT>>.cast() = this as List<ParserInternal<OUT>>