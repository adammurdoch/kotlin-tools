package net.rubygrapefruit.cli.parser

internal fun <A, B, OUT> zipping(a: Parser.Result<A>, b: Parser.Result<B>, map: (A, B) -> OUT): Parser.Result<OUT> {
    return when (a) {
        is Parser.Failure -> {
            when (b) {
                is Parser.Failure -> ParseState.Failure(a.problems + b.problems)
                is Parser.Success -> a
            }
        }

        is Parser.Success -> {
            when (b) {
                is Parser.Failure -> b
                is Parser.Success -> ParserInternal.Success(map(a.result, b.result))
            }
        }
    }
}