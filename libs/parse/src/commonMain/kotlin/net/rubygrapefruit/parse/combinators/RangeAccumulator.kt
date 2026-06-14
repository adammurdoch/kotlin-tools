package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.ValueProvider
import net.rubygrapefruit.parse.stream.BoxingInput

/**
 * Implementations are immutable.
 */
internal interface RangeAccumulator<in IN, out OUT> : ValueProvider<OUT> {
    fun extract(input: IN, start: Int, end: Int): RangeAccumulator<IN, OUT>
}

internal abstract class ListRangeAccumulator<IN : BoxingInput<*, T>, T> : RangeAccumulator<IN, List<T>> {
    protected abstract val extractor: Extractor<IN, T>

    override fun extract(input: IN, start: Int, end: Int): RangeAccumulator<IN, List<T>> {
        val items = mutableListOf<T>()
        for (i in start until end) {
            items.add(extractor.extract(input, i))
        }
        return Matched(items, extractor, this)
    }

    protected abstract fun collectInto(list: MutableList<T>)

    class Empty<IN : BoxingInput<*, T>, T>(override val extractor: Extractor<IN, T>) : ListRangeAccumulator<IN, T>() {
        override fun get(): List<T> {
            return emptyList()
        }

        override fun collectInto(list: MutableList<T>) {
        }
    }

    private class Matched<IN : BoxingInput<*, T>, T>(
        private val items: List<T>,
        override val extractor: Extractor<IN, T>,
        private val prev: ListRangeAccumulator<IN, T>
    ) : ListRangeAccumulator<IN, T>() {
        override fun get(): List<T> {
            val value = mutableListOf<T>()
            collectInto(value)
            return value
        }

        override fun collectInto(list: MutableList<T>) {
            prev.collectInto(list)
            list.addAll(items)
        }
    }
}

internal object UnitRangeAccumulator : RangeAccumulator<Any, Unit> {
    override fun get() {
    }

    override fun extract(input: Any, start: Int, end: Int): RangeAccumulator<Any, Unit> {
        return this
    }
}