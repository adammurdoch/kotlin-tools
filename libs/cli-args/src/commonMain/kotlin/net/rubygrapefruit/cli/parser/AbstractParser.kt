package net.rubygrapefruit.cli.parser

internal abstract class AbstractParser<out T> : Parser<T>, ParserInternal<T> {
    protected open fun startAll(): List<ParseState<T>> {
        return listOf(start())
    }

    override fun parse(args: List<String>): Parser.Result<T> {
        val results = startAll().map { parseAlternative(args, it) }
        for (alternative in results) {
            if (alternative.result is Parser.Success) {
                return alternative.result
            }
        }
        val best = results.maxBy { it.recognized }
        return best.result
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

        var unrecognizedProblems: List<ProblemAtIndex>? = null
        index = 0
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

        val finalResult = when (result) {
            is ParsedArguments.Failure -> {
                ParserInternal.Failure(result.problems().plus(unrecognizedProblems), this)
            }

            is ParsedArguments.Success -> {
                if (unrecognizedProblems != null) {
                    ParserInternal.Failure(unrecognizedProblems, this)
                } else {
                    ParserInternal.Success(result.value())
                }
            }
        }

        return Alternative(finalResult, recognized.count { it })
    }

    private class Alternative<T>(val result: Parser.Result<T>, val recognized: Int)
}