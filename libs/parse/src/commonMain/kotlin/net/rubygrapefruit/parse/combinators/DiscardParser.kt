package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*
import net.rubygrapefruit.parse.stream.Input

internal class DiscardParser<IN>(private val parser: Parser<IN, *>) : Parser<IN, Unit>, CombinatorBuilder<Unit>, CombinatorSingleInputBuilder<Unit>, DiscardableParser<IN> {
    override fun withNoResult(): Parser<IN, Unit> {
        return this
    }

    override fun <IN> maybeAsSingleInputParser(compiler: CombinatorSingleInputBuilder.Compiler<IN>): SingleInputParser<IN, Unit>? {
        val singleInputParser = compiler.maybeAsSingleInputParser(parser)
        return if (singleInputParser != null) {
            SingleInputParserNoResult(singleInputParser.predicate)
        } else {
            null
        }
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, Unit> {
        return compiler.compileWithNoResult(parser)
    }

    private class SingleInputParserNoResult<IN>(
        override val predicate: InputPredicate<IN>,
    ) : SingleInputParser<IN, Unit> {
        override val extractor: Extractor<Any?, Unit>
            get() = UnitExtractor
    }
}

