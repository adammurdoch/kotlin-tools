package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.BoxingInput

/**
 * Implementation may be mutable.
 */
internal interface RangeAccumulator<in IN, out OUT> {
    val value: OUT

    fun extract(input: IN, start: Int, end: Int): RangeAccumulator<IN, OUT>
}

internal abstract class ListRangeAccumulator<IN : BoxingInput<*, T>, T> : RangeAccumulator<IN, List<T>> {
    override fun extract(input: IN, start: Int, end: Int): RangeAccumulator<IN, List<T>> {
        val items = mutableListOf<T>()
        for (i in start until end) {
            items.add(input.getBoxed(i))
        }
        return Matched(items, this)
    }

    protected abstract fun collectInto(list: MutableList<T>)

    class Empty<IN : BoxingInput<*, T>, T> : ListRangeAccumulator<IN, T>() {
        override val value: List<T>
            get() = emptyList()

        override fun collectInto(list: MutableList<T>) {
        }
    }

    private class Matched<IN : BoxingInput<*, T>, T>(private val items: List<T>, private val prev: ListRangeAccumulator<IN, T>) : ListRangeAccumulator<IN, T>() {
        override val value: List<T>
            get() {
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
    override val value: Unit
        get() = Unit

    override fun extract(input: Any, start: Int, end: Int): RangeAccumulator<Any, Unit> {
        return this
    }
}