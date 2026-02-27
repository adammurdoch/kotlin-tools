package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.ContextualInput

internal interface AdvancingCharStream : CharStream, ContextualInput<TextFailureContext, CharPosition> {
    class TextStreamContext(override val position: CharPosition, override val lineText: String) : TextFailureContext {
        override fun toString(): String {
            return "{context offset=${position.offset}}"
        }
    }
}