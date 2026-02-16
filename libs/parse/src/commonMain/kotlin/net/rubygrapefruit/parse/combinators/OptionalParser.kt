package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*
import net.rubygrapefruit.parse.general.SucceedParser

internal class OptionalParser<IN, INTERMEDIATE : OUT, OUT>(
    private val parser: Parser<IN, INTERMEDIATE>,
    private val default: ValueProvider<OUT>
) : Parser<IN, OUT>, CombinatorBuilder<OUT> {
    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, OUT> {
        return ChoiceParser.of(listOf(compiler.compile(parser), SucceedParser.SucceedCompiledParser(default)))
    }
}