package net.rubygrapefruit.parse

internal interface ContextualInput<CONTEXT, POS> : AdvancingInput<POS> {
    /**
     * Returns null if the context is not yet available, and more input is required.
     */
    fun contextAt(index: Int): CONTEXT?
}