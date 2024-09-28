package net.rubygrapefruit.io

interface Result<out T, out F> {
    fun get(): T

    fun getOrNull(): T?

    fun <S> map(transform: (T) -> S): Result<S, F>
}

interface Failure<out T, out F> : Result<T, F> {
    fun rethrow(): Nothing

    fun <S> cast(): Failure<S, F>
}

abstract class AbstractFailure<out T, out F> : Failure<T, F> {
    override fun <S> map(transform: (T) -> S): Failure<S, F> {
        return cast()
    }

    override fun get() = rethrow()

    override fun getOrNull() = null

    override fun <S> cast(): Failure<S, F> {
        @Suppress("UNCHECKED_CAST")
        return this as Failure<S, F>
    }
}

open class DefaultFailure<out T, out F : IOException>(val exception: F) : AbstractFailure<T, F>() {
    override fun rethrow(): Nothing {
        throw exception
    }
}