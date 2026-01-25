package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.NextValueExtractor
import net.rubygrapefruit.parse.combinators.UnitExtractor
import net.rubygrapefruit.parse.combinators.suffixed
import net.rubygrapefruit.parse.general.SingleInputCompiledParser
import net.rubygrapefruit.parse.general.endOfInput

internal fun <CONTEXT, IN : AdvancingInput<*>, OUT> parse(
    parser: PullParser<IN, OUT>,
    input: IN,
    failureFactory: (IN, Int, String) -> ParseResult.Fail<CONTEXT>
): ParseResult<CONTEXT, OUT> {
    val parser = DefaultPushParser<CONTEXT, IN, OUT>(parser)
    return parser.endOfInput(input, failureFactory)
}

internal fun Expectation.format(): String {
    val expected = mutableSetOf<String>()
    accept(expected::add)
    return "Expected ${expected.sorted().joinToString(", ")}"
}

internal fun <IN, OUT> PullParser<IN, OUT>.parseZeroOrOne(input: IN, maxAdvance: Int): PullParser.Result<IN, OUT> {
    var current = this
    while (true) {
        val result = current.parse(input, maxAdvance)
        if (maxAdvance == 1 && result is PullParser.RequireMore && result.advance == 0) {
            current = result.parser
            continue
        }
        return result
    }
}

internal fun <IN : Input<*>, OUT> Parser<*, OUT>.start(): PullParser<IN, OUT> {
    val all = suffixed(this, endOfInput())
    return DefaultCompiler<IN>().compile(all).start()
}

internal fun <IN : Input<*>, OUT> Parser<*, OUT>.compile(): CompiledParser<IN, OUT> {
    return DefaultCompiler<IN>().compile(this)
}

private class DefaultCompiler<IN : Input<*>> : CombinatorBuilder.Compiler<IN>, CombinatorSingleInputBuilder.Compiler<IN> {
    private val compiledParsers = mutableMapOf<Parser<*, *>, CompiledParser<IN, *>>()
    private val compiledNoResultParsers = mutableMapOf<Parser<*, *>, CompiledParser<IN, *>>()

    override fun maybeAsSingleInputParser(parser: Parser<*, *>): SingleInputParser<IN>? {
        return when (parser) {
            is SingleInputParser<*> -> {
                @Suppress("UNCHECKED_CAST")
                parser as SingleInputParser<IN>
            }

            is CombinatorSingleInputBuilder -> {
                @Suppress("UNCHECKED_CAST")
                (parser as CombinatorSingleInputBuilder).maybeAsSingleInputParser(this)
            }

            else -> {
                null
            }
        }
    }

    override fun compileWithNoResult(parser: Parser<*, *>): CompiledParser<IN, Unit> {
        val compiled = compiledNoResultParsers.getOrPut(parser) { doCompileNoResult(parser) }
        @Suppress("UNCHECKED_CAST")
        return compiled as CompiledParser<IN, Unit>
    }

    private fun doCompileNoResult(parser: Parser<*, *>): CompiledParser<IN, Unit> {
        return when (parser) {
            is SingleInputParser<*> -> {
                @Suppress("UNCHECKED_CAST")
                SingleInputCompiledParser(parser as SingleInputParser<IN>, UnitExtractor)
            }

            is DiscardableParser<*> -> {
                @Suppress("UNCHECKED_CAST")
                doCompile((parser as DiscardableParser<IN>).withNoResult())
            }

            else -> throw IllegalArgumentException("Cannot compile parser $parser with unexpected type")
        }
    }

    override fun <OUT> compile(parser: Parser<*, OUT>): CompiledParser<IN, OUT> {
        val compiled = compiledParsers.getOrPut(parser) { doCompile(parser) }
        @Suppress("UNCHECKED_CAST")
        return compiled as CompiledParser<IN, OUT>
    }

    override fun <OUT> compileRecursive(outer: Parser<*, OUT>, compiledOuter: CompiledParser<IN, OUT>, parser: Parser<*, OUT>): CompiledParser<IN, OUT> {
        compiledParsers[outer] = compiledOuter
        return compile(parser)
    }

    private fun <OUT> doCompile(parser: Parser<*, OUT>): CompiledParser<IN, OUT> {
        return when (parser) {
            is ParserBuilder<*, *> -> {
                @Suppress("UNCHECKED_CAST")
                ParserBuilderAdaptor(parser as ParserBuilder<IN, OUT>)
            }

            is SingleInputParser<*> -> {
                @Suppress("UNCHECKED_CAST")
                SingleInputCompiledParser(parser as SingleInputParser<IN>, NextValueExtractor.of<_, OUT>()) as CompiledParser<IN, OUT>
            }

            is CombinatorBuilder<*> -> {
                @Suppress("UNCHECKED_CAST")
                (parser as CombinatorBuilder<OUT>).compile(this)
            }

            is TypedInputCombinatorBuilder<*, *> -> {
                @Suppress("UNCHECKED_CAST")
                (parser as TypedInputCombinatorBuilder<IN, OUT>).compile(this)
            }

            else -> throw IllegalArgumentException("Cannot compile parser $parser with unexpected type")
        }
    }
}
