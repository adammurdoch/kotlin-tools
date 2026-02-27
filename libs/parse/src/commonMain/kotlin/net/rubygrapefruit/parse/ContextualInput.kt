package net.rubygrapefruit.parse

internal interface ContextualInput<CONTEXT, POS> : AdvancingInput<POS> {
    fun contextAt(index: Int): CONTEXT
}