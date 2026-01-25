package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*

interface RecursiveParser<IN, OUT> : Parser<IN, OUT> {
    fun parser(parser: Parser<IN, OUT>)
}

internal class DefaultRecursiveParser<IN, OUT> : RecursiveParser<IN, OUT>, CombinatorBuilder<OUT> {
    private var parser: Parser<IN, OUT>? = null

    override fun parser(parser: Parser<IN, OUT>) {
        this.parser = parser
    }

    override fun withNoResult(): CombinatorBuilder<Unit> {
        TODO()
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, OUT> {
        val compiledOuter = RecursiveCompiledParser<IN, OUT>()
        val compiledInner = compiler.compileRecursive(this, compiledOuter, parser!!)
        compiledOuter.parser = compiledInner
        return compiledOuter
    }

    internal class RecursiveCompiledParser<IN, OUT> : CompiledParser<IN, OUT> {
        var parser: CompiledParser<IN, OUT>? = null

        override val mayNotAdvanceOnMatch: Boolean
            get() = parser!!.mayNotAdvanceOnMatch

        override val expectation: Expectation
            get() = parser!!.expectation

        override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return parser!!.start(next)
        }
    }
}

/**
 * Returns a parser that is applied recursively.
 */
fun <IN, OUT> recursive(): RecursiveParser<IN, OUT> {
    return DefaultRecursiveParser()
}