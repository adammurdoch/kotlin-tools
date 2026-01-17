package net.rubygrapefruit.parse

internal interface AdvancingInput<POS> : Input<POS> {
    fun advance(count: Int)
}