package net.rubygrapefruit.file

sealed class Result<T> {
    abstract fun get(): T

    abstract fun <S> map(transform: (T) -> S): Result<S>
}

sealed class Failed<T>() : Result<T>() {
    @Suppress("UNCHECKED_CAST")
    override fun <S> map(transform: (T) -> S) = this as Result<S>
}

class MissingEntry<T> internal constructor(private val path: String, private val cause: Throwable? = null) : Failed<T>() {
    override fun get(): T {
        throw missingElement(path, cause)
    }
}

class UnreadableEntry<T> internal constructor(private val path: String) : Failed<T>() {
    override fun get(): T {
        throw unreadableElement(path)
    }
}

class Success<T> internal constructor(val result: T) : Result<T>() {
    override fun get() = result

    override fun <S> map(transform: (T) -> S): Result<S> {
        return Success(transform(result))
    }
}
