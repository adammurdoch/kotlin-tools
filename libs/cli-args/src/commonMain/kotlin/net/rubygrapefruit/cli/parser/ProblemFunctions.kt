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

internal fun List<ProblemAtIndex>.plus(problems: List<ProblemAtIndex>?): List<ProblemAtIndex> {
    return if (problems == null) {
        this
    } else {
        this + problems
    }
}
