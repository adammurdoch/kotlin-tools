package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class AllOfParser<OUT>(
    private val parsers: List<ParserInternal<*>>,
    private val map: (List<ParseState.Success<*>>) -> OUT
) : AbstractParser<OUT>() {
    override fun start(): ParseState<OUT> {
        return State(parsers.map { Element(it.start()) }, map)
    }

    override fun usage(): UsageInternal {
        return AllOfUsage()
    }

    private inner class AllOfUsage : UsageInternal {
        override val isEmpty: Boolean
            get() = parsers.isEmpty()

        override fun writeTo(quoted: Boolean, output: Output) {
            if (parsers.size == 1) {
                parsers.first().usage().writeTo(quoted, output)
            } else {
                parsers.forEachIndexed { index, parser ->
                    if (index > 0) {
                        output.write(" ")
                    }
                    parser.usage().writeTo(true, output)
                }
            }
        }
    }

    private class Element<T>(val state: ParseState<T>) {
        var result: ParseState.Result<T>? = null

        fun maybeParse(args: InputArgs): ParseState.PartialResult<Nothing> {
            val parseResult = state.maybeParse(args)
            return when (parseResult) {
                is ParseState.Matched -> parseResult

                is ParseState.Finished -> {
                    result = parseResult.result
                    ParseState.Matched(parseResult.count)
                }

                is ParseState.Failure -> parseResult
                is ParseState.NotRecognized -> parseResult
            }
        }

        fun finish(index: Int): ParseState.Result<T> {
            if (result != null) {
                return result!!
            }
            return state.finish(index)
        }
    }

    private class State<OUT>(val elements: List<Element<*>>, val map: (List<ParseState.Success<*>>) -> OUT) : ParseState<OUT> {
        private val nonPositional = elements.filter { it.state !is ParseState.Positional }
        private val positional = elements.filter { it.state is ParseState.Positional }

        override fun maybeParse(args: InputArgs): ParseState.PartialResult<OUT> {
            for (element in nonPositional) {
                if (element.result != null) {
                    continue
                }
                val result = element.maybeParse(args)
                if (result !is ParseState.NotRecognized) {
                    return result
                }
            }

            for (element in positional) {
                if (element.result != null) {
                    continue
                }
                val result = element.maybeParse(args)
                return result
            }

            return ParseState.NotRecognized
        }

        override fun claimUnrecognized(args: InputArgs): ParseState.Failure? {
            for (element in elements) {
                val problem = element.state.claimUnrecognized(args)
                if (problem != null) {
                    return problem
                }
            }
            return null
        }

        override fun finish(index: Int): ParseState.Result<OUT> {
            var problems: List<ProblemAtIndex>? = null
            val results = mutableListOf<ParseState.Success<*>>()
            for (element in elements) {
                val result = element.finish(index)
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