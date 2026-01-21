package net.rubygrapefruit.parse

internal interface BoxingInput<POS, OUT> : Input<POS> {
    fun getBoxed(index: Int): OUT
}