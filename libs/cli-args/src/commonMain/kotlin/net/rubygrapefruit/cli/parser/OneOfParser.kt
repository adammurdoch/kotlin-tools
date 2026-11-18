package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output
import kotlin.math.min

internal class OneOfParser<OUT>(val choices: List<ParserInternal<OUT>>) : AbstractParser<OUT>(), Parser<OUT> {
    override fun start(): ParseState<OUT> {
        return State(choices.map { ChoiceState(it.start()) })
    }

    override fun startAll(): List<ParseState<OUT>> {
        return choices.map { it.start() }
    }

    override fun edges(consumer: (ParseEdge) -> Unit) {
        for (choice in choices) {
            choice.edges(consumer)
        }
    }

    override fun usage(): UsageInternal {
        return OneOfUsage(choices)
    }

    private class OneOfUsage(choices: List<ParserInternal<*>>) : UsageInternal {
        private val usages = choices.map { it.usage() }

        override fun choices(): List<UsageInternal> {
            return usages
        }

        override fun options(): List<UsageInternal.OptionUsage> {
            return usages.flatMap { it.options() }
        }

        override fun positionals(): List<UsageInternal.PositionalUsage> {
            return usages.flatMap { it.positionals() }
        }

        override fun writeTo(quoted: Boolean, output: Output) {
            writeChoices(quoted, output, usages) { choice ->
                choice.writeTo(true, output)
            }
        }
    }

    private class State<OUT>(val choices: List<ChoiceState<OUT>>) : ParseState<OUT> {
        override val expectingOption: Boolean
            get() = choices.any { it.state.expectingOption }

        override val expectingPositional: Boolean
            get() = choices.any { it.state.expectingPositional }

        override fun maybeParse(args: InputArgs): ParseState.Result {
            var recognized = Int.MAX_VALUE
            for (choice in choices) {
                if (choice.state.expectingOption || choice.state.expectingPositional) {
                    val result = choice.state.maybeParse(args)
                    when (result) {
                        is ParseState.NotRecognized -> continue

                        is ParseState.Recognized -> {
                            choice.recognized = true
                            recognized = min(recognized, result.count)
                        }
                    }
                }
            }
            return if (recognized != Int.MAX_VALUE) {
                ParseState.Recognized(recognized)
            } else {
                ParseState.NotRecognized
            }
        }

        override fun finish(index: Int): ParsedArguments<OUT> {
            for (choice in choices) {
                val result = choice.state.finish(index)
                when (result) {
                    is ParsedArguments.Failure -> choice.problems = choice.problems.plus(result.problems())
                    is ParsedArguments.Success -> {
                        if (choice.problems == null) {
                            return result
                        }
                    }
                }
            }
            val best = choices.find { it.recognized }
            if (best != null) {
                return ParsedArguments.FixedProblems(best.problems!!)
            }

            var problems: List<ProblemAtIndex> = emptyList()
            for (choice in choices) {
                problems += choice.problems!!
            }

            return ParsedArguments.FixedProblems(problems)
        }
    }

    private class ChoiceState<OUT>(val state: ParseState<OUT>) {
        var problems: List<ProblemAtIndex>? = null
        var recognized = false
    }
}