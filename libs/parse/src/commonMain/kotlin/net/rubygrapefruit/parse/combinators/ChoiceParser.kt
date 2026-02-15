package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*
import kotlin.math.min

internal class ChoiceParser<IN, OUT>(
    private val options: List<Parser<IN, OUT>>
) : Parser<IN, OUT>, CombinatorBuilder<OUT>, DiscardableParser<IN> {
    override fun withNoResult(): Parser<IN, Unit> {
        return ChoiceParser(options.map { DiscardParser(it) })
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, OUT> {
        return of(options.map { compiler.compile(it) })
    }

    companion object {
        fun <IN, OUT> of(options: List<CompiledParser<IN, OUT>>): CompiledParser<IN, OUT> {
            val effective = mutableListOf<CompiledParser<IN, OUT>>()
            for (option in options) {
                if (option is ChoiceCompiledParser) {
                    effective.addAll(option.parsers)
                } else {
                    effective.add(option)
                }
            }
            return ChoiceCompiledParser(effective)
        }

        fun <IN, OUT, NEXT> of(parsers: List<CompiledParser<IN, OUT>>, next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return ChoicePullParser(parsers, next)
        }
    }

    internal class ChoiceCompiledParser<IN, OUT>(val parsers: List<CompiledParser<IN, OUT>>) : CompiledParser<IN, OUT> {
        override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return ChoicePullParser(parsers, next)
        }
    }

    private class ChoicePullParser<IN, OUT, NEXT>(
        parsers: List<CompiledParser<IN, OUT>>,
        val next: ParseContinuation<IN, OUT, NEXT>
    ) : PullParser<IN, NEXT> {
        private val states: Array<OptionState<IN, NEXT>>
        private var matched = 0

        init {
            val continuation = OptionContinuation(next)
            states = Array(parsers.size) { index ->
                val parser = parsers[index]
                OptionState(parser.start(continuation))
            }
        }

        override fun toString(): String {
            return "{choice}"
        }

        override fun stop(): PullParser.Failed {
            return PullParser.Failed.merged(states.mapNotNull {
                val state = it.state
                when (state) {
                    is PullParser -> state.stop()
                    is PullParser.Failed if state.index == 0 -> state
                    else -> null
                }
            })
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN, NEXT> {
            val maxAdvance = min(max, 1)
            var waitingFor = 0
            var hasZeroAdvance = false
            for (index in states.indices) {
                val option = states[index]
                val optionState = option.state
                if (optionState is PullParser) {
                    if (option.matched > matched) {
                        waitingFor++
                        continue
                    }
                    val optionResult = optionState.parse(input, maxAdvance)
                    when (optionResult) {
                        is PullParser.Matched -> return optionResult

                        is PullParser.Failed -> {
                            val failedChoice = option.failedChoice
                            val effective = if (optionResult.index == 0 && failedChoice != null) {
                                PullParser.Failed(0, ExpectationProvider.oneOf(failedChoice, optionResult.expected))
                            } else {
                                optionResult
                            }
                            option.state = effective
                            option.matched = matched + optionResult.index
                        }

                        is PullParser.RequireMore -> {
                            if (optionResult.matched) {
                                if (waitingFor > 0) {
                                    option.successful = true
                                } else {
                                    return if (optionResult.advance == 0) {
                                        val failures = failedChoices(matched) + if (optionResult.failedChoice != null) listOf(optionResult.failedChoice) else emptyList()
                                        val expected = ExpectationProvider.oneOfOrNull(failures)
                                        PullParser.RequireMore(0, 0, next.matches, optionResult.parser, expected)
                                    } else {
                                        PullParser.RequireMore(optionResult.advance, optionResult.commit, next.matches, optionResult.parser, optionResult.failedChoice)
                                    }
                                }
                            }
                            waitingFor++
                            option.state = optionResult.parser
                            option.failedChoice = optionResult.failedChoice
                            option.matched += optionResult.advance
                            option.commit += optionResult.commit
                            if (optionResult.advance == 0) {
                                hasZeroAdvance = true
                            }
                        }
                    }
                }
            }
            if (waitingFor == 1) {
                val option = states.first { it.state is PullParser }
                val failures = states.filter { it.state is PullParser.Failed }
                val maxFailureIndex = failures.maxOf { it.matched }
                if (option.commit > maxFailureIndex) {
                    val failedChoices = ExpectationProvider.oneOfOrNull(failedChoices(option.matched))
                    return PullParser.RequireMore(option.matched - matched, option.commit, false, option.state as PullParser, failedChoices)
                }
            }
            return if (waitingFor > 0) {
                if (hasZeroAdvance) {
                    PullParser.RequireMore(0, 0, false, this)
                } else {
                    matched++
                    PullParser.RequireMore(1, 0, false, this)
                }
            } else {
                mergedFailures()
            }
        }

        private fun failedChoices(matched: Int): List<ExpectationProvider> {
            return states.mapNotNull {
                val state = it.state
                when (state) {
                    is PullParser.Failed if it.matched == matched -> state.expected
                    is PullParser if it.matched == matched -> it.failedChoice
                    else -> null
                }
            }
        }

        private fun mergedFailures(): PullParser.Failed {
            val failures = states.filter { option -> option.state is PullParser.Failed }
            val maxIndex = failures.maxOf { option -> option.matched }
            val filtered = failures.mapNotNull { option ->
                if (option.matched == maxIndex) {
                    (option.state as PullParser.Failed).expected
                } else {
                    null
                }
            }
            return PullParser.Failed(maxIndex - matched, ExpectationProvider.oneOf(filtered))
        }
    }

    private class OptionState<IN, NEXT>(var state: ParseState<IN, NEXT>) {
        var matched = 0
        var commit = 0
        var successful = false
        var failedChoice: ExpectationProvider? = null

        override fun toString(): String {
            return "{$state matched=$matched commit=$commit successful=$successful failedChoice=$failedChoice}"
        }
    }

    private class OptionContinuation<IN, OUT, NEXT>(val next: ParseContinuation<IN, OUT, NEXT>) : ParseContinuation<IN, OUT, NEXT> {
        override val matches: Boolean
            get() = true

        override fun next(length: Int, value: ValueProvider<OUT>): PullParser<IN, NEXT> {
            return next.next(length, value)
        }
    }
}