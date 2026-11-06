package net.rubygrapefruit.cli.parser

internal abstract class AbstractParser<out T> : Parser<T>, ParserInternal<T> {
    override fun parse(args: List<String>): Parser.Result<T> {
        val recognized = BooleanArray(args.size)
        val state = start()
        var problems: List<ProblemAtIndex>? = null
        var result: ParseState.Result<T>? = null
        var index = 0
        while (index < args.size) {
            val current = DefaultInputArgs(index, args, recognized)
            val parseResult = state.maybeParse(current)
            when (parseResult) {
                is ParseState.Matched -> {
                    require(parseResult.count > 0)
                    repeat(parseResult.count) {
                        recognized[index] = true
                        index++
                    }
                }

                is ParseState.Finished -> {
                    require(parseResult.count > 0)
                    repeat(parseResult.count) {
                        recognized[index] = true
                        index++
                    }
                    result = parseResult.result
                    break
                }

                is ParseState.NotRecognized -> {
                    // Attempt to recover and continue
                    index++
                }

                is ParseState.Failure -> {
                    require(parseResult.count > 0)
                    problems = problems.plus(parseResult.problem)
                    // Attempt to recover and continue
                    repeat(parseResult.count) {
                        recognized[index] = true
                        index++
                    }
                }
            }
        }

        index = 0
        while (index < args.size) {
            if (recognized[index]) {
                index++
                continue
            }

            val current = DefaultInputArgs(index, args, recognized)
            val failure = state.claimUnrecognized(current)
            if (failure != null) {
                require(failure.count > 0)
                problems = problems.plus(failure.problem)
                index += failure.count
            } else {
                val problem = if (current.firstIsOption()) {
                    UnexpectedOption(current.first())
                } else {
                    UnexpectedArgument(current.first())
                }
                problems = problems.plus(ProblemAtIndex(index, problem))
                index++
            }
        }

        val finalResult = result ?: state.finish(args.size)
        return when (finalResult) {
            is ParseState.FinalFailure -> {
                ParserInternal.Failure(problems.plus(finalResult.problems), this)
            }

            is ParseState.Success -> {
                if (problems != null) {
                    ParserInternal.Failure(problems, this)
                } else {
                    ParserInternal.Success(finalResult.result)
                }
            }
        }
    }
}