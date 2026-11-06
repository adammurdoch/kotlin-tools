package net.rubygrapefruit.cli.parser

internal fun List<ProblemAtIndex>?.plus(problem: ProblemAtIndex): List<ProblemAtIndex> {
    return if (this == null) {
        listOf(problem)
    } else {
        this + problem
    }
}

internal fun List<ProblemAtIndex>?.plus(problems: List<ProblemAtIndex>?): List<ProblemAtIndex> {
    return if (problems == null) {
        this ?: emptyList()
    } else if (this == null) {
        problems
    } else {
        this + problems
    }
}
