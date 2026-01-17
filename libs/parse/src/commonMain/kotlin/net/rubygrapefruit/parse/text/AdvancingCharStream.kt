package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.AdvancingInput
import net.rubygrapefruit.parse.FailureContext

internal interface AdvancingCharStream : CharStream, AdvancingInput<CharPosition> {
    override fun contextAt(index: Int): FailureContext<CharPosition> {
        return CharStreamContext(posAt(index))
    }

    private class CharStreamContext(override val pos: CharPosition): FailureContext<CharPosition> {
        override fun formattedMessage(expected: String): String {
            return "Line: ${pos.line}, col: ${pos.col}: $expected"
        }
    }
}