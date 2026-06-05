package net.rubygrapefruit.parse.stream

internal interface AdvancingInput<POS> : Input<POS> {
    fun advance(count: Int)
}