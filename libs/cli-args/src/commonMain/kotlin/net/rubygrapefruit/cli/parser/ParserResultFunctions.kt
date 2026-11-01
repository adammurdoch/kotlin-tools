package net.rubygrapefruit.cli.parser

internal fun Parser.Failure?.plus(index: Int, message: String): Parser.Failure {
    return if (this == null) {
        ParserInternal.Failure(listOf(Parser.Problem(index, message)))
    } else {
        ParserInternal.Failure(problems + Parser.Problem(index, message))
    }
}

internal fun Parser.Failure?.plus(index: Int, messages: List<String>): Parser.Failure {
    val newProblems = messages.map { Parser.Problem(index, it) }
    return if (this == null) {
        ParserInternal.Failure(newProblems)
    } else {
        ParserInternal.Failure(problems + newProblems)
    }
}

internal fun Parser.Failure?.plus(other: Parser.Failure): Parser.Failure {
    return if (this == null) {
        other
    } else {
        ParserInternal.Failure(problems + other.problems)
    }
}

internal fun <A, B, OUT> zipping(a: ParseState.Result<A>, b: ParseState.Result<B>, map: (A, B) -> OUT): ParseState.Result<OUT> {
    return when (a) {
        is ParseState.FinalFailure -> {
            when (b) {
                is ParseState.FinalFailure -> ParseState.FinalFailure(a.messages + b.messages)
                is ParseState.Success -> a
            }
        }

        is ParseState.Success -> {
            when (b) {
                is ParseState.FinalFailure -> b
                is ParseState.Success -> ParseState.LazyResult { map(a.result, b.result) }
            }
        }
    }
}