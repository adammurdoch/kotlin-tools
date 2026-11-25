package net.rubygrapefruit.cli.parser

internal abstract class AbstractParser<out T> : Parser<T>, ParserInternal<T> {

    override fun parse(args: List<String>): Parser.Result<T> {
        var candidates = mutableListOf<EdgeState>()
        edges {
            candidates.add(EdgeState(it, 1))
        }

        var nextCandidates = mutableListOf<EdgeState>()

        val unrecognized = Array<List<EdgeState>?>(args.size) { _ -> null }

        for (index in args.indices) {
            val current = DefaultInputArgs(index, args)
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
                                result.next.edges { edge ->
                                    nextCandidates.add(EdgeState(edge, result.count))
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
            // Ignore failures if parsing was successful
            for (candidate in candidates) {
                val edge = candidate.edge
                if (edge is ParseEdge.Failed) {
                    problems.add(edge.failure())
                } else if (edge is ParseEdge.Matcher) {
                    val expecting = edge.expectingArgument
                    if (expecting != null) {
                        finalExpected.add(expecting)
                    }
                }
            }
        }

        val reportedExpected = mutableSetOf<String>()

        var index = 0
        while (index < args.size) {
            val candidates = unrecognized[index]
            if (candidates == null) {
                index++
                continue
            }

            val current = DefaultInputArgs(index, args)
            var handled = false
            for (candidate in candidates) {
                val claimed = candidate.edge.maybeClaimUnrecognized(current)
                if (claimed != null) {
                    problems.add(ProblemAtIndex(index, claimed.problem))
                    if (candidate.edge is ParseEdge.Matcher) {
                        val expecting = candidate.edge.expectingArgument
                        if (expecting != null) {
                            reportedExpected.add(expecting)
                        }
                    }
                    if (claimed.count > 1 && unrecognized[index + 1] != null) {
                        index += 2
                    } else {
                        index++
                    }
                    handled = true
                    break
                }
            }
            if (!handled) {
                val expected = mutableSetOf<String>()
                for (candidate in candidates) {
                    if (candidate.advance == 1 && candidate.edge is ParseEdge.Matcher) {
                        val expecting = candidate.edge.expectingArgument
                        if (expecting != null && finalExpected.contains(expecting) && reportedExpected.add(expecting)) {
                            expected.addAll(candidate.edge.expectingValue)
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
            val remainingExpected = finalExpected - reportedExpected
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