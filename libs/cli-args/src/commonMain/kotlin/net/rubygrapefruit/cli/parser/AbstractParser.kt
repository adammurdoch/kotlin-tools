package net.rubygrapefruit.cli.parser

internal abstract class AbstractParser<out T> : Parser<T>, ParserInternal<T> {
    override fun parse(args: List<String>): Parser.Result<T> {
        var current = DefaultInputArgs(0, args)
        val state = start()
        val unrecognized = mutableListOf<Int>()
        var problems: List<ProblemAtIndex>? = null
        while (!current.isEmpty) {
            val result = state.maybeParse(current)
            when (result) {
                is ParseState.Matched -> {
                    require(result.count > 0)
                    current = current.drop(result.count)
                }

                is ParseState.NotRecognized -> {
                    unrecognized.add(current.index)
                    // Attempt to recover and continue
                    current = current.drop(1)
                }

                is ParseState.Failure -> {
                    require(result.count > 0)
                    problems = problems.plus(current.index + result.index, result.problem)
                    // Attempt to recover and continue
                    current = current.drop(result.count)
                }
            }
        }

        while (unrecognized.isNotEmpty()) {
            val index = unrecognized.removeFirst()
            val current = DefaultInputArgs(index, args)
            val problem = state.claimUnrecognized(current) ?: if (current.firstIsOption()) {
                UnexpectedOption(current.first())
            } else {
                UnexpectedArgument(current.first())
            }
            problems = problems.plus(current.index, problem)
        }

        val result = state.finish()
        return when (result) {
            is ParseState.FinalFailure -> {
                ParserInternal.Failure(problems.plus(current.index, result.problems))
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