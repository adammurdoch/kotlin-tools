@file:OptIn(ExperimentalStdlibApi::class, ExperimentalStdlibApi::class)

package net.rubygrapefruit.io

sealed interface ResourceResult<out T> {
    fun successful(): Resource<T>
}

class Resource<out T>(
    private val resource: T,
    private val closeAction: (T) -> Unit
) : ResourceResult<T>, AutoCloseable {
    companion object {
        fun <T : AutoCloseable> of(resource: T): Resource<T> = Resource(resource) { resource.close() }
    }

    fun <R> using(action: (T) -> R): R {
        return action(resource)
    }

    override fun successful(): Resource<T> {
        return this
    }

    override fun close() {
        closeAction(resource)
    }
}

class ResourceFailure<T>(
    val exception: Exception
) : ResourceResult<T> {
    override fun successful(): Resource<T> {
        throw exception
    }
}