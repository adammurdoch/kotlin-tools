package net.rubygrapefruit.parse.general

import net.rubygrapefruit.parse.*
import net.rubygrapefruit.parse.combinators.Extractor
import net.rubygrapefruit.parse.combinators.NextValueExtractor
import net.rubygrapefruit.parse.combinators.UnitExtractor
import net.rubygrapefruit.parse.stream.BoxingInput
import net.rubygrapefruit.parse.stream.Input

internal class MatchOneInputParser<IN, ITEM, OUT, STREAM : BoxingInput<*, ITEM>>(
    override val predicate: InputPredicate<STREAM>,
    override val expectation: Expectation,
    override val extractor: Extractor<STREAM, OUT> = NextValueExtractor.of()
) : Parser<IN, OUT>, TypedInputCombinatorBuilder<STREAM, OUT>, DiscardableParser<IN>, SingleInputParser<STREAM, OUT> {

    override fun withNoResult(): Parser<IN, Unit> {
        return MatchOneInputParser(predicate, expectation, UnitExtractor)
    }

    override fun compile(compiler: CombinatorBuilder.Compiler<STREAM>): CompiledParser<STREAM, OUT> {
        return SingleInputCompiledParser(predicate, expectation, extractor)
    }

    internal class SingleInputCompiledParser<IN : Input<*>, OUT>(
        val parser: InputPredicate<IN>,
        val expectation: Expectation,
        val extractor: Extractor<IN, OUT>
    ) : CompiledParser<IN, OUT> {
        override fun start(start: Position, next: ParseContinuation<IN, OUT>): PullParser<IN> {
            return SingleInputPullParser(parser, expectation, extractor, next)
        }
    }

    private class SingleInputPullParser<IN : Input<*>, OUT>(
        private val parser: InputPredicate<IN>,
        private val expectation: Expectation,
        private val extractor: Extractor<IN, OUT>,
        private val next: ParseContinuation<IN, OUT>
    ) : PullParser<IN> {
        override fun toString(): String {
            return "{one $parser}"
        }

        override fun stop(input: IN): PullParser.Failed {
            return stop()
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN> {
            return if (max == 0) {
                if (input.available == 0 && input.finished) {
                    stop()
                } else {
                    PullParser.RequireMore(0, this)
                }
            } else {
                if (parser.match(input, 0)) {
                    val result = extractor.extract(input, 0)
                    next.matched(input, 1, 1, result)
                } else {
                    stop()
                }
            }
        }

        private fun stop(): PullParser.Failed {
            return next.failed(0, 0, expectation)
        }
    }

}