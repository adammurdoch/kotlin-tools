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
        private var matched = 0

        override fun toString(): String {
            return "{choice}"
        }

        override fun stop(): PullParser.Failed {
            return PullParser.Failed(states.flatMap {
                val state = it.state
                when (state) {
                    is PullParser -> state.stop().failures
                    else -> emptyList()
                }
            })
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN> {
            val maxAdvance = min(max, 1)
            var waitingFor = 0
            var hasZeroAdvance = false
            val failedChoices = mutableListOf<PullParser.Failure>()
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
                            failedChoices.addAll(optionResult.failures)
                            option.state = optionResult
                        }

                        is PullParser.RequireMore -> {
                            if (optionResult.matched) {
                                if (waitingFor == 0) {
                                    return option.continuation.next.selected(
                                        optionResult.advance,
                                        optionResult.parser,
                                        failedChoices + optionResult.failedChoices
                                    )
                                }
                            }
                            waitingFor++
                            failedChoices.addAll(optionResult.failedChoices)
                            option.state = optionResult.parser
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
                option.continuation.disconnect()
                return PullParser.RequireMore(option.matched - matched, false, option.state as PullParser, failedChoices)
            }
            return if (waitingFor > 0) {
                if (hasZeroAdvance) {
                    PullParser.RequireMore(0, false, this, failedChoices)
                } else {
                    matched++
                    PullParser.RequireMore(1, false, this, failedChoices)
                }
            } else {
                PullParser.Failed(failedChoices)
            }
        }
    }

    internal class OptionState<IN> internal constructor(var state: ParseState<IN>, internal val continuation: OptionContinuation<*, *>) {
        var matched = 0

        override fun toString(): String {
            return "{$state matched=$matched}"
        }
    }

    internal class OptionContinuation<IN, OUT>(val next: ParseContinuation<IN, OUT>) : ParseContinuation<IN, OUT> {
        override fun toString(): String {
            return "{choice-option-continuation $next}"
        }

        private var connected = true

        override fun matched(advance: Int, commit: Int, length: Int, value: ValueProvider<OUT>, failedChoices: List<PullParser.Failure>): PullParser.RequireMore<IN> {
            val result = next.matched(advance, commit, length, value, failedChoices)
            return if (connected && !result.matched) {
                PullParser.RequireMore(result.advance, true, result.parser, result.failedChoices)
            } else {
                result
            }
        }

        override fun <T> selected(advance: Int, parser: PullParser<T>, failedChoices: List<PullParser.Failure>): PullParser.RequireMore<T> {
            return if (connected) {
                PullParser.RequireMore(advance, true, parser, failedChoices)
            } else {
                next.selected(advance, parser, failedChoices)
            }
        }

        fun disconnect() {
            connected = false
        }
    }
}