package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.ValueProvider

/**
 * Accumulates a sequence of values into a final value.
 */
internal interface Accumulator<ITEM, OUT> : ValueProvider<OUT> {
    fun add(item: ValueProvider<ITEM>): Accumulator<ITEM, OUT>
}

internal object UnitAccumulator : Accumulator<Unit, Unit> {
    override fun get() {
    }

    override fun add(item: ValueProvider<Unit>): Accumulator<Unit, Unit> {
        return this
    }
}

internal interface ListAccumulator<T> : Accumulator<T, List<T>> {
    fun collectInto(list: MutableList<T>)

    class Empty<T> : ListAccumulator<T> {
        override fun get(): List<T> {
            return emptyList()
        }

        override fun collectInto(list: MutableList<T>) {
        }

        override fun add(item: ValueProvider<T>): ListAccumulator<T> {
            return ListItem(item, this)
        }
    }
}

private class ListItem<T>(val item: ValueProvider<T>, val prev: ListAccumulator<T>) : ListAccumulator<T> {
    override fun get(): List<T> {
        val list = mutableListOf<T>()
        collectInto(list)
        return list
    }

    override fun collectInto(list: MutableList<T>) {
        prev.collectInto(list)
        list.add(item.get())
    }

    override fun add(item: ValueProvider<T>): ListItem<T> {
        return ListItem(item, this)
    }
}
