package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.BoxingInput
import net.rubygrapefruit.parse.Input
import net.rubygrapefruit.parse.SlicingInput

internal interface ByteStream : Input<BytePosition>, SlicingInput<ByteArray>, BoxingInput<BytePosition, Byte> {
    fun get(index: Int): Byte

    override fun getBoxed(index: Int): Byte {
        return get(index)
    }
}