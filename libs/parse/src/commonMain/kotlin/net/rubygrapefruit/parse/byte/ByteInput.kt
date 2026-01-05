package net.rubygrapefruit.parse.byte

import net.rubygrapefruit.parse.Input

internal interface ByteInput : Input {
    fun next(index: Int): Byte
}