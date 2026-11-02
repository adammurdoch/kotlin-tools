package net.rubygrapefruit.cli.parser

internal fun List<ProblemAtIndex>?.plus(index: Int, problem: Problem): List<ProblemAtIndex> {
    return if (this == null) {
        listOf(ProblemAtIndex(index, problem))
    } else {
        this + ProblemAtIndex(index, problem)
    }
}

internal fun List<ProblemAtIndex>?.plus(index: Int, problems: List<Problem>): List<ProblemAtIndex> {
    val newProblems = problems.map { ProblemAtIndex(index, it) }
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
                is ParseState.FinalFailure -> ParseState.FinalFailure(a.problems + b.problems)
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