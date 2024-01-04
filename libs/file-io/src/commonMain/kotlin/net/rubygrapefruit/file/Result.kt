package net.rubygrapefruit.file

import net.rubygrapefruit.io.IOException
import net.rubygrapefruit.io.Try

sealed class Result<out T> {
    abstract fun get(): T

    abstract fun getOrNull(): T?

    abstract fun <S> map(transform: (T) -> S): Result<S>
}

sealed class Failed<T> : Result<T>() {
    override fun <S> map(transform: (T) -> S): Result<S> {
        @Suppress("UNCHECKED_CAST")
        return this as Result<S>
    }

    override fun get() = rethrow()

    override fun getOrNull() = null

    fun <S> cast(): Result<S> {
        @Suppress("UNCHECKED_CAST")
        return this as Result<S>
    }

    fun rethrow(): Nothing {
        throw failure
    }

    abstract val failure: IOException
}

/**
 * An entry that does not exist.
 */
class MissingEntry<T> internal constructor(private val factory: () -> FileSystemException) : Failed<T>() {

    internal constructor(path: String, cause: Throwable? = null) : this({ missingElement(path, cause) })

    override val failure: FileSystemException by lazy { factory() }
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

class FailedOperation<T> internal constructor(override val failure: IOException) : Failed<T>()

class Success<T>(private val result: T) : Result<T>() {
    override fun get() = result

    override fun getOrNull() = result

    override fun <S> map(transform: (T) -> S): Result<S> {
        return Success(transform(result))
    }
}
