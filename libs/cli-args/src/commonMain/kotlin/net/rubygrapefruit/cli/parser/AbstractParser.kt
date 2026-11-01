package net.rubygrapefruit.cli.parser

internal abstract class AbstractParser<out T> : Parser<T>, ParserInternal<T> {
    override fun parse(args: List<String>): Parser.Result<T> {
        var current = InputArgs(0, args)
        val state = start()
        val problems = mutableListOf<Parser.Problem>()
        while (true) {
            val result = state.maybeParse(current)
            when (result) {
                is ParseState.NotRecognized -> break
                is ParseState.Success -> current = current.drop(result.count)
                is ParseState.Failure -> {
                    problems.add(Parser.Problem(current.index + result.index, result.message))
                    // Attempt to recover and continue
                    current = current.drop(result.count)
                    break
                }
            }
        }

        if (!current.isEmpty) {
            // Should merge with state's final result
            val first = current.first()
            val problem = if (first.startsWith("-")) {
                Parser.Problem(current.index, "Unexpected option '$first'")
            } else {
                Parser.Problem(current.index, "Unexpected argument '$first'")
            }
            problems.add(problem)
        }

        val result = state.finish()
        return when (result) {
            is Parser.Failure -> {
                problems.addAll(result.problems)
                ParserInternal.Failure(problems)
            }

            is Parser.Success -> {
                if (problems.isNotEmpty()) {
                    ParserInternal.Failure(problems)
                } else {
                    result
                }
            }
        }
    }
}