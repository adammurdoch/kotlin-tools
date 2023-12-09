package net.rubygrapefruit.io

interface Result<out T, out F> {
    fun get(): T

    fun getOrNull(): T?

    fun <S> map(transform: (T) -> S): Result<S, F>
}

interface Success<out T, out F> : Result<T, F> {
    val result: T
}

interface Failure<out T, out F> : Result<T, F> {
    fun rethrow(): Nothing
}

open class DefaultSuccess<out T, out F>(override val result: T) : Success<T, F> {
    override fun get() = result

    override fun getOrNull() = result

    override fun <S> map(transform: (T) -> S): Success<S, F> {
        return of(transform(result))
    }

    protected fun <S> of(result: S): Success<S, F> {
        return DefaultSuccess(result)
    }
}

abstract class AbstractFailure<out T, out F> : Failure<T, F> {
    override fun <S> map(transform: (T) -> S): Failure<S, F> {
        return cast()
    }

    override fun get() = rethrow()

    override fun getOrNull() = null

    fun <S> cast(): Failure<S, F> {
        @Suppress("UNCHECKED_CAST")
        return this as Failure<S, F>
    }
}

open class DefaultFailure<out T, out F : IOException>(val exception: F) : AbstractFailure<T, F>() {
    override fun rethrow(): Nothing {
        throw exception
    }
}