@file:OptIn(ExperimentalStdlibApi::class)

package net.rubygrapefruit.store

internal abstract class StoreFile: AutoCloseable {
    abstract fun closeAndDelete()
}