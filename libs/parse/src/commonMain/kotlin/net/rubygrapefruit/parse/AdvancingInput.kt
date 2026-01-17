package net.rubygrapefruit.parse

internal interface AdvancingInput<POS> : Input<POS> {
    fun contextAt(index: Int): FailureContext<POS>

    fun advance(count: Int)
}