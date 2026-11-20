package net.rubygrapefruit.cli.parser

internal abstract class AbstractParser<out T> : Parser<T>, ParserInternal<T> {

    override fun parse(args: List<String>): Parser.Result<T> {
        return parseUsingEdges(args)
    }

    override fun parseUsingEdges(args: List<String>): Parser.Result<T> {
        var candidates = mutableListOf<EdgeState>()
        edges {
            candidates.add(EdgeState(it, 1))
        }

        var nextCandidates = mutableListOf<EdgeState>()

        val unrecognized = Array<List<EdgeState>?>(args.size) { _ -> null }

        val recognized = BooleanArray(args.size)
        for (index in args.indices) {
            val current = DefaultInputArgs(index, args, recognized)
            for (candidate in candidates) {
                if (candidate.advance > 1) {
                    candidate.advance--
                    nextCandidates.add(candidate)
                } else {
                    val edge = candidate.edge
                    if (edge is ParseEdge.Matcher) {
                        val result = edge.next(current)
                        when (result) {
                            is ParseEdge.Transition.Matched -> {
                                result.next.edges {
                                    nextCandidates.add(EdgeState(it, result.count))
                                }
                            }

                            is ParseEdge.Transition.NotMatched -> {
                                // Ignore
                            }
                        }
                    }
                }
            }

            if (nextCandidates.isEmpty()) {
                // skip next input and try to recover
                unrecognized[index] = candidates.toList()
            } else {
                recognized[index] = true
                val tmp = candidates
                candidates = nextCandidates
                tmp.clear()
                nextCandidates = tmp
            }
        }

        val success = candidates.firstNotNullOfOrNull { it.edge as? ParseEdge.EndOfInput }
        val totalUnrecognized = unrecognized.count { it != null }
        if (success != null && totalUnrecognized == 0) {
            return ParserInternal.Success(success.value() as T)
        }

        val problems = mutableListOf<ProblemAtIndex>()

        val finalExpected = mutableSetOf<String>()
        if (success == null) {
            // Parsing did not succeed (vs did succeed but there are unrecognized arguments)
            for (candidate in candidates) {
                val edge = candidate.edge
                if (edge is ParseEdge.Failed) {
                    problems.add(edge.failure())
                } else if (edge is ParseEdge.NotEndOfInput) {
                    finalExpected.add(edge.expecting)
                }
            }
        }

        val allExpected = mutableSetOf<String>()

        var index = 0
        while (index < args.size) {
            val candidates = unrecognized[index]
            if (candidates == null) {
                index++
                continue
            }

            val current = DefaultInputArgs(index, args, recognized)
            val claimed = candidates.firstNotNullOfOrNull { it.edge.maybeClaimUnrecognized(current) }
            if (claimed != null) {
                problems.add(ProblemAtIndex(index, claimed.problem))
                if (claimed.count > 1 && !recognized[index + 1]) {
                    index += 2
                } else {
                    index++
                }
            } else {
                val expected = mutableSetOf<String>()
                for (candidate in candidates) {
                    if (candidate.advance == 1 && candidate.edge is ParseEdge.NotEndOfInput) {
                        if (finalExpected.contains(candidate.edge.expecting) && allExpected.add(candidate.edge.expecting)) {
                            expected.add(candidate.edge.expecting)
                        }
                    }
                }
                val problem = if (current.firstIsOption()) {
                    UnexpectedOption(current.first(), expected.toList())
                } else {
                    UnexpectedArgument(current.first(), expected.toList())
                }
                problems.add(ProblemAtIndex(index, problem))
                index++
            }
        }

        if (finalExpected.isNotEmpty()) {
            val remainingExpected = finalExpected - allExpected
            if (remainingExpected.isNotEmpty()) {
                problems.add(ProblemAtIndex(args.size, MissingParameters(remainingExpected.toList())))
            }
        }

        if (problems.isNotEmpty()) {
            return ParserInternal.Failure(problems, this)
        }
        throw IllegalStateException()
    }

    private data class EdgeState(val edge: ParseEdge, var advance: Int)
}