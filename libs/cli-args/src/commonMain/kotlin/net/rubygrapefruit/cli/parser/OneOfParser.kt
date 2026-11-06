package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output
import kotlin.math.min

internal class OneOfParser<OUT>(private val choices: List<ParserInternal<OUT>>) : AbstractParser<OUT>(), Parser<OUT> {
    override fun start(): ParseState<OUT> {
        return State(choices.map { ChoiceState(it.start()) })
    }

    override fun usage(): UsageInternal {
        return ParserUsage()
    }

    private inner class ParserUsage : UsageInternal {
        override fun writeTo(output: Output) {
            choices.forEachIndexed { index, choice ->
                if (index > 0) {
                    output.writeLine()
                }
                choice.usage().writeTo(output)
            }
        }
    }

    private class State<OUT>(val choices: List<ChoiceState<OUT>>) : ParseState<OUT> {
        override fun maybeParse(args: InputArgs): ParseState.PartialResult<OUT> {
            var recognized = Int.MAX_VALUE
            for (choice in choices) {
                if (choice.result != null) {
                    continue
                }
                val result = choice.state.maybeParse(args)
                when (result) {
                    is ParseState.NotRecognized -> continue
                    is ParseState.Matched -> {
                        choice.recognized = true
                        recognized = min(recognized, result.count)
                    }

                    is ParseState.Finished -> {
                        choice.recognized = true
                        choice.result = result.result
                        recognized = min(recognized, result.count)
                    }

                    is ParseState.Failure -> {
                        choice.recognized = true
                        choice.problems = choice.problems.plus(result.index, result.problem)
                        recognized = min(recognized, result.count)
                    }
                }
            }
            return if (recognized != Int.MAX_VALUE) {
                ParseState.Matched(recognized)
            } else {
                ParseState.NotRecognized
            }
        }

        override fun finish(): ParseState.Result<OUT> {
            val recognized = choices.filter { it.recognized }
            var problems: List<ProblemAtIndex> = emptyList()
            for (choice in recognized) {
                problems = problems.plus(choice.problems)
                val result = choice.result ?: choice.state.finish()
                when (result) {
                    is ParseState.FinalFailure -> problems.plus(0, result.problems)
                    is ParseState.Success -> {
                        if (choice.problems == null) {
                            return result
                        }
                    }
                }
            }
            if (recognized.isEmpty() && problems.isEmpty()) {
                for (choice in choices) {
                    val result = choice.state.finish()
                    when (result) {
                        is ParseState.FinalFailure -> problems.plus(0, result.problems)
                        is ParseState.Success -> return result
                    }
                }
            }
            return ParseState.FinalFailure(problems.map { it.problem })
        }
    }

    private class ChoiceState<OUT>(val state: ParseState<OUT>) {
        var problems: List<ProblemAtIndex>? = null
        var recognized = false
        var result: ParseState.Result<OUT>? = null
    }
}