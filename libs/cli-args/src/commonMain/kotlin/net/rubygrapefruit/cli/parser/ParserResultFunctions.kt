package net.rubygrapefruit.cli.parser

internal fun List<Parser.Problem>?.plus(index: Int, message: Message): List<Parser.Problem> {
    return if (this == null) {
        listOf(Parser.Problem(index, message))
    } else {
        this + Parser.Problem(index, message)
    }
}

internal fun List<Parser.Problem>?.plus(index: Int, messages: List<Message>): List<Parser.Problem> {
    val newProblems = messages.map { Parser.Problem(index, it) }
    return if (this == null) {
        newProblems
    } else {
        this + newProblems
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