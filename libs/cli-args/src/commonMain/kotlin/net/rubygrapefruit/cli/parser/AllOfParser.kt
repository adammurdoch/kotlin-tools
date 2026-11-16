package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class AllOfParser<OUT>(
    private val parsers: List<ParserInternal<*>>,
    private val summary: String?,
    private val map: (List<ParsedArguments.Success<*>>) -> OUT
) : AbstractParser<OUT>() {
    override fun start(): ParseState<OUT> {
        return State(parsers.map { Element(it.start()) }, map)
    }

    override fun usage(): UsageInternal {
        return AllOfUsage()
    }

    private inner class AllOfUsage : UsageInternal {
        private val usages = parsers.map { it.usage() }

        override val isEmpty: Boolean
            get() = usages.isEmpty()

        override fun options(): List<UsageInternal.OptionUsage> {
            return usages.flatMap { it.options() }
        }

        override fun positionals(): List<UsageInternal.PositionalUsage> {
            return usages.flatMap { it.positionals() }
        }

        override fun writeTo(quoted: Boolean, output: Output) {
            if (usages.size == 1) {
                usages.first().writeTo(quoted, output)
            } else {
                usages.forEachIndexed { index, parser ->
                    if (index > 0) {
                        output.write(" ")
                    }
                    parser.writeTo(true, output)
                }
            }
        }

        override fun writeSummaryTo(output: Output) {
            if (summary != null) {
                output.write(summary)
            }
        }
    }

    private class Element<T>(val state: ParseState<T>)

    private class State<OUT>(val elements: List<Element<*>>, val map: (List<ParsedArguments.Success<*>>) -> OUT) : ParseState<OUT> {
        override val expectingOption: Boolean
            get() = elements.any { it.state.expectingOption }

        override val expectingPositional: Boolean
            get() = elements.any { it.state.expectingPositional }

        override fun maybeParse(args: InputArgs): ParseState.Result {
            for (element in elements) {
                if (element.state.expectingOption) {
                    val result = element.state.maybeParse(args)
                    if (result !is ParseState.NotRecognized) {
                        return result
                    }
                }
            }
            for (element in elements) {
                if (element.state.expectingPositional) {
                    return element.state.maybeParse(args)
                }
            }

            return ParseState.NotRecognized
        }

        override fun finish(index: Int): ParsedArguments<OUT> {
            val results = mutableListOf<ParsedArguments<*>>()
            var failed = false
            for (element in elements) {
                val result = element.state.finish(index)
                results.add(result)
                if (result is ParsedArguments.Failure) {
                    failed = true
                }
            }
            return if (failed) {
                Failure(results)
            } else {
                @Suppress("UNCHECKED_CAST")
                Success(results as List<ParsedArguments.Success<*>>, map)
            }
        }
    }

    private abstract class Result<R : ParsedArguments<*>>(val elements: List<R>) {
        fun maybeClaimUnrecognized(args: InputArgs): ParsedArguments.Unrecognized? {
            for (element in elements) {
                val problem = element.claimUnrecognized(args)
                if (problem != null) {
                    return problem
                }
            }
            return null
        }
    }

    private class Success<T>(elements: List<ParsedArguments.Success<*>>, val map: (List<ParsedArguments.Success<*>>) -> T) : Result<ParsedArguments.Success<*>>(elements),
        ParsedArguments.Success<T> {
        override fun value(): T {
            return map(elements)
        }

        override fun claimUnrecognized(args: InputArgs): ParsedArguments.Unrecognized? {
            return maybeClaimUnrecognized(args)
        }
    }

    private class Failure(elements: List<ParsedArguments<*>>) : Result<ParsedArguments<*>>(elements), ParsedArguments.Failure {
        private val failures = elements.filterIsInstance<ParsedArguments.Failure>()

        override fun problems(): List<ProblemAtIndex> {
            val problems = mutableListOf<ProblemAtIndex>()
            for (element in failures) {
                problems.addAll(element.problems())
            }
            return problems
        }

        override fun claimUnrecognized(args: InputArgs): ParsedArguments.Unrecognized? {
            return maybeClaimUnrecognized(args)
        }
    }
}