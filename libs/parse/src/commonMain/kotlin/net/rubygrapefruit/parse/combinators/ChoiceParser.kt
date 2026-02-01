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
        private val next: ParseContinuation<IN, OUT, NEXT>
    ) : PullParser<IN, NEXT> {
        private val states = Array<ParseState<IN, NEXT>>(parsers.size) { index ->
            val parser = parsers[index]
            parser.start { length, value ->
                ContinuingMatchedOption(next.next(length, value))
            }
        }

        override fun toString(): String {
            return "{choice}"
        }

        override fun stop(): PullParser.Failed {
            return PullParser.Failed.merged(states.mapNotNull {
                if (it is PullParser) {
                    it.stop()
                } else if (it is PullParser.Failed && it.index == 0) {
                    it
                } else {
                    null
                }
            })
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN, NEXT> {
            var requireMore = false
            val maxAdvance = min(max, 1)
            for (index in states.indices) {
                val option = states[index]
                if (option is PullParser) {
                    val optionResult = option.parseZeroOrOne(input, maxAdvance)
                    when (optionResult) {
                        is PullParser.Matched -> TODO()

                        is PullParser.Failed -> {
                            states[index] = optionResult
                        }

                        is PullParser.RequireMore -> {
                            if (optionResult.parser is MatchedOption) {
                                val state = optionResult.parser.state
                                when (state) {
                                    is PullParser.Matched -> TODO()
                                    is PullParser.Failed -> {
                                        if (!requireMore) {
                                            val translated = if (optionResult.advance == 0) {
                                                state
                                            } else {
                                                PullParser.Failed(state.index + optionResult.advance, state.expected)
                                            }
                                            states[index] = translated
                                            return mergedFailures()
                                        }
                                        states[index] = optionResult.parser
                                    }
                                    is PullParser -> {
                                        if (!requireMore) {
                                            if (optionResult.failedChoice != null) {
                                                TODO()
                                            }
                                            return PullParser.RequireMore(optionResult.advance, state)
                                        }
                                        states[index] = optionResult.parser
                                    }
                                }
                            } else {
                                requireMore = true
                                states[index] = optionResult.parser
                            }
                        }
                    }
                }
            }
            return if (requireMore) {
                if (maxAdvance > 0) {
                    for (index in states.indices) {
                        val choice = states[index]
                        if (choice is PullParser.Failed) {
                            states[index] = PullParser.Failed(choice.index - maxAdvance, choice.expected)
                        }
                    }
                }
                PullParser.RequireMore(maxAdvance, this)
            } else {
                mergedFailures()
            }
        }

        private fun mergedFailures(): PullParser.Failed {
            val failures = states.filterIsInstance<PullParser.Failed>()
            return PullParser.Failed.merged(failures)
        }
    }

    internal interface MatchedOption<IN, NEXT> : PullParser<IN, NEXT> {
        val state: ParseState<IN, NEXT>
    }

    private class ContinuingMatchedOption<IN, NEXT>(var parser: PullParser<IN, NEXT>) : MatchedOption<IN, NEXT> {
        override val state: ParseState<IN, NEXT>
            get() = parser

        override fun toString(): String {
            return "{matched-option $parser}"
        }

        override fun stop(): PullParser.Failed {
            return parser.stop()
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN, NEXT> {
            val result = parser.parse(input, max)
            when (result) {
                is PullParser.Matched -> return PullParser.RequireMore(0, FinishedMatchedOption(result))
                is PullParser.Failed -> return PullParser.RequireMore(0, FailedMatchedOption(result))
                is PullParser.RequireMore -> {
                    parser = result.parser
                    if (result.failedChoice != null) {
                        TODO()
                    }
                    return PullParser.RequireMore(result.advance, this)
                }
            }
        }
    }

    private class FailedMatchedOption<IN, NEXT>(override var state: PullParser.Failed) : MatchedOption<IN, NEXT> {
        override fun toString(): String {
            return "{failed-matched-option $state}"
        }

        override fun stop(): PullParser.Failed {
            TODO()
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN, NEXT> {
            return if (max == 0) {
                TODO()
            } else {
                state = PullParser.Failed(state.index - max, state.expected)
                PullParser.RequireMore(max, this)
            }
        }
    }

    private class FinishedMatchedOption<IN, NEXT>(override val state: PullParser.Matched<NEXT>) : MatchedOption<IN, NEXT> {
        override fun stop(): PullParser.Failed {
            TODO()
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN, NEXT> {
            TODO()
        }
    }
}