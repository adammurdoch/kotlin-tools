package net.rubygrapefruit.parse.combinators

/**
 * Accumulates a sequence of values into a final value.
 */
internal interface Accumulator<ITEM, OUT> {
    val length: Int

    val value: OUT

    fun add(item: ITEM, length: Int): Accumulator<ITEM, OUT>
}

internal class UnitAccumulator(override val length: Int) : Accumulator<Unit, Unit> {
    override val value: Unit
        get() = Unit

    override fun add(item: Unit, length: Int): Accumulator<Unit, Unit> {
        return UnitAccumulator(length + this.length)
    }

    companion object {
        val Empty = UnitAccumulator(0)
    }
}

internal interface ListAccumulator<T> : Accumulator<T, List<T>> {
    fun collectInto(list: MutableList<T>)
}

internal class Empty<T> : ListAccumulator<T> {
    override val length: Int
        get() = 0

    override val value: List<T>
        get() = emptyList()

    override fun collectInto(list: MutableList<T>) {
    }

    override fun add(item: T, length: Int): ListAccumulator<T> {
        return ListItem(length, item, this)
    }
}

private class ListItem<T>(override val length: Int, val item: T, val prev: ListAccumulator<T>) : ListAccumulator<T> {
    override val value: List<T>
        get() {
            val list = mutableListOf<T>()
            collectInto(list)
            return list
        }

    override fun collectInto(list: MutableList<T>) {
        prev.collectInto(list)
        list.add(item)
    }

    override fun add(item: T, length: Int): ListItem<T> {
        return ListItem(length + this.length, item, this)
    }
}
