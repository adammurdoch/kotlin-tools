package net.rubygrapefruit.cli.parser

/**
 * Creates a parser that does not take any parameters.
 *
 * The parser produces the result of the given function.
 */
fun <OUT> params(producer: () -> OUT): Parser<OUT> {
    return AllOfParser(emptyList()) { producer() }
}

/**
 * Creates a parser that applies the given parser and maps its result to produce a value of type [OUT].
 */
fun <A, OUT> params(a: Parser<A>, map: (A) -> OUT): Parser<OUT> {
    @Suppress("UNCHECKED_CAST")
    return AllOfParser(listOf(a).cast()) { results ->
        map(results[0].result as A)
    }
}

/**
 * Creates a parser that applies the given parsers and maps their results to produce a value of type [OUT].
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
 * Creates a parser that applies the given parsers and maps their results to produce a value of type [OUT].
 *
 * Positional parameters must appear in the order specified, and options can appear in any order.
 */
fun <A, OUT> params(prefix: Parser<Unit>, a: Parser<A>, map: (A) -> OUT): Parser<OUT> {
    @Suppress("UNCHECKED_CAST")
    return AllOfParser(listOf(prefix, a).cast()) { results ->
        map(results[1].result as A)
    }
}

/**
 * Creates a parser that applies the given parsers and maps their results to produce a value of type [OUT].
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
 * Creates a parser that applies the given parsers and maps their results to produce a value of type [OUT].
 *
 * Positional parameters must appear in the order specified, and options can appear in any order.
 */
fun <A, B, OUT> params(prefix: Parser<Unit>, a: Parser<A>, b: Parser<B>, map: (A, B) -> OUT): Parser<OUT> {
    @Suppress("UNCHECKED_CAST")
    return AllOfParser(listOf(prefix, a, b).cast()) { results ->
        map(results[1].result as A, results[2].result as B)
    }
}

/**
 * Creates a parser that applies the given parsers and maps their results to produce a value of type [OUT].
 *
 * Positional parameters must appear in the order specified, and options can appear in any order.
 */
fun <A, B, C, D, OUT> params(a: Parser<A>, b: Parser<B>, c: Parser<C>, d: Parser<D>, map: (A, B, C, D) -> OUT): Parser<OUT> {
    @Suppress("UNCHECKED_CAST")
    return AllOfParser(listOf(a, b, c, d).cast()) { results ->
        map(results[0].result as A, results[1].result as B, results[2].result as C, results[3].result as D)
    }
}

/**
 * Creates a parser that applies the given parsers and maps their results to produce a value of type [OUT].
 *
 * Positional parameters must appear in the order specified, and options can appear in any order.
 */
fun <A, B, C, D, E, OUT> params(a: Parser<A>, b: Parser<B>, c: Parser<C>, d: Parser<D>, e: Parser<E>, map: (A, B, C, D, E) -> OUT): Parser<OUT> {
    @Suppress("UNCHECKED_CAST")
    return AllOfParser(listOf(a, b, c, d, e).cast()) { results ->
        map(results[0].result as A, results[1].result as B, results[2].result as C, results[3].result as D, results[4].result as E)
    }
}

/**
 * Creates a parser that attempts to match the inputs using each of the given parsers. Returns the result of the first parser to match.
 */
fun <OUT> oneOf(vararg choices: Parser<OUT>): Parser<OUT> {
    return OneOfParser(choices.toList().cast())
}

@Suppress("UNCHECKED_CAST")
internal fun <OUT> List<Parser<OUT>>.cast() = this as List<ParserInternal<OUT>>

@Suppress("UNCHECKED_CAST")
internal fun <OUT> Parser<OUT>.cast() = this as ParserInternal<OUT>