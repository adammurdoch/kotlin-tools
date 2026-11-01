package net.rubygrapefruit.cli.parser

internal abstract class AbstractParser<out T> : Parser<T>, ParserInternal<T> {
    override fun parse(args: List<String>): Parser.Result<T> {
        var current = InputArgs(0, args)
        val state = start()
        var problems: List<Parser.Problem>? = null
        while (!current.isEmpty) {
            val result = state.maybeParse(current)
            when (result) {
                is ParseState.NotRecognized -> {
                    val message = if (current.firstIsOption()) {
                        "Unexpected option '${current.first()}'"
                    } else {
                        "Unexpected argument '${current.first()}'"
                    }
                    problems = problems.plus(current.index, message)
                    // Attempt to recover and continue
                    current = current.drop(1)
                }

                is ParseState.Continue -> {
                    require(result.count > 0)
                    current = current.drop(result.count)
                }

                is ParseState.Failure -> {
                    require(result.count > 0)
                    problems = problems.plus(current.index + result.index, result.message)
                    // Attempt to recover and continue
                    current = current.drop(result.count)
                }
            }
        }

        val result = state.finish()
        return when (result) {
            is ParseState.FinalFailure -> {
                ParserInternal.Failure(problems.plus(current.index, result.messages))
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