package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*
import net.rubygrapefruit.parse.stream.Input
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

        fun <IN> of(start: Position, options: List<Option<IN, *>>): PullParser<IN> {
            return ChoicePullParser(start, options)
        }
    }

    class Option<IN, OUT>(val parser: CompiledParser<IN, OUT>, val continuation: ParseContinuation<IN, OUT>) {
        internal fun start(start: Position): OptionState<IN> {
            val continuation = OptionContinuation(continuation)
            return OptionState(parser.start(start, continuation), continuation)
        }
    }

    class ChoiceCompiledParser<IN, OUT>(val options: List<CompiledParser<IN, OUT>>) : CompiledParser<IN, OUT> {
        override fun start(start: Position, next: ParseContinuation<IN, OUT>): PullParser<IN> {
            return ChoicePullParser(start, options.map { Option(it, next) })
        }
    }

    private class ChoicePullParser<IN>(
        start: Position,
        options: List<Option<IN, *>>
    ) : PullParser<IN> {
        private val states: Array<OptionState<IN>> = Array(options.size) { index -> options[index].start(start) }
        private var advanced = 0

        override fun toString(): String {
            return "{choice}"
        }

        override fun stop(input: IN): PullParser.Failed {
            val failures = states.mapNotNull {
                val state = it.state
                when (state) {
                    is PullParser -> state.stop(input)
                    else -> null
                }
            }
            return PullParser.Failed.Flatten(failures)
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN> {
            val maxAdvance = min(max, 1)
            var waitingFor = 0
            var hasZeroAdvance = false
            var failedChoices: PullParser.Failed = PullParser.Failed.None
            for (index in states.indices) {
                val option = states[index]
                val optionState = option.state
                if (optionState is PullParser) {
                    if (option.advanced > advanced) {
                        waitingFor++
                        continue
                    }
                    val optionResult = optionState.parse(input, maxAdvance)
                    when (optionResult) {
                        is PullParser.Matched -> {
                            if (waitingFor == 0) {
                                return option.continuation.next.selected(
                                    optionResult.advance,
                                    optionResult.parser,
                                    failedChoices + optionResult.failedChoices
                                )
                            }
                            waitingFor++
                            failedChoices += optionResult.failedChoices
                            option.state = optionResult.parser
                            option.advanced += optionResult.advance
                            if (optionResult.advance == 0) {
                                hasZeroAdvance = true
                            }
                        }

                        is PullParser.Failed -> {
                            failedChoices += optionResult
                            option.state = optionResult
                        }

                        is PullParser.RequireMore -> {
                            waitingFor++
                            failedChoices += optionResult.failedChoices
                            option.state = optionResult.parser
                            option.advanced += optionResult.advance
                            if (optionResult.advance == 0) {
                                hasZeroAdvance = true
                            }
                        }
                    }
                }
            }
            if (waitingFor == 1) {
                val option = states.first { it.state is PullParser }
                option.continuation.disconnect()
                return PullParser.RequireMore(option.advanced - advanced, option.state as PullParser, failedChoices)
            }
            return if (waitingFor > 0) {
                if (hasZeroAdvance) {
                    PullParser.RequireMore(0, this, failedChoices)
                } else {
                    advanced++
                    PullParser.RequireMore(1, this, failedChoices)
                }
            } else {
                failedChoices
            }
        }
    }

    internal class OptionState<IN> internal constructor(var state: ParseState<IN>, internal val continuation: OptionContinuation<*, *>) {
        var advanced = 0

        override fun toString(): String {
            return "{$state advances=$advanced}"
        }
    }

    internal class OptionContinuation<IN, OUT>(val next: ParseContinuation<IN, OUT>) : ParseContinuation<IN, OUT> {
        override fun toString(): String {
            return "{choice-option-continuation $next}"
        }

        private var connected = true

        override fun matched(input: IN, advance: Int, length: Int, value: ValueProvider<OUT>, failedChoices: PullParser.Failed): PullParser.Result<IN> {
            val result = next.matched(input, advance, length, value, failedChoices)
            return if (connected && result is PullParser.RequireMore) {
                PullParser.Matched(result.advance, result.parser, result.failedChoices)
            } else {
                result
            }
        }

        override fun <T> selected(advance: Int, parser: PullParser<T>, failedChoices: PullParser.Failed): PullParser.Continuing<T> {
            // called when option parser selects a specific alternative
            return if (connected) {
                PullParser.Matched(advance, parser, failedChoices)
            } else {
                next.selected(advance, parser, failedChoices)
            }
        }

        override fun failed(index: Int, length: Int, expected: ExpectationProvider): PullParser.Failed {
            return next.failed(index, length, expected)
        }

        fun disconnect() {
            connected = false
        }
    }
}