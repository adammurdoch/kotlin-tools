package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.AdvancingInput
import net.rubygrapefruit.parse.FailureContext

internal interface AdvancingByteStream : ByteStream, AdvancingInput<BytePosition> {
    override fun contextAt(index: Int): FailureContext<BytePosition> {
        return ByteStreamContext(posAt(index))
    }

    private class ByteStreamContext(override val pos: BytePosition) : FailureContext<BytePosition> {
        override fun formattedMessage(expected: String): String {
            return "Offset: ${pos.offset}: $expected"
        }
    }
}
