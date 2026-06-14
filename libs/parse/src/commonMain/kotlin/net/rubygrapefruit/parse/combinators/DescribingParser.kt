package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*
import net.rubygrapefruit.parse.stream.Input

internal class DescribingParser<IN, OUT>(
    private val parser: Parser<IN, OUT>,
    private val description: String
) : Parser<IN, OUT>, CombinatorBuilder<OUT>, DiscardableParser<IN>, CombinatorSingleInputBuilder<OUT> {
    override fun withNoResult(): Parser<IN, Unit> {
        return DescribingParser(DiscardParser(parser), description)
    }

    override fun <IN> maybeAsSingleInputParser(compiler: CombinatorSingleInputBuilder.Compiler<IN>): SingleInputParser<IN, OUT>? {
        val singleInputParser = compiler.maybeAsSingleInputParser(parser)
        return if (singleInputParser != null) {
            object : SingleInputParser<IN, OUT> {
                override val predicate: InputPredicate<IN>
                    get() = singleInputParser.predicate

                override val expectation: Expectation = Expectation.One(description)

                override val extractor: Extractor<IN, OUT>
                    get() = singleInputParser.extractor
            }
        } else {
            null
        }
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, OUT> {
        return DescribingCompiledParser(compiler.compile(parser), Expectation.One(description))
    }

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
    return DescribingParser(parser, description)
}