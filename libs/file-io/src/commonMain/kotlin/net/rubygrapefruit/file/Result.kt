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

    abstract fun rethrow(): Nothing
}

/**
 * An entry that does not exist.
 */
class MissingEntry<T> internal constructor(private val path: String, private val cause: Throwable? = null) : Failed<T>() {
    override fun rethrow(): Nothing {
        throw missingElement(path, cause)
    }
}

/**
 * An entry that cannot be read due to insufficient permissions.
 */
class UnreadableEntry<T> internal constructor(private val path: String) : Failed<T>() {
    override fun rethrow(): Nothing {
        throw unreadableElement(path)
    }
}

class UnsupportedOperation<T> internal constructor(private val path: String, private val operation: String) : Failed<T>() {
    override fun rethrow(): Nothing {
        throw notSupported(path, operation)
    }
}

class FailedOperation<T> internal constructor(private val failure: FileSystemException) : Failed<T>() {
    override fun rethrow(): Nothing {
        throw failure
    }
}

class Success<T> internal constructor(private val result: T) : Result<T>() {
    override fun get() = result

    override fun getOrNull() = result

    override fun <S> map(transform: (T) -> S): Result<S> {
        return Success(transform(result))
    }
}
