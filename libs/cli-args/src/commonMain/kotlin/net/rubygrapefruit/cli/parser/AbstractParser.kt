package net.rubygrapefruit.cli.parser

internal abstract class AbstractParser<out T> : Parser<T>, ParserInternal<T> {
    protected open fun startAll(): List<ParseState<T>> {
        return listOf(start())
    }

    override fun parse(args: List<String>): Parser.Result<T> {
        val results = startAll().map { parseAlternative(args, it) }
        for (alternative in results) {
            if (alternative.result is ParsedArguments.Success && alternative.recognizedCount == args.size) {
                return ParserInternal.Success(alternative.result.value())
            }
        }

        val best = results.maxBy { it.recognizedCount }
        val recognized = best.recognized
        val result = best.result

        var unrecognizedProblems: List<ProblemAtIndex> = emptyList()
        var index = 0
        while (index < args.size) {
            if (recognized[index]) {
                index++
                continue
            }

            val current = DefaultInputArgs(index, args, recognized)
            val failure = result.claimUnrecognized(current)
            if (failure != null) {
                require(failure.count > 0)
                unrecognizedProblems = unrecognizedProblems.plus(failure.problem)
                index += failure.count
            } else {
                val problem = if (current.firstIsOption()) {
                    UnexpectedOption(current.first())
                } else {
                    UnexpectedArgument(current.first())
                }
                unrecognizedProblems = unrecognizedProblems.plus(ProblemAtIndex(index, problem))
                index++
            }
        }

        return when (result) {
            is ParsedArguments.Failure -> {
                ParserInternal.Failure(result.problems().plus(unrecognizedProblems), this)
            }

            is ParsedArguments.Success -> {
                ParserInternal.Failure(unrecognizedProblems, this)
            }
        }
    }

    private fun parseAlternative(args: List<String>, state: ParseState<T>): Alternative<T> {
        val recognized = BooleanArray(args.size)
        var index = 0
        while (index < args.size && !state.finished) {
            val current = DefaultInputArgs(index, args, recognized)
            val parseResult = state.maybeParse(current)
            when (parseResult) {
                is ParseState.Recognized -> {
                    require(parseResult.count > 0)
                    repeat(parseResult.count) {
                        recognized[index] = true
                        index++
                    }
                }

                is ParseState.NotRecognized -> {
                    // Attempt to recover and continue
                    index++
                }
            }
        }

        val result = state.finish(args.size)

        return Alternative(result, recognized)
    }

    private class Alternative<T>(val result: ParsedArguments<T>, val recognized: BooleanArray) {
        val recognizedCount = recognized.count { it }
    }
}