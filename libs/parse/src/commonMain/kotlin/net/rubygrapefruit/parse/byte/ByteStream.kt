package net.rubygrapefruit.parse.byte

import net.rubygrapefruit.parse.Input

internal interface ByteStream : Input {
    fun get(index: Int): Byte
}