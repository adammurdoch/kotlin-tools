package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.ValueProvider

/**
 * Accumulates a sequence of values into a final value.
 *
 * Implementations are immutable.
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
    class Empty<T> : ListAccumulator<T> {
        override fun get(): List<T> {
            return emptyList()
        }

        override fun add(item: ValueProvider<T>): ListAccumulator<T> {
            return OneItem(item)
        }
    }
}

private interface CollectingListAccumulator<T> : ListAccumulator<T> {
    val length: Int

    fun collectInto(dest: MutableList<T>): CollectingListAccumulator<T>?
}

private class OneItem<T>(val item: ValueProvider<T>) : CollectingListAccumulator<T> {
    override val length: Int
        get() = 1

    override fun get(): List<T> {
        return listOf(item.get())
    }

    override fun add(item: ValueProvider<T>): Accumulator<T, List<T>> {
        return ListItem(2, item, this)
    }

    override fun collectInto(dest: MutableList<T>): CollectingListAccumulator<T>? {
        dest.add(item.get())
        return null
    }
}

private class ListItem<T>(
    override val length: Int,
    val item: ValueProvider<T>,
    val prev: CollectingListAccumulator<T>
) : CollectingListAccumulator<T> {
    override fun get(): List<T> {
        // Builds list by walking back along the chain iteratively, rather than recursively
        val list = ArrayList<T>(length)
        var current: CollectingListAccumulator<T>? = this
        while (current != null) {
            current = current.collectInto(list)
        }
        list.reverse()
        return list
    }

    override fun collectInto(dest: MutableList<T>): CollectingListAccumulator<T> {
        dest.add(item.get())
        return prev
    }

    override fun add(item: ValueProvider<T>): ListItem<T> {
        return ListItem(length + 1, item, this)
    }
}
