package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*
import net.rubygrapefruit.parse.general.SucceedParser

internal class ZeroOrMoreParser<IN, OUT>(private val parser: Parser<IN, OUT>) : Parser<IN, List<OUT>>, CombinatorBuilder<List<OUT>> {
    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, List<OUT>> {
        val option = compiler.compile(parser)
        return ZeroOrMoreCompiledParser(option)
    }

    private class ZeroOrMoreCompiledParser<IN, OUT>(
        val option: CompiledParser<IN, OUT>,
    ) : CompiledParser<IN, List<OUT>> {
        private val previous = Empty<OUT>()
        private val empty = EmptyCompiledParser<IN, OUT>(previous)
        private val nested = OptionCompiledParser(option, previous)

        override val mayNotAdvanceOnMatch: Boolean
            get() = true

        override val expectation: Expectation
            get() = option.expectation

        override fun <NEXT> start(next: ParseContinuation<IN, List<OUT>, NEXT>): PullParser<IN, NEXT> {
            return ChoiceParser.of(listOf(nested, empty), next)
        }
    }

    private sealed interface Collector<T> {
        val length: Int

        val items: List<T>

        fun collectInto(list: MutableList<T>)

        fun add(item: T, length: Int): Matched<T>
    }

    private class Empty<T> : Collector<T> {
        override val length: Int
            get() = 0

        override val items: List<T>
            get() = emptyList()

        override fun collectInto(list: MutableList<T>) {
        }

        override fun add(item: T, length: Int): Matched<T> {
            return Matched(length, item, this)
        }
    }

    private class Matched<T>(override val length: Int, val item: T, val prev: Collector<T>) : Collector<T> {
        override val items: List<T>
            get() {
                val list = mutableListOf<T>()
                collectInto(list)
                return list
            }

        override fun collectInto(list: MutableList<T>) {
            prev.collectInto(list)
            list.add(item)
        }

        override fun add(item: T, length: Int): Matched<T> {
            return Matched(length + this.length, item, this)
        }
    }

    private class OptionCompiledParser<IN, OUT>(val option: CompiledParser<IN, OUT>, val previous: Collector<OUT>) :
        CompiledParser<IN, List<OUT>> {
        override val mayNotAdvanceOnMatch: Boolean
            get() = option.mayNotAdvanceOnMatch

        override val expectation: Expectation
            get() = option.expectation

        override fun <NEXT> start(next: ParseContinuation<IN, List<OUT>, NEXT>): PullParser<IN, NEXT> {
            return option.start { matched ->
                val length = matched.end - matched.start
                val result = previous.add(matched.value, length)
                val parser = if (length == 0) {
                    SucceedParser.start(result.items, next)
                } else {
                    val empty = EmptyCompiledParser<IN, OUT>(result)
                    val nested = OptionCompiledParser(option, result)
                    ChoiceParser.of(listOf(nested, empty), next)
                }
                PullParser.RequireMore(matched.end, parser)
            }
        }
    }

    private class EmptyCompiledParser<IN, OUT>(val result: Collector<OUT>) : CompiledParser<IN, List<OUT>> {
        override val mayNotAdvanceOnMatch: Boolean
            get() = true

        override val expectation: Expectation
            get() = Expectation.Nothing

        override fun <NEXT> start(next: ParseContinuation<IN, List<OUT>, NEXT>): PullParser<IN, NEXT> {
            return SucceedParser.start(result.items, next, length = result.length)
        }
    }
}