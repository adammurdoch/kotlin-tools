package net.rubygrapefruit.cli.parser

internal fun Parser.Failure?.plus(index: Int, message: String): Parser.Failure {
    return if (this == null) {
        ParserInternal.Failure(listOf(Parser.Problem(index, message)))
    } else {
        ParserInternal.Failure(problems + Parser.Problem(index, message))
    }
}

internal fun Parser.Failure?.plus(other: Parser.Failure): Parser.Failure {
    return if (this == null) {
        other
    } else {
        ParserInternal.Failure(problems + other.problems)
    }
}

internal fun <A, B, OUT> zipping(a: Parser.Result<A>, b: Parser.Result<B>, map: (A, B) -> OUT): Parser.Result<OUT> {
    return when (a) {
        is Parser.Failure -> {
            when (b) {
                is Parser.Failure -> ParserInternal.Failure(a.problems + b.problems)
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