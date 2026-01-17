package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.Input
import net.rubygrapefruit.parse.SlicingInput

internal interface ByteStream : Input<BytePosition>, SlicingInput<ByteArray> {
    fun get(index: Int): Byte
}