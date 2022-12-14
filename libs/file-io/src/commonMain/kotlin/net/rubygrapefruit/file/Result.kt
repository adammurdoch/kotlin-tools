package net.rubygrapefruit.file

sealed class Result<T> {
    abstract fun get(): T

    abstract fun getOrNull(): T?

    abstract fun <S> map(transform: (T) -> S): Result<S>
}

sealed class Failed<T>() : Result<T>() {
    @Suppress("UNCHECKED_CAST")
    override fun <S> map(transform: (T) -> S) = this as Result<S>

    override fun get() = rethrow()

    override fun getOrNull() = null

    abstract fun rethrow(): Nothing
}

class MissingEntry<T> internal constructor(private val path: String, private val cause: Throwable? = null) : Failed<T>() {
    override fun rethrow(): Nothing {
        throw missingElement(path, cause)
    }
}

class UnreadableEntry<T> internal constructor(private val path: String) : Failed<T>() {
    override fun rethrow(): Nothing {
        throw unreadableElement(path)
    }
}

class Success<T> internal constructor(val result: T) : Result<T>() {
    override fun get() = result

    override fun getOrNull() = result

    override fun <S> map(transform: (T) -> S): Result<S> {
        return Success(transform(result))
    }
}
