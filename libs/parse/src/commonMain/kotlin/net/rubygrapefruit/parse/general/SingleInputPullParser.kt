package net.rubygrapefruit.parse.general

import net.rubygrapefruit.parse.*
import net.rubygrapefruit.parse.combinators.Extractor

internal class SingleInputCompiledParser<IN : Input<*>, OUT>(
    val parser: SingleInputParser<IN>,
    val extractor: Extractor<IN, OUT>
) : CompiledParser<IN, OUT> {
    override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
        return SingleInputPullParser(parser, extractor, next)
    }

    private class SingleInputPullParser<IN : Input<*>, OUT, NEXT>(
        private val parser: SingleInputParser<IN>,
        private val extractor: Extractor<IN, OUT>,
        private val next: ParseContinuation<IN, OUT, NEXT>
    ) : PullParser<IN, NEXT> {
        override fun toString(): String {
            return "{one $parser}"
        }

        override fun stop(): PullParser.Failed {
            return PullParser.Failed(0, parser.expectation)
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN, NEXT> {
            return if (max == 0) {
                if (input.available == 0 && input.finished) {
                    stop()
                } else {
                    PullParser.RequireMore(0, 0, false, this)
                }
            } else {
                if (parser.match(input, 0)) {
                    val result = extractor.extract(input)
                    next.matched(0, 1, result)
                } else {
                    stop()
                }
            }
        }
    }
}