package net.rubygrapefruit.parse.stream

import net.rubygrapefruit.parse.Position

internal interface ContextualInput<CONTEXT, POS> : AdvancingInput<POS> {
    /**
     * Returns null if the context is not yet available, and more input is required.
     */
    fun contextAt(position: Position): CONTEXT?
}