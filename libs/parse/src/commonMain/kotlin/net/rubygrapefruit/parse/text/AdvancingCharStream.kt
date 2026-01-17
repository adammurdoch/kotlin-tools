package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.AdvancingInput

internal interface AdvancingCharStream : CharStream, AdvancingInput<CharPosition> {
    fun contextAt(index: Int): CharFailureContext {
        return CharStreamContext(posAt(index))
    }

    private class CharStreamContext(override val position: CharPosition): CharFailureContext
}