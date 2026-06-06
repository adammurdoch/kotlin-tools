package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*
import net.rubygrapefruit.parse.stream.Input

internal class CheckParser<IN, INTERMEDIATE, OUT>(
    val parser: Parser<IN, INTERMEDIATE>,
    val map: (INTERMEDIATE) -> MappingResult<OUT>
) : Parser<IN, OUT>, CombinatorBuilder<OUT>, DiscardableParser<IN> {
    override fun withNoResult(): Parser<IN, Unit> {
        return CheckParser(parser) { value ->
            val result = map(value)
            when (result) {
                is MappingResult.Success -> MappingResult.of(Unit)
                is MappingResult.Fail -> result
            }
        }
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, OUT> {
        return CheckCompiledParser(compiler.compile(parser), map)
    }

    internal class CheckCompiledParser<IN, INTERMEDIATE, OUT>(
        val parser: CompiledParser<IN, INTERMEDIATE>,
        val map: (INTERMEDIATE) -> MappingResult<OUT>
    ) : CompiledParser<IN, OUT> {
        override fun start(start: Position, next: ParseContinuation<IN, OUT>): PullParser<IN> {
            val continuation = CheckContinuation(next, map)
            return parser.start(start, continuation)
        }
    }

    private class CheckContinuation<IN, INTERMEDIATE, OUT>(
        val next: ParseContinuation<IN, OUT>,
        val map: (INTERMEDIATE) -> MappingResult<OUT>
    ) : ParseContinuation<IN, INTERMEDIATE> {
        override fun matched(input: IN, advance: Int, length: Int, value: ValueProvider<INTERMEDIATE>, failedChoices: List<PullParser.Failure>): PullParser.Result<IN> {
            val result = map(value.get())
            return when (result) {
                is MappingResult.Success -> next.matched(input, advance, length, ValueProvider.of(result.value), failedChoices)
                is MappingResult.Fail -> {
                    val parser = BrokenPullParser(PullParser.Failed(advance - length, Expectation.One(result.expected)))
                    PullParser.RequireMore(0, false, parser, failedChoices)
                }
            }
        }

        override fun <T> selected(advance: Int, parser: PullParser<T>, failedChoices: List<PullParser.Failure>): PullParser.RequireMore<T> {
            return next.selected(advance, parser, failedChoices)
        }
    }

    private class BrokenPullParser(val failure: PullParser.Failed) : PullParser<Any?> {
        override fun stop(input: Any?): PullParser.Failed {
            return failure
        }

        override fun parse(input: Any?, max: Int): PullParser.Result<Any?> {
            return failure
        }
    }
}