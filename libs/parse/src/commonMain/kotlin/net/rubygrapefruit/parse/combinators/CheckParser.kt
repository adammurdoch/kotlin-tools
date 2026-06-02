package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*

internal class CheckParser<IN, INTERMEDIATE, OUT>(
    val parser: Parser<IN, INTERMEDIATE>,
    val map: (INTERMEDIATE) -> MappingResult<OUT>
) : Parser<IN, OUT>, CombinatorBuilder<OUT> {
    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, OUT> {
        return CheckCompiledParser(compiler.compile(parser), map)
    }

    internal class CheckCompiledParser<IN, INTERMEDIATE, OUT>(
        val parser: CompiledParser<IN, INTERMEDIATE>,
        val map: (INTERMEDIATE) -> MappingResult<OUT>
    ) : CompiledParser<IN, OUT> {
        override fun start(next: ParseContinuation<IN, OUT>): PullParser<IN> {
            val continuation = CheckContinuation(next, map)
            return CheckPullParser(parser.start(continuation), continuation)
        }
    }

    private class CheckContinuation<IN, INTERMEDIATE, OUT>(
        val next: ParseContinuation<IN, OUT>,
        val map: (INTERMEDIATE) -> MappingResult<OUT>
    ) : ParseContinuation<IN, INTERMEDIATE> {
        var advance = 0

        override val matches: Boolean
            get() = next.matches

        override fun next(length: Int, value: ValueProvider<INTERMEDIATE>): PullParser<IN> {
            val result = map(value.get())
            return when (result) {
                is MappingResult.Success -> next.next(length, ValueProvider.of(result.value))
                is MappingResult.Fail -> BrokenPullParser(PullParser.Failed(-advance, Expectation.One(result.expected)))
            }
        }
    }

    private class BrokenPullParser<IN>(val failure: PullParser.Failed) : PullParser<IN> {
        override fun stop(): PullParser.Failed {
            return failure
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN> {
            return failure
        }
    }

    private class CheckPullParser<IN, INTERMEDIATE, OUT>(
        val parser: PullParser<IN>,
        val continuation: CheckContinuation<IN, INTERMEDIATE, OUT>
    ) : PullParser<IN> {
        override fun stop(): PullParser.Failed {
            return parser.stop()
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN> {
            val result = parser.parse(input, max)
            if (result is PullParser.RequireMore) {
                continuation.advance += result.advance
            }
            return result
        }
    }
}