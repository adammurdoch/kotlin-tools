package net.rubygrapefruit.cli.parser

/**
 * Returns a parser that does not take any parameters.
 *
 * The parser produces the result of the given function.
 */
fun <OUT> params(producer: () -> OUT): Parser<OUT> {
    return AllOfParser(emptyList()) { producer() }
}

/**
 * Applies the given parser and maps it result to produce a value of type [OUT].
 */
fun <A, OUT> params(a: Parser<A>, map: (A) -> OUT): Parser<OUT> {
    @Suppress("UNCHECKED_CAST")
    return AllOfParser(listOf(a).cast()) { results ->
        map(results[0].result as A)
    }
}

/**
 * Applies the given parsers and maps their results to produce a value of type [OUT].
 *
 * Positional parameters must appear in the order specified, and options can appear in any order.
 */
fun <A, B, OUT> params(a: Parser<A>, b: Parser<B>, map: (A, B) -> OUT): Parser<OUT> {
    @Suppress("UNCHECKED_CAST")
    return AllOfParser(listOf(a, b).cast()) { results ->
        map(results[0].result as A, results[1].result as B)
    }
}

/**
 * Applies the given parsers and maps their results to produce a value of type [OUT].
 *
 * Positional parameters must appear in the order specified, and options can appear in any order.
 */
fun <A, B, C, OUT> params(a: Parser<A>, b: Parser<B>, c: Parser<C>, map: (A, B, C) -> OUT): Parser<OUT> {
    @Suppress("UNCHECKED_CAST")
    return AllOfParser(listOf(a, b, c).cast()) { results ->
        map(results[0].result as A, results[1].result as B, results[2].result as C)
    }
}

/**
 * Attempts to match the inputs using the given parsers. Returns the result of the first parser to match.
 */
fun <OUT> oneOf(vararg choices: Parser<OUT>): Parser<OUT> {
    return OneOfParser(choices.toList().cast())
}

@Suppress("UNCHECKED_CAST")
private fun <OUT> List<Parser<OUT>>.cast() = this as List<ParserInternal<OUT>>