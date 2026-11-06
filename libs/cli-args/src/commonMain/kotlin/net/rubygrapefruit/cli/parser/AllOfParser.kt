package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class AllOfParser<OUT>(
    private val parsers: List<ParserInternal<*>>,
    private val map: (List<ParseState.Success<*>>) -> OUT
) : AbstractParser<OUT>() {
    override fun start(): ParseState<OUT> {
        return State(parsers.map { it.start() }, map)
    }

    override fun usage(): UsageInternal {
        return ParserUsage()
    }

    private inner class ParserUsage : UsageInternal {
        override fun writeTo(output: Output) {
            parsers.forEachIndexed { index, parser ->
                if (index > 0) {
                    output.write(" ")
                }
                parser.usage().writeTo(output)
            }
        }
    }

    private class State<OUT>(val states: List<ParseState<*>>, val map: (List<ParseState.Success<*>>) -> OUT) : ParseState<OUT> {
        private val remaining = states.toMutableList()

        override fun maybeParse(args: InputArgs): ParseState.PartialResult {
            var index = 0
            while (index < remaining.size) {
                val state = remaining[index]
                val result = state.maybeParse(args)
                when (result) {
                    is ParseState.Matched -> {
                        remaining.removeAt(index)
                        return ParseState.Matched(result.count, false)
                    }
                    is ParseState.Failure -> return result
                    is ParseState.NotRecognized -> {
                        index++
                    }
                }
            }
            return ParseState.NotRecognized
        }

        override fun claimUnrecognized(args: InputArgs): ParseState.Failure? {
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