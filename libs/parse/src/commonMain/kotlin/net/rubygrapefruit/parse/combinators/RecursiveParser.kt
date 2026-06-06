package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*
import net.rubygrapefruit.parse.stream.Input
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

interface RecursiveParser<IN, OUT> : Parser<IN, OUT> {
    fun parser(parser: Parser<IN, OUT>)
}

@OptIn(ExperimentalAtomicApi::class)
internal class DefaultRecursiveParser<IN, OUT>(
    initialValue: Parser<IN, OUT>? = null
) : RecursiveParser<IN, OUT>, CombinatorBuilder<OUT>, DiscardableParser<IN> {
    private val parser = AtomicReference<Parser<IN, OUT>?>(initialValue)

    override fun parser(parser: Parser<IN, OUT>) {
        if (!this.parser.compareAndSet(null, parser)) {
            throw IllegalStateException("Parser already set")
        }
    }

    private fun parser() = parser.load() ?: throw IllegalStateException("No parser set")

    override fun withNoResult(): Parser<IN, Unit> {
        return DefaultRecursiveParser(DiscardParser(parser()))
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, OUT> {
        val compiledOuter = RecursiveCompiledParser<IN, OUT>()
        val compiledInner = compiler.compileRecursive(this, compiledOuter, parser())
        compiledOuter.parser = compiledInner
        return compiledOuter
    }

    internal class RecursiveCompiledParser<IN, OUT> : CompiledParser<IN, OUT> {
        lateinit var parser: CompiledParser<IN, OUT>

        override fun start(start: Position, next: ParseContinuation<IN, OUT>): PullParser<IN> {
            return parser.start(start, next)
        }
    }
}

/**
 * Returns a parser that is applied recursively.
 */
fun <IN, OUT> recursive(): RecursiveParser<IN, OUT> {
    return DefaultRecursiveParser()
}