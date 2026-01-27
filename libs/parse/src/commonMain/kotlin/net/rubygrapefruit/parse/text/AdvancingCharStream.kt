package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.AdvancingInput

internal interface AdvancingCharStream : CharStream, AdvancingInput<CharPosition> {
    fun contextAt(index: Int): CharFailureContext

    class CharStreamContext(override val position: CharPosition, override val lineText: String) : CharFailureContext {
        override fun toString(): String {
            return "{context offset=${position.offset}}"
        }
    }
}