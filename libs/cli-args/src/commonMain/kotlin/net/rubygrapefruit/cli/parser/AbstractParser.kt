package net.rubygrapefruit.cli.parser

internal abstract class AbstractParser<out T> : Parser<T>, ParserInternal<T> {
    override fun parse(args: List<String>): Parser.Result<T> {
        val recognized = BooleanArray(args.size)
        val state = start()
        var problems: List<ProblemAtIndex>? = null
        var index = 0
        while (index < args.size) {
            val current = DefaultInputArgs(index, args, recognized)
            val result = state.maybeParse(current)
            when (result) {
                is ParseState.Matched -> {
                    require(result.count > 0)
                    repeat(result.count) {
                        recognized[index] = true
                        index++
                    }
                }

                is ParseState.NotRecognized -> {
                    // Attempt to recover and continue
                    index++
                }

                is ParseState.Failure -> {
                    require(result.count > 0)
                    problems = problems.plus(current.index + result.index, result.problem)
                    // Attempt to recover and continue
                    repeat(result.count) {
                        recognized[index] = true
                        index++
                    }
                }
            }
        }

        index = 0
        while (index < args.size) {
            if (!recognized[index]) {
                val current = DefaultInputArgs(index, args, recognized)
                val problem = state.claimUnrecognized(current) ?: if (current.firstIsOption()) {
                    UnexpectedOption(current.first())
                } else {
                    UnexpectedArgument(current.first())
                }
                problems = problems.plus(index, problem)
            }
            index++
        }

        val result = state.finish()
        return when (result) {
            is ParseState.FinalFailure -> {
                ParserInternal.Failure(problems.plus(args.size, result.problems))
            }

            is ParseState.Success -> {
                if (problems != null) {
                    ParserInternal.Failure(problems)
                } else {
                    ParserInternal.Success(result.result)
                }
            }
        }
    }
}