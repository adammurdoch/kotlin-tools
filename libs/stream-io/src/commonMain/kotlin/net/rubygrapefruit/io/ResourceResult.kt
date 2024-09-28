@file:OptIn(ExperimentalStdlibApi::class, ExperimentalStdlibApi::class)

package net.rubygrapefruit.io

class Resource<out T>(
    private val resource: T,
    private val closeAction: (T) -> Unit
) : AutoCloseable {
    companion object {
        fun <T : AutoCloseable> of(resource: T): Resource<T> = Resource(resource) { resource.close() }
    }

    fun <R> using(action: (T) -> R): R {
        return action(resource)
    }

    override fun close() {
        closeAction(resource)
    }
}
