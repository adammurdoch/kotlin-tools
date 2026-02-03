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
        private var advancedZero = true

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
                if (state is PullParser) {
                    state.stop()
                } else if (state is PullParser.Failed && state.index == 0) {
                    state
                } else {
                    null
                }
            })
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN, NEXT> {
            var requireMore = false
            val maxAdvance = min(max, 1)
            var actualAdvance = 1
            for (index in states.indices) {
                val option = states[index]
                val optionState = option.state
                if (optionState is PullParser) {
                    if (advancedZero && option.advance > 0) {
                        requireMore = true
                        continue
                    }
                    val optionResult = optionState.parse(input, maxAdvance)
                    when (optionResult) {
                        is PullParser.Matched -> TODO()

                        is PullParser.Failed -> {
                            option.state = optionResult
                        }

                        is PullParser.RequireMore -> {
                            if (optionResult.matched) {
                                if (requireMore) {
                                    option.matched = true
                                } else {
                                    return if (optionResult.advance == 0) {
                                        val failures = states.mapNotNull {
                                            val state = it.state
                                            if (state is PullParser.Failed && state.index == 0) {
                                                state.expected
                                            } else {
                                                null
                                            }
                                        } + if (optionResult.failedChoice != null) listOf(optionResult.failedChoice) else emptyList()
                                        val expected = ExpectationProvider.oneOfOrNull(failures)
                                        PullParser.RequireMore(0, next.matches, optionResult.parser, expected)
                                    } else {
                                        PullParser.RequireMore(optionResult.advance, next.matches, optionResult.parser, optionResult.failedChoice)
                                    }
                                }
                            } else {
                                requireMore = true
                            }
                            option.state = optionResult.parser
                            option.advance = optionResult.advance
                            if (optionResult.advance == 0) {
                                actualAdvance = 0
                            }
                        }
                    }
                }
            }
            advancedZero = actualAdvance == 0
            return if (requireMore) {
                if (actualAdvance > 0) {
                    for (index in states.indices) {
                        val choice = states[index].state
                        if (choice is PullParser.Failed) {
                            states[index].state = PullParser.Failed(choice.index - actualAdvance, choice.expected)
                        }
                    }
                }
                PullParser.RequireMore(actualAdvance, false, this)
            } else {
                mergedFailures()
            }
        }

        private fun mergedFailures(): PullParser.Failed {
            val failures = states.mapNotNull { it.state as? PullParser.Failed }
            return PullParser.Failed.merged(failures)
        }
    }

    private class OptionState<IN, NEXT>(var state: ParseState<IN, NEXT>) {
        var advance = 0
        var matched = false

        override fun toString(): String {
            return "{$state advance=$advance matched=$matched}"
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