package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*

internal class ZeroOrMoreProduceNothingParser<IN>(private val parser: Parser<IN, Unit>) : Parser<IN, Unit>, CombinatorBuilder<Unit> {
    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, Unit> {
        val compiled = ZeroOrMoreParser(parser).compile(compiler)

        return object : CompiledParser<IN, Unit> {
            override val mayNotAdvanceOnMatch: Boolean
                get() = compiled.mayNotAdvanceOnMatch

            override val expectation: Expectation
                get() = compiled.expectation

            override fun <NEXT> start(next: ParseContinuation<IN, Unit, NEXT>): PullParser<IN, NEXT> {
                return compiled.start { matched ->
                    next.matched(matched.start, matched.end, Unit)
                }
            }
        }
    }
}