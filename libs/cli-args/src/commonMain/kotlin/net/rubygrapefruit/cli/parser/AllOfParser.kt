package net.rubygrapefruit.cli.parser

internal class AllOfParser<OUT>(
    private val parsers: List<ParserInternal<*>>,
    private val map: (List<ParseState.Success<*>>) -> OUT
) : AbstractParser<OUT>() {
    override fun start(): ParseState<OUT> {
        return State(parsers.map { it.start() }, map)
    }

    private class State<OUT>(val states: List<ParseState<*>>, val map: (List<ParseState.Success<*>>) -> OUT) : ParseState<OUT> {
        override fun maybeParse(args: InputArgs): ParseState.PartialResult {
            for (state in states) {
                val result = state.maybeParse(args)
                if (result != ParseState.NotRecognized) {
                    return result
                }
            }
            return ParseState.NotRecognized
        }

        override fun claimUnrecognized(args: InputArgs): Problem? {
            for (state in states) {
                val problem = state.claimUnrecognized(args)
                if (problem != null) {
                    return problem
                }
            }
            return null
        }

        override fun finish(): ParseState.Result<OUT> {
            var problems: List<Problem>? = null
            val results = mutableListOf<ParseState.Success<*>>()
            for (state in states) {
                val result = state.finish()
                when (result) {
                    is ParseState.FinalFailure -> {
                        if (problems == null) {
                            problems = result.problems
                        } else {
                            problems = problems + result.problems
                        }
                    }

                    is ParseState.Success<*> -> results.add(result)
                }
            }
            return if (problems != null) {
                ParseState.FinalFailure(problems)
            } else {
                ParseState.LazyResult { map(results) }
            }
        }
    }
}