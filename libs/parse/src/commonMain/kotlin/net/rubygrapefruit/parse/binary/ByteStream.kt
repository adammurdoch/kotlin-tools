package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.Input

internal interface ByteStream : Input<BytePosition> {
    fun get(index: Int): Byte
}