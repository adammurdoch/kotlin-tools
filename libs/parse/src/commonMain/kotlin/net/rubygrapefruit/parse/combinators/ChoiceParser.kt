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
                    effective.addAll(option.options)
                } else {
                    effective.add(option)
                }
            }
            return ChoiceCompiledParser(effective)
        }

        fun <IN, NEXT> of(options: List<Option<IN, *, NEXT>>): PullParser<IN, NEXT> {
            return ChoicePullParser(options)
        }
    }

    class Option<IN, OUT, NEXT>(val parser: CompiledParser<IN, OUT>, val continuation: ParseContinuation<IN, OUT, NEXT>) {
        internal fun start(): OptionState<IN, NEXT> {
            val continuation = OptionContinuation(continuation)
            return OptionState(parser.start(continuation), continuation)
        }
    }

    class ChoiceCompiledParser<IN, OUT>(val options: List<CompiledParser<IN, OUT>>) : CompiledParser<IN, OUT> {
        override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return ChoicePullParser(options.map { Option(it, next) })
        }
    }

    private class ChoicePullParser<IN, NEXT>(
        options: List<Option<IN, *, NEXT>>
    ) : PullParser<IN, NEXT> {
        private val states: Array<OptionState<IN, NEXT>>
        private var matched = 0

        init {
            states = Array(options.size) { index -> options[index].start() }
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
                            option.commit += optionResult.commit
                            if (optionResult.matched) {
                                if (waitingFor > 0) {
                                    option.successful = true
                                } else {
                                    return if (optionResult.advance == 0) {
                                        val failures = failedChoices(matched) + if (optionResult.failedChoice != null) listOf(optionResult.failedChoice) else emptyList()
                                        val expected = ExpectationProvider.oneOfOrNull(failures)
                                        PullParser.RequireMore(0, option.commit, option.continuation.next.matches, optionResult.parser, expected)
                                    } else {
                                        PullParser.RequireMore(optionResult.advance, option.commit, option.continuation.next.matches, optionResult.parser, optionResult.failedChoice)
                                    }
                                }
                            }
                            waitingFor++
                            option.state = optionResult.parser
                            option.failedChoice = optionResult.failedChoice
                            option.matched += optionResult.advance
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
                    option.continuation.matches = option.continuation.next.matches
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

    internal class OptionState<IN, NEXT> internal constructor(var state: ParseState<IN, NEXT>, internal val continuation: OptionContinuation<*, *, *>) {
        var matched = 0
        var commit = 0
        var successful = false
        var failedChoice: ExpectationProvider? = null

        override fun toString(): String {
            return "{$state matched=$matched commit=$commit successful=$successful failedChoice=$failedChoice}"
        }
    }

    internal class OptionContinuation<IN, OUT, NEXT>(val next: ParseContinuation<IN, OUT, NEXT>) : ParseContinuation<IN, OUT, NEXT> {
        override fun toString(): String {
            return "{choice-option-continuation $next}"
        }

        override var matches: Boolean = true

        override fun next(length: Int, value: ValueProvider<OUT>): PullParser<IN, NEXT> {
            return next.next(length, value)
        }
    }
}