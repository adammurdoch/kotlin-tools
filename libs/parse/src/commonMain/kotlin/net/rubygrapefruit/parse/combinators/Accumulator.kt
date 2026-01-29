package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.ValueProvider

/**
 * Accumulates a sequence of values into a final value.
 */
internal interface Accumulator<ITEM, OUT> : ValueProvider<OUT> {
    val length: Int

    fun add(item: ValueProvider<ITEM>, length: Int): Accumulator<ITEM, OUT>
}

internal class UnitAccumulator(override val length: Int) : Accumulator<Unit, Unit> {
    override fun get() {
    }

    override fun add(item: ValueProvider<Unit>, length: Int): Accumulator<Unit, Unit> {
        return UnitAccumulator(length + this.length)
    }

    companion object {
        val Empty = UnitAccumulator(0)
    }
}

internal interface ListAccumulator<T> : Accumulator<T, List<T>> {
    fun collectInto(list: MutableList<T>)

    class Empty<T> : ListAccumulator<T> {
        override val length: Int
            get() = 0

        override fun get(): List<T> {
            return emptyList()
        }

        override fun collectInto(list: MutableList<T>) {
        }

        override fun add(item: ValueProvider<T>, length: Int): ListAccumulator<T> {
            return ListItem(length, item, this)
        }
    }
}

private class ListItem<T>(override val length: Int, val item: ValueProvider<T>, val prev: ListAccumulator<T>) : ListAccumulator<T> {
    override fun get(): List<T> {
        val list = mutableListOf<T>()
        collectInto(list)
        return list
    }

    override fun collectInto(list: MutableList<T>) {
        prev.collectInto(list)
        list.add(item.get())
    }

    override fun add(item: ValueProvider<T>, length: Int): ListItem<T> {
        return ListItem(length + this.length, item, this)
    }
}
