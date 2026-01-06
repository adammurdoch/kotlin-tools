package net.rubygrapefruit.parse.byte

import net.rubygrapefruit.parse.Input

internal interface ByteStream : Input<BytePosition> {
    fun get(index: Int): Byte

    override fun posAt(index: Int): BytePosition {
        return BytePosition(index)
    }
}