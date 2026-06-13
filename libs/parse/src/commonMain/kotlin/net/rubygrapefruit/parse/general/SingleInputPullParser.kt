package net.rubygrapefruit.parse.general

import net.rubygrapefruit.parse.*
import net.rubygrapefruit.parse.combinators.Extractor
import net.rubygrapefruit.parse.stream.Input

internal class SingleInputCompiledParser<IN : Input<*>, OUT>(
    val parser: SingleInputParser<IN>,
    val extractor: Extractor<IN, OUT>
) : CompiledParser<IN, OUT> {
    override fun start(start: Position, next: ParseContinuation<IN, OUT>): PullParser<IN> {
        return SingleInputPullParser(parser, extractor, next)
    }

    private class SingleInputPullParser<IN : Input<*>, OUT>(
        private val parser: SingleInputParser<IN>,
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
                    val result = extractor.extract(input)
                    next.matched(input, 1, 1, result)
                } else {
                    stop()
                }
            }
        }

        private fun stop(): PullParser.Failed {
            return next.failed(0, 0, parser.expectation)
        }
    }
}