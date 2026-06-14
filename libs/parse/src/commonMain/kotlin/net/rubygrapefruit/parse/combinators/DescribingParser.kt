package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*
import net.rubygrapefruit.parse.stream.Input

internal class DescribingParser<IN, OUT>(
    private val parser: Parser<IN, OUT>,
    private val expectation: Expectation
) : Parser<IN, OUT>, CombinatorBuilder<OUT>, DiscardableParser<IN>, CombinatorSingleInputBuilder<OUT> {

    override fun withNoResult(): Parser<IN, Unit> {
        return DescribingParser(DiscardParser(parser), expectation)
    }

    override fun <IN> maybeAsSingleInputParser(compiler: CombinatorSingleInputBuilder.Compiler<IN>): SingleInputParser<IN, OUT>? {
        val singleInputParser = compiler.maybeAsSingleInputParser(parser)
        return if (singleInputParser != null) {
            DescribingSingleInputParser(singleInputParser, expectation)
        } else {
            null
        }
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, OUT> {
        return DescribingCompiledParser(compiler.compile(parser), expectation)
    }

    private class DescribingSingleInputParser<IN, OUT>(
        val delegate: SingleInputParser<IN, OUT>,
        override val expectation: Expectation
    ) : SingleInputParser<IN, OUT> by delegate

    internal class DescribingCompiledParser<IN, OUT>(
        val parser: CompiledParser<IN, OUT>,
        private val expectation: Expectation
    ) : CompiledParser<IN, OUT> {
        override fun start(start: Position, next: ParseContinuation<IN, OUT>): PullParser<IN> {
            return parser.start(start, DescribingParseContinuation(expectation, next))
        }
    }

    private class DescribingParseContinuation<IN, OUT>(
        private val expectation: Expectation,
        private val next: ParseContinuation<IN, OUT>
    ) : ParseContinuation<IN, OUT> {
        override fun matched(
            input: IN,
            advance: Int,
            length: Int,
            value: ValueProvider<OUT>,
            failedChoices: List<PullParser.Failure>
        ): PullParser.Result<IN> {
            return next.matched(input, advance, length, value, failedChoices.map { failure ->
                val start = advance - length
                if (failure.index == start) {
                    PullParser.Failure(failure.index, expectation)
                } else {
                    failure
                }
            })
        }

        override fun <T> selected(
            advance: Int,
            parser: PullParser<T>,
            failedChoices: List<PullParser.Failure>
        ): PullParser.Continuing<T> {
            return next.selected(advance, parser, failedChoices)
        }

        override fun failed(index: Int, length: Int, expected: ExpectationProvider): PullParser.Failed {
            return if (length == 0) {
                next.failed(index, length, expectation)
            } else {
                next.failed(index, length, expected)
            }
        }
    }
}

/**
 * Returns a parser that applies the given parser and uses the given description in error messages.
 */
fun <IN, OUT> describedAs(parser: Parser<IN, OUT>, description: String): Parser<IN, OUT> {
    return DescribingParser(parser, Expectation.One(description))
}