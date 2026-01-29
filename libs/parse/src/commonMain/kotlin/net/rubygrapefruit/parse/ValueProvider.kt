package net.rubygrapefruit.parse

internal interface ValueProvider<out T> {
    fun get(): T

    fun <S> map(map: (T) -> S): ValueProvider<S> {
        return MappingValueProvider(this, map)
    }

    fun <S, U> zip(other: ValueProvider<S>, map: (T, S) -> U): ValueProvider<U> {
        return ZippingValueProvider(this, other, map)
    }

    companion object {
        val Nothing = of(Unit)

        fun <T> of(value: T): ValueProvider<T> {
            return FixedValueProvider(value)
        }
    }

    private class FixedValueProvider<T>(val value: T) : ValueProvider<T> {
        override fun get(): T {
            return value
        }
    }

    private class MappingValueProvider<T, S>(val value: ValueProvider<T>, val map: (T) -> S) : ValueProvider<S> {
        override fun get(): S {
            return map(value.get())
        }
    }

    private class ZippingValueProvider<T, S, U>(val first: ValueProvider<T>, val second: ValueProvider<S>, val map: (T, S) -> U) : ValueProvider<U> {
        override fun get(): U {
            return map(first.get(), second.get())
        }
    }
}