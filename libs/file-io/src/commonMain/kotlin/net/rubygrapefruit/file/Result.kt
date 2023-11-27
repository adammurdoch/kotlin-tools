package net.rubygrapefruit.file

sealed class Result<T> {
    abstract fun get(): T

    abstract fun getOrNull(): T?

    abstract fun <S> map(transform: (T) -> S): Result<S>
}

sealed class Failed<T> : Result<T>() {
    @Suppress("UNCHECKED_CAST")
    override fun <S> map(transform: (T) -> S) = this as Result<S>

    override fun get() = rethrow()

    override fun getOrNull() = null

    fun rethrow(): Nothing {
        throw failure
    }

    abstract val failure: FileSystemException
}

/**
 * An entry that does not exist.
 */
class MissingEntry<T> internal constructor(private val path: String, private val cause: Throwable? = null) : Failed<T>() {
    override val failure: FileSystemException
        get() = missingElement(path, cause)
}

/**
 * An entry that cannot be read due to insufficient permissions.
 */
class UnreadableEntry<T> internal constructor(private val path: String) : Failed<T>() {
    override val failure: FileSystemException
        get() = unreadableElement(path)
}

class UnsupportedOperation<T> internal constructor(private val path: String, private val operation: String) : Failed<T>() {
    override val failure: FileSystemException
        get() = notSupported(path, operation)
}

class FailedOperation<T> internal constructor(override val failure: FileSystemException) : Failed<T>()

class Success<T> internal constructor(private val result: T) : Result<T>() {
    override fun get() = result

    override fun getOrNull() = result

    override fun <S> map(transform: (T) -> S): Result<S> {
        return Success(transform(result))
    }
}
