package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.stream.ContextualInput
import net.rubygrapefruit.parse.stream.Input

internal fun <CONTEXT, IN : ContextualInput<CONTEXT, *>, OUT> parse(
    parser: Parser<*, OUT>,
    input: IN,
    failureFormatter: (CONTEXT, String) -> String
): ParseResult<CONTEXT, OUT> {
    val parser = DefaultPushParser(parser, failureFormatter)
    return parser.endOfInput(input)
}

internal fun Expectation.format(): String {
    val expected = mutableSetOf<String>()
    accept(expected::add)
    return "Expected ${expected.sorted().joinToString(", ")}"
}

internal fun <IN : Input<*>, OUT> Parser<*, OUT>.compile(): CompiledParser<IN, OUT> {
    return DefaultCompiler<IN>().compile(this)
}

private class DefaultCompiler<IN : Input<*>> : CombinatorBuilder.Compiler<IN>, CombinatorSingleInputBuilder.Compiler<IN> {
    private val compiledParsers = mutableMapOf<Parser<*, *>, CompiledParser<IN, *>>()
    private val compiledNoResultParsers = mutableMapOf<Parser<*, *>, CompiledParser<IN, *>>()

    override fun <OUT> maybeAsSingleInputParser(parser: Parser<*, OUT>): LookaheadOneParser<IN, OUT>? {
        return when (parser) {
            is LookaheadOneParser<*, *> -> {
                @Suppress("UNCHECKED_CAST")
                parser as LookaheadOneParser<IN, OUT>
            }

            is CombinatorSingleInputBuilder<*> -> {
                @Suppress("UNCHECKED_CAST")
                (parser as CombinatorSingleInputBuilder<OUT>).maybeAsSingleInputParser(this)
            }

            else -> null
        }
    }

    override fun compile(predicate: InputPredicate<*>): InputPredicate<IN> {
        @Suppress("UNCHECKED_CAST")
        return predicate as InputPredicate<IN>
    }

    override fun compileWithNoResult(parser: Parser<*, *>): CompiledParser<IN, Unit> {
        val compiled = compiledNoResultParsers.getOrPut(parser) { doCompileNoResult(parser) }
        @Suppress("UNCHECKED_CAST")
        return compiled as CompiledParser<IN, Unit>
    }

    private fun doCompileNoResult(parser: Parser<*, *>): CompiledParser<IN, Unit> {
        return when (parser) {
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
